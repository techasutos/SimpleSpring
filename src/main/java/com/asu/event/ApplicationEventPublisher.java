package com.asu.event;

public class ApplicationEventPublisher {

    private final ApplicationEventMulticaster multicaster;

    public ApplicationEventPublisher(ApplicationEventMulticaster multicaster) {
        this.multicaster = multicaster;
    }

    public void publishEvent(ApplicationEvent event) {
        multicaster.multicastEvent(event);
    }
}