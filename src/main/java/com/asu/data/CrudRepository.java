package com.asu.data;

import java.util.List;

public interface CrudRepository<T, ID> {

    T save(T entity);

    T findById(ID id);

    List<T> findAll();

    void deleteById(ID id);
}