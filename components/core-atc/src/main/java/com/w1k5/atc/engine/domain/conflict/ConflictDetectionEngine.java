package com.w1k5.atc.engine.domain.conflict;

import com.w1k5.atc.engine.domain.constraints.AircraftPerformance;
import com.w1k5.atc.engine.domain.entities.Flight;
import com.w1k5.atc.engine.domain.separation.SeparationMinima;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core conflict detection engine for ATC operations.
 * Detects potential conflicts between aircraft based on separation minima.
 */
public class ConflictDetectionEngine {
    private final SeparationMinima defaultSeparationMinima;
    private final Map<String, Flight> flights;

    public ConflictDetectionEngine() {
        this(new SeparationMinima());
    }

    public ConflictDetectionEngine(SeparationMinima defaultSeparationMinima) {
        this.defaultSeparationMinima = Objects.requireNonNull(defaultSeparationMinima);
        this.flights = new HashMap<>();
    }

    /**
     * Add or update a flight in the system.
     */
    public void updateFlight(Flight flight) {
        flights.put(flight.getFlightId(), flight);
    }

    /**
     * Remove a flight from the system.
     */
    public void removeFlight(String flightId) {
        flights.remove(flightId);
    }

    /**
     * Get all flights in the system.
     */
    public Collection<Flight> getAllFlights() {
        return flights.values();
    }

    /**
     * Detect all conflicts in the system.
     */
    public List<Conflict> detectAllConflicts() {
        List<Conflict> conflicts = new ArrayList<>();
        List<Flight> flightList = new ArrayList<>(flights.values());

        for (int i = 0; i < flightList.size(); i++) {
            for (int j = i + 1; j < flightList.size(); j++) {
                Flight flight1 = flightList.get(i);
                Flight flight2 = flightList.get(j);

                Conflict conflict = detectConflict(flight1, flight2);
                if (conflict != null) {
                    conflicts.add(conflict);
                }
            }
        }

        return conflicts;
    }

    /**
     * Detect conflicts for a specific flight.
     */
    public List<Conflict> detectConflictsForFlight(String flightId) {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return Collections.emptyList();
        }

        return flights.values().stream()
                .filter(f -> !f.equals(flight))
                .map(f -> detectConflict(flight, f))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Detect conflicts in a specific sector.
     */
    public List<Conflict> detectConflictsInSector(int sectorId) {
        List<Flight> sectorFlights = flights.values().stream()
                .filter(f -> f.isInSector(sectorId))
                .collect(Collectors.toList());

        List<Conflict> conflicts = new ArrayList<>();
        for (int i = 0; i < sectorFlights.size(); i++) {
            for (int j = i + 1; j < sectorFlights.size(); j++) {
                Conflict conflict = detectConflict(sectorFlights.get(i), sectorFlights.get(j));
                if (conflict != null) {
                    conflicts.add(conflict);
                }
            }
        }

        return conflicts;
    }

    /**
     * Detect conflict between two specific flights.
     */
    private Conflict detectConflict(Flight flight1, Flight flight2) {
        // Get appropriate separation minima considering wake turbulence
        SeparationMinima minima = getSeparationMinima(flight1, flight2);

        if (flight1.hasConflictWith(flight2, minima)) {
            return new Conflict(
                flight1.getFlightId(),
                flight2.getFlightId(),
                Conflict.Severity.HIGH,
                calculateConflictDistance(flight1, flight2),
                calculateTimeToConflict(flight1, flight2)
            );
        }

        return null;
    }

    /**
     * Get appropriate separation minima for two flights.
     */
    private SeparationMinima getSeparationMinima(Flight flight1, Flight flight2) {
        // Consider wake turbulence - use the more restrictive separation
        AircraftPerformance.WakeTurbulenceCategory category1 = flight1.getPerformance().getWakeCategory();
        AircraftPerformance.WakeTurbulenceCategory category2 = flight2.getPerformance().getWakeCategory();
        
        // Use the higher wake category for separation
        AircraftPerformance.WakeTurbulenceCategory higherCategory = 
            getHigherWakeCategory(category1, category2);
        
        return SeparationMinima.createWithWakeTurbulence(higherCategory);
    }

    /**
     * Get the higher wake turbulence category.
     */
    private AircraftPerformance.WakeTurbulenceCategory getHigherWakeCategory(
            AircraftPerformance.WakeTurbulenceCategory cat1,
            AircraftPerformance.WakeTurbulenceCategory cat2) {
        
        if (cat1 == AircraftPerformance.WakeTurbulenceCategory.SUPER || 
            cat2 == AircraftPerformance.WakeTurbulenceCategory.SUPER) {
            return AircraftPerformance.WakeTurbulenceCategory.SUPER;
        }
        if (cat1 == AircraftPerformance.WakeTurbulenceCategory.HEAVY || 
            cat2 == AircraftPerformance.WakeTurbulenceCategory.HEAVY) {
            return AircraftPerformance.WakeTurbulenceCategory.HEAVY;
        }
        if (cat1 == AircraftPerformance.WakeTurbulenceCategory.MEDIUM || 
            cat2 == AircraftPerformance.WakeTurbulenceCategory.MEDIUM) {
            return AircraftPerformance.WakeTurbulenceCategory.MEDIUM;
        }
        return AircraftPerformance.WakeTurbulenceCategory.LIGHT;
    }

    /**
     * Calculate the current distance between two flights.
     */
    private double calculateConflictDistance(Flight flight1, Flight flight2) {
        double deltaX = flight1.getState().getPositionXInMeters() - flight2.getState().getPositionXInMeters();
        double deltaY = flight1.getState().getPositionYInMeters() - flight2.getState().getPositionYInMeters();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Calculate estimated time to conflict (simplified calculation).
     */
    private double calculateTimeToConflict(Flight flight1, Flight flight2) {
        // Simple calculation based on relative velocity
        // In a real system, this would be more sophisticated
        double relativeVelocity = Math.abs(
            flight1.getState().getVelocityXInMetersPerSecond() - 
            flight2.getState().getVelocityXInMetersPerSecond()
        );
        
        if (relativeVelocity < 0.1) return Double.MAX_VALUE; // No relative movement
        
        double distance = calculateConflictDistance(flight1, flight2);
        return distance / relativeVelocity; // seconds
    }

    /**
     * Get system statistics.
     */
    public ConflictDetectionStats getStats() {
        int totalFlights = flights.size();
        List<Conflict> allConflicts = detectAllConflicts();
        int totalConflicts = allConflicts.size();
        
        return new ConflictDetectionStats(totalFlights, totalConflicts, allConflicts);
    }

    /**
     * Clear all flights from the system.
     */
    public void clear() {
        flights.clear();
    }
}
