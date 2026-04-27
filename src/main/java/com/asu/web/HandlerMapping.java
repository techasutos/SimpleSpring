package com.asu.web;

import com.asu.annotations.Controller;
import com.asu.annotations.RequestMapping;
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
}