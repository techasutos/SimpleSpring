package com.asu.data.fetch;

public class JoinFetchEngine {

    public String buildSelect(Class<?> entity, EntityGraph graph) {

        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append(entity.getSimpleName().toLowerCase());

        for (String field : graph.eagerFields) {
            sql.append(" LEFT JOIN ")
                    .append(field)
                    .append(" ON ... ");
        }

        return sql.toString();
    }
}
