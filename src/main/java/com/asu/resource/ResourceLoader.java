package com.asu.resource;

public class ResourceLoader {

    public Resource getResource(String location) {
        return new ClassPathResource(location);
    }
}
