package com.w1k5.atc.domain;

import com.w1k5.atc.engine.domain.AircraftState;
import com.w1k5.atc.engine.domain.Sector;
import com.w1k5.atc.engine.domain.SectorBalancer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class SectorBalancerTest {

    private SectorBalancer sectorBalancer;
    private AircraftState aircraft1;
    private AircraftState aircraft2;
    private AircraftState aircraft3;

    @BeforeEach
    public void setUp() {
        // Define sectors
        Sector sector1 = new Sector(1, 0, 0, 500, 500, 0, 10000);  // sector 1: (0,0) to (500,500), alt 0 to 10,000
        Sector sector2 = new Sector(2, 501, 0, 1000, 500, 0, 10000);  // sector 2: (501,0) to (1000,500), alt 0 to 10,000
        Sector sector3 = new Sector(3, 0, 501, 500, 1000, 0, 10000);  // sector 3: (0,501) to (500,1000), alt 0 to 10,000

        List<Sector> sectors = Arrays.asList(sector1, sector2, sector3);
        sectorBalancer = new SectorBalancer(sectors);

        // Create aircraft
        aircraft1 = new AircraftState(100, 100, 20, 20, 5000, -1); // Aircraft initially in sector 1
        aircraft2 = new AircraftState(600, 100, 20, 20, 5000, -1); // Aircraft initially in sector 2
        aircraft3 = new AircraftState(100, 600, 20, 20, 5000, -1); // Aircraft initially in sector 3
    }

    @Test
    public void testAssignSectorToAircraft1() {
        sectorBalancer.assignSector(aircraft1);
        assertEquals(1, aircraft1.getSectorId(), "Aircraft 1 should be assigned to Sector 1.");
    }

    @Test
    public void testAssignSectorToAircraft2() {
        sectorBalancer.assignSector(aircraft2);
        assertEquals(2, aircraft2.getSectorId(), "Aircraft 2 should be assigned to Sector 2.");
    }

    @Test
    public void testAssignSectorToAircraft3() {
        sectorBalancer.assignSector(aircraft3);
        assertEquals(3, aircraft3.getSectorId(), "Aircraft 3 should be assigned to Sector 3.");
    }

    @Test
    public void testAssignSectorWhenAircraftIsOutOfBounds() {
        // Create an aircraft outside of any sector
        AircraftState aircraftOutOfBounds = new AircraftState(2000, 2000, 20, 20, 5000, -1);

        sectorBalancer.assignSector(aircraftOutOfBounds);

        // Assert that the aircraft does not get assigned a sector (no sector found)
        assertEquals(-1, aircraftOutOfBounds.getSectorId(), "Aircraft out of bounds should not be assigned a sector.");
    }

    @Test
    public void testAssignSectorWithUpdatedPosition() {
        // Simulate aircraft movement
        aircraft1.updatePosition(10);  // Move aircraft 1 to a new position

        // Reassign sector
        sectorBalancer.assignSector(aircraft1);

        // After update, aircraft1 should still be in sector 1
        assertEquals(1, aircraft1.getSectorId(), "Aircraft 1 should still be assigned to Sector 1 after moving.");
    }

    @Test
    public void testSectorAssignmentOrder() {
        // Assign sectors to aircraft
        sectorBalancer.assignSector(aircraft1);
        sectorBalancer.assignSector(aircraft2);
        sectorBalancer.assignSector(aircraft3);

        // Check the order of assignments
        assertAll("Sector Assignments",
                () -> assertEquals(1, aircraft1.getSectorId(), "Aircraft 1 should be assigned to Sector 1."),
                () -> assertEquals(2, aircraft2.getSectorId(), "Aircraft 2 should be assigned to Sector 2."),
                () -> assertEquals(3, aircraft3.getSectorId(), "Aircraft 3 should be assigned to Sector 3.")
        );
    }

    @Test
    public void testSectorAssignmentToMultipleAircraft() {
        // Create a second aircraft in the same sector as aircraft1
        AircraftState aircraftInSector1 = new AircraftState(200, 200, 10, 10, 5000, -1);

        sectorBalancer.assignSector(aircraft1);
        sectorBalancer.assignSector(aircraftInSector1);

        assertEquals(1, aircraft1.getSectorId(), "Aircraft 1 should be in Sector 1.");
        assertEquals(1, aircraftInSector1.getSectorId(), "Aircraft in the same position should also be in Sector 1.");
    }
}
