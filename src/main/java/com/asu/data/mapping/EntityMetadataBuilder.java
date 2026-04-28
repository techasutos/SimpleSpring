package com.asu.data.mapping;

import com.asu.data.annotation.Column;
import com.asu.data.annotation.Entity;
import com.asu.data.annotation.Id;

import java.lang.reflect.Field;

public class EntityMetadataBuilder {

    public static EntityMetadata build(Class<?> clazz) {

        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new RuntimeException("Not an entity: " + clazz);
        }

        EntityMetadata meta = new EntityMetadata();

        Entity entity = clazz.getAnnotation(Entity.class);

        meta.setTableName(
                entity.name().isEmpty()
                        ? clazz.getSimpleName().toLowerCase()
                        : entity.name()
        );

        for (Field field : clazz.getDeclaredFields()) {

            if (field.isAnnotationPresent(Id.class)) {
                meta.setIdField(field);
            }

            if (field.isAnnotationPresent(Column.class)) {
                Column col = field.getAnnotation(Column.class);
                meta.getColumns().put(
                        col.name().isEmpty() ? field.getName() : col.name(),
                        field
                );
            } else {
                meta.getColumns().put(field.getName(), field);
            }

            field.setAccessible(true);
        }

        return meta;
    }
}