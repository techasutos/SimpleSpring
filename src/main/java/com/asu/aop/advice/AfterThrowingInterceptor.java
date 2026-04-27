package com.asu.aop.advice;

import com.asu.aop.MethodInterceptor;
import com.asu.aop.MethodInvocation;

import java.lang.reflect.Method;

public class AfterThrowingInterceptor implements MethodInterceptor {

    private Object aspect;
    private Method adviceMethod;

    public AfterThrowingInterceptor(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        try {
            return invocation.proceed();
        } catch (Throwable ex) {

            if (adviceMethod.getParameterCount() == 1) {
                adviceMethod.invoke(aspect, ex);
            } else {
                adviceMethod.invoke(aspect);
            }

            throw ex;
        }
    }
}