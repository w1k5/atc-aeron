package com.w1k5.atc.engine.domain;

import java.util.Objects;

/**
 * Represents the performance characteristics of an aircraft type.
 * This includes climb/descent rates, speed envelopes, and wake turbulence category.
 */
public class AircraftPerformance {
    private final String aircraftType;
    private final WakeTurbulenceCategory wakeCategory;
    private final double maxClimbRate; // feet per minute
    private final double maxDescentRate; // feet per minute
    private final double maxSpeed; // knots
    private final double minSpeed; // knots
    private final double maxAltitude; // feet

    public enum WakeTurbulenceCategory {
        LIGHT, MEDIUM, HEAVY, SUPER
    }

    public AircraftPerformance(String aircraftType, WakeTurbulenceCategory wakeCategory,
                             double maxClimbRate, double maxDescentRate,
                             double maxSpeed, double minSpeed, double maxAltitude) {
        this.aircraftType = Objects.requireNonNull(aircraftType, "Aircraft type cannot be null");
        this.wakeCategory = Objects.requireNonNull(wakeCategory, "Wake category cannot be null");
        this.maxClimbRate = maxClimbRate;
        this.maxDescentRate = maxDescentRate;
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        this.maxAltitude = maxAltitude;
        
        if (minSpeed > maxSpeed) {
            throw new IllegalArgumentException("Min speed cannot be greater than max speed");
        }
    }

    // Getters
    public String getAircraftType() { return aircraftType; }
    public WakeTurbulenceCategory getWakeCategory() { return wakeCategory; }
    public double getMaxClimbRate() { return maxClimbRate; }
    public double getMaxDescentRate() { return maxDescentRate; }
    public double getMaxSpeed() { return maxSpeed; }
    public double getMinSpeed() { return minSpeed; }
    public double getMaxAltitude() { return maxAltitude; }

    /**
     * Get the required separation distance based on wake turbulence category.
     */
    public double getRequiredSeparationDistance() {
        return switch (wakeCategory) {
            case LIGHT -> 3.0; // 3 nautical miles
            case MEDIUM -> 5.0; // 5 nautical miles
            case HEAVY -> 6.0; // 6 nautical miles
            case SUPER -> 8.0; // 8 nautical miles
        };
    }

    /**
     * Check if the aircraft can climb to the target altitude.
     */
    public boolean canClimbTo(double targetAltitude) {
        return targetAltitude <= maxAltitude;
    }

    /**
     * Check if the aircraft can maintain the given speed.
     */
    public boolean canMaintainSpeed(double speed) {
        return speed >= minSpeed && speed <= maxSpeed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AircraftPerformance that = (AircraftPerformance) o;
        return Objects.equals(aircraftType, that.aircraftType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aircraftType);
    }

    @Override
    public String toString() {
        return String.format("AircraftPerformance{type='%s', wake=%s, speed=%.0f-%.0f kt, maxAlt=%.0f ft}",
                aircraftType, wakeCategory, minSpeed, maxSpeed, maxAltitude);
    }
}
