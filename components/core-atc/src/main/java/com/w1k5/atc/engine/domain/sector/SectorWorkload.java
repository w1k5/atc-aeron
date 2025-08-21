package com.w1k5.atc.engine.domain.sector;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Tracks the workload and capacity of a sector for intelligent balancing.
 * This includes aircraft count, complexity metrics, and capacity limits.
 */
public class SectorWorkload {
    private final int sectorId;
    private final int maxAircraftCapacity;
    private final double maxComplexityScore;
    private final List<String> currentAircraftIds;
    private double currentComplexityScore;
    private long lastUpdateTime;

    public SectorWorkload(int sectorId, int maxAircraftCapacity, double maxComplexityScore) {
        this.sectorId = sectorId;
        this.maxAircraftCapacity = maxAircraftCapacity;
        this.maxComplexityScore = maxComplexityScore;
        this.currentAircraftIds = new ArrayList<>();
        this.currentComplexityScore = 0.0;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    // Getters
    public int getSectorId() { return sectorId; }
    public int getMaxAircraftCapacity() { return maxAircraftCapacity; }
    public double getMaxComplexityScore() { return maxComplexityScore; }
    public List<String> getCurrentAircraftIds() { return new ArrayList<>(currentAircraftIds); }
    public double getCurrentComplexityScore() { return currentComplexityScore; }
    public long getLastUpdateTime() { return lastUpdateTime; }

    /**
     * Get current aircraft count.
     */
    public int getCurrentAircraftCount() {
        return currentAircraftIds.size();
    }

    /**
     * Get remaining aircraft capacity.
     */
    public int getRemainingAircraftCapacity() {
        return Math.max(0, maxAircraftCapacity - getCurrentAircraftCount());
    }

    /**
     * Get remaining complexity capacity.
     */
    public double getRemainingComplexityCapacity() {
        return Math.max(0.0, maxComplexityScore - currentComplexityScore);
    }

    /**
     * Check if sector can accept more aircraft.
     */
    public boolean canAcceptAircraft() {
        return getRemainingAircraftCapacity() > 0 && getRemainingComplexityCapacity() > 0.0;
    }

    /**
     * Check if sector is at capacity.
     */
    public boolean isAtCapacity() {
        return !canAcceptAircraft();
    }

    /**
     * Get utilization percentage (0.0 to 1.0).
     */
    public double getUtilizationPercentage() {
        double aircraftUtilization = (double) getCurrentAircraftCount() / maxAircraftCapacity;
        double complexityUtilization = currentComplexityScore / maxComplexityScore;
        return Math.max(aircraftUtilization, complexityUtilization);
    }

    /**
     * Add aircraft to sector.
     */
    public void addAircraft(String aircraftId, double complexityContribution) {
        if (!currentAircraftIds.contains(aircraftId)) {
            currentAircraftIds.add(aircraftId);
            currentComplexityScore += complexityContribution;
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    /**
     * Remove aircraft from sector.
     */
    public void removeAircraft(String aircraftId, double complexityContribution) {
        if (currentAircraftIds.remove(aircraftId)) {
            currentComplexityScore = Math.max(0.0, currentComplexityScore - complexityContribution);
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    /**
     * Update complexity score for an aircraft.
     */
    public void updateAircraftComplexity(String aircraftId, double oldComplexity, double newComplexity) {
        if (currentAircraftIds.contains(aircraftId)) {
            currentComplexityScore = currentComplexityScore - oldComplexity + newComplexity;
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    /**
     * Get sector health status.
     */
    public SectorHealth getHealthStatus() {
        double utilization = getUtilizationPercentage();
        if (utilization < 0.6) return SectorHealth.HEALTHY;
        if (utilization < 0.8) return SectorHealth.MODERATE;
        if (utilization < 0.95) return SectorHealth.HIGH;
        return SectorHealth.CRITICAL;
    }

    public enum SectorHealth {
        HEALTHY, MODERATE, HIGH, CRITICAL
    }

    @Override
    public String toString() {
        return String.format("SectorWorkload{id=%d, aircraft=%d/%d, complexity=%.2f/%.2f, health=%s}",
                sectorId, getCurrentAircraftCount(), maxAircraftCapacity, 
                currentComplexityScore, maxComplexityScore, getHealthStatus());
    }
}
