package com.asu.aop;

import com.asu.aop.advisor.Advisor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ProxyFactory {

    public static Object createProxy(Object target, List<Advisor> advisors) {

        Class<?>[] interfaces = target.getClass().getInterfaces();

        if (interfaces.length > 0) {
            return createJdkProxy(target, interfaces, advisors);
        }

        return CglibProxyFactory.createProxy(target, advisors);
    }

    private static Object createJdkProxy(Object target,
                                         Class<?>[] interfaces,
                                         List<Advisor> advisors) {

        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfaces,
                (proxy, method, args) -> {

                    List<MethodInterceptor> interceptors =
                            getInterceptors(method, target.getClass(), advisors);

                    MethodInvocation invocation =
                            new MethodInvocation(target, method, args, interceptors);

                    return invocation.proceed();
                }
        );
    }

    // 🔥 CORE LOGIC
    public static List<MethodInterceptor> getInterceptors(
            Method method,
            Class<?> targetClass,
            List<Advisor> advisors) {

        List<MethodInterceptor> interceptors = new ArrayList<>();

        for (Advisor advisor : advisors) {
            if (advisor.matches(method, targetClass)) {
                interceptors.add(advisor.getInterceptor());
            }
        }

        return interceptors;
    }
}