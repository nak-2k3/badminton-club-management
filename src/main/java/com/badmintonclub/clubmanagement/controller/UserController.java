package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.Level;
import com.badmintonclub.clubmanagement.entity.enums.Role;
import com.badmintonclub.clubmanagement.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private static final int PAGE_SIZE = 5;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "") String level,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        if (page < 0) {
            page = 0;
        }

        keyword = keyword != null ? keyword.trim() : "";

        Role selectedRole = parseRole(role);
        Level selectedLevel = parseLevel(level);
        Boolean selectedEnabled = parseStatus(status);

        if (selectedRole == null) {
            role = "";
        }

        if (selectedLevel == null) {
            level = "";
        }

        if (selectedEnabled == null) {
            status = "";
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        Page<User> userPage = userService.searchUsers(
                keyword,
                selectedRole,
                selectedLevel,
                selectedEnabled,
                pageable);

        model.addAttribute("userPage", userPage);
        model.addAttribute("users", userPage.getContent());

        model.addAttribute("totalUsers", userPage.getTotalElements());
        model.addAttribute("totalAllUsers", userService.countUsers());
        model.addAttribute("activeUsers", userService.countActiveUsers());
        model.addAttribute("lockedUsers", userService.countLockedUsers());

        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("level", level);
        model.addAttribute("status", status);

        model.addAttribute("currentPage", userPage.getNumber());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("size", PAGE_SIZE);

        return "users/list";
    }

    @GetMapping("/users/create")
    public String showCreateForm(Model model) {

        model.addAttribute("user", new User());

        return "users/create";
    }

    @PostMapping("/users/save")
    public String saveUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            @RequestParam(value = "newPassword", required = false) String newPassword) {
        if (result.hasErrors()) {
            if (user.getId() != null) {
                return "users/edit";
            }

            return "users/create";
        }

        userService.saveUser(user, newPassword);

        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        User user = userService.getUserById(id);

        if (user == null) {
            return "redirect:/users";
        }

        model.addAttribute("user", user);

        return "users/edit";
    }

    @GetMapping("/users/lock/{id}")
    public String lockUser(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "") String level,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page) {
        userService.lockUser(id);

        return buildRedirect(keyword, role, level, status, page);
    }

    @GetMapping("/users/unlock/{id}")
    public String unlockUser(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "") String level,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page) {
        userService.unlockUser(id);

        return buildRedirect(keyword, role, level, status, page);
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }

        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Level parseLevel(String level) {
        if (level == null || level.isBlank()) {
            return null;
        }

        try {
            return Level.valueOf(level);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Boolean parseStatus(String status) {
        if ("ACTIVE".equals(status)) {
            return true;
        }

        if ("LOCKED".equals(status)) {
            return false;
        }

        return null;
    }

    private String buildRedirect(
            String keyword,
            String role,
            String level,
            String status,
            int page) {

        if (page < 0) {
            page = 0;
        }

        StringBuilder redirect = new StringBuilder("redirect:/users?page=");
        redirect.append(page);

        if (keyword != null && !keyword.isBlank()) {
            redirect.append("&keyword=").append(keyword);
        }

        if (role != null && !role.isBlank()) {
            redirect.append("&role=").append(role);
        }

        if (level != null && !level.isBlank()) {
            redirect.append("&level=").append(level);
        }

        if (status != null && !status.isBlank()) {
            redirect.append("&status=").append(status);
        }

        return redirect.toString();
    }
}