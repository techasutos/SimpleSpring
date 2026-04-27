package com.asu.web.exception;

import com.asu.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.*;

public class ExceptionHandlerRegistry {

    private Map<Class<?>, HandlerMethod> handlers = new HashMap<>();

    public void register(Object adviceBean) {

        for (Method method : adviceBean.getClass().getMethods()) {

            if (method.isAnnotationPresent(com.asu.web.annotation.ExceptionHandler.class)) {

                Class<?> exType =
                        method.getAnnotation(com.asu.web.annotation.ExceptionHandler.class).value();

                handlers.put(exType, new HandlerMethod(adviceBean, method));
            }
        }
    }

    public HandlerMethod resolve(Exception e) {

        Class<?> exClass = e.getClass();

        for (Class<?> key : handlers.keySet()) {
            if (key.isAssignableFrom(exClass)) {
                return handlers.get(key);
            }
        }

        return null;
    }
}