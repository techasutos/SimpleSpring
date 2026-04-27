package com.asu.beans;

public interface BeanFactory {

    Object getBean(String name);

    <T> T getBean(Class<T> type);
}
