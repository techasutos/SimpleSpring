package com.asu.annotations;

import com.asu.beans.BeanDefinition;
import com.asu.context.ApplicationContext;

import java.lang.reflect.Method;

public class ConfigurationClassParser {

    public void parse(Class<?> configClass, ApplicationContext context) {

        try {
            String configBeanName = configClass.getSimpleName();

            for (Method method : configClass.getDeclaredMethods()) {

                if (method.isAnnotationPresent(Bean.class)) {

                    String beanName = method.getName();

                    BeanDefinition def =
                            new BeanDefinition(method, configBeanName);

                    context.register(beanName, def);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}