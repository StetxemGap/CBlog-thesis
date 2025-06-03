package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.service.MessageService;
import com.korenko.CBlog.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class UserController {

    @Autowired
    MyUserDetailService userDetailService;

    @Autowired
    MessageService messageService;

    @MessageMapping("/addNewUser")
    public void addNewUser(@Payload Map<String, Object> userInfo) {
        String username = (String) userInfo.get("username");
        String password = (String) userInfo.get("password");
        String email = (String) userInfo.get("email");
        String firstname = (String) userInfo.get("firstname");
        String lastname = (String) userInfo.get("lastname");
        String position = (String) userInfo.get("position");
        Boolean admin = (Boolean) userInfo.get("admin");

        userDetailService.saveUserWithInfo(username, password, email, firstname, lastname, position, admin);
    }

    @MessageMapping("/deleteUser")
    public void deleteUser(@Payload String username) {
        userDetailService.deleteUserInfoById(username);
        userDetailService.deleteUserById(username);
        messageService.deleteAllMessagesByUser(username);
    }
}
