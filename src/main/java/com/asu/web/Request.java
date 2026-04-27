package com.asu.web;

import java.util.Map;
import java.util.HashMap;

public class Request {

    private String path;
    private Map<String, String> params;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> pathVariables = new HashMap<>();
    private String body;

    public Request(String path, Map<String, String> params) {
        this.path = path;
        this.params = params;
    }

    public String getPath() { return path; }

    public String getParam(String name) {
        return params.get(name);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void setPathVariables(Map<String, String> vars) {
        this.pathVariables = vars;
    }

    public String getPathVariable(String name) {
        return pathVariables.get(name);
    }


    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
