package com.asu.example.controller;

import com.asu.annotations.Controller;
import com.asu.annotations.Autowired;
import com.asu.web.annotations.RequestMapping;
import com.asu.web.annotations.ResponseBody;
import com.asu.example.service.UserService;
import com.asu.example.entity.User;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/create")
    @ResponseBody
    public String create() {
        userService.createUser();
        return "User created";
    }

    @RequestMapping("/get")
    @ResponseBody
    public User get() {
        return userService.getUser(1L);
    }

    @RequestMapping("/update")
    @ResponseBody
    public String update() {
        userService.updateUser(1L);
        return "Updated";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String delete() {
        userService.deleteUser(1L);
        return "Deleted";
    }
}