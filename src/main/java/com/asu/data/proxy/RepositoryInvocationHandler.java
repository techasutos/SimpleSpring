package com.asu.data.proxy;

import com.asu.data.jdbc.EntityExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RepositoryInvocationHandler implements InvocationHandler {

    private final EntityExecutor executor;
    private final Class<?> repoType;

    public RepositoryInvocationHandler(EntityExecutor executor, Class<?> repoType) {
        this.executor = executor;
        this.repoType = repoType;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String name = method.getName();

        // CRUD shortcuts
        if (name.equals("findAll")) {
            return executor.findAll(repoType);
        }

        if (name.equals("findById")) {
            return executor.findById(repoType, args[0]);
        }

        if (name.equals("save")) {
            return executor.save(args[0]);
        }

        if (name.equals("deleteById")) {
            executor.deleteById(repoType, args[0]);
            return null;
        }

        // 🔥 Query derivation engine
        return executor.executeDerivedQuery(repoType, method, args);
    }
}