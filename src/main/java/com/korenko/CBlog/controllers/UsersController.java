package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.repo.UserRepo;
import com.korenko.CBlog.service.MyUserDetailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users-info")
public class UsersController {
    private final UserRepo userRepo;
    private MyUserDetailService myUserDetailService;

    public UsersController(MyUserDetailService myUserDetailService, UserRepo userRepo) {
        this.myUserDetailService = myUserDetailService;
        this.userRepo = userRepo;
    }

    @GetMapping("/activation")
    public String showForm(Model model) {
        model.addAttribute("userInfo", new UsersDto());
        return "users-info-form";
    }

    @PostMapping
    public String submitForm(@ModelAttribute UsersDto userInfoDto) {
        Users user = new Users();
        user.setUsername(userInfoDto.getUsername());

        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setFirstName(userInfoDto.getFirstName());
        usersInfo.setLastName(userInfoDto.getLastName());
        usersInfo.setPosition(userInfoDto.getPosition());
        usersInfo.setDoB(userInfoDto.getDoB());
        usersInfo.setGender(userInfoDto.getGender());
        usersInfo.setCity(userInfoDto.getCity());
        usersInfo.setStreet(userInfoDto.getStreet());
        usersInfo.setOfficeFloor(userInfoDto.getOfficeFloor());
        usersInfo.setOfficeNumber(userInfoDto.getOfficeNumber());

        usersInfo.setUser(user);
        user.setUsersInfo(usersInfo);

        myUserDetailService.saveUsersInfo(usersInfo);
        return "redirect:/profile";
    }

}
