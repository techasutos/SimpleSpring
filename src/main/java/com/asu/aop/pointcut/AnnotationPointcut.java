package com.asu.aop.pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationPointcut implements Pointcut {

    private Class<? extends Annotation> annotationType;

    public AnnotationPointcut(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return method.isAnnotationPresent(annotationType);
    }
}