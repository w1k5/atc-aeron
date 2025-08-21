package com.w1k5.atc.engine.domain.entities;

public class AircraftState {
    private long positionX; // Position in centimeters (scaled by 100)
    private long positionY; // Position in centimeters (scaled by 100)
    private long velocityX; // Velocity in centimeters per second (scaled by 100)
    private long velocityY; // Velocity in centimeters per second (scaled by 100)
    private long altitude;  // Altitude in centimeters (scaled by 100)
    private int sectorId;

    private static final int SCALE = 100; // Scale factor (100 for centimeters)

    // Constructor
    public AircraftState(long positionX, long positionY, long velocityX, long velocityY, long altitude, int sectorId) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.altitude = altitude;
    }

    // Getters and Setters
    public long getPositionX() {
        return positionX;
    }

    public long getPositionY() {
        return positionY;
    }

    public long getVelocityX() {
        return velocityX;
    }

    public long getVelocityY() {
        return velocityY;
    }

    public long getAltitude() {
        return altitude;
    }

    // Method to convert to real-world value (meters)
    public double getPositionXInMeters() {
        return (double) positionX / SCALE;
    }

    public double getPositionYInMeters() {
        return (double) positionY / SCALE;
    }

    public double getVelocityXInMetersPerSecond() {
        return (double) velocityX / SCALE;
    }

    public double getVelocityYInMetersPerSecond() {
        return (double) velocityY / SCALE;
    }

    public double getAltitudeInMeters() {
        return (double) altitude / SCALE;
    }

    // Add velocity to position (assuming velocity is constant over time)
    public void updatePosition(long deltaTimeInSeconds) {
        positionX += velocityX * deltaTimeInSeconds;
        positionY += velocityY * deltaTimeInSeconds;
    }

    // Method to calculate distance between this aircraft and another (Euclidean distance)
    public long getDistanceToOtherAircraft(AircraftState otherAircraft) {
        long deltaX = this.positionX - otherAircraft.positionX;
        long deltaY = this.positionY - otherAircraft.positionY;
        return (long) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Override
    public String toString() {
        return String.format("AircraftState [positionX=%.2f m, positionY=%.2f m, velocityX=%.2f m/s, velocityY=%.2f m/s, altitude=%.2f m]",
                getPositionXInMeters(), getPositionYInMeters(), getVelocityXInMetersPerSecond(), getVelocityYInMetersPerSecond(), getAltitudeInMeters());
    }

    public int getSectorId() {
        return sectorId;
    }

    public void setSectorId(int alternativeSector) {
        this.sectorId = alternativeSector;
    }
}