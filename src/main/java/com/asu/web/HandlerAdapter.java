package com.asu.web;

public interface HandlerAdapter {

    boolean supports(Object handler);

    void handle(Request request, Response response, Object handler) throws Exception;
}
