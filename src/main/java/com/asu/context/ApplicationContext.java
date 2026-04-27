package com.asu.context;

import com.asu.annotations.ClassMetadata;
import com.asu.beans.BeanDefinition;
import com.asu.beans.DefaultBeanFactory;
import com.asu.processors.TransactionPostProcessor;
import com.asu.scanning.ASMScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ApplicationContext {

    private DefaultBeanFactory beanFactory = new DefaultBeanFactory();

    public ApplicationContext(String basePackage) {

        // 1. Scan
        scan(basePackage);

        // 2. Register processors
        beanFactory.addPostProcessor(new TransactionPostProcessor());

        // 3. Pre-instantiate singletons
        refresh();
    }

    // 🔥 NEW: refresh lifecycle (important for future features)
    public void refresh() {
        for (String name : beanFactory.beanDefinitions.keySet()) {
            beanFactory.getBean(name);
        }
    }

    private void scan(String basePackage) {

        ASMScanner scanner = new ASMScanner();
        List<ClassMetadata> classes = scanner.scan(basePackage);

        for (ClassMetadata meta : classes) {

            if (meta.isComponent()) {

                try {
                    Class<?> clazz = Class.forName(meta.getClassName());

                    register(clazz); // 🔥 USE NEW METHOD

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 🚀🔥 THIS IS WHAT YOU WERE MISSING
    public void register(Class<?> clazz) {

        String beanName = clazz.getSimpleName();

        if (beanFactory.beanDefinitions.containsKey(beanName)) {
            return; // avoid duplicate
        }

        beanFactory.registerBeanDefinition(
                beanName,
                new BeanDefinition(clazz)
        );
    }

    // Optional: register with custom name
    public void register(String name, Class<?> clazz) {

        if (beanFactory.beanDefinitions.containsKey(name)) {
            return;
        }

        beanFactory.registerBeanDefinition(
                name,
                new BeanDefinition(clazz)
        );
    }

    public Object getBean(String name) {
        return beanFactory.getBean(name);
    }

    public <T> T getBean(Class<T> type) {
        return beanFactory.getBean(type);
    }

    public <T> List<T> getBeansWithAnnotation(Class<? extends Annotation> annotation) {

        List<T> result = new ArrayList<>();

        for (String name : beanFactory.beanDefinitions.keySet()) {

            Object bean = beanFactory.getBean(name);

            if (bean.getClass().isAnnotationPresent(annotation)) {
                result.add((T) bean);
            }
        }

        return result;
    }
}