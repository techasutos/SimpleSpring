package com.asu.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class SimpleApplicationEventMulticaster implements ApplicationEventMulticaster {

    private final List<ApplicationListener<?>> listeners = new CopyOnWriteArrayList<>();

    private Executor executor; // async support

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        listeners.add(listener);
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {

        for (ApplicationListener listener : listeners) {

            if (supportsEvent(listener, event)) {

                if (executor != null) {
                    executor.execute(() -> invokeListener(listener, event));
                } else {
                    invokeListener(listener, event);
                }
            }
        }
    }

    private void invokeListener(ApplicationListener listener, ApplicationEvent event) {
        try {
            listener.onApplicationEvent(event);
        } catch (Exception e) {
            e.printStackTrace(); // plug ErrorHandler later
        }
    }

    // 🔥 GENERIC TYPE FILTERING
    private boolean supportsEvent(ApplicationListener listener, ApplicationEvent event) {

        // Simplified generic resolution
        return true; // extend with ResolvableType later
    }
}