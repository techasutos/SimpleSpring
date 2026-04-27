package com.asu.processors;

import com.asu.aop.ProxyFactory;
import com.asu.aop.TransactionInterceptor;
import com.asu.aop.advisor.Advisor;
import com.asu.aop.pointcut.ExecutionExpressionPointcut;
import com.asu.lifecycle.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

public class TransactionPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {

        // Match methods annotated with @Transactional
        ExecutionExpressionPointcut pointcut =
                new ExecutionExpressionPointcut("execution(* *(..))");

        List<Advisor> advisors = new ArrayList<>();
        advisors.add(new Advisor(pointcut, new TransactionInterceptor()));

        return ProxyFactory.createProxy(bean, advisors);
    }
}