package com.asu.processors;

import com.asu.lifecycle.BeanPostProcessor;

public class AutowiredProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        return bean;
    }
}
