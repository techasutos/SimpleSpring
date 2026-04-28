package com.asu.example;

import com.asu.context.ApplicationContext;
import com.asu.example.service.UserService;

public class AppRunner {

    public static void main(String[] args) {

        ApplicationContext context =
                new ApplicationContext("com.asu");

        UserService service = context.getBean(UserService.class);

        // =============================
        // FLOW TEST
        // =============================
        service.createUser();

        service.getUser(1L);

        service.updateUser(1L);

        service.deleteUser(1L);
    }
}