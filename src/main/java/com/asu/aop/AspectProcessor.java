package com.asu.aop;

import com.asu.aop.advice.*;
import com.asu.aop.advisor.Advisor;
import com.asu.aop.annotation.*;
import com.asu.aop.pointcut.AnnotationPointcut;
import com.asu.aop.pointcut.ExecutionExpressionPointcut;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AspectProcessor {

    public static List<Advisor> buildAdvisors(List<Object> beans) {

        List<Advisor> advisors = new ArrayList<>();

        for (Object bean : beans) {

            Class<?> clazz = bean.getClass();

            if (!clazz.isAnnotationPresent(Aspect.class)) continue;

            for (Method method : clazz.getMethods()) {

                // @Before
                if (method.isAnnotationPresent(Before.class)) {

                    Class<?> annotation =
                            resolveAnnotation(method.getAnnotation(Before.class).value());

                    advisors.add(new Advisor(
                            new AnnotationPointcut((Class) annotation),
                            new BeforeInterceptor(bean, method)
                    ));
                }

                // @After
                if (method.isAnnotationPresent(After.class)) {

                    Class<?> annotation =
                            resolveAnnotation(method.getAnnotation(After.class).value());

                    advisors.add(new Advisor(
                            new AnnotationPointcut((Class) annotation),
                            new AfterInterceptor(bean, method)
                    ));
                }

                // @Around
                if (method.isAnnotationPresent(Around.class)) {

                    Class<?> annotation =
                            resolveAnnotation(method.getAnnotation(Around.class).value());

                    advisors.add(new Advisor(
                            new AnnotationPointcut((Class) annotation),
                            new AroundInterceptor(bean, method)
                    ));
                }

                // AfterReturning
                if (method.isAnnotationPresent(AfterReturning.class)) {

                    advisors.add(new Advisor(
                            new ExecutionExpressionPointcut(method.getAnnotation(AfterReturning.class).value()),
                            new AfterReturningInterceptor(bean, method)
                    ));
                }

                // AfterThrowing
                if (method.isAnnotationPresent(AfterThrowing.class)) {

                    advisors.add(new Advisor(
                            new ExecutionExpressionPointcut(method.getAnnotation(AfterThrowing.class).value()),
                            new AfterThrowingInterceptor(bean, method)
                    ));
                }
            }
        }

        return advisors;
    }

    // Simplified: @annotation(com.asu.Tx)
    private static Class<?> resolveAnnotation(String expr) {

        try {
            String className = expr
                    .replace("@annotation(", "")
                    .replace(")", "");

            return Class.forName(className);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}