package com.asu.aop.advice;

import com.asu.aop.MethodInterceptor;
import com.asu.aop.MethodInvocation;

import java.lang.reflect.Method;

public class AfterInterceptor implements MethodInterceptor {

    private Object aspect;
    private Method adviceMethod;

    public AfterInterceptor(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        try {
            return invocation.proceed();
        } finally {
            adviceMethod.invoke(aspect);
        }
    }
}