package com.asu.web.interceptor;

import com.asu.web.Request;
import com.asu.web.Response;

public class LoggingInterceptor implements HandlerInterceptor {

    public boolean preHandle(Request request) {
        System.out.println("Incoming: " + request.getPath());
        return true;
    }

    public void postHandle(Request request, Response response) {
        System.out.println("Handled request");
    }

    public void afterCompletion() {
        System.out.println("Completed");
    }
}
