package com.asu.boot.autoconfig;

import com.asu.annotations.Bean;
import com.asu.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.List;

public class AutoConfigurationEngine {

    private final ApplicationContext context;

    public AutoConfigurationEngine(ApplicationContext context) {
        this.context = context;
    }

    public void run(List<Class<?>> configClasses) {

        for (Class<?> config : configClasses) {

            if (!isAllowed(config)) continue;

            Object configInstance = instantiate(config);

            registerBeans(config, configInstance);
        }
    }

    // ---------------------------
    // condition evaluation
    // ---------------------------
    private boolean isAllowed(Class<?> config) {

        if (config.isAnnotationPresent(ConditionalOnClass.class)) {

            Class<?> required = config.getAnnotation(ConditionalOnClass.class).value();

            try {
                Class.forName(required.getName());
            } catch (Exception e) {
                return false;
            }
        }

        if (config.isAnnotationPresent(ConditionalOnProperty.class)) {

            ConditionalOnProperty p = config.getAnnotation(ConditionalOnProperty.class);

            String value = System.getProperty(p.name());

            if (!p.havingValue().equals(value)) {
                return false;
            }
        }

        return true;
    }

    // ---------------------------
    // instantiate config class
    // ---------------------------
    private Object instantiate(Class<?> config) {
        try {
            return config.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------
    // process @Bean methods
    // ---------------------------
    private void registerBeans(Class<?> config, Object instance) {

        for (Method method : config.getDeclaredMethods()) {

            if (!method.isAnnotationPresent(Bean.class)) continue;

            try {

                Object bean = method.invoke(instance, resolveParams(method));

                String beanName = method.getName();

                context.registerSingleton(beanName, bean);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // naive DI for @Bean methods
    private Object[] resolveParams(Method method) {

        Class<?>[] types = method.getParameterTypes();
        Object[] args = new Object[types.length];

        for (int i = 0; i < types.length; i++) {
            args[i] = context.getBean(types[i]);
        }

        return args;
    }
}