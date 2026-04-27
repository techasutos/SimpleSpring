package com.asu.aop.pointcut;

import java.lang.reflect.Method;

public interface Pointcut {
    boolean matches(Method method, Class<?> targetClass);
}
