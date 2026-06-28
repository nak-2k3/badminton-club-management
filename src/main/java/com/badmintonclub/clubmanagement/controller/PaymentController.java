package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.FeeSetting;
import com.badmintonclub.clubmanagement.entity.Payment;
import com.badmintonclub.clubmanagement.entity.PaymentBatch;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;
import com.badmintonclub.clubmanagement.service.FeeSettingService;
import com.badmintonclub.clubmanagement.service.PaymentBatchService;
import com.badmintonclub.clubmanagement.service.PaymentService;
import com.badmintonclub.clubmanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class PaymentController {

    private static final int PAGE_SIZE = 5;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentBatchService paymentBatchService;

    @Autowired
    private FeeSettingService feeSettingService;

    @Autowired
    private UserService userService;

    @GetMapping("/payments")
    public String listPaymentBatches(
            @RequestParam(defaultValue = "") String batchType,
            @RequestParam(defaultValue = "") String monthKey,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        if (page < 0) {
            page = 0;
        }

        batchType = normalizeBatchType(batchType);
        keyword = keyword != null ? keyword.trim() : "";

        String month = convertMonthKeyToMonth(monthKey);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        Page<PaymentBatch> batchPage = paymentBatchService.searchBatches(
                batchType,
                month,
                keyword,
                pageable);

        model.addAttribute("batchPage", batchPage);
        model.addAttribute("batches", batchPage.getContent());

        // Cho phép Thymeleaf gọi các hàm thống kê trong paymentService
        model.addAttribute("paymentService", paymentService);

        model.addAttribute("batchType", batchType);
        model.addAttribute("monthKey", monthKey);
        model.addAttribute("keyword", keyword);

        model.addAttribute("currentPage", batchPage.getNumber());
        model.addAttribute("totalPages", batchPage.getTotalPages());
        model.addAttribute("totalItems", batchPage.getTotalElements());
        model.addAttribute("size", PAGE_SIZE);

        return "payments/list";
    }

    @GetMapping("/payments/create-monthly")
    public String showCreateMonthlyForm(Model model) {

        PaymentBatch batch = new PaymentBatch();

        FeeSetting setting = feeSettingService.getCurrentSetting();

        model.addAttribute("batch", batch);
        model.addAttribute("setting", setting);

        return "payments/create-monthly";
    }

    @PostMapping("/payments/create-monthly")
    public String createMonthlyPayment(
            @ModelAttribute PaymentBatch batch,
            @RequestParam String monthKey) {
        String month = convertMonthKeyToMonth(monthKey);

        if (paymentBatchService.existsMonthlyBatchByMonth(month)) {
            return "redirect:/payments/create-monthly?duplicate";
        }

        batch.setMonth(month);
        batch.setBatchType("MONTHLY");

        PaymentBatch savedBatch = paymentBatchService.saveBatch(batch);

        FeeSetting setting = feeSettingService.getCurrentSetting();

        paymentService.createMonthlyPaymentsByGender(
                savedBatch,
                setting);

        return "redirect:/payments/detail/" + savedBatch.getId();
    }

    @GetMapping("/payments/detail/{id}")
    public String paymentBatchDetail(
            @PathVariable Long id,
            Model model) {

        PaymentBatch batch = paymentBatchService.getBatchById(id);

        if (batch == null) {
            return "redirect:/payments";
        }

        List<Payment> payments = paymentService.getPaymentsByBatch(batch);

        model.addAttribute("batch", batch);
        model.addAttribute("payments", payments);

        model.addAttribute("totalAmount", paymentService.getTotalAmountByBatch(batch));
        model.addAttribute("paidAmount", paymentService.getPaidAmountByBatch(batch));
        model.addAttribute("unpaidAmount", paymentService.getUnpaidAmountByBatch(batch));

        model.addAttribute("totalCount", paymentService.countByBatch(batch));
        model.addAttribute("paidCount", paymentService.countPaidByBatch(batch));
        model.addAttribute("unpaidCount", paymentService.countUnpaidByBatch(batch));
        model.addAttribute("progressPercent", paymentService.getProgressPercent(batch));

        return "payments/detail";
    }

    @PostMapping("/payments/paid/{id}")
    public String markAsPaid(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Tiền mặt") String paymentMethod) {

        Payment payment = paymentService.getPaymentById(id);

        if (payment == null || payment.getBatch() == null) {
            return "redirect:/payments";
        }

        Long batchId = payment.getBatch().getId();

        paymentService.markAsPaid(id, paymentMethod);

        return "redirect:/payments/detail/" + batchId;
    }

    @PostMapping("/payments/unpaid/{id}")
    public String markAsUnpaid(
            @PathVariable Long id) {

        Payment payment = paymentService.getPaymentById(id);

        if (payment == null || payment.getBatch() == null) {
            return "redirect:/payments";
        }

        Long batchId = payment.getBatch().getId();

        paymentService.markAsUnpaid(id);

        return "redirect:/payments/detail/" + batchId;
    }

    @GetMapping("/payments/edit/{id}")
    public String showEditPaymentForm(
            @PathVariable Long id,
            Model model) {

        Payment payment = paymentService.getPaymentById(id);

        if (payment == null) {
            return "redirect:/payments";
        }

        model.addAttribute("payment", payment);

        return "payments/edit";
    }

    @PostMapping("/payments/update")
    public String updatePayment(
            @RequestParam Long id,
            @RequestParam Integer amount,
            @RequestParam PaymentStatus status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String note) {

        Payment payment = paymentService.getPaymentById(id);

        if (payment == null || payment.getBatch() == null) {
            return "redirect:/payments";
        }

        Long batchId = payment.getBatch().getId();

        payment.setAmount(amount);
        payment.setNote(note);
        payment.setStatus(status);

        if (status == PaymentStatus.PAID) {
            if (payment.getPaidDate() == null) {
                payment.setPaidDate(LocalDate.now());
            }

            if (paymentMethod == null || paymentMethod.isBlank()) {
                payment.setPaymentMethod("Tiền mặt");
            } else {
                payment.setPaymentMethod(paymentMethod);
            }
        } else {
            payment.setPaidDate(null);
            payment.setPaymentMethod(null);
        }

        paymentService.savePayment(payment);

        return "redirect:/payments/detail/" + batchId;
    }

    @GetMapping("/payments/create-event")
    public String showCreateEventForm(Model model) {

        PaymentBatch batch = new PaymentBatch();

        model.addAttribute("batch", batch);
        model.addAttribute("users", userService.getActiveUsers());

        return "payments/create-event";
    }

    @PostMapping("/payments/create-event")
    public String createEventPayment(
            @ModelAttribute PaymentBatch batch,
            @RequestParam String monthKey,
            @RequestParam Integer amount,
            @RequestParam String applyType,
            @RequestParam(required = false) List<Long> userIds) {

        String month = convertMonthKeyToMonth(monthKey);

        if (paymentBatchService.existsByTitleAndMonth(
                batch.getTitle(),
                month)) {
            return "redirect:/payments/create-event?duplicate";
        }

        if ("SELECTED".equals(applyType) && (userIds == null || userIds.isEmpty())) {
            return "redirect:/payments/create-event?noUser";
        }

        batch.setMonth(month);
        batch.setBatchType("EVENT");

        PaymentBatch savedBatch = paymentBatchService.saveBatch(batch);

        if ("ALL".equals(applyType)) {
            paymentService.createEventPayments(savedBatch, amount, null);
        } else {
            paymentService.createEventPayments(savedBatch, amount, userIds);
        }

        return "redirect:/payments/detail/" + savedBatch.getId();
    }

    @GetMapping("/payments/detail/{id}/add-members")
    public String showAddMembersToEventForm(
            @PathVariable Long id,
            Model model) {

        PaymentBatch batch = paymentBatchService.getBatchById(id);

        if (batch == null) {
            return "redirect:/payments";
        }

        if (!"EVENT".equals(batch.getBatchType())) {
            return "redirect:/payments/detail/" + id;
        }

        List<Long> existingUserIds = paymentService.getUserIdsByBatch(batch);

        List<User> availableUsers = userService.getActiveUsers()
                .stream()
                .filter(user -> !existingUserIds.contains(user.getId()))
                .toList();

        model.addAttribute("batch", batch);
        model.addAttribute("users", availableUsers);

        return "payments/add-members";
    }

    @PostMapping("/payments/detail/{id}/add-members")
    public String addMembersToEventPayment(
            @PathVariable Long id,
            @RequestParam Integer amount,
            @RequestParam(required = false) List<Long> userIds) {

        PaymentBatch batch = paymentBatchService.getBatchById(id);

        if (batch == null) {
            return "redirect:/payments";
        }

        if (!"EVENT".equals(batch.getBatchType())) {
            return "redirect:/payments/detail/" + id;
        }

        if (userIds == null || userIds.isEmpty()) {
            return "redirect:/payments/detail/" + id + "/add-members?noUser";
        }

        paymentService.addUsersToEventPayment(batch, amount, userIds);

        return "redirect:/payments/detail/" + id;
    }

    @PostMapping("/payments/remove/{id}")
    public String removePaymentFromEvent(
            @PathVariable Long id) {

        Payment payment = paymentService.getPaymentById(id);

        if (payment == null || payment.getBatch() == null) {
            return "redirect:/payments";
        }

        Long batchId = payment.getBatch().getId();

        if (!paymentService.canRemovePaymentFromEvent(payment)) {
            return "redirect:/payments/detail/" + batchId + "?cannotRemove";
        }

        paymentService.deletePayment(id);

        return "redirect:/payments/detail/" + batchId;
    }

    private String normalizeBatchType(String batchType) {
        if ("MONTHLY".equals(batchType) || "EVENT".equals(batchType)) {
            return batchType;
        }

        return "";
    }

    private String convertMonthKeyToMonth(String monthKey) {
        if (monthKey == null || monthKey.isBlank()) {
            return "";
        }

        // input type="month" hoặc Flatpickr monthSelect trả về dạng yyyy-MM
        // ví dụ: 2026-06
        String[] parts = monthKey.split("-");

        if (parts.length == 2) {
            String year = parts[0];
            String month = parts[1];

            return month + "/" + year;
        }

        return monthKey;
    }
}