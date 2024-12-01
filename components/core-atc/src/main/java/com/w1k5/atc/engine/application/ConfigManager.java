package com.w1k5.atc.engine.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager implements AutoCloseable {
    private Properties config;

    public Properties loadConfig(String configFileName) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IOException("Unable to find " + configFileName);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration", ex);
        }
        return properties;
    }

    public String getProperty(String key) {
        return config.getProperty(key);
    }

    public int getIntProperty(String key) {
        String value = config.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Property " + key + " not found");
        }
        return Integer.parseInt(value);
    }

    @Override
    public void close() {
        // Implement any necessary cleanup (e.g., closing streams) here if needed
    }
}
