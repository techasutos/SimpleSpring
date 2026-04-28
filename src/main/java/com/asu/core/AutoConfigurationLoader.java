/*
package com.asu.core;

import com.asu.annotations.ConditionalOnProperty;
import com.asu.context.ApplicationContext;

import java.util.List;

public class AutoConfigurationLoader {

    private final PropertyResolver propertyResolver;

    public AutoConfigurationLoader(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    public void load(ApplicationContext context) {

        // 🔥 Replace hardcoded list later with file-based loading
        List<Class<?>> configs = List.of(MyAutoConfig.class);

        for (Class<?> config : configs) {

            if (!shouldLoad(config)) {
                continue;
            }

            context.register(config);
        }
    }

    private boolean shouldLoad(Class<?> config) {

        if (!config.isAnnotationPresent(ConditionalOnProperty.class)) {
            return true;
        }

        ConditionalOnProperty cond = config.getAnnotation(ConditionalOnProperty.class);

        String key = buildKey(cond.prefix(), cond.name());

        boolean exists = propertyResolver.containsProperty(key);

        if (!exists) {
            return cond.matchIfMissing();
        }

        String actualValue = propertyResolver.getProperty(key);

        // If no havingValue specified → just presence check
        if (cond.havingValue().isEmpty()) {
            return true;
        }

        return cond.havingValue().equalsIgnoreCase(actualValue);
    }

    private String buildKey(String prefix, String name) {

        if (prefix == null || prefix.isEmpty()) {
            return name;
        }

        return prefix + "." + name;
    }
}*/
