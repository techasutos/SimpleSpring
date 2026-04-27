package com.asu.resource;

import java.io.InputStream;

public class ClassPathResource implements Resource {

    private String path;

    public ClassPathResource(String path) {
        this.path = path;
    }

    public InputStream getInputStream() {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    public String getFilename() {
        return path;
    }
}
