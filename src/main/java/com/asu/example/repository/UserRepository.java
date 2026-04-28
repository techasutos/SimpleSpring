package com.asu.example.repository;

import com.asu.data.annotation.Repository;
import com.asu.example.entity.User;

import java.util.List;

@Repository
public interface UserRepository {

    User findById(Long id);

    List<User> findByName(String name);

    List<User> findAll();

    User save(User user);

    void deleteById(Long id);
}