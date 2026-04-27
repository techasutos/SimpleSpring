package com.asu.web.converter;

public interface HttpMessageConverter {

    boolean canRead(Class<?> clazz, String contentType);

    Object read(String body, Class<?> clazz);

    boolean canWrite(Class<?> clazz, String contentType);

    String write(Object value);
}