package com.w1k5.atc.engine.application;

public enum AdapterType {
    WEATHER("weather"),
    RADAR("radar"),
    ADSB("adsb");

    private final String configPrefix;

    AdapterType(String configPrefix) {
        this.configPrefix = configPrefix;
    }

    public String getConfigPrefix() {
        return configPrefix;
    }
}