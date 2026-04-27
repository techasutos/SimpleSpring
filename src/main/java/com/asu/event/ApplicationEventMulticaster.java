package com.asu.event;

public interface ApplicationEventMulticaster {

    void addApplicationListener(ApplicationListener<?> listener);

    void multicastEvent(ApplicationEvent event);
}