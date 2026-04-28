package com.asu.boot.autoconfig;

import com.asu.annotations.Bean;
import com.asu.data.jdbc.JdbcBatchExecutor;
import com.asu.db.pool.ConnectionPool;
import com.asu.tx.core.TransactionManager;

import java.sql.Connection;

@ConditionalOnClass(Connection.class)
public class DataAutoConfiguration {

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(10, "jdbc:mysql://localhost:3306/app");
    }

    @Bean
    public TransactionManager transactionManager(ConnectionPool pool) {
        return new TransactionManager(pool);
    }

    @Bean
    public JdbcBatchExecutor batchExecutor() {
        return new JdbcBatchExecutor();
    }
}