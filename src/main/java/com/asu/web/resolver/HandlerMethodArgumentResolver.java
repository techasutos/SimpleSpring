package com.asu.web.resolver;

import com.asu.web.Request;

import java.lang.reflect.Parameter;

public interface HandlerMethodArgumentResolver {

    boolean supportsParameter(Parameter parameter);

    Object resolveArgument(Parameter parameter, Request request);
}