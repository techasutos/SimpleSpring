package com.asu.boot;

import com.asu.annotations.Controller;
import com.asu.context.ApplicationContext;
import com.asu.server.HttpServer;
import com.asu.web.DispatcherServlet;
import com.asu.web.HandlerAdapter;
import com.asu.web.HandlerMapping;
import com.asu.web.adapter.RequestMappingHandlerAdapter;
import com.asu.web.annotation.ControllerAdvice;
import com.asu.web.converter.HttpMessageConverter;
import com.asu.web.converter.JsonMessageConverter;
import com.asu.web.exception.ExceptionHandlerRegistry;
import com.asu.web.negotiation.ContentNegotiationManager;

import java.util.ArrayList;
import java.util.List;

public class SimpleSpringApplicationRunner {

    public static void run(Class<?> mainClass, String[] args) {

        try {
            // 1️⃣ Resolve base package
            String basePackage = resolveBasePackage(mainClass);

            // 2️⃣ ApplicationContext
            ApplicationContext context = new ApplicationContext(basePackage);

            // 3️⃣ HandlerMapping
            HandlerMapping mapping = new HandlerMapping();

            List<Object> controllers = context.getBeansWithAnnotation(Controller.class);
            for (Object controller : controllers) {
                mapping.register(controller);
            }

            // 4️⃣ Message Converters
            List<HttpMessageConverter> converters = new ArrayList<>();
            converters.add(new JsonMessageConverter());

            // 5️⃣ Content Negotiation
            ContentNegotiationManager negotiationManager = new ContentNegotiationManager();

            // 6️⃣ HandlerAdapter (UPDATED constructor)
            RequestMappingHandlerAdapter adapter =
                    new RequestMappingHandlerAdapter(negotiationManager, converters);

            // 7️⃣ ControllerAdvice → Exception Registry
            ExceptionHandlerRegistry exceptionRegistry = new ExceptionHandlerRegistry();

            List<Object> advices = context.getBeansWithAnnotation(ControllerAdvice.class);
            for (Object advice : advices) {
                exceptionRegistry.register(advice);
            }

            // 8️⃣ DispatcherServlet (UPDATED constructor)
            List<HandlerAdapter> adapters = new ArrayList<>();
            adapters.add(new RequestMappingHandlerAdapter(negotiationManager, converters));

            DispatcherServlet dispatcher =
                    new DispatcherServlet(mapping, exceptionRegistry, adapters);

            // 🔥 Inject adapter manually (since you control framework)
            dispatcher.getResolvers(); // ensures list init
            dispatcher.getInterceptors(); // ensures list init

            // ⚠️ Add adapter manually (important)
            // If you add setter later, cleaner
            dispatcher.getClass(); // no-op, placeholder if you want reflection injection

            // 9️⃣ Start HTTP Server
            HttpServer server = new HttpServer(8080, dispatcher);
            server.start();

            System.out.println("🚀 Server started at http://localhost:8080");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String resolveBasePackage(Class<?> mainClass) {

        SimpleSpringApplication annotation =
                mainClass.getAnnotation(SimpleSpringApplication.class);

        if (annotation != null && !annotation.basePackage().isEmpty()) {
            return annotation.basePackage();
        }

        return mainClass.getPackage().getName();
    }
}