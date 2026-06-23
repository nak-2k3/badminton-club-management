package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.FeeSetting;
import com.badmintonclub.clubmanagement.entity.Payment;
import com.badmintonclub.clubmanagement.entity.PaymentBatch;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;
import com.badmintonclub.clubmanagement.service.FeeSettingService;
import com.badmintonclub.clubmanagement.service.PaymentBatchService;
import com.badmintonclub.clubmanagement.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentBatchService paymentBatchService;

    @Autowired
    private FeeSettingService feeSettingService;

    @GetMapping("/payments")
    public String listPaymentBatches(Model model) {

        model.addAttribute("batches", paymentBatchService.getAllBatches());

        // Cho phép Thymeleaf gọi các hàm thống kê trong paymentService
        model.addAttribute("paymentService", paymentService);

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
            @ModelAttribute PaymentBatch batch) {

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
}