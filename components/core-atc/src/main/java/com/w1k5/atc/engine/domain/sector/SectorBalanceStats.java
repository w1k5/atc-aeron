package com.w1k5.atc.engine.domain.sector;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides overall system balance statistics for the sector balancer.
 * This helps controllers and system operators understand the current state.
 */
public class SectorBalanceStats {
    private final Map<Integer, SectorWorkload> sectorWorkloads;
    private final Map<String, Integer> aircraftSectorAssignments;

    public SectorBalanceStats(Map<Integer, SectorWorkload> sectorWorkloads, 
                            Map<String, Integer> aircraftSectorAssignments) {
        this.sectorWorkloads = sectorWorkloads;
        this.aircraftSectorAssignments = aircraftSectorAssignments;
    }

    /**
     * Get total number of aircraft in the system.
     */
    public int getTotalAircraftCount() {
        return aircraftSectorAssignments.size();
    }

    /**
     * Get total number of sectors.
     */
    public int getTotalSectorCount() {
        return sectorWorkloads.size();
    }

    /**
     * Get average aircraft per sector.
     */
    public double getAverageAircraftPerSector() {
        if (getTotalSectorCount() == 0) return 0.0;
        return (double) getTotalAircraftCount() / getTotalSectorCount();
    }

    /**
     * Get average sector utilization.
     */
    public double getAverageSectorUtilization() {
        if (getTotalSectorCount() == 0) return 0.0;
        
        double totalUtilization = sectorWorkloads.values().stream()
                .mapToDouble(SectorWorkload::getUtilizationPercentage)
                .sum();
        
        return totalUtilization / getTotalSectorCount();
    }

    /**
     * Get sectors by health status.
     */
    public Map<SectorWorkload.SectorHealth, Long> getSectorsByHealth() {
        return sectorWorkloads.values().stream()
                .collect(Collectors.groupingBy(
                    SectorWorkload::getHealthStatus,
                    Collectors.counting()
                ));
    }

    /**
     * Get overloaded sectors (HIGH or CRITICAL health).
     */
    public List<SectorWorkload> getOverloadedSectors() {
        return sectorWorkloads.values().stream()
                .filter(w -> w.getHealthStatus() == SectorWorkload.SectorHealth.HIGH ||
                           w.getHealthStatus() == SectorWorkload.SectorHealth.CRITICAL)
                .collect(Collectors.toList());
    }

    /**
     * Get healthy sectors.
     */
    public List<SectorWorkload> getHealthySectors() {
        return sectorWorkloads.values().stream()
                .filter(w -> w.getHealthStatus() == SectorWorkload.SectorHealth.HEALTHY)
                .collect(Collectors.toList());
    }

    /**
     * Get sectors that need attention (MODERATE, HIGH, or CRITICAL health).
     */
    public List<SectorWorkload> getSectorsNeedingAttention() {
        return sectorWorkloads.values().stream()
                .filter(w -> w.getHealthStatus() != SectorWorkload.SectorHealth.HEALTHY)
                .collect(Collectors.toList());
    }

    /**
     * Get the most overloaded sector.
     */
    public SectorWorkload getMostOverloadedSector() {
        return sectorWorkloads.values().stream()
                .max((a, b) -> Double.compare(a.getUtilizationPercentage(), b.getUtilizationPercentage()))
                .orElse(null);
    }

    /**
     * Get the least loaded sector.
     */
    public SectorWorkload getLeastLoadedSector() {
        return sectorWorkloads.values().stream()
                .min((a, b) -> Double.compare(a.getUtilizationPercentage(), b.getUtilizationPercentage()))
                .orElse(null);
    }

    /**
     * Check if the system is balanced.
     */
    public boolean isSystemBalanced() {
        return getOverloadedSectors().isEmpty();
    }

    /**
     * Get system health score (0.0 to 1.0).
     */
    public double getSystemHealthScore() {
        if (getTotalSectorCount() == 0) return 0.0;
        
        long healthySectors = getHealthySectors().size();
        return (double) healthySectors / getTotalSectorCount();
    }

    /**
     * Get workload distribution statistics.
     */
    public WorkloadDistribution getWorkloadDistribution() {
        double minUtilization = sectorWorkloads.values().stream()
                .mapToDouble(SectorWorkload::getUtilizationPercentage)
                .min()
                .orElse(0.0);
        
        double maxUtilization = sectorWorkloads.values().stream()
                .mapToDouble(SectorWorkload::getUtilizationPercentage)
                .max()
                .orElse(0.0);
        
        double standardDeviation = calculateStandardDeviation();
        
        return new WorkloadDistribution(minUtilization, maxUtilization, standardDeviation);
    }

    /**
     * Calculate standard deviation of sector utilization.
     */
    private double calculateStandardDeviation() {
        if (getTotalSectorCount() == 0) return 0.0;
        
        double mean = getAverageSectorUtilization();
        double variance = sectorWorkloads.values().stream()
                .mapToDouble(w -> Math.pow(w.getUtilizationPercentage() - mean, 2))
                .average()
                .orElse(0.0);
        
        return Math.sqrt(variance);
    }

    /**
     * Get a summary of the current system state.
     */
    public String getSystemSummary() {
        return String.format(
            "System Status: %d aircraft across %d sectors, %.1f%% average utilization, %.1f%% system health",
            getTotalAircraftCount(),
            getTotalSectorCount(),
            getAverageSectorUtilization() * 100,
            getSystemHealthScore() * 100
        );
    }

    /**
     * Get detailed sector information.
     */
    public List<SectorInfo> getSectorDetails() {
        return sectorWorkloads.values().stream()
                .map(this::createSectorInfo)
                .collect(Collectors.toList());
    }

    /**
     * Create sector information summary.
     */
    private SectorInfo createSectorInfo(SectorWorkload workload) {
        return new SectorInfo(
            workload.getSectorId(),
            workload.getCurrentAircraftCount(),
            workload.getMaxAircraftCapacity(),
            workload.getCurrentComplexityScore(),
            workload.getMaxComplexityScore(),
            workload.getUtilizationPercentage(),
            workload.getHealthStatus()
        );
    }

    /**
     * Workload distribution statistics.
     */
    public static class WorkloadDistribution {
        private final double minUtilization;
        private final double maxUtilization;
        private final double standardDeviation;

        public WorkloadDistribution(double minUtilization, double maxUtilization, double standardDeviation) {
            this.minUtilization = minUtilization;
            this.maxUtilization = maxUtilization;
            this.standardDeviation = standardDeviation;
        }

        public double getMinUtilization() { return minUtilization; }
        public double getMaxUtilization() { return maxUtilization; }
        public double getStandardDeviation() { return standardDeviation; }
        public double getRange() { return maxUtilization - minUtilization; }

        @Override
        public String toString() {
            return String.format("WorkloadDistribution{min=%.2f, max=%.2f, stdDev=%.2f, range=%.2f}",
                    minUtilization, maxUtilization, standardDeviation, getRange());
        }
    }

    /**
     * Sector information summary.
     */
    public static class SectorInfo {
        private final int sectorId;
        private final int currentAircraft;
        private final int maxAircraft;
        private final double currentComplexity;
        private final double maxComplexity;
        private final double utilization;
        private final SectorWorkload.SectorHealth health;

        public SectorInfo(int sectorId, int currentAircraft, int maxAircraft,
                         double currentComplexity, double maxComplexity,
                         double utilization, SectorWorkload.SectorHealth health) {
            this.sectorId = sectorId;
            this.currentAircraft = currentAircraft;
            this.maxAircraft = maxAircraft;
            this.currentComplexity = currentComplexity;
            this.maxComplexity = maxComplexity;
            this.utilization = utilization;
            this.health = health;
        }

        // Getters
        public int getSectorId() { return sectorId; }
        public int getCurrentAircraft() { return currentAircraft; }
        public int getMaxAircraft() { return maxAircraft; }
        public double getCurrentComplexity() { return currentComplexity; }
        public double getMaxComplexity() { return maxComplexity; }
        public double getUtilization() { return utilization; }
        public SectorWorkload.SectorHealth getHealth() { return health; }

        @Override
        public String toString() {
            return String.format("SectorInfo{id=%d, aircraft=%d/%d, complexity=%.1f/%.1f, utilization=%.1f%%, health=%s}",
                    sectorId, currentAircraft, maxAircraft, currentComplexity, maxComplexity, 
                    utilization * 100, health);
        }
    }
}
