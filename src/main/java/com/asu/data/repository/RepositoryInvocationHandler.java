package com.asu.data.repository;

import com.asu.data.annotation.Query;
import com.asu.data.jdbc.EntityExecutor;

import java.lang.reflect.*;
import java.util.*;

public class RepositoryInvocationHandler implements InvocationHandler {

    private final Class<?> repoInterface;
    private final EntityExecutor executor;

    // 🔥 cache resolved entity type
    private Class<?> entityClass;

    public RepositoryInvocationHandler(Class<?> repoInterface,
                                       EntityExecutor executor) {
        this.repoInterface = repoInterface;
        this.executor = executor;
        this.entityClass = resolveEntityClass();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // -------------------------------
        // 0. Object methods
        // -------------------------------
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        String methodName = method.getName();

        // -------------------------------
        // 1. CRUD METHODS
        // -------------------------------
        switch (methodName) {

            case "findById":
                Object result = executor.findById(entityClass, args[0]);
                return wrapOptional(method, result);

            case "findAll":
                return executor.findAll(entityClass);

            case "save":
                return executor.save(args[0]);

            case "deleteById":
                executor.deleteById(entityClass, args[0]);
                return null;

            case "delete":
                executor.delete(args[0]);
                return null;
        }

        // -------------------------------
        // 2. @Query SUPPORT
        // -------------------------------
        if (method.isAnnotationPresent(Query.class)) {

            String sql = method.getAnnotation(Query.class).value();

            return executor.executeNativeQuery(
                    entityClass,
                    sql,
                    args,
                    resolveReturnType(method)
            );
        }

        // -------------------------------
        // 3. DERIVED QUERY PARSING
        // -------------------------------
        QueryMeta meta = parseDerivedQuery(method);

        Object queryResult = executor.executeDerivedQuery(
                entityClass,
                meta,
                args
        );

        return adaptReturnType(method, queryResult);
    }

    // =========================================================
    // 🔥 GENERIC ENTITY RESOLUTION (CRITICAL)
    // =========================================================
    private Class<?> resolveEntityClass() {

        // Look for Repository<T, ID>
        for (Type type : repoInterface.getGenericInterfaces()) {

            if (type instanceof ParameterizedType) {

                ParameterizedType pt = (ParameterizedType) type;

                Type raw = pt.getRawType();

                if (raw instanceof Class &&
                        ((Class<?>) raw).getSimpleName().contains("Repository")) {

                    Type entityType = pt.getActualTypeArguments()[0];

                    if (entityType instanceof Class) {
                        return (Class<?>) entityType;
                    }
                }
            }
        }

        throw new RuntimeException(
                "Cannot resolve entity type for: " + repoInterface.getName()
        );
    }

    // =========================================================
    // 🔥 DERIVED QUERY PARSER
    // =========================================================
    private QueryMeta parseDerivedQuery(Method method) {

        String name = method.getName();

        if (!name.startsWith("findBy")) {
            throw new RuntimeException("Unsupported method: " + name);
        }

        String criteria = name.substring("findBy".length());

        // split by And / Or
        List<QueryField> fields = new ArrayList<>();

        String[] parts = criteria.split("And");

        for (String part : parts) {

            QueryOperator operator = QueryOperator.EQUALS;

            if (part.endsWith("GreaterThan")) {
                operator = QueryOperator.GREATER_THAN;
                part = part.replace("GreaterThan", "");
            } else if (part.endsWith("LessThan")) {
                operator = QueryOperator.LESS_THAN;
                part = part.replace("LessThan", "");
            } else if (part.endsWith("Like")) {
                operator = QueryOperator.LIKE;
                part = part.replace("Like", "");
            }

            String fieldName = lowerFirst(part);

            fields.add(new QueryField(fieldName, operator));
        }

        return new QueryMeta(fields);
    }

    private String lowerFirst(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    // =========================================================
    // 🔥 RETURN TYPE HANDLING
    // =========================================================
    private Object wrapOptional(Method method, Object result) {

        if (method.getReturnType().equals(Optional.class)) {
            return Optional.ofNullable(result);
        }

        return result;
    }

    private Object adaptReturnType(Method method, Object result) {

        Class<?> returnType = method.getReturnType();

        if (returnType.equals(List.class)) {
            return result;
        }

        if (returnType.equals(Optional.class)) {
            if (result instanceof List && !((List<?>) result).isEmpty()) {
                return Optional.of(((List<?>) result).get(0));
            }
            return Optional.empty();
        }

        // single object
        if (result instanceof List) {
            return ((List<?>) result).isEmpty() ? null : ((List<?>) result).get(0);
        }

        return result;
    }

    private Class<?> resolveReturnType(Method method) {

        if (method.getReturnType().equals(List.class)) {
            return entityClass;
        }

        return method.getReturnType();
    }

    // =========================================================
    // 🔥 SUPPORT CLASSES
    // =========================================================
    static class QueryMeta {

        private final List<QueryField> fields;

        public QueryMeta(List<QueryField> fields) {
            this.fields = fields;
        }

        public List<QueryField> getFields() {
            return fields;
        }
    }

    static class QueryField {

        private final String name;
        private final QueryOperator operator;

        public QueryField(String name, QueryOperator operator) {
            this.name = name;
            this.operator = operator;
        }

        public String getName() {
            return name;
        }

        public QueryOperator getOperator() {
            return operator;
        }
    }

    enum QueryOperator {
        EQUALS,
        GREATER_THAN,
        LESS_THAN,
        LIKE
    }
}