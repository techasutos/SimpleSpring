package com.asu.tx.core;

import com.asu.db.pool.ConnectionPool;
import com.asu.tx.annotation.Propagation;

import java.sql.Connection;

public class TransactionManager {

    private final ConnectionPool pool;

    public TransactionManager(ConnectionPool pool) {
        this.pool = pool;
    }

    // ------------------------
    // BEGIN
    // ------------------------
    public void begin(Propagation propagation) {

        try {

            if (propagation == Propagation.REQUIRED && TransactionContext.isActive()) {
                return; // join existing
            }

            Connection conn = pool.borrow();
            conn.setAutoCommit(false);

            TransactionContext.push(conn);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------------
    // COMMIT
    // ------------------------
    public void commit(Propagation propagation) {

        Connection conn = TransactionContext.peek();

        try {
            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {

            TransactionContext.pop();
            pool.release(conn);
        }
    }

    // ------------------------
    // ROLLBACK
    // ------------------------
    public void rollback() {

        Connection conn = TransactionContext.peek();

        try {
            conn.rollback();

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {

            TransactionContext.pop();
            pool.release(conn);
        }
    }
}