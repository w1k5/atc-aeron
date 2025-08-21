package com.w1k5.atc.engine.domain.conflict;

import java.util.Objects;

/**
 * Represents a detected conflict between two aircraft.
 * This is the core output of the conflict detection engine.
 */
public class Conflict {
    private final String flightId1;
    private final String flightId2;
    private final Severity severity;
    private final double distance; // meters
    private final double timeToConflict; // seconds

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public Conflict(String flightId1, String flightId2, Severity severity, 
                   double distance, double timeToConflict) {
        this.flightId1 = Objects.requireNonNull(flightId1, "Flight ID 1 cannot be null");
        this.flightId2 = Objects.requireNonNull(flightId2, "Flight ID 2 cannot be null");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.distance = distance;
        this.timeToConflict = timeToConflict;
    }

    // Getters
    public String getFlightId1() { return flightId1; }
    public String getFlightId2() { return flightId2; }
    public Severity getSeverity() { return severity; }
    public double getDistance() { return distance; }
    public double getTimeToConflict() { return timeToConflict; }

    /**
     * Get the other flight ID in this conflict.
     */
    public String getOtherFlightId(String flightId) {
        if (flightId.equals(flightId1)) {
            return flightId2;
        } else if (flightId.equals(flightId2)) {
            return flightId1;
        } else {
            throw new IllegalArgumentException("Flight ID not part of this conflict: " + flightId);
        }
    }

    /**
     * Check if this conflict involves the specified flight.
     */
    public boolean involvesFlight(String flightId) {
        return flightId.equals(flightId1) || flightId.equals(flightId2);
    }

    /**
     * Get the urgency level based on time to conflict.
     */
    public Urgency getUrgency() {
        if (timeToConflict < 30) return Urgency.IMMEDIATE; // Less than 30 seconds
        if (timeToConflict < 120) return Urgency.URGENT;   // Less than 2 minutes
        if (timeToConflict < 300) return Urgency.HIGH;     // Less than 5 minutes
        return Urgency.NORMAL;                              // More than 5 minutes
    }

    public enum Urgency {
        NORMAL, HIGH, URGENT, IMMEDIATE
    }

    /**
     * Get a human-readable description of the conflict.
     */
    public String getDescription() {
        return String.format("Conflict between %s and %s: %.1f meters, %.1f seconds, %s severity",
                flightId1, flightId2, distance, timeToConflict, severity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conflict conflict = (Conflict) o;
        // Conflicts are equal if they involve the same two flights (order doesn't matter)
        return (Objects.equals(flightId1, conflict.flightId1) && 
                Objects.equals(flightId2, conflict.flightId2)) ||
               (Objects.equals(flightId1, conflict.flightId2) && 
                Objects.equals(flightId2, conflict.flightId1));
    }

    @Override
    public int hashCode() {
        // Order-independent hash code
        return Objects.hash(flightId1, flightId2) + Objects.hash(flightId2, flightId1);
    }

    @Override
    public String toString() {
        return String.format("Conflict{%s<->%s, %s, %.1fm, %.1fs}",
                flightId1, flightId2, severity, distance, timeToConflict);
    }
}
