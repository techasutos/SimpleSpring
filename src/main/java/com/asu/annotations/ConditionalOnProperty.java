package com.asu.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConditionalOnProperty {

    String prefix() default "";

    String name();

    String havingValue() default "";

    boolean matchIfMissing() default false;
}