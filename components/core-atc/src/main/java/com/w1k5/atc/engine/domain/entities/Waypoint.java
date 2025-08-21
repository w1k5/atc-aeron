package com.w1k5.atc.engine.domain.entities;

import java.util.Objects;

/**
 * Represents a navigation waypoint in 3D space.
 */
public class Waypoint {
    private final String name;
    private final double x; // meters
    private final double y; // meters
    private final double altitude; // meters above sea level

    public Waypoint(String name, double x, double y, double altitude) {
        this.name = Objects.requireNonNull(name, "Waypoint name cannot be null");
        this.x = x;
        this.y = y;
        this.altitude = altitude;
    }

    // Getters
    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAltitude() { return altitude; }

    /**
     * Calculate distance to another waypoint in meters.
     */
    public double distanceTo(Waypoint other) {
        double deltaX = this.x - other.x;
        double deltaY = this.y - other.y;
        double deltaZ = this.altitude - other.altitude;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    /**
     * Calculate horizontal distance to another waypoint in meters.
     */
    public double horizontalDistanceTo(Waypoint other) {
        double deltaX = this.x - other.x;
        double deltaY = this.y - other.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waypoint waypoint = (Waypoint) o;
        return Objects.equals(name, waypoint.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("Waypoint{name='%s', position=(%.1f, %.1f), altitude=%.1f}",
                name, x, y, altitude);
    }
}
