package com.tirana.smartparking.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/")
@RequestMapping
public class UserController {

    @GetMapping("/hello/")
    public String welcome() {
        return "Welcome!";
    }

    @PostMapping("/create/")
    public String createUser() {
        return "User created!";
    }

}
