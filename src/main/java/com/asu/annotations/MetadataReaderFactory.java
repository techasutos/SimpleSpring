package com.asu.annotations;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetadataReaderFactory {

    private Map<String, MetadataReader> cache = new ConcurrentHashMap<>();

    public MetadataReader getMetadataReader(String className) throws Exception {

        if (cache.containsKey(className)) {
            return cache.get(className);
        }

        String path = className.replace(".", "/") + ".class";

        InputStream is = getClass().getClassLoader().getResourceAsStream(path);

        MetadataReader reader = new MetadataReader(is);

        cache.put(className, reader);

        return reader;
    }
}
