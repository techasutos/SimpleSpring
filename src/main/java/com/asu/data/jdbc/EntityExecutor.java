package com.asu.data.jdbc;

import com.asu.data.mapping.EntityMetadata;
import com.asu.data.mapping.EntityMetadataBuilder;
import com.asu.data.query.QueryModel;
import com.asu.tx.ConnectionContext;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityExecutor {

    // 🔥 Metadata cache (VERY IMPORTANT)
    private static final Map<Class<?>, EntityMetadata> CACHE = new ConcurrentHashMap<>();

    private EntityMetadata getMeta(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, EntityMetadataBuilder::build);
    }

    private Connection getConnection() {
        Connection conn = ConnectionContext.get();
        if (conn == null) {
            throw new RuntimeException("No active transaction");
        }
        return conn;
    }

    // =========================================================
    // FIND BY ID
    // =========================================================
    public <T> T findById(Class<T> type, Object id) {

        EntityMetadata meta = getMeta(type);

        String sql = "SELECT * FROM " + meta.getTableName()
                + " WHERE " + meta.getIdColumn() + " = ?";

        List<T> list = executeQuery(sql, new Object[]{id}, type);

        return list.isEmpty() ? null : list.get(0);
    }

    // =========================================================
    // FIND ALL
    // =========================================================
    public <T> List<T> findAll(Class<T> type) {

        EntityMetadata meta = getMeta(type);

        String sql = "SELECT * FROM " + meta.getTableName();

        return executeQuery(sql, null, type);
    }

    // =========================================================
    // SAVE (INSERT / UPDATE)
    // =========================================================
    public <T> T save(T entity) {

        EntityMetadata meta = getMeta(entity.getClass());

        Object id = get(meta.getIdField(), entity);

        if (id == null) {
            insert(entity, meta);
        } else {
            update(entity, meta);
        }

        return entity;
    }

    private void insert(Object entity, EntityMetadata meta) {

        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();

        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Field> e : meta.getColumns().entrySet()) {

            String col = e.getKey();
            Field field = e.getValue();

            cols.append(col).append(",");
            vals.append("?,");

            params.add(get(field, entity));
        }

        trim(cols);
        trim(vals);

        String sql = "INSERT INTO " + meta.getTableName()
                + " (" + cols + ") VALUES (" + vals + ")";

        try (PreparedStatement ps =
                     getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bind(ps, params.toArray());
            ps.executeUpdate();

            // 🔥 auto ID population
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                set(meta.getIdField(), entity, keys.getObject(1));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void update(Object entity, EntityMetadata meta) {

        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(meta.getTableName())
                .append(" SET ");

        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Field> e : meta.getColumns().entrySet()) {

            String col = e.getKey();
            Field field = e.getValue();

            if (col.equals(meta.getIdColumn())) continue;

            sql.append(col).append("=?,");
            params.add(get(field, entity));
        }

        trim(sql);

        sql.append(" WHERE ").append(meta.getIdColumn()).append("=?");

        params.add(get(meta.getIdField(), entity));

        executeUpdate(sql.toString(), params.toArray());
    }

    // =========================================================
    // DELETE
    // =========================================================
    public void delete(Object entity) {

        EntityMetadata meta = getMeta(entity.getClass());

        Object id = get(meta.getIdField(), entity);

        deleteById(entity.getClass(), id);
    }

    public void deleteById(Class<?> type, Object id) {

        EntityMetadata meta = getMeta(type);

        String sql = "DELETE FROM " + meta.getTableName()
                + " WHERE " + meta.getIdColumn() + "=?";

        executeUpdate(sql, new Object[]{id});
    }

    // =========================================================
    // 🔥 DERIVED QUERY ENGINE
    // =========================================================
    public Object executeDerivedQuery(Class<?> entity,
                                      Object queryMeta,
                                      Object[] args) {

        QueryModel model = (QueryModel) queryMeta;

        EntityMetadata meta = getMeta(entity);

        StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(meta.getTableName())
                .append(" WHERE ");

        for (int i = 0; i < model.getFields().length; i++) {

            sql.append(model.getFields()[i])
                    .append(" ")
                    .append(resolveOperator(model.getOperators()[i]))
                    .append(" ?");

            if (i < model.getFields().length - 1) {
                sql.append(" AND ");
            }
        }

        return executeQuery(sql.toString(), args, entity);
    }

    private String resolveOperator(String op) {
        switch (op) {
            case "GT": return ">";
            case "LT": return "<";
            case "LIKE": return "LIKE";
            default: return "=";
        }
    }

    // =========================================================
    // 🔥 NATIVE QUERY
    // =========================================================
    public Object executeNativeQuery(Class<?> entity,
                                     String sql,
                                     Object[] args,
                                     Class<?> returnType) {

        if (List.class.isAssignableFrom(returnType)) {
            return executeQuery(sql, args, entity);
        }

        List<?> list = executeQuery(sql, args, entity);
        return list.isEmpty() ? null : list.get(0);
    }

    // =========================================================
    // JDBC CORE
    // =========================================================
    private <T> List<T> executeQuery(String sql, Object[] args, Class<T> type) {

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            bind(ps, args);

            ResultSet rs = ps.executeQuery();

            List<T> result = new ArrayList<>();

            while (rs.next()) {
                result.add(map(rs, type));
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeUpdate(String sql, Object[] args) {

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            bind(ps, args);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void bind(PreparedStatement ps, Object[] args) throws SQLException {

        if (args == null) return;

        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
    }

    // =========================================================
    // ROW MAPPER
    // =========================================================
    private <T> T map(ResultSet rs, Class<T> type) {

        try {
            T obj = type.getDeclaredConstructor().newInstance();

            EntityMetadata meta = getMeta(type);

            for (Map.Entry<String, Field> e : meta.getColumns().entrySet()) {

                Object val = rs.getObject(e.getKey());

                set(e.getValue(), obj, val);
            }

            return obj;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // =========================================================
    // REFLECTION UTILS
    // =========================================================
    private Object get(Field f, Object obj) {
        try {
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void set(Field f, Object obj, Object val) {
        try {
            f.setAccessible(true);
            f.set(obj, val);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void trim(StringBuilder sb) {
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}