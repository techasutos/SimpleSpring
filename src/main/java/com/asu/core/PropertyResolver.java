package com.asu.core;

import java.util.Properties;

public class PropertyResolver {

    private final Properties properties;

    public PropertyResolver(Properties properties) {
        this.properties = properties;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }
}