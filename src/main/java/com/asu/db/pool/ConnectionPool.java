package com.asu.db.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {

    private BlockingQueue<Connection> pool;

    public ConnectionPool(int size, String url) {

        pool = new ArrayBlockingQueue<>(size);

        for (int i = 0; i < size; i++) {
            pool.add(createConnection(url));
        }
    }

    public Connection borrow() {
        return pool.poll();
    }

    public void release(Connection conn) {
        pool.offer(conn);
    }

    private Connection createConnection(String url) {
        try {
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}