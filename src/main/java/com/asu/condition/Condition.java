package com.asu.condition;

import com.asu.annotations.AnnotationMetadata;

public interface Condition {
    boolean matches(AnnotationMetadata metadata, ClassLoader classLoader);
}
