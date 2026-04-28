package com.asu.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TransactionalEventListener {

    Phase phase() default Phase.AFTER_COMMIT;

    enum Phase {
        BEFORE_COMMIT,
        AFTER_COMMIT,
        AFTER_ROLLBACK
    }
}