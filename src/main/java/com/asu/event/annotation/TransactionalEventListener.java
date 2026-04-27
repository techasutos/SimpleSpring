package com.asu.event.annotation;

import java.lang.annotation.*;

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