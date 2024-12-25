package com.w1k5.atc.engine.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private final Properties properties;

    public PropertiesLoader(String filePath) {
        this.properties = loadProperties(filePath);
    }

    public Properties loadProperties(String configFileName) {
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
        return properties.getProperty(key);
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}
