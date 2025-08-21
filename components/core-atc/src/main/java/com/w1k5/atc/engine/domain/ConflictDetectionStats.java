package com.w1k5.atc.engine.domain;

import java.util.List;
import java.util.Objects;

/**
 * Statistics about the conflict detection system.
 * Provides insights into system performance and conflict patterns.
 */
public class ConflictDetectionStats {
    private final int totalFlights;
    private final int totalConflicts;
    private final List<Conflict> conflicts;

    public ConflictDetectionStats(int totalFlights, int totalConflicts, List<Conflict> conflicts) {
        this.totalFlights = totalFlights;
        this.totalConflicts = totalConflicts;
        this.conflicts = Objects.requireNonNull(conflicts, "Conflicts list cannot be null");
    }

    // Getters
    public int getTotalFlights() { return totalFlights; }
    public int getTotalConflicts() { return totalConflicts; }
    public List<Conflict> getConflicts() { return conflicts; }

    /**
     * Get the conflict rate (conflicts per flight).
     */
    public double getConflictRate() {
        if (totalFlights == 0) return 0.0;
        return (double) totalConflicts / totalFlights;
    }

    /**
     * Get conflicts by severity.
     */
    public long getConflictsBySeverity(Conflict.Severity severity) {
        return conflicts.stream()
                .filter(c -> c.getSeverity() == severity)
                .count();
    }

    /**
     * Get conflicts by urgency.
     */
    public long getConflictsByUrgency(Conflict.Urgency urgency) {
        return conflicts.stream()
                .filter(c -> c.getUrgency() == urgency)
                .count();
    }

    /**
     * Get the average time to conflict.
     */
    public double getAverageTimeToConflict() {
        if (conflicts.isEmpty()) return 0.0;
        
        double totalTime = conflicts.stream()
                .mapToDouble(Conflict::getTimeToConflict)
                .sum();
        
        return totalTime / conflicts.size();
    }

    /**
     * Get the average conflict distance.
     */
    public double getAverageConflictDistance() {
        if (conflicts.isEmpty()) return 0.0;
        
        double totalDistance = conflicts.stream()
                .mapToDouble(Conflict::getDistance)
                .sum();
        
        return totalDistance / conflicts.size();
    }

    /**
     * Check if there are any immediate conflicts (less than 30 seconds).
     */
    public boolean hasImmediateConflicts() {
        return conflicts.stream()
                .anyMatch(c -> c.getUrgency() == Conflict.Urgency.IMMEDIATE);
    }

    /**
     * Get a summary of the current situation.
     */
    public String getSummary() {
        return String.format("System Status: %d flights, %d conflicts (%.2f rate), %.1f avg time to conflict",
                totalFlights, totalConflicts, getConflictRate(), getAverageTimeToConflict());
    }

    @Override
    public String toString() {
        return String.format("ConflictDetectionStats{flights=%d, conflicts=%d, rate=%.2f}",
                totalFlights, totalConflicts, getConflictRate());
    }
}
