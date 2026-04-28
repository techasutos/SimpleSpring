package com.asu.processors;

import com.asu.aop.ProxyFactory;
import com.asu.aop.advisor.Advisor;
import com.asu.aop.pointcut.ExecutionExpressionPointcut;
import com.asu.lifecycle.BeanPostProcessor;
import com.asu.tx.aop.TransactionInterceptor;
import com.asu.tx.core.TransactionManager;

import java.util.ArrayList;
import java.util.List;

public class TransactionPostProcessor implements BeanPostProcessor {

    private final TransactionManager txManager;

    public TransactionPostProcessor(TransactionManager txManager) {
        this.txManager = txManager;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {

        // 🔥 skip infrastructure beans
        if (bean instanceof TransactionManager) {
            return bean;
        }

        ExecutionExpressionPointcut pointcut =
                new ExecutionExpressionPointcut("execution(* *(..))");

        List<Advisor> advisors = new ArrayList<>();
        advisors.add(new Advisor(pointcut, new TransactionInterceptor(txManager)));

        // 🔥 only proxy if needed
        if (!hasTransactionalMethod(bean)) {
            return bean;
        }

        return ProxyFactory.createProxy(bean, advisors);
    }

    private boolean hasTransactionalMethod(Object bean) {

        for (var m : bean.getClass().getMethods()) {
            if (m.isAnnotationPresent(com.asu.tx.annotation.Transactional.class)) {
                return true;
            }
        }
        return false;
    }
}