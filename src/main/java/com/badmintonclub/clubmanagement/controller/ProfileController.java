package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.service.RegistrationService;
import com.badmintonclub.clubmanagement.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        User user = userService.findByEmail(principal.getName());

        int totalJoined = registrationService.countByUser(user);
        long presentCount = registrationService.countPresentByUser(user);
        long absentCount = registrationService.countAbsentByUser(user);
        long notMarkedCount = registrationService.countNotMarkedByUser(user);
        int attendanceRate = registrationService.calculateAttendanceRate(user);

        model.addAttribute("user", user);

        model.addAttribute("totalJoined", totalJoined);
        model.addAttribute("presentCount", presentCount);
        model.addAttribute("absentCount", absentCount);
        model.addAttribute("notMarkedCount", notMarkedCount);
        model.addAttribute("attendanceRate", attendanceRate);

        model.addAttribute("myRegistrations", registrationService.getRegistrationsByUser(user));

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());

        model.addAttribute("user", user);

        return "profile-edit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("user") User formUser,
            BindingResult result,
            Principal principal) {
        if (result.hasErrors()) {
            return "profile-edit";
        }

        User currentUser = userService.findByEmail(principal.getName());

        userService.updateProfile(currentUser.getId(), formUser);

        return "redirect:/profile";
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm() {
        return "change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            Model model) {
        User user = userService.findByEmail(principal.getName());

        boolean success = userService.changePassword(
                user.getId(),
                currentPassword,
                newPassword,
                confirmPassword);

        if (!success) {
            model.addAttribute("error", "Đổi mật khẩu thất bại. Vui lòng kiểm tra lại thông tin.");
            return "change-password";
        }

        model.addAttribute("success", "Đổi mật khẩu thành công.");

        return "change-password";
    }
}