package com.asu.beans;

import com.asu.annotations.Autowired;
import com.asu.lifecycle.BeanPostProcessor;
import com.asu.lifecycle.InitializingBean;
import com.asu.lifecycle.SmartInstantiationAwareBeanPostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory implements BeanFactory {

    public Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    // 🔥 3-Level Cache
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    private Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();

    private Set<String> beansCurrentlyInCreation = new HashSet<>();

    private List<BeanPostProcessor> postProcessors = new ArrayList<>();

    // 🔥 Dependency graph
    private Map<String, Set<String>> dependencyGraph = new ConcurrentHashMap<>();

    // -------------------------------
    // Register definition
    // -------------------------------
    public void registerBeanDefinition(String name, BeanDefinition def) {
        beanDefinitions.put(name, def);
    }

    // Register post processor
    public void addPostProcessor(BeanPostProcessor processor) {
        postProcessors.add(processor);
    }

    // -------------------------------
    // GET BEAN
    // -------------------------------
    @Override
    public Object getBean(String name) {

        // 🔥 0. Fast path: fully initialized singleton
        Object bean = singletonObjects.get(name);
        if (bean != null) return bean;

        // 🔥 1. Early reference (AOP-safe)
        bean = earlySingletonObjects.get(name);
        if (bean != null) return bean;

        // 🔥 2. Level 3 factory (CRITICAL FIX)
        ObjectFactory<?> factory = singletonFactories.get(name);
        if (factory != null) {

            bean = factory.getObject();

            // ⚠️ IMPORTANT FIX:
            // Do NOT blindly store raw bean here.
            // It may already be proxy-enhanced OR need post-processing.

            earlySingletonObjects.put(name, bean);
            singletonFactories.remove(name);

            return bean;
        }

        // 🔥 3. Prevent recursive creation loops
        if (beansCurrentlyInCreation.contains(name)) {
            throw new RuntimeException("Circular dependency detected while creating: " + name);
        }

        // 🔥 4. Create bean
        return createBean(name, beanDefinitions.get(name));
    }

    @Override
    public <T> T getBean(Class<T> type) {

        for (String name : beanDefinitions.keySet()) {

            Object bean = getBean(name);

            if (type.isAssignableFrom(bean.getClass())) {
                return (T) bean;
            }
        }

        throw new RuntimeException("Bean not found: " + type);
    }

    // -------------------------------
    // CREATE BEAN
    // -------------------------------
    private Object createBean(String name, BeanDefinition def) {

        if (beansCurrentlyInCreation.contains(name)) {
            throw new RuntimeException("Circular dependency detected for: " + name);
        }
        try {
            beansCurrentlyInCreation.add(name);

            Object bean;

            // 🔥 FACTORY METHOD SUPPORT
            if (def.isFactoryBean()) {

                Object factory = getBean(def.getFactoryBeanName());

                Method method = def.getFactoryMethod();

                // 🔥 Resolve parameters WITH graph tracking
                Object[] args = resolveMethodArguments(method, name);

                // 🔥 Detect circular dependency BEFORE invocation
                detectCircularDependency(name);

                bean = method.invoke(factory, args);

            } else {
                bean = createInstance(def);
            }

            // 🔥 EARLY EXPOSURE (for circular deps)
            Object finalBean = bean;
            singletonFactories.put(name, () -> getEarlyBeanReference(name, finalBean));

            // 3️⃣ Dependency Injection
            populateBean(bean);

            // 4️⃣ Before Init
            for (BeanPostProcessor p : postProcessors) {
                bean = p.postProcessBeforeInitialization(bean, name);
            }

            // 5️⃣ Init callback
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }

            // 6️⃣ After Init (AOP / Proxying happens here)
            for (BeanPostProcessor p : postProcessors) {
                bean = p.postProcessAfterInitialization(bean, name);
            }

            // 7️⃣ Move to Level 1
            singletonObjects.put(name, bean);

            // Cleanup
            earlySingletonObjects.remove(name);
            singletonFactories.remove(name);
            beansCurrentlyInCreation.remove(name);

            return bean;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------
    // CONSTRUCTOR INJECTION
    // -------------------------------
    private Object createInstance(BeanDefinition def) throws Exception {

        Class<?> clazz = def.getBeanClass();

        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {

            if (ctor.isAnnotationPresent(Autowired.class)) {

                Class<?>[] paramTypes = ctor.getParameterTypes();
                Object[] args = new Object[paramTypes.length];

                Annotation[][] paramAnnotations = ctor.getParameterAnnotations();

                for (int i = 0; i < paramTypes.length; i++) {

                    String qualifier = null;

                    for (Annotation ann : paramAnnotations[i]) {
                        if (ann instanceof com.asu.annotations.Qualifier) {
                            qualifier = ((com.asu.annotations.Qualifier) ann).value();
                        }
                    }

                    args[i] = resolveDependency(paramTypes[i], qualifier);
                }

                ctor.setAccessible(true);
                return ctor.newInstance(args);
            }
        }

        return clazz.getDeclaredConstructor().newInstance();
    }

    // -------------------------------
    // FIELD INJECTION
    // -------------------------------
    private void populateBean(Object bean) throws Exception {

        for (Field field : bean.getClass().getDeclaredFields()) {

            if (field.isAnnotationPresent(Autowired.class)) {

                field.setAccessible(true);

                String qualifier = null;

                if (field.isAnnotationPresent(com.asu.annotations.Qualifier.class)) {
                    qualifier = field.getAnnotation(
                            com.asu.annotations.Qualifier.class
                    ).value();
                }

                Object dependency = resolveDependency(
                        field.getType(),
                        qualifier
                );

                field.set(bean, dependency);
            }
        }
    }

    private Object getEarlyBeanReference(String name, Object bean) {

        Object exposed = bean;

        for (BeanPostProcessor p : postProcessors) {
            if (p instanceof SmartInstantiationAwareBeanPostProcessor) {
                exposed = ((SmartInstantiationAwareBeanPostProcessor) p)
                        .getEarlyBeanReference(exposed, name);
            }
        }

        return exposed;
    }

    private Object[] resolveMethodArguments(Method method, String beanName) {

        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];

        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < paramTypes.length; i++) {

            String qualifier = null;

            for (Annotation ann : paramAnnotations[i]) {
                if (ann instanceof com.asu.annotations.Qualifier) {
                    qualifier = ((com.asu.annotations.Qualifier) ann).value();
                }
            }

            // 🔥 Track dependency graph
            String depName = findBeanNameByType(paramTypes[i]);

            dependencyGraph
                    .computeIfAbsent(beanName, k -> new HashSet<>())
                    .add(depName);

            args[i] = resolveDependency(paramTypes[i], qualifier);
        }

        return args;
    }

    private String findBeanNameByType(Class<?> type) {

        for (String name : beanDefinitions.keySet()) {

            BeanDefinition def = beanDefinitions.get(name);

            Class<?> clazz = def.isFactoryBean()
                    ? def.getFactoryMethod().getReturnType()
                    : def.getBeanClass();

            if (type.isAssignableFrom(clazz)) {
                return name;
            }
        }

        throw new RuntimeException("No bean found for type: " + type);
    }

    private void detectCircularDependency(String beanName) {

        Set<String> visited = new HashSet<>();
        Set<String> stack = new HashSet<>();

        if (hasCycle(beanName, visited, stack)) {
            throw new RuntimeException("Circular dependency detected: " + beanName);
        }
    }

    private boolean hasCycle(String node,
                             Set<String> visited,
                             Set<String> stack) {

        if (stack.contains(node)) return true;
        if (visited.contains(node)) return false;

        visited.add(node);
        stack.add(node);

        for (String dep : dependencyGraph.getOrDefault(node, Collections.emptySet())) {

            if (hasCycle(dep, visited, stack)) {
                return true;
            }
        }

        stack.remove(node);
        return false;
    }

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

        // Single bean
        if (candidates.size() == 1) {
            return getBean(candidates.get(0));
        }

        return resolveMultipleBeans(type, candidates, qualifier);
    }

    private Object resolveMultipleBeans(Class<?> type,
                                        List<String> candidates,
                                        String qualifier) {

        // 1️⃣ QUALIFIER wins
        if (qualifier != null) {

            for (String name : candidates) {
                if (name.equals(qualifier)) {
                    return getBean(name);
                }
            }

            throw new RuntimeException("No bean found with qualifier: " + qualifier);
        }

        // 2️⃣ PRIMARY wins
        List<String> primaryBeans = new ArrayList<>();

        for (String name : candidates) {
            if (beanDefinitions.get(name).isPrimary()) {
                primaryBeans.add(name);
            }
        }

        if (primaryBeans.size() == 1) {
            return getBean(primaryBeans.get(0));
        }

        if (primaryBeans.size() > 1) {
            throw new RuntimeException("Multiple @Primary beans found: " + primaryBeans);
        }

        // 3️⃣ FAIL
        throw new RuntimeException(
                "Multiple beans found for type " + type + ": " + candidates
        );
    }
}