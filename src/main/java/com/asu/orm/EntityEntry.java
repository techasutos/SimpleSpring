package com.asu.orm;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityEntry {

    private Object entity;
    private EntityState state;

    // 🔥 snapshot for dirty checking
    private Map<String, Object> snapshot = new HashMap<>();

    public EntityEntry(Object entity, EntityState state) {
        this.entity = entity;
        this.state = state;
        this.snapshot = takeSnapshot(entity);
    }

    public Object getEntity() {
        return entity;
    }

    public EntityState getState() {
        return state;
    }

    public void setState(EntityState state) {
        this.state = state;
    }

    public Map<String, Object> getSnapshot() {
        return snapshot;
    }

    public void refreshSnapshot() {
        this.snapshot = takeSnapshot(entity);
    }

    // 🔥 capture field values
    private Map<String, Object> takeSnapshot(Object entity) {

        Map<String, Object> snap = new HashMap<>();

        try {
            for (Field f : entity.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                snap.put(f.getName(), f.get(entity));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return snap;
    }
}