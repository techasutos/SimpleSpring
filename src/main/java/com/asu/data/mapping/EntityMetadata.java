package com.asu.data.mapping;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityMetadata {

    private String tableName;

    private Field idField;
    private String idColumn;

    private Map<String, Field> columns = new HashMap<>();

    // ========================
    // GETTERS
    // ========================
    public String getTableName() {
        return tableName;
    }

    public Field getIdField() {
        return idField;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public Map<String, Field> getColumns() {
        return columns;
    }

    // ========================
    // SETTERS
    // ========================
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setIdField(Field idField) {
        this.idField = idField;
    }

    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }
}