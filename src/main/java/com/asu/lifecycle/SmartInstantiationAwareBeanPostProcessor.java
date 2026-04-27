package com.asu.lifecycle;

public interface SmartInstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    //  called during early exposure (level 3 cache)
    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}