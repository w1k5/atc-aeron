package com.w1k5.atc.engine.domain;

public class Sector {
    private int id;
    private long minX, minY, maxX, maxY, minAltitude, maxAltitude;

    // Constructor
    public Sector(int id, long minX, long minY, long maxX, long maxY, long minAltitude, long maxAltitude) {
        this.id = id;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
    }

    // Check if the aircraft is within this sector's boundaries
    public boolean contains(AircraftState aircraftState) {
        long posX = aircraftState.getPositionX();
        long posY = aircraftState.getPositionY();
        long altitude = aircraftState.getAltitude();

        return posX >= minX && posX <= maxX &&
                posY >= minY && posY <= maxY &&
                altitude >= minAltitude && altitude <= maxAltitude;
    }

    // Getters
    public int getId() {
        return id;
    }
}
