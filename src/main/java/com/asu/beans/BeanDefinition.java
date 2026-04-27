package com.asu.beans;

import java.lang.reflect.Method;

public class BeanDefinition {

    private Class<?> beanClass;

    private Method factoryMethod;
    private String factoryBeanName;

    private boolean primary;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.primary = beanClass.isAnnotationPresent(
                com.asu.annotations.Primary.class
        );
    }

    public BeanDefinition(Method method, String factoryBeanName) {
        this.factoryMethod = method;
        this.factoryBeanName = factoryBeanName;

        this.primary = method.isAnnotationPresent(
                com.asu.annotations.Primary.class
        );
    }

    public boolean isFactoryBean() {
        return factoryMethod != null;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Method getFactoryMethod() {
        return factoryMethod;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public boolean isPrimary() {
        return primary;
    }
}