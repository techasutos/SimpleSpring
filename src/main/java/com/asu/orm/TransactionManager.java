package com.asu.orm;

public class TransactionManager {

    private Session session;

    public TransactionManager(Session session) {
        this.session = session;
    }

    public void begin() {
        System.out.println("TX BEGIN");
    }

    public void commit() {
        session.flush();
        System.out.println("TX COMMIT");
    }

    public void rollback() {
        System.out.println("TX ROLLBACK");
    }
}