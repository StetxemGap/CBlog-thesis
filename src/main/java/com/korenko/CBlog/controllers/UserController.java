package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.*;
import com.korenko.CBlog.repo.*;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

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
    private UserInfoRepo userInfoRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    HRRequestsRepo hrRequestsRepo;

    @MessageMapping("/addNewUser")
    public void addNewUser(@Payload Map<String, Object> userInfo) {
        String username = (String) userInfo.get("username");
        String password = (String) userInfo.get("password");
        String email = (String) userInfo.get("email");
        String firstname = (String) userInfo.get("firstname");
        String lastname = (String) userInfo.get("lastname");
        String position = (String) userInfo.get("position");
        String hiringDateString = (String) userInfo.get("hiringDate");
        Boolean admin = (Boolean) userInfo.get("admin");

        LocalDate hiringDate = LocalDate.parse(hiringDateString, DateTimeFormatter.ISO_LOCAL_DATE);

        userDetailService.saveUserWithInfo(username, password, email, firstname, lastname, position, hiringDate, admin);
    }

    @MessageMapping("/deleteUser")
    public void deleteUser(@Payload String username) {
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

    @MessageMapping("/createNewUserRequest")
    @SendToUser("/queue/actionRequest")
    public String createNewUserRequest(@Payload Map<String, Object> userInfo) {
        String firstname = (String) userInfo.get("firstName");
        String lastname = (String) userInfo.get("lastName");
        String position = (String) userInfo.get("position");
        String hiringDateString = (String) userInfo.get("hiringDate");
        LocalDate hiringDate = LocalDate.parse(hiringDateString, DateTimeFormatter.ISO_LOCAL_DATE);

        Boolean isExist = userDetailService.existsByFourParameters(firstname, lastname, position, hiringDate);
        if (isExist) {
            return "Данный сотрудник уже существует";
        } else {
            HRRequests request = new HRRequests();
            request.setFirstname(firstname);
            request.setLastname(lastname);
            request.setPosition(position);
            request.setHiringDate(hiringDate);
            request.setIsDelete(false);
            hrRequestsRepo.save(request);
            return "Запрос успешно создан";
        }
    }

    @MessageMapping("/deleteUserRequest")
    @SendToUser("/queue/actionRequest")
    public String createDeleteUserRequest(@Payload Map<String, Object> userInfo) {
        String firstname = (String) userInfo.get("firstName");
        String lastname = (String) userInfo.get("lastName");
        String position = (String) userInfo.get("position");
        String hiringDateString = (String) userInfo.get("hiringDate");
        LocalDate hiringDate = LocalDate.parse(hiringDateString, DateTimeFormatter.ISO_LOCAL_DATE);

        Boolean isExist = userDetailService.existsByFourParameters(firstname, lastname, position, hiringDate);
        if (isExist) {
            HRRequests request = new HRRequests();
            request.setFirstname(firstname);
            request.setLastname(lastname);
            request.setPosition(position);
            request.setHiringDate(hiringDate);
            request.setIsDelete(true);
            hrRequestsRepo.save(request);
            return "Запрос успешно создан";
        } else {
            return "Такого сотрудника не существует";
        }
    }

    @MessageMapping("/cancelHRRequest")
    public void cancelHRRequest(@Payload Integer id) {
        hrRequestsRepo.deleteById(id);
    }

    @MessageMapping("/acceptDeleteRequest")
    public void deleteHRRequest(@Payload Map<String, Object> userInfo) {
        String firstname = (String) userInfo.get("firstname");
        String lastname = (String) userInfo.get("lastname");
        String position = (String) userInfo.get("position");
        String hiringDateString = (String) userInfo.get("hiringDate");
        LocalDate hiringDate = LocalDate.parse(hiringDateString, DateTimeFormatter.ISO_LOCAL_DATE);

        Optional<UsersInfo> user = userInfoRepo.findUserByFourParameters(firstname, lastname, position, hiringDate);

        if (user.isPresent()) {
            Users deletedUser = user.get().getUser();
            userDetailService.deleteUserById(deletedUser.getUsername());
        }
    }
}
