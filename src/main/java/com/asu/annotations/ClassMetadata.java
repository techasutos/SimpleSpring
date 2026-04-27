package com.asu.annotations;

public class ClassMetadata {
    private String className;
    private boolean isComponent;

    public ClassMetadata(String className, boolean isComponent) {
        this.className = className;
        this.isComponent = isComponent;
    }

    public String getClassName() { return className; }
    public boolean isComponent() { return isComponent; }
}
