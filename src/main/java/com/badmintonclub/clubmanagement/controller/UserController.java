package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.badmintonclub.clubmanagement.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) {

        var users = userService.getAllUsers();

        model.addAttribute("users", users);
        model.addAttribute("totalUsers", users.size());

        return "users/list";
    }

    // mở form
    @GetMapping("/users/create")
    public String showCreateForm(Model model) {

        model.addAttribute("user", new User());

        return "users/create";
    }

    // lưu dữ liệu
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
    public String showEditForm(@PathVariable Long id, Model model) {

        User user = userService.getUserById(id);

        model.addAttribute("user", user);

        return "users/edit";
    }

    @GetMapping("/users/lock/{id}")
    public String lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return "redirect:/users";
    }

    @GetMapping("/users/unlock/{id}")
    public String unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return "redirect:/users";
    }
}