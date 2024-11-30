package com.w1k5.atc.engine;

import com.w1k5.atc.engine.model.AircraftState;

import java.util.List;

public class ConflictDetectionAlgorithm {

    private static final long CONFLICT_DISTANCE_THRESHOLD = 500; // In centimeters (5 meters)
    private static final long CONFLICT_ALTITUDE_THRESHOLD = 300000; // In centimeters (3000 meters)

    public void processAircraftState(AircraftState state, List<AircraftState> otherAircrafts) {
        for (AircraftState otherAircraft : otherAircrafts) {
            if (isConflictDetected(state, otherAircraft)) {
                resolveConflict(state, otherAircraft);
            }
        }
    }

    private boolean isConflictDetected(AircraftState state, AircraftState otherAircraft) {
        boolean isAltitudeConflict = Math.abs(state.getAltitude() - otherAircraft.getAltitude()) < CONFLICT_ALTITUDE_THRESHOLD;
        boolean isDistanceConflict = state.getDistanceToOtherAircraft(otherAircraft) < CONFLICT_DISTANCE_THRESHOLD;

        return isAltitudeConflict && isDistanceConflict;
    }

    private void resolveConflict(AircraftState state, AircraftState otherAircraft) {
        // Logic for resolving conflict (e.g., rerouting aircraft)
        System.out.println("Conflict detected between aircraft " + state + " and " + otherAircraft + ". Resolving...");
        // Placeholder for actual conflict resolution logic, such as adjusting altitude or rerouting
    }
}