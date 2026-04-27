package com.asu.tx;

import com.asu.aop.MethodInterceptor;
import com.asu.aop.MethodInvocation;
import com.asu.annotations.Transactional;

public class TransactionInterceptor implements MethodInterceptor {

    private TransactionManager txManager = new TransactionManager();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        if (invocation.getMethod().isAnnotationPresent(Transactional.class)) {

            try {
                txManager.begin();

                Object result = invocation.proceed();

                txManager.commit();
                return result;

            } catch (Exception e) {
                txManager.rollback();
                throw e;
            }
        }

        return invocation.proceed();
    }
}