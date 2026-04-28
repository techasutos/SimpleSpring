package com.asu.data.jdbc;

import com.asu.db.pool.ConnectionPool;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class BatchExecutor {

    private ConnectionPool pool;

    public BatchExecutor(ConnectionPool pool) {
        this.pool = pool;
    }

    public void executeBatch(List<String> sqls) {

        try (Connection conn = pool.borrow()) {

            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();

            for (String sql : sqls) {
                stmt.addBatch(sql);
            }

            stmt.executeBatch();

            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
