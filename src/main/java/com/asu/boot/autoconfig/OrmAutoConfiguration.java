package com.asu.boot.autoconfig;

import com.asu.annotations.Bean;
import com.asu.data.jdbc.EntityExecutor;
import com.asu.orm.Session;

public class OrmAutoConfiguration {

    // 1️⃣ EntityExecutor FIRST
    @Bean
    public EntityExecutor entityExecutor() {
        return new EntityExecutor(); // uses ConnectionContext internally
    }

    // 2️⃣ Inject into Session
    @Bean
    public Session session(EntityExecutor executor) {
        return new Session(executor);
    }
}