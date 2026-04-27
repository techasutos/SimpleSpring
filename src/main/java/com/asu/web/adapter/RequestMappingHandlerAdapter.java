package com.asu.web.adapter;

import com.asu.web.HandlerAdapter;
import com.asu.web.Request;
import com.asu.web.Response;
import com.asu.web.converter.HttpMessageConverter;
import com.asu.web.method.HandlerMethod;
import com.asu.web.negotiation.ContentNegotiationManager;
import com.asu.web.resolver.HandlerMethodArgumentResolver;
import com.asu.web.resolver.RequestBodyResolver;
import com.asu.web.resolver.RequestParamResolver;
import com.asu.web.returnvalue.HandlerMethodReturnValueHandler;
import com.asu.web.returnvalue.JsonReturnValueHandler;
import com.asu.web.returnvalue.StringReturnValueHandler;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RequestMappingHandlerAdapter implements HandlerAdapter {

    private List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
    private List<HandlerMethodReturnValueHandler> returnHandlers = new ArrayList<>();

    private ContentNegotiationManager negotiationManager;
    private List<HttpMessageConverter> converters;

    public RequestMappingHandlerAdapter(ContentNegotiationManager negotiationManager,
                                        List<HttpMessageConverter> converters) {

        this.negotiationManager = negotiationManager;
        this.converters = converters;

        // 🔥 Argument Resolvers
        argumentResolvers.add(new RequestParamResolver());
        argumentResolvers.add(new RequestBodyResolver(converters));

        // 🔥 Return Handlers
        returnHandlers.add(new JsonReturnValueHandler(converters));
        returnHandlers.add(new StringReturnValueHandler());
    }

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    @Override
    public void handle(Request request, Response response, Object handler) throws Exception {

        HandlerMethod hm = (HandlerMethod) handler;

        Object[] args = resolveArguments(request, hm);

        Object result = hm.getMethod().invoke(hm.getBean(), args);

        handleReturnValue(result, request, response);
    }

    private Object[] resolveArguments(Request request, HandlerMethod hm) {

        Parameter[] params = hm.getMethod().getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {

            for (HandlerMethodArgumentResolver resolver : argumentResolvers) {

                if (resolver.supportsParameter(params[i])) {
                    args[i] = resolver.resolveArgument(params[i], request);
                    break;
                }
            }
        }

        return args;
    }

    // 🔥 CLEAN CONTENT NEGOTIATION (NO instanceof)
    private void handleReturnValue(Object result, Request request, Response response) {

        String contentType = negotiationManager.resolveContentType(request);

        for (HandlerMethodReturnValueHandler handler : returnHandlers) {

            if (handler.supports(result)) {

                // Let handler internally decide using converters
                handler.handle(result, response);
                return;
            }
        }

        throw new RuntimeException("No suitable return handler found for: " + contentType);
    }
}