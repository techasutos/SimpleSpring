package com.asu.web.interceptor;

import com.asu.web.Request;
import com.asu.web.Response;

public interface HandlerInterceptor {

    boolean preHandle(Request request);

    void postHandle(Request request, Response response);

    void afterCompletion();
}
