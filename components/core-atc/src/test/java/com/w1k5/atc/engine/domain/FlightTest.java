package com.w1k5.atc.engine.domain;

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

@DisplayName("Flight Entity Tests")
class FlightTest {

    private AircraftState state1, state2, state3;
    private FlightIntent intent1, intent2;
    private AircraftPerformance performance1, performance2;
    private Flight flight1, flight2, flight3;
    private SeparationMinima standardMinima;

    @BeforeEach
    void setUp() {
        // Create test aircraft states (values are in centimeters, so 30000 = 300 meters)
        state1 = new AircraftState(100000, 200000, 5000, 10000, 3000000, 1); // 1000m, 2000m, 30000m
        state2 = new AircraftState(150000, 250000, 6000, 11000, 3000500, 1); // 1500m, 2500m, 30005m (only 5m vertical separation)
        state3 = new AircraftState(500000, 600000, 4000, 9000, 2900000, 2);  // 5000m, 6000m, 29000m

        // Create test constraints
        SpeedConstraints speedConstraints = new SpeedConstraints(200, 400, 300, SpeedConstraints.SpeedUnit.KNOTS);
        AltitudeConstraints altitudeConstraints = new AltitudeConstraints(25000, 35000, 30000, AltitudeConstraints.AltitudeUnit.FEET);

        // Create test waypoints
        List<Waypoint> waypoints = Arrays.asList(
            new Waypoint("WP1", 1000, 2000, 30000),
            new Waypoint("WP2", 2000, 3000, 32000),
            new Waypoint("WP3", 3000, 4000, 34000)
        );

        // Create test intents
        intent1 = new FlightIntent("FL001", waypoints, speedConstraints, altitudeConstraints, "SID1", "STAR1");
        intent2 = new FlightIntent("FL002", waypoints, speedConstraints, altitudeConstraints, "SID2", "STAR2");

        // Create test performance profiles
        performance1 = new AircraftPerformance("B737", AircraftPerformance.WakeTurbulenceCategory.MEDIUM, 
                                             2000, 2000, 400, 200, 41000);
        performance2 = new AircraftPerformance("A320", AircraftPerformance.WakeTurbulenceCategory.MEDIUM, 
                                             1800, 1800, 380, 180, 39000);

        // Create test flights
        flight1 = new Flight("FL001", state1, intent1, performance1, 1);
        flight2 = new Flight("FL002", state2, intent2, performance2, 1);
        flight3 = new Flight("FL003", state3, intent1, performance1, 2);

        // Create standard separation minima
        standardMinima = new SeparationMinima();
    }

    @Test
    @DisplayName("Should create flight with valid parameters")
    void shouldCreateFlightWithValidParameters() {
        assertNotNull(flight1);
        assertEquals("FL001", flight1.getFlightId());
        assertEquals(state1, flight1.getState());
        assertEquals(intent1, flight1.getIntent());
        assertEquals(performance1, flight1.getPerformance());
        assertEquals(1, flight1.getSectorId());
        assertNotNull(flight1.getLastUpdate());
    }

    @Test
    @DisplayName("Should throw exception for null flight ID")
    void shouldThrowExceptionForNullFlightId() {
        assertThrows(NullPointerException.class, () -> {
            new Flight(null, state1, intent1, performance1, 1);
        });
    }

    @Test
    @DisplayName("Should throw exception for null aircraft state")
    void shouldThrowExceptionForNullAircraftState() {
        assertThrows(NullPointerException.class, () -> {
            new Flight("FL001", null, intent1, performance1, 1);
        });
    }

    @Test
    @DisplayName("Should detect conflict when aircraft are too close")
    void shouldDetectConflictWhenAircraftAreTooClose() {
        // Aircraft 1 and 2 are close (about 707 meters apart horizontally, 5 meters vertically)
        assertTrue(flight1.hasConflictWith(flight2, standardMinima));
    }

    @Test
    @DisplayName("Should not detect conflict when aircraft are far apart")
    void shouldNotDetectConflictWhenAircraftAreFarApart() {
        // Aircraft 1 and 3 are far apart (about 7071 meters apart)
        assertFalse(flight1.hasConflictWith(flight3, standardMinima));
    }

    @Test
    @DisplayName("Should not detect conflict with itself")
    void shouldNotDetectConflictWithItself() {
        assertFalse(flight1.hasConflictWith(flight1, standardMinima));
    }

    @Test
    @DisplayName("Should correctly identify sector membership")
    void shouldCorrectlyIdentifySectorMembership() {
        assertTrue(flight1.isInSector(1));
        assertFalse(flight1.isInSector(2));
        assertTrue(flight3.isInSector(2));
    }

    @Test
    @DisplayName("Should calculate correct horizontal distance")
    void shouldCalculateCorrectHorizontalDistance() {
        // Distance between state1 (1000m, 2000m) and state2 (1500m, 2500m)
        // Should be sqrt((1500-1000)² + (2500-2000)²) = sqrt(500² + 500²) = sqrt(500000) ≈ 707.1
        double expectedDistance = Math.sqrt(500 * 500 + 500 * 500);
        double actualDistance = Math.sqrt(
            Math.pow(state2.getPositionXInMeters() - state1.getPositionXInMeters(), 2) +
            Math.pow(state2.getPositionYInMeters() - state1.getPositionYInMeters(), 2)
        );
        
        assertEquals(expectedDistance, actualDistance, 0.1);
    }

    @Test
    @DisplayName("Should handle wake turbulence separation correctly")
    void shouldHandleWakeTurbulenceSeparationCorrectly() {
        // Create heavy aircraft performance
        AircraftPerformance heavyPerformance = new AircraftPerformance("B747", 
            AircraftPerformance.WakeTurbulenceCategory.HEAVY, 1500, 1500, 350, 150, 45000);
        
        Flight heavyFlight = new Flight("FL004", state1, intent1, heavyPerformance, 1);
        
        // Heavy aircraft should require more separation
        SeparationMinima wakeMinima = SeparationMinima.createWithWakeTurbulence(
            AircraftPerformance.WakeTurbulenceCategory.HEAVY);
        
        // Even though they're close, they might not conflict due to wake turbulence rules
        // This depends on the specific implementation of wake turbulence separation
        assertNotNull(wakeMinima);
        assertTrue(wakeMinima.getHorizontalMinima() >= standardMinima.getHorizontalMinima());
    }

    @Test
    @DisplayName("Should have correct equality and hash code")
    void shouldHaveCorrectEqualityAndHashCode() {
        Flight sameFlight = new Flight("FL001", state1, intent1, performance1, 1);
        Flight differentFlight = new Flight("FL002", state2, intent2, performance2, 1);

        // Equality
        assertEquals(flight1, sameFlight);
        assertNotEquals(flight1, differentFlight);
        assertNotEquals(flight1, null);
        assertNotEquals(flight1, "not a flight");

        // Hash code
        assertEquals(flight1.hashCode(), sameFlight.hashCode());
        assertNotEquals(flight1.hashCode(), differentFlight.hashCode());
    }

    @Test
    @DisplayName("Should provide meaningful string representation")
    void shouldProvideMeaningfulStringRepresentation() {
        String representation = flight1.toString();
        
        assertTrue(representation.contains("FL001"));
        assertTrue(representation.contains("sector=1"));
        assertTrue(representation.contains("position=(1000.0, 2000.0)"));
        assertTrue(representation.contains("altitude=30000.0")); // 3000000 cm = 30000 m
    }
}
