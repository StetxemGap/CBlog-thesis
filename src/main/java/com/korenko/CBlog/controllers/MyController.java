package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.UserPrincipal;
import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.service.ActivationService;
import com.korenko.CBlog.service.MessageService;
import com.korenko.CBlog.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class MyController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/login")
    public String auth(Model model) {
        String helloString = "Здравствуйте!";

        LocalTime now = LocalTime.now();
        LocalTime morning = LocalTime.of(5, 0);
        LocalTime day = LocalTime.of(12, 0);
        LocalTime evening = LocalTime.of(18, 0);
        LocalTime night = LocalTime.of(0, 0);

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
        Users user = userDetailService.findByUsername(username);
        model.addAttribute("currentUser", user);

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
        Users user = userDetailService.findByUsername(username);
        UsersInfo usersInfo = user.getUsersInfo() != null ? user.getUsersInfo() : new UsersInfo();

        model.addAttribute("userId", user.getId());
        model.addAttribute("usersInfo", usersInfo);
        return "activation";
    }

    @PostMapping("/activation")
    public String processActivation(
            @RequestParam Integer userId,
            @ModelAttribute UsersInfo usersInfo,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile) {

        if (usersInfo.getGender() != null) {
            switch (usersInfo.getGender()) {
                case "male":
                    usersInfo.setGender("Мужской");
                    break;
                case "female":
                    usersInfo.setGender("Женский");
                    break;
                case "none":
                    usersInfo.setGender(null);
                    break;
            }
        }

        activationService.activateUser(userId, usersInfo, photoFile);
        return "redirect:/profile";
    }

    @GetMapping("/chat")
    public String chat(@CookieValue(name = "currentChatUser", required = false) String selectedUsername, Model model, Principal principal) {
        Users currentUser = userDetailService.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);

        // получаем никнеймы тех, с кем есть диалог
        List<String> participantUsernames = messageService.findChatParticipants(currentUser.getUsername());

        // получаем их данные
        List<UsersDto> chatParticipants = participantUsernames.stream()
                .map(username -> userDetailService.findByUsername(username))
                .filter(Objects::nonNull)
                .map(user -> new UsersDto(user))
                .collect(Collectors.toList());

        model.addAttribute("usersAll", chatParticipants);

        if (selectedUsername == null || selectedUsername.isEmpty()) {
            UsersDto profile = userDetailService.getUserProfile(currentUser.getUsername());
            model.addAttribute("selectedUser", profile);
        } else {
            UsersDto profile = userDetailService.getUserProfile(selectedUsername);
            model.addAttribute("selectedUser", profile);
        }

        return "chat";
    }

    @GetMapping("/allUsers")
    public String allUsers(Model model, Principal principal) {
        List<Users> usersAll = userDetailService.findByActivationTrue();
        List<UsersDto> usersNamesAll = usersAll.stream()
                .map(user -> new UsersDto(user))
                .collect(Collectors.toList());
        model.addAttribute("usersAll", usersNamesAll);

        List<String> allPositions = userDetailService.findAllPositions();
        List<String> allCities = userDetailService.findAllCities();
        model.addAttribute("positions", allPositions);
        model.addAttribute("cities", allCities);

        Users user = userDetailService.findByUsername(principal.getName());
        model.addAttribute("currentUser", user);
        return "allUsers";
    }

    @GetMapping("/profile/{username}")
    public String viewUserProfile(@PathVariable String username, Model model, Principal principal) {
        Users currentUser = userDetailService.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);

        UsersDto profile = userDetailService.getUserProfile(username);
        model.addAttribute("profile", profile);

        return "profile";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        Users currenUser = userDetailService.findByUsername(principal.getName());
        model.addAttribute("currentUser", currenUser);

        List<UsersInfo> usersAll = userDetailService.findAllUsersInfo();

        List<UsersDto> users = usersAll.stream()
                .map(user -> new UsersDto(user))
                .collect(Collectors.toList());

        List<UsersDto> sortedUsers = users.stream()
                .sorted(Comparator.comparing(UsersDto::getId))  // Сортировка по возрастанию (ASC)
                .collect(Collectors.toList());
        model.addAttribute("usersAll", sortedUsers);
        ;


        return "admin";
    }
}
