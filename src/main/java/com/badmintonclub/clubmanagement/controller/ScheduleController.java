package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.service.RegistrationService;
import com.badmintonclub.clubmanagement.service.ScheduleService;
import com.badmintonclub.clubmanagement.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ScheduleController {

        @Autowired
        private ScheduleService scheduleService;

        @Autowired
        private RegistrationService registrationService;

        @Autowired
        private UserService userService;

        @GetMapping("/schedules")
        public String listSchedules(Model model, Principal principal) {

                var schedules = scheduleService.getAllSchedules();

                User currentUser = null;

                if (principal != null) {
                        currentUser = userService.findByEmail(principal.getName());
                }

                Map<Long, Integer> memberCountMap = new HashMap<>();
                Map<Long, Integer> guestCountMap = new HashMap<>();

                for (Schedule schedule : schedules) {
                        int memberCount = registrationService.countMemberParticipants(schedule);
                        int guestCount = registrationService.countGuestParticipants(schedule);
                        int totalPlayers = memberCount + guestCount;

                        schedule.setCurrentPlayers(totalPlayers);

                        memberCountMap.put(schedule.getId(), memberCount);
                        guestCountMap.put(schedule.getId(), guestCount);

                        if (currentUser != null) {
                                boolean registered = registrationService.isRegistered(
                                                currentUser,
                                                schedule);

                                schedule.setRegistered(registered);
                        }
                }

                model.addAttribute("schedules", schedules);
                model.addAttribute("memberCountMap", memberCountMap);
                model.addAttribute("guestCountMap", guestCountMap);

                return "schedules/list";
        }

        @GetMapping("/schedules/create")
        public String showCreateForm(Model model) {

                model.addAttribute("schedule", new Schedule());

                return "schedules/create";
        }

        @PostMapping("/schedules/save")
        public String saveSchedule(
                        @Valid @ModelAttribute("schedule") Schedule schedule,
                        BindingResult result) {

                if (result.hasErrors()) {
                        if (schedule.getId() != null) {
                                return "schedules/edit";
                        }

                        return "schedules/create";
                }

                scheduleService.saveSchedule(schedule);

                return "redirect:/schedules";
        }

        @GetMapping("/schedules/register/{id}")
        public String registerSchedule(
                        @PathVariable Long id,
                        Principal principal) {

                if (principal == null) {
                        return "redirect:/login";
                }

                Schedule schedule = scheduleService.getScheduleById(id);

                if (schedule == null) {
                        return "redirect:/schedules";
                }

                User user = userService.findByEmail(principal.getName());

                if (registrationService.isScheduleFull(schedule)) {
                        return "redirect:/schedules?full";
                }

                boolean success = registrationService.register(user, schedule);

                if (!success) {
                        return "redirect:/schedules?registerFailed";
                }

                return "redirect:/schedules";
        }

        @GetMapping("/schedules/participants/{id}")
        public String participants(
                        @PathVariable Long id,
                        Model model) {

                Schedule schedule = scheduleService.getScheduleById(id);

                var registrations = registrationService.getRegistrationsBySchedule(schedule);

                model.addAttribute("schedule", schedule);
                model.addAttribute("registrations", registrations);

                return "schedules/participants";
        }

        @GetMapping("/schedules/edit/{id}")
        public String showEditForm(
                        @PathVariable Long id,
                        Model model) {

                Schedule schedule = scheduleService.getScheduleById(id);

                if (schedule == null) {
                        return "redirect:/schedules";
                }

                model.addAttribute("schedule", schedule);

                return "schedules/edit";
        }

        @GetMapping("/schedules/cancel/{id}")
        public String cancelSchedule(@PathVariable Long id) {

                scheduleService.cancelSchedule(id);

                return "redirect:/schedules";
        }

        @GetMapping("/schedules/lock/{id}")
        public String lockSchedule(@PathVariable Long id) {

                scheduleService.lockSchedule(id);

                return "redirect:/schedules";
        }

        @GetMapping("/schedules/open/{id}")
        public String openSchedule(@PathVariable Long id) {

                scheduleService.openSchedule(id);

                return "redirect:/schedules";
        }

        @GetMapping("/schedules/cancel-registration/{id}")
        public String cancelRegistration(
                        @PathVariable Long id,
                        Principal principal) {

                if (principal == null) {
                        return "redirect:/login";
                }

                Schedule schedule = scheduleService.getScheduleById(id);
                User user = userService.findByEmail(principal.getName());

                registrationService.cancelRegistration(user, schedule);

                return "redirect:/schedules";
        }
}