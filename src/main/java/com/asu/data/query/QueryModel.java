package com.asu.data.query;

import java.util.Arrays;

public class QueryModel {

    private final String[] fields;
    private final String[] operators;
    private final String[] logicals;

    private int limit = -1;
    private int offset = -1;

    public QueryModel(String[] fields,
                      String[] operators,
                      String[] logicals) {

        this.fields = fields;
        this.operators = operators;
        this.logicals = logicals;
    }

    // =========================
    // GETTERS
    // =========================
    public String[] getFields() {
        return fields;
    }

    public String[] getOperators() {
        return operators;
    }

    public String[] getLogicals() {
        return logicals;
    }

    // =========================
    // Pagination (future ready)
    // =========================
    public QueryModel limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryModel offset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    // =========================
    // Debug
    // =========================
    @Override
    public String toString() {
        return "QueryModel{" +
                "fields=" + Arrays.toString(fields) +
                ", operators=" + Arrays.toString(operators) +
                ", logicals=" + Arrays.toString(logicals) +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}