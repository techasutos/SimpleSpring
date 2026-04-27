package com.asu.aop.advisor;

import com.asu.aop.MethodInterceptor;
import com.asu.aop.pointcut.Pointcut;

public class Advisor {

    private Pointcut pointcut;
    private MethodInterceptor interceptor;

    public Advisor(Pointcut pointcut, MethodInterceptor interceptor) {
        this.pointcut = pointcut;
        this.interceptor = interceptor;
    }

    public boolean matches(java.lang.reflect.Method method, Class<?> targetClass) {
        return pointcut.matches(method, targetClass);
    }

    public MethodInterceptor getInterceptor() {
        return interceptor;
    }
}