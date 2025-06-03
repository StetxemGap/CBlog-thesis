package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.PasswordRequests;
import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersContact;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.repo.PasswordRequestsRepo;
import com.korenko.CBlog.repo.UserContactRepo;
import com.korenko.CBlog.repo.UserRepo;
import com.korenko.CBlog.service.EmailService;
import com.korenko.CBlog.service.MessageService;
import com.korenko.CBlog.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class UserController {

    @Lazy
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    MyUserDetailService userDetailService;

    @Autowired
    MessageService messageService;

    @Autowired
    PasswordRequestsRepo passwordRequestsRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    EmailService emailService;

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

    @MessageMapping("/requestUserExist")
    @SendToUser("/queue/userExist")
    public Boolean userExist(@Payload String username) {
        Users user = userDetailService.findByUsername(username);
        if (user != null) {
            UsersContact usersContact = user.getUsersContact();
            PasswordRequests passwordRequests = new PasswordRequests();
            passwordRequests.setUsername(username);
            passwordRequests.setEmail(usersContact.getEmail());
            passwordRequestsRepo.save(passwordRequests);
            return true;
        } else {
            return false;
        }
    }

    @MessageMapping("/requestEmailExist")
    @SendToUser("/queue/emailExist")
    public Boolean emailExist(@Payload String email) {
        UsersContact user = userDetailService.findByEmail(email);
        if (user != null) {
            Users users = user.getUser();
            PasswordRequests passwordRequests = new PasswordRequests();
            passwordRequests.setUsername(users.getUsername());
            passwordRequests.setEmail(email);
            passwordRequestsRepo.save(passwordRequests);
            return true;
        } else {
            return false;
        }
    }

    @MessageMapping("/changePassword")
    public void changePassword(@Payload Map<String, Object> userInfo) {
        Integer id = (Integer) userInfo.get("id");
        String password = (String) userInfo.get("password");
        PasswordRequests passwordRequests = passwordRequestsRepo.findById(id).orElse(new PasswordRequests());

        Users user = userRepo.findByUsername(passwordRequests.getUsername());
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);
        passwordRequestsRepo.deleteById(id);

        emailService.sendPasswordRecoveryEmail(passwordRequests.getEmail(), password);
    }

    @MessageMapping("/cancelRequest")
    public void cancelRequest(@Payload Integer id) {
        PasswordRequests passwordRequests = passwordRequestsRepo.findById(id).orElse(new PasswordRequests());
        emailService.sendCancelRequest(passwordRequests.getEmail());
        passwordRequestsRepo.deleteById(id);
    }
}
