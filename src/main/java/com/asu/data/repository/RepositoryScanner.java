package com.asu.data.repository;

import com.asu.context.ApplicationContext;
import com.asu.data.jdbc.EntityExecutor;

import java.util.Set;

public class RepositoryScanner {

    private final ApplicationContext context;
    private final EntityExecutor executor;

    public RepositoryScanner(ApplicationContext context,
                             EntityExecutor executor) {
        this.context = context;
        this.executor = executor;
    }

    public void scanAndRegister(String basePackage) {

        Set<Class<?>> repos = RepositoryUtils.findRepositories(basePackage);

        for (Class<?> repo : repos) {

            Object proxy = RepositoryProxyFactory.create(repo, executor);

            context.registerSingleton(repo.getSimpleName(), proxy);
        }
    }
}