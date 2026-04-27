package com.asu.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConditionalOnProperty {

    String prefix() default "";

    String name();

    String havingValue() default "";

    boolean matchIfMissing() default false;
}