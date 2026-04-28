package com.asu.tx;

public interface TransactionParticipant {
    void prepare();
    void commit();
    void rollback();
}
