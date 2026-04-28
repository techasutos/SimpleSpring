package com.asu.annotations;

public class ClassMetadata {

    private String className;

    private boolean component;
    private boolean configuration;

    public String getClassName() {
        return className;
    }

    public boolean isComponent() {
        return component;
    }

    public boolean isConfiguration() {
        return configuration;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setComponent(boolean component) {
        this.component = component;
    }

    public void setConfiguration(boolean configuration) {
        this.configuration = configuration;
    }
}