package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        User user = userService.findByEmail(principal.getName());

        model.addAttribute("user", user);

        return "profile";
    }
}