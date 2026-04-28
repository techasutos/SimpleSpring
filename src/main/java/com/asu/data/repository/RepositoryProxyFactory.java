package com.asu.data.repository;

import com.asu.data.jdbc.EntityExecutor;

import java.lang.reflect.Proxy;

public class RepositoryProxyFactory {

    public static Object create(Class<?> repoInterface,
                                EntityExecutor executor) {

        return Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class[]{repoInterface},
                new RepositoryInvocationHandler(repoInterface, executor)
        );
    }
}