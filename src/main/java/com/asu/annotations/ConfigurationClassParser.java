package com.asu.annotations;

import com.asu.context.ApplicationContext;

import java.lang.reflect.Method;

public class ConfigurationClassParser {

    public void parse(Class<?> configClass, ApplicationContext context) {

        for (Method method : configClass.getDeclaredMethods()) {

            if (method.isAnnotationPresent(Bean.class)) {

                try {
                    Object configInstance = configClass.getDeclaredConstructor().newInstance();

                    Object bean = method.invoke(configInstance);

                    context.registerBean(method.getName(), bean);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
