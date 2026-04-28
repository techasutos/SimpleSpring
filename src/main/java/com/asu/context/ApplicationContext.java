package com.asu.context;

import com.asu.annotations.ClassMetadata;
import com.asu.annotations.Configuration;
import com.asu.annotations.ConfigurationClassParser;
import com.asu.annotations.Repository;
import com.asu.beans.BeanDefinition;
import com.asu.beans.DefaultBeanFactory;
import com.asu.data.jdbc.EntityExecutor;
import com.asu.data.repository.RepositoryProxyFactory;
import com.asu.lifecycle.BeanPostProcessor;
import com.asu.scanning.ASMScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ApplicationContext {

    private final DefaultBeanFactory beanFactory = new DefaultBeanFactory();
    private final String basePackage;

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;

        scan(basePackage);
        refresh();
    }

    // =====================================================
    // REFRESH (Spring lifecycle boot)
    // =====================================================
    public void refresh() {

        // 1️⃣ Ensure core infrastructure exists first
        ensureCoreBeans();

        // 2️⃣ Register BeanPostProcessors first
        for (String name : beanFactory.beanDefinitions.keySet()) {

            Class<?> clazz = beanFactory.beanDefinitions.get(name).getBeanClass();

            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                Object bean = beanFactory.getBean(name);
                beanFactory.addPostProcessor((BeanPostProcessor) bean);
            }
        }

        // 3️⃣ Initialize all beans
        for (String name : beanFactory.beanDefinitions.keySet()) {
            beanFactory.getBean(name);
        }
    }

    // =====================================================
    // CORE BEANS (IMPORTANT FIX)
    // =====================================================
    private void ensureCoreBeans() {

        // EntityExecutor MUST exist before repositories
        try {
            beanFactory.getBean(EntityExecutor.class);
        } catch (Exception e) {
            register(EntityExecutor.class);
        }
    }

    // =====================================================
    // SCANNER (CORE FIXED VERSION)
    // =====================================================
    private void scan(String basePackage) {

        ASMScanner scanner = new ASMScanner();
        List<ClassMetadata> classes = scanner.scan(basePackage);

        for (ClassMetadata meta : classes) {

            try {
                Class<?> clazz = Class.forName(meta.getClassName());

                // ---------------------------------------
                // 1️⃣ CONFIGURATION CLASS
                // ---------------------------------------
                if (clazz.isAnnotationPresent(Configuration.class)) {

                    register(clazz);

                    ConfigurationClassParser parser =
                            new ConfigurationClassParser();

                    parser.parse(clazz, this);

                    continue;
                }

                // ---------------------------------------
                // 2️⃣ REPOSITORY (INTERFACE → PROXY)
                // ---------------------------------------
                if (clazz.isInterface() &&
                        clazz.isAnnotationPresent(Repository.class)) {

                    EntityExecutor executor = getBean(EntityExecutor.class);

                    Object proxy = RepositoryProxyFactory.create(clazz, executor);

                    registerSingleton(getBeanName(clazz), proxy);

                    continue;
                }

                // ---------------------------------------
                // 3️⃣ NORMAL COMPONENT
                // ---------------------------------------
                if (meta.isComponent()) {
                    register(clazz);
                }

            } catch (Exception e) {
                throw new RuntimeException("Scan failed: " + meta.getClassName(), e);
            }
        }
    }

    // =====================================================
    // BEAN NAME STRATEGY (IMPORTANT FIX)
    // =====================================================
    private String getBeanName(Class<?> clazz) {

        String simple = clazz.getSimpleName();

        return Character.toLowerCase(simple.charAt(0)) +
                simple.substring(1);
    }

    // =====================================================
    // REGISTRATION METHODS
    // =====================================================
    public void register(Class<?> clazz) {

        String name = getBeanName(clazz);

        if (beanFactory.beanDefinitions.containsKey(name)) return;

        beanFactory.registerBeanDefinition(
                name,
                new BeanDefinition(clazz)
        );
    }

    public void register(String name, Class<?> clazz) {

        if (beanFactory.beanDefinitions.containsKey(name)) return;

        beanFactory.registerBeanDefinition(
                name,
                new BeanDefinition(clazz)
        );
    }

    public void register(String name, BeanDefinition def) {

        if (beanFactory.beanDefinitions.containsKey(name)) return;

        beanFactory.registerBeanDefinition(name, def);
    }

    public void registerSingleton(String name, Object instance) {

        if (beanFactory.singletonObjects.containsKey(name)) return;

        beanFactory.singletonObjects.put(name, instance);

        beanFactory.registerBeanDefinition(
                name,
                new BeanDefinition(instance.getClass())
        );
    }

    // =====================================================
    // GETTERS
    // =====================================================
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

    public String getBasePackage() {
        return basePackage;
    }
}