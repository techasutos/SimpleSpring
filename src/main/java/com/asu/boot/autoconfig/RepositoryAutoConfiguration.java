package com.asu.boot.autoconfig;

import com.asu.annotations.Bean;
import com.asu.context.ApplicationContext;
import com.asu.data.jdbc.EntityExecutor;
import com.asu.data.repository.RepositoryScanner;

public class RepositoryAutoConfiguration {

    @Bean
    public RepositoryScanner repositoryScanner(ApplicationContext context,
                                               EntityExecutor executor) {

        RepositoryScanner scanner = new RepositoryScanner(context, executor);

        // 🔥 trigger scanning immediately (simplified bootstrapping)
        scanner.scanAndRegister(context.getBasePackage());

        return scanner;
    }
}