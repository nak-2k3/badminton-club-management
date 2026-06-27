package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Payment;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;
import com.badmintonclub.clubmanagement.service.PaymentService;
import com.badmintonclub.clubmanagement.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class MyPaymentController {

    private final UserService userService;

    private final PaymentService paymentService;

    MyPaymentController(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @GetMapping("/my-payments")
    public String myPayments(Model model, Principal principal) {

        User user = userService.findByEmail(principal.getName());

        List<Payment> payments = paymentService.getPaymentsByUser(user);

        long totalAmount = payments.stream()
                .mapToLong(Payment::getAmount)
                .sum();

        long paidAmount = payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PAID)
                .mapToLong(Payment::getAmount)
                .sum();

        long unpaidAmount = totalAmount - paidAmount;

        model.addAttribute("payments", payments);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("paidAmount", paidAmount);
        model.addAttribute("unpaidAmount", unpaidAmount);

        return "payments/my-payments";
    }
}