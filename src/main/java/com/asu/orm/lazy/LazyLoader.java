package com.asu.orm.lazy;

public interface LazyLoader {
    void __initialize();
    boolean __isInitialized();
}