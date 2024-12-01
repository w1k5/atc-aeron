package com.w1k5.atc.engine.domain;

import java.util.List;

public class SectorBalancer {

    // Define sectors as boundaries with (minX, minY, maxX, maxY, minAltitude, maxAltitude)
    private List<Sector> sectors;

    // Constructor
    public SectorBalancer(List<Sector> sectors) {
        this.sectors = sectors;
    }

    // Main balancing method
    public void assignSector(AircraftState aircraftState) {
        // Check the aircraft's position and altitude to assign a sector
        for (Sector sector : sectors) {
            if (sector.contains(aircraftState)) {
                // Assign the sector ID to the aircraft
                aircraftState.setSectorId(sector.getId());
                System.out.println("Assigned Aircraft to Sector " + sector.getId());
                return;
            }
        }

        // If no sector is found, handle this case (e.g., notify or log)
        aircraftState.setSectorId(-1);
        System.out.println("No sector found for aircraft at position (" + aircraftState.getPositionX() + ", " + aircraftState.getPositionY() + ")");
    }
}