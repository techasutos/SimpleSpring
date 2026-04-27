package com.asu.web.converter;

import com.asu.web.json.JsonMapper;

public class JsonMessageConverter implements HttpMessageConverter {

    private JsonMapper mapper = new JsonMapper();

    @Override
    public boolean canRead(Class<?> clazz, String contentType) {
        return "application/json".equals(contentType);
    }

    @Override
    public Object read(String body, Class<?> clazz) {
        return mapper.fromJson(body, clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, String contentType) {
        return "application/json".equals(contentType);
    }

    @Override
    public String write(Object value) {
        return mapper.toJson(value);
    }
}
