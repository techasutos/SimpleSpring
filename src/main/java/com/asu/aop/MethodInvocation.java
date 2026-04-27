package com.asu.aop;

import java.lang.reflect.Method;
import java.util.List;

public class MethodInvocation {

    private Object target;
    private Method method;
    private Object[] args;
    private List<MethodInterceptor> interceptors;

    private int currentIndex = -1;

    public MethodInvocation(Object target, Method method,
                            Object[] args,
                            List<MethodInterceptor> interceptors) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.interceptors = interceptors;
    }

    public Object proceed() throws Throwable {

        if (currentIndex == interceptors.size() - 1) {
            return method.invoke(target, args);
        }

        currentIndex++;
        return interceptors.get(currentIndex).invoke(this);
    }

    // 🔥 NEW
    public Object[] getArgs() { return args; }
    public Method getMethod() { return method; }
    public Object getTarget() { return target; }
}