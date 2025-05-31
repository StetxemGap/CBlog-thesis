package com.korenko.CBlog.controllers;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class UserController {

    @Autowired
    MyUserDetailService userDetailService;

    @MessageMapping("/addNewUser")
    public void addNewUser(@Payload Map<String, Object> userInfo) {
        String username = (String) userInfo.get("username");
        String password = (String) userInfo.get("password");
        String firstname = (String) userInfo.get("firstname");
        String lastname = (String) userInfo.get("lastname");
        String position = (String) userInfo.get("position");
        Boolean admin = (Boolean) userInfo.get("admin");

        userDetailService.saveUserWithInfo(username, password, firstname, lastname, position, admin);
    }
}
