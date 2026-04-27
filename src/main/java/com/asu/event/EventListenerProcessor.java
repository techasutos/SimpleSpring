package com.asu.event;

import com.asu.event.annotation.EventListener;

import java.lang.reflect.Method;
import java.util.List;

public class EventListenerProcessor {

    public static void process(List<Object> beans,
                               ListenerRegistry registry,
                               ApplicationEventMulticaster multicaster) {

        for (Object bean : beans) {

            for (Method method : bean.getClass().getMethods()) {

                if (method.isAnnotationPresent(EventListener.class)) {

                    ApplicationListenerMethodAdapter adapter =
                            new ApplicationListenerMethodAdapter(bean, method);

                    registry.register(adapter);
                    multicaster.addApplicationListener(adapter);
                }
            }
        }
    }
}