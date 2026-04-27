package com.asu.aop.advice;

import com.asu.aop.MethodInterceptor;
import com.asu.aop.MethodInvocation;

import java.lang.reflect.Method;

public class BeforeInterceptor implements MethodInterceptor {

    private Object aspect;
    private Method adviceMethod;

    public BeforeInterceptor(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (adviceMethod.getParameterCount() == 1) {
            adviceMethod.invoke(aspect, invocation);
        } else {
            adviceMethod.invoke(aspect);
        }
        return invocation.proceed();
    }
}