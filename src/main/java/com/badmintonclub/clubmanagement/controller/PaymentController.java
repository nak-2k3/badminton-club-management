package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Payment;
import com.badmintonclub.clubmanagement.service.PaymentService;
import com.badmintonclub.clubmanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @GetMapping("/payments")
    public String listPayments(Model model) {

        model.addAttribute(
                "payments",
                paymentService.getAllPayments());

        return "payments/list";
    }

    @GetMapping("/payments/create")
    public String showCreateForm(Model model) {

        model.addAttribute("payment", new Payment());

        model.addAttribute("users", userService.getActiveUsers());

        return "payments/create";
    }

    @PostMapping("/payments/save")
    public String savePayment(
            @ModelAttribute Payment payment) {

        paymentService.savePayment(payment);

        return "redirect:/payments";
    }

    @GetMapping("/payments/paid/{id}")
    public String markAsPaid(
            @PathVariable Long id) {

        paymentService.markAsPaid(id);

        return "redirect:/payments";
    }

    @GetMapping("/payments/unpaid/{id}")
    public String markAsUnpaid(
            @PathVariable Long id) {

        paymentService.markAsUnpaid(id);

        return "redirect:/payments";
    }

    @GetMapping("/payments/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Payment payment = paymentService.getPaymentById(id);

        if (payment == null) {
            return "redirect:/payments";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("users", userService.getActiveUsers());

        return "payments/edit";
    }
}