package com.asu.example;

import com.asu.annotations.Autowired;
import com.asu.annotations.Controller;
import com.asu.annotations.RequestMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/user")
    public String getUser() {
        return userService.getUser();
    }
}
