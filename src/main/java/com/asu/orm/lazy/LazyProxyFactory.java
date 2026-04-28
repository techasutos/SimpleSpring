package com.asu.orm.lazy;

import com.asu.orm.Session;

import java.lang.reflect.Proxy;

public class LazyProxyFactory {

    public static Object createProxy(Class<?> type,
                                     Object id,
                                     Session session) {

        return Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class[]{type, LazyLoader.class},
                new LazyInvocationHandler(type, id, session)
        );
    }
}