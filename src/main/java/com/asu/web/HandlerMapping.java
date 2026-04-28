package com.asu.web;

import com.asu.annotations.Controller;
import com.asu.web.annotations.RequestMapping;
import com.asu.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HandlerMapping {

    private Map<String, HandlerMethod> mapping = new HashMap<>();

    public void register(Object bean) {

        Class<?> clazz = bean.getClass();

        if (clazz.isAnnotationPresent(Controller.class)) {

            for (Method method : clazz.getDeclaredMethods()) {

                if (method.isAnnotationPresent(RequestMapping.class)) {

                    String path = method.getAnnotation(RequestMapping.class).value();

                    mapping.put(path, new HandlerMethod(bean, method));
                }
            }
        }
    }

    public HandlerMethod getHandler(Request request) {

        for (String pattern : mapping.keySet()) {

            if (match(pattern, request.getPath())) {
                request.setPathVariables(extract(pattern, request.getPath()));
                return mapping.get(pattern);
            }
        }

        return null;
    }

    // 🔥 simple pattern match (/user/{id})
    private boolean match(String pattern, String path) {

        String[] p1 = pattern.split("/");
        String[] p2 = path.split("/");

        if (p1.length != p2.length) return false;

        for (int i = 0; i < p1.length; i++) {

            if (p1[i].startsWith("{") && p1[i].endsWith("}")) {
                continue;
            }

            if (!p1[i].equals(p2[i])) {
                return false;
            }
        }

        return true;
    }

    private Map<String, String> extract(String pattern, String path) {

        Map<String, String> vars = new HashMap<>();

        String[] p1 = pattern.split("/");
        String[] p2 = path.split("/");

        for (int i = 0; i < p1.length; i++) {

            if (p1[i].startsWith("{")) {

                String key = p1[i].substring(1, p1[i].length() - 1);
                vars.put(key, p2[i]);
            }
        }

        return vars;
    }
}