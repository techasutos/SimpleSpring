package com.asu.orm.lazy;

import com.asu.orm.Session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LazyInvocationHandler implements InvocationHandler, LazyLoader {

    private final Class<?> type;
    private final Object id;
    private final Session session;

    private Object target; // real entity
    private boolean initialized = false;

    public LazyInvocationHandler(Class<?> type,
                                 Object id,
                                 Session session) {
        this.type = type;
        this.id = id;
        this.session = session;
    }

    // -------------------------
    // Lazy initialization
    // -------------------------
    @Override
    public void __initialize() {

        if (!initialized) {
            this.target = session.find(type, id);
            this.initialized = true;
        }
    }

    @Override
    public boolean __isInitialized() {
        return initialized;
    }

    // -------------------------
    // Intercept method calls
    // -------------------------
    @Override
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {

        // skip lazy interface methods
        if (method.getDeclaringClass().equals(LazyLoader.class)) {
            return method.invoke(this, args);
        }

        // 🔥 trigger DB load
        __initialize();

        return method.invoke(target, args);
    }
}