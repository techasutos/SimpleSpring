package com.asu.data.fetch;

import java.util.HashSet;
import java.util.Set;

public class EntityGraph {

    Set<String> eagerFields = new HashSet<>();

    public void addEager(String field) {
        eagerFields.add(field);
    }

    public boolean isEager(String field) {
        return eagerFields.contains(field);
    }
}