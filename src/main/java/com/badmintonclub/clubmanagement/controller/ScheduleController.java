package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.service.RegistrationService;
import com.badmintonclub.clubmanagement.service.ScheduleService;
import com.badmintonclub.clubmanagement.service.UserService;
import com.badmintonclub.clubmanagement.entity.enums.AttendanceStatus;
import com.badmintonclub.clubmanagement.service.GuestPaymentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ScheduleController {

        @Autowired
        private ScheduleService scheduleService;

        @Autowired
        private RegistrationService registrationService;

        @Autowired
        private UserService userService;

        @Autowired
        private GuestPaymentService guestPaymentService;

        @InitBinder
        public void initBinder(WebDataBinder binder) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
                        @Override
                        public void setAsText(String text) {
                                if (text == null || text.trim().isEmpty()) {
                                        setValue(null);
                                        return;
                                }

                                setValue(LocalDateTime.parse(text.trim(), formatter));
                        }

                        @Override
                        public String getAsText() {
                                LocalDateTime value = (LocalDateTime) getValue();

                                if (value == null) {
                                        return "";
                                }

                                return value.format(formatter);
                        }
                });
        }

        @GetMapping("/schedules")
        public String listSchedules(
                        @RequestParam(defaultValue = "today") String filter,
                        Model model,
                        Principal principal) {

                List<Schedule> schedules;
                String pageTitle;

                if ("upcoming".equals(filter)) {
                        schedules = scheduleService.getUpcomingSchedules();
                        pageTitle = "Danh sách lịch đánh sắp tới";
                } else if ("all".equals(filter)) {
                        schedules = scheduleService.getAllSchedules();
                        pageTitle = "Tất cả lịch đánh";
                } else {
                        filter = "today";
                        schedules = scheduleService.getTodaySchedules();
                        pageTitle = "Danh sách lịch đánh hôm nay";
                }

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
                model.addAttribute("filter", filter);
                model.addAttribute("pageTitle", pageTitle);

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

                return "redirect:/schedules?filter=today";
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

                if (schedule == null) {
                        return "redirect:/schedules";
                }

                var registrations = registrationService.getRegistrationsBySchedule(schedule);

                var guestPayments = guestPaymentService.getGuestPaymentsBySchedule(schedule);

                int memberCount = registrations.size();
                int guestCount = guestPayments.size();
                int totalCount = memberCount + guestCount;

                model.addAttribute("schedule", schedule);
                model.addAttribute("registrations", registrations);
                model.addAttribute("guestPayments", guestPayments);

                model.addAttribute("memberCount", memberCount);
                model.addAttribute("guestCount", guestCount);
                model.addAttribute("totalCount", totalCount);

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

        // điểm danh
        @PostMapping("/schedules/registrations/{id}/attendance")
        public String markRegistrationAttendance(
                        @PathVariable Long id,
                        @RequestParam AttendanceStatus attendanceStatus,
                        @RequestParam Long scheduleId) {

                registrationService.markAttendance(id, attendanceStatus);

                return "redirect:/schedules/participants/" + scheduleId;
        }

        @PostMapping("/schedules/guest-payments/{id}/attendance")
        public String markGuestAttendance(
                        @PathVariable Long id,
                        @RequestParam AttendanceStatus attendanceStatus,
                        @RequestParam Long scheduleId) {

                guestPaymentService.markAttendance(id, attendanceStatus);

                return "redirect:/schedules/participants/" + scheduleId;
        }
}