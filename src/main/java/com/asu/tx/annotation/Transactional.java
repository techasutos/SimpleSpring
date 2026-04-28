package com.asu.tx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE}) // 🔥 FIX 1
public @interface Transactional {

    // propagation behavior
    Propagation propagation() default Propagation.REQUIRED;

    // rollback rules
    Class<? extends Throwable>[] rollbackFor() default {Exception.class}; // 🔥 FIX 2

    // 🔥 ADD (future-ready, Spring parity)
    boolean readOnly() default false;
}