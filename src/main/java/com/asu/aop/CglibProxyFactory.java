package com.asu.aop;

import com.asu.aop.advisor.Advisor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

public class CglibProxyFactory {

    public static Object createProxy(Object target, List<Advisor> advisors) {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());

        enhancer.setCallback(new CglibMethodInterceptorAdapter(target, advisors));

        return enhancer.create();
    }

    // 🔥 Adapter now uses Advisor instead of direct interceptors
    private static class CglibMethodInterceptorAdapter
            implements net.sf.cglib.proxy.MethodInterceptor {

        private Object target;
        private List<Advisor> advisors;

        public CglibMethodInterceptorAdapter(Object target, List<Advisor> advisors) {
            this.target = target;
            this.advisors = advisors;
        }

        @Override
        public Object intercept(Object obj,
                                Method method,
                                Object[] args,
                                MethodProxy proxy) throws Throwable {

            // 🔥 Resolve interceptors dynamically per method
            List<com.asu.aop.MethodInterceptor> interceptors =
                    ProxyFactory.getInterceptors(method, target.getClass(), advisors);

            // No interceptor → direct invocation (optimization)
            if (interceptors.isEmpty()) {
                return proxy.invoke(target, args);
            }

            MethodInvocation invocation =
                    new MethodInvocation(target, method, args, interceptors);

            return invocation.proceed();
        }
    }
}