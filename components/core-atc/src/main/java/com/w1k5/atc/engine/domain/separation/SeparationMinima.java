package com.w1k5.atc.engine.domain.separation;

import com.w1k5.atc.engine.domain.constraints.AircraftPerformance;

/**
 * Defines the minimum separation distances required between aircraft.
 * These are the core safety constraints for ATC operations.
 */
public class SeparationMinima {
    // Standard separation minima in nautical miles and feet
    public static final double STANDARD_HORIZONTAL_MINIMA = 3.0; // 3 NM
    public static final double STANDARD_VERTICAL_MINIMA = 1000.0; // 1000 feet
    
    // Reduced separation minima for specific conditions
    public static final double REDUCED_HORIZONTAL_MINIMA = 2.5; // 2.5 NM
    public static final double REDUCED_VERTICAL_MINIMA = 500.0; // 500 feet
    
    // Wake turbulence separation (in nautical miles)
    public static final double WAKE_SEPARATION_LIGHT = 3.0;
    public static final double WAKE_SEPARATION_MEDIUM = 5.0;
    public static final double WAKE_SEPARATION_HEAVY = 6.0;
    public static final double WAKE_SEPARATION_SUPER = 8.0;

    private final double horizontalMinima;
    private final double verticalMinima;
    private final boolean isReduced;

    /**
     * Create standard separation minima.
     */
    public SeparationMinima() {
        this(STANDARD_HORIZONTAL_MINIMA, STANDARD_VERTICAL_MINIMA, false);
    }

    /**
     * Create custom separation minima.
     */
    public SeparationMinima(double horizontalMinima, double verticalMinima, boolean isReduced) {
        this.horizontalMinima = horizontalMinima;
        this.verticalMinima = verticalMinima;
        this.isReduced = isReduced;
    }

    // Getters
    public double getHorizontalMinima() { return horizontalMinima; }
    public double getVerticalMinima() { return verticalMinima; }
    public boolean isReduced() { return isReduced; }

    /**
     * Get wake turbulence separation distance for the given category.
     */
    public static double getWakeSeparationDistance(AircraftPerformance.WakeTurbulenceCategory category) {
        return switch (category) {
            case LIGHT -> WAKE_SEPARATION_LIGHT;
            case MEDIUM -> WAKE_SEPARATION_MEDIUM;
            case HEAVY -> WAKE_SEPARATION_HEAVY;
            case SUPER -> WAKE_SEPARATION_SUPER;
        };
    }

    /**
     * Create reduced separation minima for specific conditions.
     */
    public static SeparationMinima createReduced() {
        return new SeparationMinima(REDUCED_HORIZONTAL_MINIMA, REDUCED_VERTICAL_MINIMA, true);
    }

    /**
     * Create custom separation minima with wake turbulence consideration.
     */
    public static SeparationMinima createWithWakeTurbulence(AircraftPerformance.WakeTurbulenceCategory category) {
        double wakeSeparation = getWakeSeparationDistance(category);
        double horizontal = Math.max(STANDARD_HORIZONTAL_MINIMA, wakeSeparation);
        return new SeparationMinima(horizontal, STANDARD_VERTICAL_MINIMA, false);
    }

    @Override
    public String toString() {
        return String.format("SeparationMinima{h=%.1f NM, v=%.0f ft, reduced=%s}",
                horizontalMinima, verticalMinima, isReduced);
    }
}
