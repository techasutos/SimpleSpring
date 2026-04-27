package com.asu.example;

import com.asu.annotations.Component;

@Component
public class UserService {

    public String getUser() {
        return "User from Service";
    }
}
