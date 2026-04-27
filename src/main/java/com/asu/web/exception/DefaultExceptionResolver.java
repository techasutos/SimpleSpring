package com.asu.web.exception;

import com.asu.web.Response;

public class DefaultExceptionResolver implements HandlerExceptionResolver {

    @Override
    public boolean resolve(Exception e, Response response) {
        response.write("Error: " + e.getMessage());
        return true;
    }
}