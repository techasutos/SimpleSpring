package com.asu.aop.pointcut;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class ExecutionExpressionPointcut implements Pointcut {

    private Pattern classPattern;
    private Pattern methodPattern;

    public ExecutionExpressionPointcut(String expression) {

        // Example:
        // execution(* com.asu.service.*.*(..))

        String exp = expression
                .replace("execution(", "")
                .replace(")", "")
                .trim();

        // remove return type (*)
        exp = exp.substring(exp.indexOf(" ") + 1);
        // com.asu.service.*.*(..)

        exp = exp.replace("(..)", "");

        int lastDot = exp.lastIndexOf(".");
        if (lastDot == -1) {
            throw new RuntimeException("Invalid expression: " + expression);
        }

        String classPart = exp.substring(0, lastDot);
        String methodPart = exp.substring(lastDot + 1);

        // convert wildcard → regex
        this.classPattern = Pattern.compile(
                classPart.replace(".", "\\.")
                        .replace("*", ".*")
        );

        this.methodPattern = Pattern.compile(
                methodPart.replace("*", ".*")
        );
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {

        String className = targetClass.getName();
        String methodName = method.getName();

        return classPattern.matcher(className).matches()
                && methodPattern.matcher(methodName).matches();
    }
}