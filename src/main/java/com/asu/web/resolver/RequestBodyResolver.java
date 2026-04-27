package com.asu.web.resolver;

import com.asu.web.Request;
import com.asu.web.annotation.RequestBody;
import com.asu.web.converter.HttpMessageConverter;

import java.lang.reflect.Parameter;
import java.util.List;

public class RequestBodyResolver implements HandlerMethodArgumentResolver {

    private List<HttpMessageConverter> converters;

    public RequestBodyResolver(List<HttpMessageConverter> converters) {
        this.converters = converters;
    }

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolveArgument(Parameter parameter, Request request) {

        String contentType = request.getHeader("Content-Type");
        String body = request.getBody();

        for (HttpMessageConverter converter : converters) {
            if (converter.canRead(parameter.getType(), contentType)) {
                return converter.read(body, parameter.getType());
            }
        }

        throw new RuntimeException("No suitable message converter found");
    }
}