package com.w1k5.atc.engine.domain.constraints;

import java.util.Objects;

/**
 * Represents altitude constraints for a flight segment.
 */
public class AltitudeConstraints {
    private final double minAltitude; // feet above sea level
    private final double maxAltitude; // feet above sea level
    private final double targetAltitude; // feet above sea level
    private final AltitudeUnit unit;

    public enum AltitudeUnit {
        FEET, METERS, FLIGHT_LEVEL
    }

    public AltitudeConstraints(double minAltitude, double maxAltitude, double targetAltitude, AltitudeUnit unit) {
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
        this.targetAltitude = targetAltitude;
        this.unit = Objects.requireNonNull(unit, "Altitude unit cannot be null");
        
        if (minAltitude > maxAltitude) {
            throw new IllegalArgumentException("Min altitude cannot be greater than max altitude");
        }
        if (targetAltitude < minAltitude || targetAltitude > maxAltitude) {
            throw new IllegalArgumentException("Target altitude must be within min/max range");
        }
    }

    // Getters
    public double getMinAltitude() { return minAltitude; }
    public double getMaxAltitude() { return maxAltitude; }
    public double getTargetAltitude() { return targetAltitude; }
    public AltitudeUnit getUnit() { return unit; }

    /**
     * Check if a given altitude is within the allowed range.
     */
    public boolean isAltitudeValid(double altitude) {
        return altitude >= minAltitude && altitude <= maxAltitude;
    }

    /**
     * Get the recommended altitude adjustment to stay within constraints.
     */
    public double getRecommendedAltitudeAdjustment(double currentAltitude) {
        if (currentAltitude < minAltitude) {
            return minAltitude - currentAltitude; // Need to climb
        } else if (currentAltitude > maxAltitude) {
            return maxAltitude - currentAltitude; // Need to descend
        }
        return 0.0; // Altitude is within range
    }

    /**
     * Convert altitude to feet (standard ATC unit).
     */
    public double getMinAltitudeInFeet() {
        return convertToFeet(minAltitude, unit);
    }

    public double getMaxAltitudeInFeet() {
        return convertToFeet(maxAltitude, unit);
    }

    public double getTargetAltitudeInFeet() {
        return convertToFeet(targetAltitude, unit);
    }

    private double convertToFeet(double altitude, AltitudeUnit unit) {
        return switch (unit) {
            case FEET -> altitude;
            case METERS -> altitude * 3.28084; // Convert meters to feet
            case FLIGHT_LEVEL -> altitude * 100; // FL100 = 10,000 feet
        };
    }

    @Override
    public String toString() {
        return String.format("AltitudeConstraints{min=%.1f, max=%.1f, target=%.1f %s}",
                minAltitude, maxAltitude, targetAltitude, unit);
    }
}
