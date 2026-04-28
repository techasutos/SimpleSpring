package com.asu.web.resolver;

import com.asu.web.Request;
import com.asu.web.annotations.PathVariable;

import java.lang.reflect.Parameter;

public class PathVariableResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public Object resolveArgument(Parameter parameter, Request request) {

        String name = parameter.getAnnotation(PathVariable.class).value();

        return request.getPathVariable(name);
    }
}
