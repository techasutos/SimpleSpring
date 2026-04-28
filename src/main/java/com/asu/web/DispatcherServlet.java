package com.asu.web;

import com.asu.web.exception.ExceptionHandlerRegistry;
import com.asu.web.exception.HandlerExceptionResolver;
import com.asu.web.interceptor.HandlerInterceptor;
import com.asu.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.List;

public class DispatcherServlet {

    private HandlerMapping handlerMapping;

    private List<HandlerAdapter> adapters = new ArrayList<>();
    private List<HandlerInterceptor> interceptors = new ArrayList<>();
    private List<HandlerExceptionResolver> resolvers = new ArrayList<>();

    // 🔥 NEW: Global exception handling
    private ExceptionHandlerRegistry exceptionRegistry;

    public DispatcherServlet(HandlerMapping mapping,
                             ExceptionHandlerRegistry exceptionRegistry,
                             List<HandlerAdapter> adapters) {

        this.handlerMapping = mapping;
        this.exceptionRegistry = exceptionRegistry;
        this.adapters.addAll(adapters);
    }

    public void dispatch(Request request, Response response) {

        HandlerMethod handler = null;

        try {

            handler = handlerMapping.getHandler(request);

            if (handler == null) {
                response.write("404 NOT FOUND");
                return;
            }

            // 🔥 PRE HANDLE
            for (HandlerInterceptor i : interceptors) {
                if (!i.preHandle(request)) return;
            }

            HandlerAdapter adapter = getAdapter(handler);

            try {
                adapter.handle(request, response, handler);
            } catch (Exception e) {

                // 🔥 1. ControllerAdvice handling
                if (exceptionRegistry != null) {
                    HandlerMethod exHandler = exceptionRegistry.resolve(e);

                    if (exHandler != null) {
                        Object result = exHandler.getMethod()
                                .invoke(exHandler.getBean(), e);

                        response.write(result);
                        return;
                    }
                }

                // 🔥 2. Fallback resolvers
                for (HandlerExceptionResolver resolver : resolvers) {
                    if (resolver.resolve(e, response)) return;
                }
            }

            // 🔥 POST HANDLE
            for (HandlerInterceptor i : interceptors) {
                i.postHandle(request, response);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // 🔥 AFTER COMPLETION
            for (HandlerInterceptor i : interceptors) {
                i.afterCompletion();
            }
        }
    }

    private HandlerAdapter getAdapter(HandlerMethod handler) {

        for (HandlerAdapter adapter : adapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }

        throw new RuntimeException("No adapter found");
    }

    // 🔧 Extension points
    public List<HandlerInterceptor> getInterceptors() {
        return interceptors;
    }

    public List<HandlerExceptionResolver> getResolvers() {
        return resolvers;
    }
}