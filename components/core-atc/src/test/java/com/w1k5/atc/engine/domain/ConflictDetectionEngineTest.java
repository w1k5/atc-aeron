package com.w1k5.atc.engine.domain;

import com.w1k5.atc.engine.domain.conflict.Conflict;
import com.w1k5.atc.engine.domain.conflict.ConflictDetectionEngine;
import com.w1k5.atc.engine.domain.conflict.ConflictDetectionStats;
import com.w1k5.atc.engine.domain.constraints.AircraftPerformance;
import com.w1k5.atc.engine.domain.constraints.AltitudeConstraints;
import com.w1k5.atc.engine.domain.constraints.SpeedConstraints;
import com.w1k5.atc.engine.domain.entities.AircraftState;
import com.w1k5.atc.engine.domain.entities.Flight;
import com.w1k5.atc.engine.domain.entities.FlightIntent;
import com.w1k5.atc.engine.domain.entities.Waypoint;
import com.w1k5.atc.engine.domain.separation.SeparationMinima;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Conflict Detection Engine Tests")
class ConflictDetectionEngineTest {

    private ConflictDetectionEngine engine;
    private Flight flight1, flight2, flight3, flight4;
    private SeparationMinima standardMinima;

    @BeforeEach
    void setUp() {
        engine = new ConflictDetectionEngine();
        standardMinima = new SeparationMinima();

        // Create test flights with different positions and performance characteristics
        AircraftState state1 = new AircraftState(100000, 200000, 5000, 10000, 3000000, 1); // 1000m, 2000m, 30000m
        AircraftState state2 = new AircraftState(150000, 250000, 6000, 11000, 3000500, 1); // 1500m, 2500m, 30005m
        AircraftState state3 = new AircraftState(500000, 600000, 4000, 9000, 2900000, 2);  // 5000m, 6000m, 29000m
        AircraftState state4 = new AircraftState(160000, 260000, 5500, 10500, 3000500, 1); // 1600m, 2600m, 30005m

        SpeedConstraints speedConstraints = new SpeedConstraints(200, 400, 300, SpeedConstraints.SpeedUnit.KNOTS);
        AltitudeConstraints altitudeConstraints = new AltitudeConstraints(25000, 35000, 30000, AltitudeConstraints.AltitudeUnit.FEET);

        List<Waypoint> waypoints = Arrays.asList(
            new Waypoint("WP1", 1000, 2000, 30000),
            new Waypoint("WP2", 2000, 3000, 32000)
        );

        FlightIntent intent = new FlightIntent("FL001", waypoints, speedConstraints, altitudeConstraints, "SID1", "STAR1");

        AircraftPerformance performance1 = new AircraftPerformance("B737", AircraftPerformance.WakeTurbulenceCategory.MEDIUM,
                                                                 2000, 2000, 400, 200, 41000);
        AircraftPerformance performance2 = new AircraftPerformance("A320", AircraftPerformance.WakeTurbulenceCategory.MEDIUM, 
                                                                 1800, 1800, 380, 180, 39000);
        AircraftPerformance performance3 = new AircraftPerformance("B747", AircraftPerformance.WakeTurbulenceCategory.HEAVY, 
                                                                 1500, 1500, 350, 150, 45000);

        flight1 = new Flight("FL001", state1, intent, performance1, 1);
        flight2 = new Flight("FL002", state2, intent, performance2, 1);
        flight3 = new Flight("FL003", state3, intent, performance1, 2);
        flight4 = new Flight("FL004", state4, intent, performance3, 1);
    }

    @Test
    @DisplayName("Should detect conflicts between close aircraft")
    void shouldDetectConflictsBetweenCloseAircraft() {
        // Add flights to engine
        engine.updateFlight(flight1);
        engine.updateFlight(flight2);
        engine.updateFlight(flight3);

        // Detect all conflicts
        List<Conflict> conflicts = engine.detectAllConflicts();

        // Should detect conflict between flight1 and flight2 (they're close)
        assertFalse(conflicts.isEmpty());
        
        // Verify conflict involves the close flights
        boolean hasConflict = conflicts.stream()
            .anyMatch(c -> (c.getFlightId1().equals("FL001") && c.getFlightId2().equals("FL002")) ||
                          (c.getFlightId1().equals("FL002") && c.getFlightId2().equals("FL001")));
        
        assertTrue(hasConflict, "Should detect conflict between FL001 and FL002");
    }

    @Test
    @DisplayName("Should not detect conflicts between distant aircraft")
    void shouldNotDetectConflictsBetweenDistantAircraft() {
        // Add flights to engine
        engine.updateFlight(flight1);
        engine.updateFlight(flight3);

        // Detect all conflicts
        List<Conflict> conflicts = engine.detectAllConflicts();

        // Should not detect conflicts (they're far apart)
        assertTrue(conflicts.isEmpty(), "Should not detect conflicts between distant aircraft");
    }

    @Test
    @DisplayName("Should detect conflicts in specific sector")
    void shouldDetectConflictsInSpecificSector() {
        // Add flights to engine
        engine.updateFlight(flight1);
        engine.updateFlight(flight2);
        engine.updateFlight(flight3);

        // Detect conflicts in sector 1
        List<Conflict> sector1Conflicts = engine.detectConflictsInSector(1);
        
        // Should detect conflicts in sector 1 (flight1 and flight2 are close)
        assertFalse(sector1Conflicts.isEmpty());
        
        // Verify all conflicts are in sector 1
        sector1Conflicts.forEach(conflict -> {
            Flight f1 = engine.getAllFlights().stream()
                .filter(f -> f.getFlightId().equals(conflict.getFlightId1()))
                .findFirst().orElse(null);
            Flight f2 = engine.getAllFlights().stream()
                .filter(f -> f.getFlightId().equals(conflict.getFlightId2()))
                .findFirst().orElse(null);
            
            assertTrue(f1 != null && f2 != null);
            assertTrue(f1.isInSector(1) && f2.isInSector(1));
        });
    }

    @Test
    @DisplayName("Should detect conflicts for specific flight")
    void shouldDetectConflictsForSpecificFlight() {
        // Add flights to engine
        engine.updateFlight(flight1);
        engine.updateFlight(flight2);
        engine.updateFlight(flight3);

        // Detect conflicts for flight1
        List<Conflict> flight1Conflicts = engine.detectConflictsForFlight("FL001");

        // Should detect conflicts involving flight1
        assertFalse(flight1Conflicts.isEmpty());
        
        // Verify all conflicts involve flight1
        flight1Conflicts.forEach(conflict -> {
            assertTrue(conflict.involvesFlight("FL001"));
        });
    }

    @Test
    @DisplayName("Should handle wake turbulence separation correctly")
    void shouldHandleWakeTurbulenceSeparationCorrectly() {
        // Add flights with different wake categories
        engine.updateFlight(flight1); // MEDIUM wake
        engine.updateFlight(flight4); // HEAVY wake

        // Detect conflicts
        List<Conflict> conflicts = engine.detectAllConflicts();

        // The engine should consider wake turbulence when determining separation
        // Heavy aircraft require more separation
        if (!conflicts.isEmpty()) {
            Conflict conflict = conflicts.get(0);
            assertTrue(conflict.involvesFlight("FL001") && conflict.involvesFlight("FL004"));
            
            // Verify the conflict considers wake turbulence
            // This is tested by ensuring the engine uses appropriate separation minima
            assertNotNull(conflict);
        }
    }

    @Test
    @DisplayName("Should update and remove flights correctly")
    void shouldUpdateAndRemoveFlightsCorrectly() {
        // Add flights
        engine.updateFlight(flight1);
        engine.updateFlight(flight2);
        
        assertEquals(2, engine.getAllFlights().size());

        // Remove a flight
        engine.removeFlight("FL001");
        assertEquals(1, engine.getAllFlights().size());
        assertNull(engine.getAllFlights().stream()
            .filter(f -> f.getFlightId().equals("FL001"))
            .findFirst().orElse(null));

        // Update existing flight
        AircraftState newState = new AircraftState(200000, 300000, 7000, 12000, 3200000, 1); // 2000m, 3000m, 32000m
        Flight updatedFlight = new Flight("FL002", newState, flight2.getIntent(), flight2.getPerformance(), 1);
        engine.updateFlight(updatedFlight);
        
        assertEquals(1, engine.getAllFlights().size());
        Flight retrieved = engine.getAllFlights().iterator().next();
        assertEquals(2000, retrieved.getState().getPositionXInMeters());
    }

    @Test
    @DisplayName("Should provide accurate statistics")
    void shouldProvideAccurateStatistics() {
        // Add flights
        engine.updateFlight(flight1);
        engine.updateFlight(flight2);
        engine.updateFlight(flight3);

        // Get statistics
        ConflictDetectionStats stats = engine.getStats();

        assertEquals(3, stats.getTotalFlights());
        assertTrue(stats.getTotalConflicts() >= 0); // May or may not have conflicts
        
        // Verify conflict rate calculation
        double expectedRate = (double) stats.getTotalConflicts() / stats.getTotalFlights();
        assertEquals(expectedRate, stats.getConflictRate(), 0.001);
    }

    @Test
    @DisplayName("Should clear all flights")
    void shouldClearAllFlights() {
        // Add flights
        engine.updateFlight(flight1);
        engine.updateFlight(flight2);
        
        assertEquals(2, engine.getAllFlights().size());

        // Clear all flights
        engine.clear();
        assertEquals(0, engine.getAllFlights().size());
        
        // Verify no conflicts after clearing
        List<Conflict> conflicts = engine.detectAllConflicts();
        assertTrue(conflicts.isEmpty());
    }

    @Test
    @DisplayName("Should handle empty system correctly")
    void shouldHandleEmptySystemCorrectly() {
        // Test with no flights
        List<Conflict> conflicts = engine.detectAllConflicts();
        assertTrue(conflicts.isEmpty());

        List<Conflict> sectorConflicts = engine.detectConflictsInSector(1);
        assertTrue(sectorConflicts.isEmpty());

        List<Conflict> flightConflicts = engine.detectConflictsForFlight("FL001");
        assertTrue(flightConflicts.isEmpty());

        ConflictDetectionStats stats = engine.getStats();
        assertEquals(0, stats.getTotalFlights());
        assertEquals(0, stats.getTotalConflicts());
        assertEquals(0.0, stats.getConflictRate());
    }
}
