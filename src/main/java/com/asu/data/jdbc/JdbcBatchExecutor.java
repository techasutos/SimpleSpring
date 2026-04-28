package com.asu.data.jdbc;

import com.asu.tx.ConnectionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class JdbcBatchExecutor {

    private final List<BatchItem> batch = new ArrayList<>();

    public void add(String sql, Object[] params) {
        batch.add(new BatchItem(sql, params));
    }

    public void execute() {

        try {
            Connection conn = ConnectionContext.get();

            for (BatchItem item : batch) {

                PreparedStatement ps = conn.prepareStatement(item.sql);

                Object[] args = item.params;

                if (args != null) {
                    for (int i = 0; i < args.length; i++) {
                        ps.setObject(i + 1, args[i]);
                    }
                }

                ps.addBatch();
                ps.executeBatch();
            }

            batch.clear();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class BatchItem {
        String sql;
        Object[] params;

        BatchItem(String sql, Object[] params) {
            this.sql = sql;
            this.params = params;
        }
    }
}