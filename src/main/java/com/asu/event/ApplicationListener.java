package com.asu.event;

public interface ApplicationListener<T extends ApplicationEvent> {
    void onApplicationEvent(T event);
}