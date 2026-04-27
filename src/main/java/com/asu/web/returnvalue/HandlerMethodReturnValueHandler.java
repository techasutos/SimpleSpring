package com.asu.web.returnvalue;

import com.asu.web.Response;

public interface HandlerMethodReturnValueHandler {

    boolean supports(Object returnValue);

    void handle(Object returnValue, Response response);
}
