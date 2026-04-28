package com.asu.orm;

import com.asu.data.annotation.Id;
import com.asu.data.jdbc.EntityExecutor;
import com.asu.orm.lazy.LazyProxyFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<Object, EntityEntry> context = new HashMap<>();
    private final EntityExecutor executor;

    public Session(EntityExecutor executor) {
        this.executor = executor;
    }

    // =========================================================
    // PERSIST (INSERT)
    // =========================================================
    public void persist(Object entity) {

        Object id = extractId(entity);

        if (id == null) {
            throw new RuntimeException("ID cannot be null for persist()");
        }

        context.put(id, new EntityEntry(entity, EntityState.NEW));
    }

    // =========================================================
    // FIND (CACHE → LAZY PROXY)
    // =========================================================
    public <T> T find(Class<T> type, Object id) {

        EntityEntry entry = context.get(id);

        if (entry != null) {
            return type.cast(entry.getEntity());
        }

        return type.cast(
                LazyProxyFactory.createProxy(type, id, this)
        );
    }

    // =========================================================
    // LOAD (REAL DB HIT)
    // =========================================================
    public <T> T load(Class<T> type, Object id) {

        T entity = executor.findById(type, id);

        if (entity != null) {
            context.put(id, new EntityEntry(entity, EntityState.MANAGED));
        }

        return entity;
    }

    // =========================================================
    // MERGE (UPDATE TRACKING)
    // =========================================================
    public void merge(Object entity) {

        Object id = extractId(entity);

        if (id == null) {
            throw new RuntimeException("Cannot merge entity without ID");
        }

        context.put(id, new EntityEntry(entity, EntityState.MANAGED));
    }

    // =========================================================
    // REMOVE (DELETE)
    // =========================================================
    public void remove(Object entity) {

        Object id = extractId(entity);

        if (id == null) {
            throw new RuntimeException("Cannot remove entity without ID");
        }

        context.put(id, new EntityEntry(entity, EntityState.REMOVED));
    }

    // =========================================================
    // FLUSH (FULL ORM ENGINE)
    // =========================================================
    public void flush() {

        for (Map.Entry<Object, EntityEntry> entry : context.entrySet()) {

            Object id = entry.getKey();
            EntityEntry e = entry.getValue();

            // 🟢 INSERT
            if (e.getState() == EntityState.NEW) {
                executor.save(e.getEntity());
                e.setState(EntityState.MANAGED);
                e.refreshSnapshot();
            }

            // 🔵 UPDATE (ONLY IF DIRTY)
            else if (e.getState() == EntityState.MANAGED) {

                if (isDirty(e)) {
                    executor.save(e.getEntity());
                    e.refreshSnapshot();
                }
            }

            // 🔴 DELETE
            else if (e.getState() == EntityState.REMOVED) {
                executor.deleteById(e.getEntity().getClass(), id);
            }
        }
    }

    // =========================================================
    // REAL DIRTY CHECKING (FIELD DIFF)
    // =========================================================
    private boolean isDirty(EntityEntry entry) {

        Object entity = entry.getEntity();
        Map<String, Object> oldSnap = entry.getSnapshot();

        try {
            for (Field f : entity.getClass().getDeclaredFields()) {

                f.setAccessible(true);

                Object current = f.get(entity);
                Object old = oldSnap.get(f.getName());

                if (!equals(current, old)) {
                    return true;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private boolean equals(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    // =========================================================
    // CLEAR
    // =========================================================
    public void clear() {
        context.clear();
    }

    // =========================================================
    // ID EXTRACTION (@Id)
    // =========================================================
    private Object extractId(Object entity) {

        for (Field field : entity.getClass().getDeclaredFields()) {

            if (field.isAnnotationPresent(Id.class)) {
                try {
                    field.setAccessible(true);
                    return field.get(entity);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        throw new RuntimeException(
                "No @Id field found in " + entity.getClass().getName()
        );
    }
}