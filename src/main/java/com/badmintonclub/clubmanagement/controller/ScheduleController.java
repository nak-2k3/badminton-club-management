package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.service.ScheduleService;
import com.badmintonclub.clubmanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.service.RegistrationService;

import java.security.Principal;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @GetMapping("/schedules")
    public String listSchedules(Model model) {
        var schedules = scheduleService.getAllSchedules();
        for (Schedule schedule : schedules) {
            int currentPlayers = registrationService
                    .countParticipants(schedule);

            schedule.setCurrentPlayers(
                    currentPlayers);
        }
        model.addAttribute(
                "schedules",
                schedules);
        return "schedules/list";
    }

    @GetMapping("/schedules/create")
    public String showCreateForm(Model model) {

        model.addAttribute("schedule", new Schedule());

        return "schedules/create";
    }

    @PostMapping("/schedules/save")
    public String saveSchedule(
            @ModelAttribute Schedule schedule) {

        scheduleService.saveSchedule(schedule);

        return "redirect:/schedules";
    }

    @GetMapping("/schedules/register/{id}")
    public String registerSchedule(
            @PathVariable Long id,
            Principal principal) {

        Schedule schedule = scheduleService.getScheduleById(id);

        User user = userService.findByEmail(
                principal.getName());

        registrationService.register(user, schedule);

        return "redirect:/schedules";
    }

    @GetMapping("/schedules/participants/{id}")
    public String participants(
            @PathVariable Long id,
            Model model) {

        Schedule schedule = scheduleService.getScheduleById(id);

        var registrations = registrationService
                .getRegistrationsBySchedule(
                        schedule);

        model.addAttribute(
                "schedule",
                schedule);

        model.addAttribute(
                "registrations",
                registrations);

        return "schedules/participants";
    }
}