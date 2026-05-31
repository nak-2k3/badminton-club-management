package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.service.ScheduleService;
import com.badmintonclub.clubmanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.badmintonclub.clubmanagement.service.RegistrationService;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute(
                "totalUsers",
                userService.countUsers());

        model.addAttribute(
                "totalSchedules",
                scheduleService.countSchedules());
        model.addAttribute(
                "activeUsers",
                userService.countActiveUsers());
        model.addAttribute(
                "lockedUsers",
                userService.countLockedUsers());
        model.addAttribute(
                "totalRegistrations",
                registrationService.countRegistrations());
        model.addAttribute(
                "latestSchedules",
                scheduleService.getLatestSchedules());
        return "dashboard";
    }
}