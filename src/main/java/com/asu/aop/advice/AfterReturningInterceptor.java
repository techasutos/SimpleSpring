package com.asu.aop.advice;

import com.asu.aop.MethodInterceptor;
import com.asu.aop.MethodInvocation;

import java.lang.reflect.Method;

public class AfterReturningInterceptor implements MethodInterceptor {

    private Object aspect;
    private Method adviceMethod;

    public AfterReturningInterceptor(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object result = invocation.proceed();

        // 🔥 pass return value if method expects it
        if (adviceMethod.getParameterCount() == 1) {
            adviceMethod.invoke(aspect, result);
        } else {
            adviceMethod.invoke(aspect);
        }

        return result;
    }
}