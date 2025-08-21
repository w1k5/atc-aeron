package com.w1k5.atc.engine.domain.sector;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a sector assignment decision made by the sector balancer.
 * Includes the reasoning and priority for the assignment.
 */
public class SectorAssignment {
    private final String aircraftId;
    private final int assignedSectorId;
    private final int previousSectorId;
    private final AssignmentReason reason;
    private final AssignmentPriority priority;
    private final double complexityContribution;
    private final Instant assignmentTime;
    private final String reasoning;

    public enum AssignmentReason {
        INITIAL_ASSIGNMENT,      // First time assignment
        LOAD_BALANCING,          // Moved for workload distribution
        EMERGENCY,               // Emergency situation
        WEATHER_AVOIDANCE,       // Weather-related rerouting
        TRAFFIC_CONFLICT,        // Conflict resolution
        OPTIMIZATION,            // Performance optimization
        CAPACITY_OVERFLOW        // Current sector at capacity
    }

    public enum AssignmentPriority {
        LOW,        // Normal operations
        MEDIUM,     // Load balancing
        HIGH,       // Conflict resolution
        CRITICAL    // Emergency situations
    }

    public SectorAssignment(String aircraftId, int assignedSectorId, int previousSectorId,
                           AssignmentReason reason, AssignmentPriority priority,
                           double complexityContribution, String reasoning) {
        this.aircraftId = Objects.requireNonNull(aircraftId, "Aircraft ID cannot be null");
        this.assignedSectorId = assignedSectorId;
        this.previousSectorId = previousSectorId;
        this.reason = Objects.requireNonNull(reason, "Assignment reason cannot be null");
        this.priority = Objects.requireNonNull(priority, "Assignment priority cannot be null");
        this.complexityContribution = complexityContribution;
        this.assignmentTime = Instant.now();
        this.reasoning = Objects.requireNonNull(reasoning, "Reasoning cannot be null");
    }

    // Getters
    public String getAircraftId() { return aircraftId; }
    public int getAssignedSectorId() { return assignedSectorId; }
    public int getPreviousSectorId() { return previousSectorId; }
    public AssignmentReason getReason() { return reason; }
    public AssignmentPriority getPriority() { return priority; }
    public double getComplexityContribution() { return complexityContribution; }
    public Instant getAssignmentTime() { return assignmentTime; }
    public String getReasoning() { return reasoning; }

    /**
     * Check if this is a sector change (not initial assignment).
     */
    public boolean isSectorChange() {
        return previousSectorId != -1 && previousSectorId != assignedSectorId;
    }

    /**
     * Check if this is an initial assignment.
     */
    public boolean isInitialAssignment() {
        return previousSectorId == -1;
    }

    /**
     * Get a human-readable description of the assignment.
     */
    public String getDescription() {
        if (isInitialAssignment()) {
            return String.format("Aircraft %s assigned to Sector %d (%s)", 
                    aircraftId, assignedSectorId, reason.name());
        } else {
            return String.format("Aircraft %s moved from Sector %d to Sector %d (%s)", 
                    aircraftId, previousSectorId, assignedSectorId, reason.name());
        }
    }

    /**
     * Check if this assignment requires immediate action.
     */
    public boolean requiresImmediateAction() {
        return priority == AssignmentPriority.CRITICAL || 
               reason == AssignmentReason.EMERGENCY ||
               reason == AssignmentReason.TRAFFIC_CONFLICT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectorAssignment that = (SectorAssignment) o;
        return Objects.equals(aircraftId, that.aircraftId) &&
               assignedSectorId == that.assignedSectorId &&
               assignmentTime.equals(that.assignmentTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aircraftId, assignedSectorId, assignmentTime);
    }

    @Override
    public String toString() {
        return String.format("SectorAssignment{aircraft=%s, sector=%d->%d, reason=%s, priority=%s}",
                aircraftId, previousSectorId, assignedSectorId, reason, priority);
    }
}
