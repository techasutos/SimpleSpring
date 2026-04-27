package com.asu.aop.advice;

import com.asu.aop.MethodInterceptor;
import com.asu.aop.MethodInvocation;

import java.lang.reflect.Method;

public class AroundInterceptor implements MethodInterceptor {

    private Object aspect;
    private Method adviceMethod;

    public AroundInterceptor(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        // pass invocation to aspect method
        return adviceMethod.invoke(aspect, invocation);
    }
}