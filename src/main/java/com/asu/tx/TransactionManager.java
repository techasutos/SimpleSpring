package com.asu.tx;

public class TransactionManager {

    public void begin() {
        System.out.println("Transaction Started");
    }

    public void commit() {
        System.out.println("Transaction Committed");
    }

    public void rollback() {
        System.out.println("Transaction Rolled Back");
    }
}
