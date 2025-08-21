package com.w1k5.atc.engine.domain.sector;

import com.w1k5.atc.engine.domain.entities.AircraftState;
import com.w1k5.atc.engine.domain.constraints.AircraftPerformance;
import com.w1k5.atc.engine.domain.entities.FlightIntent;
import com.w1k5.atc.engine.domain.entities.Flight;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Intelligent sector balancer that distributes aircraft across sectors based on:
 * - Workload balancing
 * - Complexity distribution
 * - Geographic optimization
 * - Emergency prioritization
 */
public class SectorBalancer {
    private final List<Sector> sectors;
    private final Map<Integer, SectorWorkload> sectorWorkloads;
    private final Map<String, Integer> aircraftSectorAssignments;
    private final Map<String, Double> aircraftComplexityCache;
    
    // Balancing configuration
    private static final double LOAD_BALANCE_THRESHOLD = 0.2; // 20% difference triggers rebalancing
    private static final double COMPLEXITY_BALANCE_THRESHOLD = 0.3; // 30% complexity difference
    private static final int MAX_REBALANCE_ATTEMPTS = 3;
    private static final boolean ENABLE_AUTO_REBALANCING = true;

    public SectorBalancer(List<Sector> sectors) {
        this.sectors = Objects.requireNonNull(sectors, "Sectors cannot be null");
        this.sectorWorkloads = new HashMap<>();
        this.aircraftSectorAssignments = new HashMap<>();
        this.aircraftComplexityCache = new HashMap<>();
        
        // Initialize workload tracking for each sector
        initializeSectorWorkloads();
    }

    /**
     * Initialize workload tracking for all sectors.
     */
    private void initializeSectorWorkloads() {
        for (Sector sector : sectors) {
            // Default capacity: 20 aircraft, complexity score 100
            sectorWorkloads.put(sector.getId(), new SectorWorkload(sector.getId(), 20, 100.0));
        }
    }

    /**
     * Main method to assign an aircraft to the optimal sector.
     */
    public SectorAssignment assignAircraftToSector(Flight flight) {
        String aircraftId = flight.getFlightId();
        AircraftState state = flight.getState();
        AircraftPerformance performance = flight.getPerformance();
        FlightIntent intent = flight.getIntent();
        
        // Calculate aircraft complexity
        double complexity = ComplexityCalculator.calculateAircraftComplexity(state, performance, intent);
        aircraftComplexityCache.put(aircraftId, complexity);
        
        // Get current sector assignment
        int currentSectorId = aircraftSectorAssignments.getOrDefault(aircraftId, -1);
        
        // Find optimal sector
        int optimalSectorId = findOptimalSector(state, complexity);
        
        // Create assignment
        SectorAssignment assignment = createAssignment(aircraftId, optimalSectorId, currentSectorId, 
                                                    complexity, determineReason(currentSectorId, optimalSectorId));
        
        // Update workload tracking
        updateSectorWorkload(aircraftId, currentSectorId, optimalSectorId, complexity);
        
        // Update aircraft assignment
        aircraftSectorAssignments.put(aircraftId, optimalSectorId);
        
        return assignment;
    }

    /**
     * Find the optimal sector for an aircraft based on multiple factors.
     */
    private int findOptimalSector(AircraftState state, double complexity) {
        List<SectorCandidate> candidates = new ArrayList<>();
        
        for (Sector sector : sectors) {
            if (sector.contains(state)) {
                SectorWorkload workload = sectorWorkloads.get(sector.getId());
                double score = calculateSectorScore(sector, workload, complexity);
                candidates.add(new SectorCandidate(sector.getId(), score, workload));
            }
        }
        
        if (candidates.isEmpty()) {
            // Aircraft is outside all sectors, find closest sector
            return findClosestSector(state);
        }
        
        // Sort by score (higher is better) and return best candidate
        candidates.sort((a, b) -> Double.compare(b.score, a.score));
        return candidates.get(0).sectorId;
    }

    /**
     * Calculate a score for a sector based on multiple factors.
     */
    private double calculateSectorScore(Sector sector, SectorWorkload workload, double aircraftComplexity) {
        double score = 0.0;
        
        // Capacity score (prefer sectors with available capacity)
        double capacityScore = 1.0 - workload.getUtilizationPercentage();
        score += capacityScore * 0.4; // 40% weight
        
        // Complexity balance score (prefer sectors with balanced complexity)
        double complexityBalance = 1.0 - Math.abs(workload.getCurrentComplexityScore() - aircraftComplexity) / 100.0;
        score += complexityBalance * 0.3; // 30% weight
        
        // Geographic score (prefer sectors where aircraft is centered)
        double geographicScore = calculateGeographicScore(sector, workload);
        score += geographicScore * 0.2; // 20% weight
        
        // Health score (prefer healthy sectors)
        double healthScore = calculateHealthScore(workload);
        score += healthScore * 0.1; // 10% weight
        
        return score;
    }

    /**
     * Calculate geographic score based on aircraft position within sector.
     */
    private double calculateGeographicScore(Sector sector, SectorWorkload workload) {
        // Prefer sectors where aircraft is not at the edge
        // This is a simplified calculation - in practice would use actual sector boundaries
        return 0.8; // Placeholder
    }

    /**
     * Calculate health score based on sector workload.
     */
    private double calculateHealthScore(SectorWorkload workload) {
        return switch (workload.getHealthStatus()) {
            case HEALTHY -> 1.0;
            case MODERATE -> 0.7;
            case HIGH -> 0.4;
            case CRITICAL -> 0.1;
        };
    }

    /**
     * Find the closest sector when aircraft is outside all sectors.
     */
    private int findClosestSector(AircraftState state) {
        Sector closestSector = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Sector sector : sectors) {
            double distance = calculateDistanceToSector(state, sector);
            if (distance < minDistance) {
                minDistance = distance;
                closestSector = sector;
            }
        }
        
        return closestSector != null ? closestSector.getId() : sectors.get(0).getId();
    }

    /**
     * Calculate distance from aircraft to sector center.
     */
    private double calculateDistanceToSector(AircraftState state, Sector sector) {
        // Simplified distance calculation - would use actual sector boundaries
        double sectorCenterX = (sector.getMinX() + sector.getMaxX()) / 2.0;
        double sectorCenterY = (sector.getMinY() + sector.getMaxY()) / 2.0;
        
        double deltaX = state.getPositionXInMeters() - sectorCenterX;
        double deltaY = state.getPositionYInMeters() - sectorCenterY;
        
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Create a sector assignment with appropriate reasoning.
     */
    private SectorAssignment createAssignment(String aircraftId, int optimalSectorId, int currentSectorId,
                                           double complexity, SectorAssignment.AssignmentReason reason) {
        SectorAssignment.AssignmentPriority priority = determinePriority(reason, complexity);
        String reasoning = generateReasoning(aircraftId, optimalSectorId, currentSectorId, reason, complexity);
        
        return new SectorAssignment(aircraftId, optimalSectorId, currentSectorId, 
                                  reason, priority, complexity, reasoning);
    }

    /**
     * Determine the reason for the sector assignment.
     */
    private SectorAssignment.AssignmentReason determineReason(int currentSectorId, int optimalSectorId) {
        if (currentSectorId == -1) {
            return SectorAssignment.AssignmentReason.INITIAL_ASSIGNMENT;
        } else if (currentSectorId != optimalSectorId) {
            return SectorAssignment.AssignmentReason.LOAD_BALANCING;
        } else {
            return SectorAssignment.AssignmentReason.OPTIMIZATION;
        }
    }

    /**
     * Determine the priority of the assignment.
     */
    private SectorAssignment.AssignmentPriority determinePriority(SectorAssignment.AssignmentReason reason, double complexity) {
        if (reason == SectorAssignment.AssignmentReason.EMERGENCY) {
            return SectorAssignment.AssignmentPriority.CRITICAL;
        } else if (complexity > 7.0) {
            return SectorAssignment.AssignmentPriority.HIGH;
        } else if (reason == SectorAssignment.AssignmentReason.LOAD_BALANCING) {
            return SectorAssignment.AssignmentPriority.MEDIUM;
        } else {
            return SectorAssignment.AssignmentPriority.LOW;
        }
    }

    /**
     * Generate human-readable reasoning for the assignment.
     */
    private String generateReasoning(String aircraftId, int optimalSectorId, int currentSectorId,
                                   SectorAssignment.AssignmentReason reason, double complexity) {
        if (reason == SectorAssignment.AssignmentReason.INITIAL_ASSIGNMENT) {
            return String.format("Initial assignment of aircraft %s to sector %d based on optimal workload distribution", 
                               aircraftId, optimalSectorId);
        } else if (reason == SectorAssignment.AssignmentReason.LOAD_BALANCING) {
            return String.format("Moved aircraft %s from sector %d to sector %d for workload balancing", 
                               aircraftId, currentSectorId, optimalSectorId);
        } else {
            return String.format("Optimized assignment of aircraft %s to sector %d (complexity: %.2f)", 
                               aircraftId, optimalSectorId, complexity);
        }
    }

    /**
     * Update sector workload tracking.
     */
    private void updateSectorWorkload(String aircraftId, int oldSectorId, int newSectorId, double complexity) {
        // Remove from old sector
        if (oldSectorId != -1) {
            SectorWorkload oldWorkload = sectorWorkloads.get(oldSectorId);
            if (oldWorkload != null) {
                oldWorkload.removeAircraft(aircraftId, complexity);
            }
        }
        
        // Add to new sector
        SectorWorkload newWorkload = sectorWorkloads.get(newSectorId);
        if (newWorkload != null) {
            newWorkload.addAircraft(aircraftId, complexity);
        }
    }

    /**
     * Perform automatic rebalancing across all sectors.
     */
    public List<SectorAssignment> performRebalancing() {
        if (!ENABLE_AUTO_REBALANCING) {
            return Collections.emptyList();
        }
        
        List<SectorAssignment> rebalancingAssignments = new ArrayList<>();
        
        // Find sectors that need rebalancing
        List<SectorWorkload> overloadedSectors = findOverloadedSectors();
        List<SectorWorkload> underloadedSectors = findUnderloadedSectors();
        
        // Attempt to rebalance
        for (SectorWorkload overloaded : overloadedSectors) {
            for (SectorWorkload underloaded : underloadedSectors) {
                List<SectorAssignment> assignments = rebalanceBetweenSectors(overloaded, underloaded);
                rebalancingAssignments.addAll(assignments);
                
                if (overloaded.getHealthStatus() == SectorWorkload.SectorHealth.HEALTHY) {
                    break; // This sector is now healthy
                }
            }
        }
        
        return rebalancingAssignments;
    }

    /**
     * Find sectors that are overloaded.
     */
    private List<SectorWorkload> findOverloadedSectors() {
        return sectorWorkloads.values().stream()
                .filter(w -> w.getHealthStatus() == SectorWorkload.SectorHealth.HIGH || 
                           w.getHealthStatus() == SectorWorkload.SectorHealth.CRITICAL)
                .sorted((a, b) -> Double.compare(b.getUtilizationPercentage(), a.getUtilizationPercentage()))
                .collect(Collectors.toList());
    }

    /**
     * Find sectors that are underloaded.
     */
    private List<SectorWorkload> findUnderloadedSectors() {
        return sectorWorkloads.values().stream()
                .filter(w -> w.getHealthStatus() == SectorWorkload.SectorHealth.HEALTHY)
                .sorted((a, b) -> Double.compare(a.getUtilizationPercentage(), b.getUtilizationPercentage()))
                .collect(Collectors.toList());
    }

    /**
     * Rebalance aircraft between two sectors.
     */
    private List<SectorAssignment> rebalanceBetweenSectors(SectorWorkload overloaded, SectorWorkload underloaded) {
        List<SectorAssignment> assignments = new ArrayList<>();
        
        // Find aircraft that can be moved from overloaded to underloaded sector
        List<String> candidateAircraft = findMovableAircraft(overloaded.getSectorId(), underloaded.getSectorId());
        
        for (String aircraftId : candidateAircraft) {
            if (shouldMoveAircraft(aircraftId, overloaded, underloaded)) {
                // Create rebalancing assignment
                double complexity = aircraftComplexityCache.getOrDefault(aircraftId, 1.0);
                SectorAssignment assignment = new SectorAssignment(
                    aircraftId, underloaded.getSectorId(), overloaded.getSectorId(),
                    SectorAssignment.AssignmentReason.LOAD_BALANCING,
                    SectorAssignment.AssignmentPriority.MEDIUM,
                    complexity,
                    "Rebalancing from overloaded sector " + overloaded.getSectorId() + 
                    " to underloaded sector " + underloaded.getSectorId()
                );
                
                assignments.add(assignment);
                
                // Update workload tracking
                updateSectorWorkload(aircraftId, overloaded.getSectorId(), underloaded.getSectorId(), complexity);
                aircraftSectorAssignments.put(aircraftId, underloaded.getSectorId());
                
                // Check if we've achieved balance
                if (overloaded.getHealthStatus() == SectorWorkload.SectorHealth.HEALTHY) {
                    break;
                }
            }
        }
        
        return assignments;
    }

    /**
     * Find aircraft that can be moved between sectors.
     */
    private List<String> findMovableAircraft(int fromSectorId, int toSectorId) {
        // This would check aircraft intent, position, and other constraints
        // For now, return a simple list of aircraft in the from sector
        SectorWorkload fromWorkload = sectorWorkloads.get(fromSectorId);
        if (fromWorkload != null) {
            return new ArrayList<>(fromWorkload.getCurrentAircraftIds());
        }
        return Collections.emptyList();
    }

    /**
     * Determine if an aircraft should be moved for rebalancing.
     */
    private boolean shouldMoveAircraft(String aircraftId, SectorWorkload from, SectorWorkload to) {
        // Check if the move would improve balance
        double fromUtilization = from.getUtilizationPercentage();
        double toUtilization = to.getUtilizationPercentage();
        
        // Only move if it improves the balance
        return (fromUtilization - toUtilization) > LOAD_BALANCE_THRESHOLD;
    }

    /**
     * Get current sector workload statistics.
     */
    public Map<Integer, SectorWorkload> getSectorWorkloads() {
        return new HashMap<>(sectorWorkloads);
    }

    /**
     * Get current aircraft sector assignments.
     */
    public Map<String, Integer> getAircraftSectorAssignments() {
        return new HashMap<>(aircraftSectorAssignments);
    }

    /**
     * Get overall system balance statistics.
     */
    public SectorBalanceStats getBalanceStats() {
        return new SectorBalanceStats(sectorWorkloads, aircraftSectorAssignments);
    }

    /**
     * Helper class for sector candidate evaluation.
     */
    private static class SectorCandidate {
        final int sectorId;
        final double score;
        final SectorWorkload workload;
        
        SectorCandidate(int sectorId, double score, SectorWorkload workload) {
            this.sectorId = sectorId;
            this.score = score;
            this.workload = workload;
        }
    }
}