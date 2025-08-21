package com.w1k5.atc.engine.domain.constraints;

import java.util.Objects;

/**
 * Represents speed constraints for a flight segment.
 */
public class SpeedConstraints {
    private final double minSpeed; // knots
    private final double maxSpeed; // knots
    private final double targetSpeed; // knots
    private final SpeedUnit unit;

    public enum SpeedUnit {
        KNOTS, MACH, KPH
    }

    public SpeedConstraints(double minSpeed, double maxSpeed, double targetSpeed, SpeedUnit unit) {
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.targetSpeed = targetSpeed;
        this.unit = Objects.requireNonNull(unit, "Speed unit cannot be null");
        
        if (minSpeed > maxSpeed) {
            throw new IllegalArgumentException("Min speed cannot be greater than max speed");
        }
        if (targetSpeed < minSpeed || targetSpeed > maxSpeed) {
            throw new IllegalArgumentException("Target speed must be within min/max range");
        }
    }

    // Getters
    public double getMinSpeed() { return minSpeed; }
    public double getMaxSpeed() { return maxSpeed; }
    public double getTargetSpeed() { return targetSpeed; }
    public SpeedUnit getUnit() { return unit; }

    /**
     * Check if a given speed is within the allowed range.
     */
    public boolean isSpeedValid(double speed) {
        return speed >= minSpeed && speed <= maxSpeed;
    }

    /**
     * Get the recommended speed adjustment to stay within constraints.
     */
    public double getRecommendedSpeedAdjustment(double currentSpeed) {
        if (currentSpeed < minSpeed) {
            return minSpeed - currentSpeed; // Need to speed up
        } else if (currentSpeed > maxSpeed) {
            return maxSpeed - currentSpeed; // Need to slow down
        }
        return 0.0; // Speed is within range
    }

    @Override
    public String toString() {
        return String.format("SpeedConstraints{min=%.1f, max=%.1f, target=%.1f %s}",
                minSpeed, maxSpeed, targetSpeed, unit);
    }
}
