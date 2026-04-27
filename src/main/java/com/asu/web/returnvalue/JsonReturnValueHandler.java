package com.asu.web.returnvalue;

import com.asu.web.Response;
import com.asu.web.converter.HttpMessageConverter;

import java.util.List;

public class JsonReturnValueHandler implements HandlerMethodReturnValueHandler {

    private List<HttpMessageConverter> converters;

    public JsonReturnValueHandler(List<HttpMessageConverter> converters) {
        this.converters = converters;
    }

    @Override
    public boolean supports(Object returnValue) {
        return returnValue != null;
    }

    @Override
    public void handle(Object returnValue, Response response) {

        String contentType = "application/json";

        for (HttpMessageConverter converter : converters) {
            if (converter.canWrite(returnValue.getClass(), contentType)) {

                String body = converter.write(returnValue);
                response.write(body);
                return;
            }
        }

        throw new RuntimeException("No suitable message converter found");
    }
}