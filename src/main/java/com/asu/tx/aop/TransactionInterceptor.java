package com.asu.tx.aop;

import com.asu.aop.MethodInterceptor;
import com.asu.aop.MethodInvocation;
import com.asu.tx.annotation.Transactional;
import com.asu.tx.core.TransactionManager;

import java.lang.reflect.Method;

public class TransactionInterceptor implements MethodInterceptor {

    private final TransactionManager txManager;

    public TransactionInterceptor(TransactionManager txManager) {
        this.txManager = txManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        Transactional tx = method.getAnnotation(Transactional.class);

        if (tx == null) {
            return invocation.proceed();
        }

        try {

            txManager.begin(tx.propagation());

            Object result = invocation.proceed();

            txManager.commit(tx.propagation());

            return result;

        } catch (Throwable ex) {

            if (shouldRollback(tx, ex)) {
                txManager.rollback();
            }

            throw ex;
        }
    }

    private boolean shouldRollback(Transactional tx, Throwable ex) {

        for (Class<? extends Throwable> rule : tx.rollbackFor()) {
            if (rule.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }

        return false;
    }
}