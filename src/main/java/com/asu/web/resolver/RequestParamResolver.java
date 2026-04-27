package com.asu.web.resolver;

import com.asu.annotations.RequestParam;
import com.asu.web.Request;
import com.asu.web.validation.Valid;
import com.asu.web.validation.Validator;

import java.lang.reflect.Parameter;

public class RequestParamResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object resolveArgument(Parameter parameter, Request request) {

        String name = parameter.getAnnotation(RequestParam.class).value();
        Object value = request.getParam(name);

        // 🔥 Validation integration
        if (parameter.isAnnotationPresent(Valid.class)) {
            Validator.validate(value);
        }

        return value;
    }
}
