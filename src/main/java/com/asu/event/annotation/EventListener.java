package com.asu.event.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener {

    Class<?>[] value() default {};

    String condition() default ""; // SpEL-like (simplified)
}