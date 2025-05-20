package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.UserPrincipal;
import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.repo.UserRepo;
import com.korenko.CBlog.service.ActivationService;
import com.korenko.CBlog.service.MyUserDetailService;
import com.korenko.CBlog.service.UsersOnline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MyController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UsersOnline usersOnline;

    @GetMapping("/login")
    public String auth(Model model) {
        String helloString = "Здравствуйте!";

        LocalTime now = LocalTime.now();
        LocalTime morning = LocalTime.of(5, 0);
        LocalTime day = LocalTime.of(12, 0);
        LocalTime evening = LocalTime.of(18, 0);
        LocalTime night = LocalTime.of(23, 0);

        if (now.isAfter(morning) && now.isBefore(day)) {
            helloString = "Доброго утра!";
        } else if (now.isAfter(day) && now.isBefore(evening)) {
            helloString = "Доброго дня!";
        } else if (now.isAfter(evening) && now.isBefore(night)) {
            helloString = "Доброго вечера!";
        } else if (now.isAfter(night) && now.isBefore(morning)) {
            helloString = "Доброй ночи!";
        }

        model.addAttribute("today", helloString);
        return "login";
    }

    @Autowired
    private MyUserDetailService userDetailService;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepo.findByUsername(username);
        model.addAttribute("username", user.getUsername());

        UsersDto profile = userDetailService.getUserProfile(principal.getName());
        model.addAttribute("profile", profile);
        return "profile";
    }

    @Autowired
    private ActivationService activationService;

    @GetMapping("/activation")
    public String activationPage(Model model, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String username = userPrincipal.getUsername();

        Users user = userRepo.findByUsername(username);

        model.addAttribute("userId", user.getId());
        model.addAttribute("usersInfo", new UsersInfo());
        return "activation";
    }

    @PostMapping("/activation")
    public String processActivation(
            @RequestParam Integer userId,
            @ModelAttribute UsersInfo usersInfo) {

        activationService.activateUser(userId, usersInfo);
        return "redirect:/profile";
    }

    @GetMapping("/chat")
    public String usersOverview(Model model, Principal principal) {
        List<Users> usersAll = userRepo.findByActivationTrue();
        List<UsersDto> usersNamesAll = usersAll.stream()
                .map(user -> new UsersDto(
                        user.getUsername(),
                        user.getUsersInfo().getFirstName(),
                        user.getUsersInfo().getLastName()
                ))
                .collect(Collectors.toList());
        List<String> usersOnlineAll = usersOnline.getUsersOnline();
        model.addAttribute("usersAll", usersNamesAll);
        model.addAttribute("usersOnline", usersOnlineAll);

        Users user = userRepo.findByUsername(principal.getName());
        model.addAttribute("currentUser", user);
        return "chat";
    }
}
