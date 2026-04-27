package com.asu.web.method;

import java.lang.reflect.Method;

public class HandlerMethod {

    private Object bean;
    private Method method;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }
}
