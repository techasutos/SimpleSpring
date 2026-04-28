package com.asu.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListenerRegistry {

    private final List<ApplicationListenerMethodAdapter> listeners = new ArrayList<>();

    public void register(ApplicationListenerMethodAdapter adapter) {
        listeners.add(adapter);
        listeners.sort(Comparator.comparingInt(ApplicationListenerMethodAdapter::getOrder));
    }

    public List<ApplicationListenerMethodAdapter> getListeners() {
        return listeners;
    }
}