package com.korenko.CBlog.controllers;

import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.service.MyUserDetailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users-info")
public class UsersRestController {
    private MyUserDetailService myUserDetailService;

    public UsersRestController(MyUserDetailService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }

    @PostMapping
    public UsersInfo createUser(@RequestBody UsersInfo usersInfo) {
        return myUserDetailService.saveUsersInfo(usersInfo);
    }
}
