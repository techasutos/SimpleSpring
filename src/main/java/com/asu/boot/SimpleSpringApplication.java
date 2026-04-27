package com.asu.boot;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SimpleSpringApplication {
    String basePackage() default "";
}
