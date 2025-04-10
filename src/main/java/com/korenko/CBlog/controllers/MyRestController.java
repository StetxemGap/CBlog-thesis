package com.korenko.CBlog.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {
    @GetMapping("/")
    public String helloWorld() {
        return "Hello World 123";
    }
}
