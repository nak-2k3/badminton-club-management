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

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        // truyền data sang html
        model.addAttribute("users", userService.getAllUsers());

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
            // kích hoạt vadition
            @Valid @ModelAttribute("user") User user,
            // chứa lỗi
            BindingResult result) {
        if (result.hasErrors()) { // kiểm tra lỗi
            if (user.getId() != null) {
                return "users/edit";
            }
            return "users/create";
        }
        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        User user = userService.getUserById(id);

        model.addAttribute("user", user);

        return "users/edit";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return "redirect:/users";
    }
}