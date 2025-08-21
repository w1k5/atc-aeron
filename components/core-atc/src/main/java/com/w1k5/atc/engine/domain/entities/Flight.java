package com.w1k5.atc.engine.domain.entities;

import com.w1k5.atc.engine.domain.constraints.AircraftPerformance;
import com.w1k5.atc.engine.domain.separation.SeparationMinima;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a flight with its current state, intent, and performance characteristics.
 * This is the central entity for ATC decision making.
 */
public class Flight {
    private final String flightId;
    private final AircraftState state;
    private final FlightIntent intent;
    private final AircraftPerformance performance;
    private final Instant lastUpdate;
    private final int sectorId;

    public Flight(String flightId, AircraftState state, FlightIntent intent, 
                  AircraftPerformance performance, int sectorId) {
        this.flightId = Objects.requireNonNull(flightId, "Flight ID cannot be null");
        this.state = Objects.requireNonNull(state, "Aircraft state cannot be null");
        this.intent = Objects.requireNonNull(intent, "Flight intent cannot be null");
        this.performance = Objects.requireNonNull(performance, "Aircraft performance cannot be null");
        this.sectorId = sectorId;
        this.lastUpdate = Instant.now();
    }

    // Getters
    public String getFlightId() { return flightId; }
    public AircraftState getState() { return state; }
    public FlightIntent getIntent() { return intent; }
    public AircraftPerformance getPerformance() { return performance; }
    public Instant getLastUpdate() { return lastUpdate; }
    public int getSectorId() { return sectorId; }

    /**
     * Check if this flight conflicts with another flight based on separation minima.
     */
    public boolean hasConflictWith(Flight other, SeparationMinima minima) {
        if (this.equals(other)) return false;
        
        double horizontalDistance = calculateHorizontalDistance(other);
        double verticalDistance = Math.abs(this.state.getAltitudeInMeters() - other.state.getAltitudeInMeters());
        
        // Convert separation minima from nautical miles to meters (1 NM = 1852 meters)
        double horizontalMinimaMeters = minima.getHorizontalMinima() * 1852.0;
        // Convert vertical minima from feet to meters (1 foot = 0.3048 meters)
        double verticalMinimaMeters = minima.getVerticalMinima() * 0.3048;
        
        return horizontalDistance < horizontalMinimaMeters && 
               verticalDistance < verticalMinimaMeters;
    }

    /**
     * Calculate horizontal distance to another flight in meters.
     */
    private double calculateHorizontalDistance(Flight other) {
        double deltaX = this.state.getPositionXInMeters() - other.state.getPositionXInMeters();
        double deltaY = this.state.getPositionYInMeters() - other.state.getPositionYInMeters();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Check if this flight is in the specified sector.
     */
    public boolean isInSector(int sectorId) {
        return this.sectorId == sectorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(flightId, flight.flightId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId);
    }

    @Override
    public String toString() {
        return String.format("Flight{id='%s', sector=%d, position=(%.1f, %.1f), altitude=%.1f}",
                flightId, sectorId, 
                state.getPositionXInMeters(), state.getPositionYInMeters(),
                state.getAltitudeInMeters());
    }
}
