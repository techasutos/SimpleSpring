package com.asu.event;

import com.asu.event.annotation.Async;
import com.asu.event.annotation.Order;

import java.lang.reflect.Method;

public class ApplicationListenerMethodAdapter implements ApplicationListener<ApplicationEvent> {

    private final Object bean;
    private final Method method;
    private final boolean async;
    private final int order;

    public ApplicationListenerMethodAdapter(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.async = method.isAnnotationPresent(Async.class);
        this.order = resolveOrder(method);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        try {
            if (supports(event)) {

                if (method.getParameterCount() == 1) {
                    method.invoke(bean, event);
                } else {
                    method.invoke(bean);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean supports(ApplicationEvent event) {
        return method.getParameterTypes()[0].isAssignableFrom(event.getClass());
    }

    public boolean isAsync() { return async; }
    public int getOrder() { return order; }

    private int resolveOrder(Method method) {
        if (method.isAnnotationPresent(Order.class)) {
            return method.getAnnotation(Order.class).value();
        }
        return 0;
    }
}