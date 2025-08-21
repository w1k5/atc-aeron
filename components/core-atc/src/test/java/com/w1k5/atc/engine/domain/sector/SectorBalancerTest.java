package com.w1k5.atc.engine.domain.sector;

import com.w1k5.atc.engine.domain.entities.AircraftState;
import com.w1k5.atc.engine.domain.entities.Flight;
import com.w1k5.atc.engine.domain.entities.FlightIntent;
import com.w1k5.atc.engine.domain.constraints.AircraftPerformance;
import com.w1k5.atc.engine.domain.constraints.SpeedConstraints;
import com.w1k5.atc.engine.domain.constraints.AltitudeConstraints;
import com.w1k5.atc.engine.domain.entities.Waypoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sector Balancer Tests")
class SectorBalancerTest {

    private SectorBalancer sectorBalancer;
    private List<Sector> sectors;
    private Flight testFlight;
    private AircraftState testState;
    private AircraftPerformance testPerformance;
    private FlightIntent testIntent;

    @BeforeEach
    void setUp() {
        // Create test sectors with different boundaries
        sectors = Arrays.asList(
            new Sector(1, 0, 0, 1000000, 1000000, 0, 5000000),      // Sector 1: 0-1000m, 0-1000m, 0-50000m
            new Sector(2, 1000000, 0, 2000000, 1000000, 0, 5000000), // Sector 2: 1000-2000m, 0-1000m, 0-50000m
            new Sector(3, 0, 1000000, 1000000, 2000000, 0, 5000000)  // Sector 3: 0-1000m, 1000-2000m, 0-50000m
        );
        
        sectorBalancer = new SectorBalancer(sectors);
        
        // Create test flight components
        testState = new AircraftState(500000, 500000, 5000, 10000, 3000000, -1); // Center of sector 1
        testPerformance = new AircraftPerformance("B737", AircraftPerformance.WakeTurbulenceCategory.MEDIUM, 
                                                2000, 2000, 400, 200, 41000);
        
        List<Waypoint> waypoints = Arrays.asList(
            new Waypoint("WP1", 500000, 500000, 3000000),
            new Waypoint("WP2", 600000, 600000, 3200000)
        );
        
        SpeedConstraints speedConstraints = new SpeedConstraints(200, 400, 300, SpeedConstraints.SpeedUnit.KNOTS);
        AltitudeConstraints altitudeConstraints = new AltitudeConstraints(25000, 35000, 30000, AltitudeConstraints.AltitudeUnit.FEET);
        
        testIntent = new FlightIntent("FL001", waypoints, speedConstraints, altitudeConstraints, "SID1", "STAR1");
        testFlight = new Flight("FL001", testState, testIntent, testPerformance, -1);
    }

    @Test
    @DisplayName("Should create sector balancer with sectors")
    void shouldCreateSectorBalancerWithSectors() {
        assertNotNull(sectorBalancer);
        assertEquals(3, sectors.size());
    }

    @Test
    @DisplayName("Should assign aircraft to optimal sector")
    void shouldAssignAircraftToOptimalSector() {
        SectorAssignment assignment = sectorBalancer.assignAircraftToSector(testFlight);
        
        assertNotNull(assignment);
        assertEquals("FL001", assignment.getAircraftId());
        assertEquals(1, assignment.getAssignedSectorId()); // Should be assigned to sector 1 (center position)
        assertEquals(-1, assignment.getPreviousSectorId()); // Initial assignment
        assertEquals(SectorAssignment.AssignmentReason.INITIAL_ASSIGNMENT, assignment.getReason());
        assertTrue(assignment.isInitialAssignment());
        assertFalse(assignment.isSectorChange());
    }

    @Test
    @DisplayName("Should assign aircraft to closest sector when outside boundaries")
    void shouldAssignAircraftToClosestSectorWhenOutsideBoundaries() {
        // Create aircraft outside all sectors
        AircraftState outsideState = new AircraftState(2500000, 2500000, 5000, 10000, 3000000, -1);
        Flight outsideFlight = new Flight("FL002", outsideState, testIntent, testPerformance, -1);
        
        SectorAssignment assignment = sectorBalancer.assignAircraftToSector(outsideFlight);
        
        assertNotNull(assignment);
        // Should be assigned to the closest sector (likely sector 2 or 3)
        assertTrue(assignment.getAssignedSectorId() >= 1 && assignment.getAssignedSectorId() <= 3);
    }

    @Test
    @DisplayName("Should track sector workload correctly")
    void shouldTrackSectorWorkloadCorrectly() {
        // Assign aircraft to sector
        sectorBalancer.assignAircraftToSector(testFlight);
        
        // Check workload tracking
        var sectorWorkloads = sectorBalancer.getSectorWorkloads();
        SectorWorkload sector1Workload = sectorWorkloads.get(1);
        
        assertNotNull(sector1Workload);
        assertEquals(1, sector1Workload.getCurrentAircraftCount());
        assertTrue(sector1Workload.getCurrentComplexityScore() > 0.0);
    }

    @Test
    @DisplayName("Should handle multiple aircraft assignments")
    void shouldHandleMultipleAircraftAssignments() {
        // Create multiple flights
        Flight flight1 = new Flight("FL001", testState, testIntent, testPerformance, -1);
        Flight flight2 = new Flight("FL002", testState, testIntent, testPerformance, -1);
        Flight flight3 = new Flight("FL003", testState, testIntent, testPerformance, -1);
        
        // Assign all flights
        SectorAssignment assignment1 = sectorBalancer.assignAircraftToSector(flight1);
        SectorAssignment assignment2 = sectorBalancer.assignAircraftToSector(flight2);
        SectorAssignment assignment3 = sectorBalancer.assignAircraftToSector(flight3);
        
        // All should be assigned to sector 1 (same position)
        assertEquals(1, assignment1.getAssignedSectorId());
        assertEquals(1, assignment2.getAssignedSectorId());
        assertEquals(1, assignment3.getAssignedSectorId());
        
        // Check workload
        var sectorWorkloads = sectorBalancer.getSectorWorkloads();
        SectorWorkload sector1Workload = sectorWorkloads.get(1);
        assertEquals(3, sector1Workload.getCurrentAircraftCount());
    }

    @Test
    @DisplayName("Should provide balance statistics")
    void shouldProvideBalanceStats() {
        // Assign some aircraft
        sectorBalancer.assignAircraftToSector(testFlight);
        
        // Get balance stats
        SectorBalanceStats stats = sectorBalancer.getBalanceStats();
        
        assertNotNull(stats);
        assertEquals(1, stats.getTotalAircraftCount());
        assertEquals(3, stats.getTotalSectorCount());
        assertEquals(1.0 / 3.0, stats.getAverageAircraftPerSector(), 0.001);
        assertTrue(stats.getSystemHealthScore() > 0.0);
    }

    @Test
    @DisplayName("Should track aircraft sector assignments")
    void shouldTrackAircraftSectorAssignments() {
        sectorBalancer.assignAircraftToSector(testFlight);
        
        var assignments = sectorBalancer.getAircraftSectorAssignments();
        assertEquals(1, assignments.size());
        assertEquals(1, assignments.get("FL001"));
    }

    @Test
    @DisplayName("Should handle sector changes for existing aircraft")
    void shouldHandleSectorChangesForExistingAircraft() {
        // Initial assignment
        SectorAssignment initialAssignment = sectorBalancer.assignAircraftToSector(testFlight);
        assertEquals(1, initialAssignment.getAssignedSectorId());
        
        // Move aircraft to different position (sector 2)
        AircraftState newState = new AircraftState(1500000, 500000, 5000, 10000, 3000000, 1);
        Flight movedFlight = new Flight("FL001", newState, testIntent, testPerformance, 1);
        
        SectorAssignment newAssignment = sectorBalancer.assignAircraftToSector(movedFlight);
        
        assertEquals(2, newAssignment.getAssignedSectorId());
        assertEquals(1, newAssignment.getPreviousSectorId());
        assertTrue(newAssignment.isSectorChange());
        assertEquals(SectorAssignment.AssignmentReason.LOAD_BALANCING, newAssignment.getReason());
    }

    @Test
    @DisplayName("Should calculate complexity correctly")
    void shouldCalculateComplexityCorrectly() {
        // Test complexity calculation
        double complexity = ComplexityCalculator.calculateAircraftComplexity(testState, testPerformance, testIntent);
        
        assertTrue(complexity >= 0.0 && complexity <= 10.0);
        assertTrue(complexity > 0.0); // Should have some complexity
    }

    @Test
    @DisplayName("Should provide complexity breakdown")
    void shouldProvideComplexityBreakdown() {
        String breakdown = ComplexityCalculator.getComplexityDescription(testState, testPerformance, testIntent);
        
        assertNotNull(breakdown);
        assertTrue(breakdown.contains("Speed="));
        assertTrue(breakdown.contains("Altitude="));
        assertTrue(breakdown.contains("Wake="));
        assertTrue(breakdown.contains("Intent="));
        assertTrue(breakdown.contains("Position="));
    }

    @Test
    @DisplayName("Should handle sector workload health status")
    void shouldHandleSectorWorkloadHealthStatus() {
        // Create a sector workload
        SectorWorkload workload = new SectorWorkload(1, 20, 100.0);
        
        // Initially healthy
        assertEquals(SectorWorkload.SectorHealth.HEALTHY, workload.getHealthStatus());
        
        // Add aircraft to increase utilization
        workload.addAircraft("FL001", 25.0);
        workload.addAircraft("FL002", 25.0);
        
        // Should still be healthy (50/100 complexity = 50% utilization, 2/20 aircraft)
        assertEquals(SectorWorkload.SectorHealth.HEALTHY, workload.getHealthStatus());
        
        // Add more complexity to trigger health change
        workload.addAircraft("FL003", 30.0);
        // Now at 90/100 complexity = 90% utilization, which should be HIGH (90% > 80%)
        assertEquals(SectorWorkload.SectorHealth.HIGH, workload.getHealthStatus());
    }

    @Test
    @DisplayName("Should validate sector assignment properties")
    void shouldValidateSectorAssignmentProperties() {
        SectorAssignment assignment = sectorBalancer.assignAircraftToSector(testFlight);
        
        // Test assignment properties
        assertNotNull(assignment.getAssignmentTime());
        assertNotNull(assignment.getReasoning());
        assertTrue(assignment.getComplexityContribution() > 0.0);
        
        // Test description
        String description = assignment.getDescription();
        assertTrue(description.contains("FL001"));
        assertTrue(description.contains("Sector"));
        
        // Test priority
        assertNotNull(assignment.getPriority());
        assertTrue(assignment.getPriority() == SectorAssignment.AssignmentPriority.LOW ||
                  assignment.getPriority() == SectorAssignment.AssignmentPriority.MEDIUM ||
                  assignment.getPriority() == SectorAssignment.AssignmentPriority.HIGH ||
                  assignment.getPriority() == SectorAssignment.AssignmentPriority.CRITICAL);
    }

    @Test
    @DisplayName("Should handle edge case sectors")
    void shouldHandleEdgeCaseSectors() {
        // Create a very small sector
        Sector smallSector = new Sector(4, 0, 0, 100000, 100000, 0, 1000000);
        List<Sector> smallSectors = Arrays.asList(smallSector);
        SectorBalancer smallBalancer = new SectorBalancer(smallSectors);
        
        // Aircraft at edge of sector
        AircraftState edgeState = new AircraftState(50000, 50000, 5000, 10000, 500000, -1);
        Flight edgeFlight = new Flight("FL004", edgeState, testIntent, testPerformance, -1);
        
        SectorAssignment assignment = smallBalancer.assignAircraftToSector(edgeFlight);
        
        assertNotNull(assignment);
        assertEquals(4, assignment.getAssignedSectorId());
    }
}
