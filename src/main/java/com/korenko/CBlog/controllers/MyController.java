package com.korenko.CBlog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalTime;

@Controller
public class MyController {

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

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/activation")
    public String activation() {
        return "activation";
    }
}
