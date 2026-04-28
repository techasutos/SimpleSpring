package com.asu.tx.core;

import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.Deque;

public class TransactionContext {

    private static final ThreadLocal<Deque<Connection>> STACK =
            ThreadLocal.withInitial(ArrayDeque::new);

    public static void push(Connection conn) {
        STACK.get().push(conn);
    }

    public static Connection peek() {
        return STACK.get().peek();
    }

    public static Connection pop() {
        return STACK.get().pop();
    }

    public static boolean isActive() {
        return !STACK.get().isEmpty();
    }
}