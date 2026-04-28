package com.asu.data.fetch;

public class EntityGraphBuilder {

    public static EntityGraph build(Class<?> entityClass) {

        EntityGraph graph = new EntityGraph();

        for (var field : entityClass.getDeclaredFields()) {

            Fetch fetch = field.getAnnotation(Fetch.class);

            if (fetch != null && fetch.value() == FetchType.EAGER) {
                graph.addEager(field.getName());
            }
        }

        return graph;
    }
}
