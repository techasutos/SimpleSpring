package com.asu.example.entity;

import com.asu.data.annotation.Entity;
import com.asu.data.annotation.Id;
import com.asu.data.annotation.Column;

@Entity
public class User {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    public User() {}

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}