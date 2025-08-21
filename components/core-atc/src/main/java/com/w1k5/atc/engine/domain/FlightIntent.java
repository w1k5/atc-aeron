package com.w1k5.atc.engine.domain;

import java.util.List;
import java.util.Objects;

/**
 * Represents the planned route and operational constraints for a flight.
 * This includes waypoints, speed/altitude constraints, and SID/STAR procedures.
 */
public class FlightIntent {
    private final String flightId;
    private final List<Waypoint> waypoints;
    private final SpeedConstraints speedConstraints;
    private final AltitudeConstraints altitudeConstraints;
    private final String sid; // Standard Instrument Departure
    private final String star; // Standard Terminal Arrival Route

    public FlightIntent(String flightId, List<Waypoint> waypoints, 
                       SpeedConstraints speedConstraints, AltitudeConstraints altitudeConstraints,
                       String sid, String star) {
        this.flightId = Objects.requireNonNull(flightId, "Flight ID cannot be null");
        this.waypoints = Objects.requireNonNull(waypoints, "Waypoints cannot be null");
        this.speedConstraints = Objects.requireNonNull(speedConstraints, "Speed constraints cannot be null");
        this.altitudeConstraints = Objects.requireNonNull(altitudeConstraints, "Altitude constraints cannot be null");
        this.sid = sid;
        this.star = star;
    }

    // Getters
    public String getFlightId() { return flightId; }
    public List<Waypoint> getWaypoints() { return waypoints; }
    public SpeedConstraints getSpeedConstraints() { return speedConstraints; }
    public AltitudeConstraints getAltitudeConstraints() { return altitudeConstraints; }
    public String getSid() { return sid; }
    public String getStar() { return star; }

    /**
     * Get the next waypoint after the current position.
     */
    public Waypoint getNextWaypoint(double currentX, double currentY) {
        if (waypoints.isEmpty()) return null;
        
        // Simple logic: find the closest waypoint ahead of current position
        // In a real system, this would be more sophisticated
        return waypoints.stream()
                .filter(wp -> wp.getX() > currentX || wp.getY() > currentY)
                .findFirst()
                .orElse(waypoints.get(waypoints.size() - 1));
    }

    /**
     * Check if the flight is in the departure phase (before first waypoint).
     */
    public boolean isDeparting() {
        return waypoints.size() > 0 && sid != null;
    }

    /**
     * Check if the flight is in the arrival phase (approaching last waypoint).
     */
    public boolean isArriving() {
        return waypoints.size() > 0 && star != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightIntent that = (FlightIntent) o;
        return Objects.equals(flightId, that.flightId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId);
    }

    @Override
    public String toString() {
        return String.format("FlightIntent{id='%s', waypoints=%d, sid='%s', star='%s'}",
                flightId, waypoints.size(), sid, star);
    }
}
