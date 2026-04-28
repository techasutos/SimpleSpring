package com.asu.processors;

import com.asu.aop.AspectProcessor;
import com.asu.aop.ProxyFactory;
import com.asu.aop.advisor.Advisor;
import com.asu.lifecycle.SmartInstantiationAwareBeanPostProcessor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AspectPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    private final List<Advisor> advisors;

    // 🔥 cache: beanClass → shouldProxy
    private final Map<Class<?>, Boolean> proxyCache = new ConcurrentHashMap<>();

    // 🔥 track already proxied beans (avoid double proxy)
    private final Set<String> earlyProxyReferences = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public AspectPostProcessor(List<Object> beans) {
        this.advisors = AspectProcessor.buildAdvisors(beans);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        return bean;
    }

    // 🔥 EARLY EXPOSURE (CRITICAL)
    @Override
    public Object getEarlyBeanReference(Object bean, String name) {

        if (!shouldProxy(bean.getClass())) {
            return bean;
        }

        earlyProxyReferences.add(name);

        return createProxy(bean);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {

        // If already proxied early → return as-is
        if (earlyProxyReferences.contains(name)) {
            return bean;
        }

        if (!shouldProxy(bean.getClass())) {
            return bean;
        }

        return createProxy(bean);
    }

    // ----------------------------------
    // 🔥 CORE: proxy decision (cached)
    // ----------------------------------
    private boolean shouldProxy(Class<?> clazz) {

        return proxyCache.computeIfAbsent(clazz, c -> {

            if (advisors == null || advisors.isEmpty()) return false;

            for (Method method : c.getMethods()) {
                for (Advisor advisor : advisors) {
                    if (advisor.matches(method, c)) {
                        return true;
                    }
                }
            }

            return false;
        });
    }

    // ----------------------------------
    // 🔥 PROXY CREATION
    // ----------------------------------
    private Object createProxy(Object bean) {
        return ProxyFactory.createProxy(bean, advisors);
    }
}