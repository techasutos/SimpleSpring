package com.asu.aop;

import com.asu.annotations.Transactional;
import com.asu.tx.TransactionManager;

import java.lang.reflect.*;

public class TransactionProxy implements InvocationHandler {

    private Object target;
    private TransactionManager txManager = new TransactionManager();

    public TransactionProxy(Object target) {
        this.target = target;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                this
        );
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isAnnotationPresent(Transactional.class)) {
            try {
                txManager.begin();
                Object result = method.invoke(target, args);
                txManager.commit();
                return result;
            } catch (Exception e) {
                txManager.rollback();
                throw e;
            }
        }

        return method.invoke(target, args);
    }
}