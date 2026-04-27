package com.asu.web.exception;

import com.asu.web.Response;

public interface HandlerExceptionResolver {

    boolean resolve(Exception e, Response response);
}
