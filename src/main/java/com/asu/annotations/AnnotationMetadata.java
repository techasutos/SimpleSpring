package com.asu.annotations;

import java.util.HashSet;
import java.util.Set;

public class AnnotationMetadata {

    private String className;
    private Set<String> annotations = new HashSet<>();

    public AnnotationMetadata(String className) {
        this.className = className;
    }

    public void addAnnotation(String desc) {
        annotations.add(desc);
    }

    public boolean hasAnnotation(String desc) {
        return annotations.contains(desc);
    }

    public String getClassName() {
        return className;
    }
}
