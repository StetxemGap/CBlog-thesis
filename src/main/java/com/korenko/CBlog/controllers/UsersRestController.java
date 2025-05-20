package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.service.MyUserDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    @GetMapping
//    public ResponseEntity<List<UsersDto>> getAllUsers() {
//        return ResponseEntity.ok(myUserDetailService.getAllUsers());
//    }
}
