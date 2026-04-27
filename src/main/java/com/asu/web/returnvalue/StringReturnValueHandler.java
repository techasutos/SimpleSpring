package com.asu.web.returnvalue;

import com.asu.web.Response;

public class StringReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supports(Object returnValue) {
        return returnValue instanceof String;
    }

    @Override
    public void handle(Object returnValue, Response response) {
        response.write(returnValue);
    }
}
