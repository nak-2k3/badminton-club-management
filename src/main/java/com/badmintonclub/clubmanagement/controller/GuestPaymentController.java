package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.GuestPayment;
import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.service.FeeSettingService;
import com.badmintonclub.clubmanagement.service.GuestPaymentService;
import com.badmintonclub.clubmanagement.service.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GuestPaymentController {

    @Autowired
    private GuestPaymentService guestPaymentService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private FeeSettingService feeSettingService;

    @GetMapping("/guest-payments")
    public String listGuestPayments(Model model) {

        model.addAttribute("guestPayments", guestPaymentService.getAllGuestPayments());

        model.addAttribute("totalGuestAmount", guestPaymentService.getTotalGuestAmount());
        model.addAttribute("paidGuestAmount", guestPaymentService.getPaidGuestAmount());
        model.addAttribute("unpaidGuestAmount", guestPaymentService.getUnpaidGuestAmount());

        return "guest-payments/list";
    }

    @GetMapping("/guest-payments/create")
    public String showCreateForm(Model model) {

        GuestPayment guestPayment = new GuestPayment();

        guestPayment.setAmount(
                feeSettingService.getCurrentSetting().getGuestSessionFee());

        model.addAttribute("guestPayment", guestPayment);
        model.addAttribute("schedules", scheduleService.getAllSchedules());

        return "guest-payments/create";
    }

    @PostMapping("/guest-payments/save")
    public String saveGuestPayment(
            @RequestParam Long scheduleId,
            @ModelAttribute GuestPayment guestPayment) {

        Schedule schedule = scheduleService.getScheduleById(scheduleId);

        guestPayment.setSchedule(schedule);

        guestPaymentService.saveGuestPayment(guestPayment);

        return "redirect:/guest-payments";
    }

    @GetMapping("/guest-payments/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        GuestPayment guestPayment = guestPaymentService.getGuestPaymentById(id);

        if (guestPayment == null) {
            return "redirect:/guest-payments";
        }

        model.addAttribute("guestPayment", guestPayment);
        model.addAttribute("schedules", scheduleService.getAllSchedules());

        return "guest-payments/edit";
    }

    @PostMapping("/guest-payments/update")
    public String updateGuestPayment(
            @RequestParam Long id,
            @RequestParam Long scheduleId,
            @ModelAttribute GuestPayment formGuestPayment) {

        GuestPayment guestPayment = guestPaymentService.getGuestPaymentById(id);

        if (guestPayment == null) {
            return "redirect:/guest-payments";
        }

        Schedule schedule = scheduleService.getScheduleById(scheduleId);

        guestPayment.setSchedule(schedule);
        guestPayment.setGuestName(formGuestPayment.getGuestName());
        guestPayment.setPhone(formGuestPayment.getPhone());
        guestPayment.setAmount(formGuestPayment.getAmount());
        guestPayment.setNote(formGuestPayment.getNote());

        guestPaymentService.saveGuestPayment(guestPayment);

        return "redirect:/guest-payments";
    }

    @PostMapping("/guest-payments/paid/{id}")
    public String markAsPaid(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Tiền mặt") String paymentMethod) {

        guestPaymentService.markAsPaid(id, paymentMethod);

        return "redirect:/guest-payments";
    }

    @PostMapping("/guest-payments/unpaid/{id}")
    public String markAsUnpaid(@PathVariable Long id) {

        guestPaymentService.markAsUnpaid(id);

        return "redirect:/guest-payments";
    }

    @GetMapping("/guest-payments/delete/{id}")
    public String deleteGuestPayment(@PathVariable Long id) {

        guestPaymentService.deleteGuestPayment(id);

        return "redirect:/guest-payments";
    }
}