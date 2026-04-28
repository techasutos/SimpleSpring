package com.asu.beans;

import com.asu.annotations.Autowired;
import com.asu.lifecycle.BeanPostProcessor;
import com.asu.lifecycle.InitializingBean;
import com.asu.lifecycle.SmartInstantiationAwareBeanPostProcessor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory implements BeanFactory {

    public Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    // =========================
    // 3-Level Cache (SPRING STYLE)
    // =========================
    public final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();

    private final Set<String> beansCurrentlyInCreation = new HashSet<>();
    private final List<BeanPostProcessor> postProcessors = new ArrayList<>();

    private final Map<String, Set<String>> dependencyGraph = new ConcurrentHashMap<>();

    // =========================================================
    // REGISTRATION
    // =========================================================
    public void registerBeanDefinition(String name, BeanDefinition def) {
        beanDefinitions.put(name, def);
    }

    public void addPostProcessor(BeanPostProcessor processor) {
        postProcessors.add(processor);
    }

    public void registerSingleton(String name, Object bean) {
        singletonObjects.put(name, bean);
        beanDefinitions.put(name, new BeanDefinition(bean.getClass()));
    }

    // =========================================================
    // GET BEAN (String)
    // =========================================================
    @Override
    public Object getBean(String name) {

        // 1. Fully initialized
        Object bean = singletonObjects.get(name);
        if (bean != null) return bean;

        // 2. Early reference
        bean = earlySingletonObjects.get(name);
        if (bean != null) return bean;

        // 3. Factory reference (CRITICAL FIX)
        ObjectFactory<?> factory = singletonFactories.get(name);
        if (factory != null) {
            Object early = factory.getObject();
            earlySingletonObjects.put(name, early);
            singletonFactories.remove(name);
            return early;
        }

        // 4. Circular protection
        if (beansCurrentlyInCreation.contains(name)) {
            throw new RuntimeException("Circular dependency detected: " + name);
        }

        return createBean(name, beanDefinitions.get(name));
    }

    // =========================================================
    // GET BEAN (TYPE)
    // =========================================================
    @Override
    public <T> T getBean(Class<T> type) {

        for (String name : beanDefinitions.keySet()) {
            Object bean = getBean(name);
            if (bean != null && type.isAssignableFrom(bean.getClass())) {
                return (T) bean;
            }
        }

        throw new RuntimeException("Bean not found: " + type);
    }

    // =========================================================
    // CREATE BEAN (SPRING ORDER FIXED)
    // =========================================================
    private Object createBean(String name, BeanDefinition def) {

        System.out.println("Creating bean: " + name);

        try {
            beansCurrentlyInCreation.add(name);

            Object bean;

            // 1. Instantiate
            if (def.isFactoryBean()) {
                Object factory = getBean(def.getFactoryBeanName());
                Method method = def.getFactoryMethod();
                bean = method.invoke(factory);
            } else {
                bean = createInstance(def);
            }

            // 🔥 CRITICAL FIX: expose EARLY BEFORE injection
            Object finalBean = bean;
            singletonFactories.put(name, () -> getEarlyReference(name, finalBean));

            // 2. Dependency injection (NOW SAFE)
            populateBean(bean);

            // 3. Before init
            for (BeanPostProcessor p : postProcessors) {
                bean = p.postProcessBeforeInitialization(bean, name);
            }

            // 4. init callback
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }

            // 5. After init
            for (BeanPostProcessor p : postProcessors) {
                bean = p.postProcessAfterInitialization(bean, name);
            }

            // 6. move to singleton cache
            singletonObjects.put(name, bean);

            earlySingletonObjects.remove(name);
            singletonFactories.remove(name);
            beansCurrentlyInCreation.remove(name);

            return bean;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // =========================================================
    // INSTANTIATION
    // =========================================================
    private Object createInstance(BeanDefinition def) throws Exception {

        Class<?> clazz = def.getBeanClass();

        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            if (ctor.isAnnotationPresent(Autowired.class)) {
                ctor.setAccessible(true);
                return ctor.newInstance();
            }
        }

        return clazz.getDeclaredConstructor().newInstance();
    }

    // =========================================================
    // FIELD INJECTION (FIXED ORDER SAFETY)
    // =========================================================
    private void populateBean(Object bean) throws Exception {

        for (Field field : bean.getClass().getDeclaredFields()) {

            if (!field.isAnnotationPresent(Autowired.class)) continue;

            field.setAccessible(true);

            Object dependency = resolveDependency(field.getType(), null);

            field.set(bean, dependency);
        }
    }

    // =========================================================
    // EARLY REFERENCE (AOP SAFE)
    // =========================================================
    private Object getEarlyReference(String name, Object bean) {

        Object exposed = bean;

        for (BeanPostProcessor p : postProcessors) {
            if (p instanceof SmartInstantiationAwareBeanPostProcessor) {
                exposed = ((SmartInstantiationAwareBeanPostProcessor) p)
                        .getEarlyBeanReference(exposed, name);
            }
        }

        return exposed;
    }

    // =========================================================
    // DEPENDENCY RESOLUTION (FIXED)
    // =========================================================
    private Object resolveDependency(Class<?> type, String qualifier) {

        List<String> candidates = new ArrayList<>();

        for (String name : beanDefinitions.keySet()) {

            BeanDefinition def = beanDefinitions.get(name);

            Class<?> clazz = def.isFactoryBean()
                    ? def.getFactoryMethod().getReturnType()
                    : def.getBeanClass();

            if (type.isAssignableFrom(clazz)) {
                candidates.add(name);
            }
        }

        if (candidates.isEmpty()) {
            throw new RuntimeException("No bean found for type: " + type);
        }

        return getBean(candidates.get(0));
    }
}