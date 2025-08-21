package com.w1k5.atc.engine.domain.sector;

import com.w1k5.atc.engine.domain.entities.AircraftState;
import com.w1k5.atc.engine.domain.constraints.AircraftPerformance;
import com.w1k5.atc.engine.domain.entities.FlightIntent;

/**
 * Calculates the complexity contribution of aircraft to sector workload.
 * This helps the sector balancer make intelligent decisions about workload distribution.
 */
public class ComplexityCalculator {
    
    // Complexity weights for different factors
    private static final double SPEED_WEIGHT = 0.3;
    private static final double ALTITUDE_WEIGHT = 0.2;
    private static final double WAKE_TURBULENCE_WEIGHT = 0.25;
    private static final double INTENT_COMPLEXITY_WEIGHT = 0.15;
    private static final double POSITION_WEIGHT = 0.1;

    /**
     * Calculate the complexity contribution of an aircraft to a sector.
     * Higher complexity means more controller attention and workload.
     */
    public static double calculateAircraftComplexity(AircraftState state, AircraftPerformance performance, 
                                                   FlightIntent intent) {
        double complexity = 0.0;

        // Speed complexity (faster aircraft = more complex)
        complexity += calculateSpeedComplexity(state, performance) * SPEED_WEIGHT;

        // Altitude complexity (climbing/descending = more complex)
        complexity += calculateAltitudeComplexity(state, performance) * ALTITUDE_WEIGHT;

        // Wake turbulence complexity (heavier aircraft = more complex)
        complexity += calculateWakeTurbulenceComplexity(performance) * WAKE_TURBULENCE_WEIGHT;

        // Intent complexity (departure/arrival = more complex)
        complexity += calculateIntentComplexity(intent) * INTENT_COMPLEXITY_WEIGHT;

        // Position complexity (edge of sector = more complex)
        complexity += calculatePositionComplexity(state) * POSITION_WEIGHT;

        return Math.max(0.0, Math.min(10.0, complexity)); // Normalize to 0-10 scale
    }

    /**
     * Calculate speed-based complexity.
     */
    private static double calculateSpeedComplexity(AircraftState state, AircraftPerformance performance) {
        double currentSpeed = Math.sqrt(
            state.getVelocityXInMetersPerSecond() * state.getVelocityXInMetersPerSecond() +
            state.getVelocityYInMetersPerSecond() * state.getVelocityYInMetersPerSecond()
        );
        
        double maxSpeed = performance.getMaxSpeed() * 0.514444; // Convert knots to m/s
        
        // Normalize speed (0.0 = stationary, 1.0 = max speed)
        double normalizedSpeed = Math.min(1.0, currentSpeed / maxSpeed);
        
        // Higher speed = higher complexity
        return normalizedSpeed * 10.0;
    }

    /**
     * Calculate altitude-based complexity.
     */
    private static double calculateAltitudeComplexity(AircraftState state, AircraftPerformance performance) {
        double currentAltitude = state.getAltitudeInMeters();
        double maxAltitude = performance.getMaxAltitude() * 0.3048; // Convert feet to meters
        
        // Check if aircraft is climbing or descending
        double verticalVelocity = Math.abs(state.getVelocityYInMetersPerSecond());
        double climbRate = performance.getMaxClimbRate() * 0.3048 / 60.0; // Convert fpm to m/s
        
        double altitudeComplexity = 0.0;
        
        // Base complexity based on altitude
        if (currentAltitude < 1000) {
            altitudeComplexity = 8.0; // Low altitude = high complexity
        } else if (currentAltitude < 5000) {
            altitudeComplexity = 6.0; // Terminal area
        } else if (currentAltitude < 10000) {
            altitudeComplexity = 4.0; // Transition altitude
        } else {
            altitudeComplexity = 2.0; // Cruise altitude
        }
        
        // Add complexity for climbing/descending
        if (verticalVelocity > climbRate * 0.1) {
            altitudeComplexity += 3.0;
        }
        
        return altitudeComplexity;
    }

    /**
     * Calculate wake turbulence complexity.
     */
    private static double calculateWakeTurbulenceComplexity(AircraftPerformance performance) {
        return switch (performance.getWakeCategory()) {
            case LIGHT -> 2.0;
            case MEDIUM -> 4.0;
            case HEAVY -> 7.0;
            case SUPER -> 10.0;
        };
    }

    /**
     * Calculate intent-based complexity.
     */
    private static double calculateIntentComplexity(FlightIntent intent) {
        double complexity = 3.0; // Base complexity
        
        if (intent.isDeparting()) {
            complexity += 3.0; // Departures are complex
        }
        
        if (intent.isArriving()) {
            complexity += 4.0; // Arrivals are most complex
        }
        
        // Add complexity based on number of waypoints
        complexity += Math.min(3.0, intent.getWaypoints().size() * 0.5);
        
        return complexity;
    }

    /**
     * Calculate position-based complexity.
     */
    private static double calculatePositionComplexity(AircraftState state) {
        // This would typically use sector boundaries
        // For now, return base complexity
        return 2.0;
    }

    /**
     * Calculate sector boundary complexity (how close aircraft is to sector edges).
     */
    public static double calculateSectorBoundaryComplexity(AircraftState state, Sector sector) {
        // This is a placeholder - would need sector boundary information
        // Higher complexity when aircraft is near sector boundaries
        return 1.0;
    }

    /**
     * Get a human-readable description of complexity factors.
     */
    public static String getComplexityDescription(AircraftState state, AircraftPerformance performance, 
                                                FlightIntent intent) {
        double speedComplexity = calculateSpeedComplexity(state, performance) * SPEED_WEIGHT;
        double altitudeComplexity = calculateAltitudeComplexity(state, performance) * ALTITUDE_WEIGHT;
        double wakeComplexity = calculateWakeTurbulenceComplexity(performance) * WAKE_TURBULENCE_WEIGHT;
        double intentComplexity = calculateIntentComplexity(intent) * INTENT_COMPLEXITY_WEIGHT;
        double positionComplexity = calculatePositionComplexity(state) * POSITION_WEIGHT;
        
        return String.format("Complexity breakdown: Speed=%.2f, Altitude=%.2f, Wake=%.2f, Intent=%.2f, Position=%.2f",
                speedComplexity, altitudeComplexity, wakeComplexity, intentComplexity, positionComplexity);
    }
}
