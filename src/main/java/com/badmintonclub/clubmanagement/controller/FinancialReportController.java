package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.service.ExpenseService;
import com.badmintonclub.clubmanagement.service.PaymentBatchService;
import com.badmintonclub.clubmanagement.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
public class FinancialReportController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentBatchService paymentBatchService;

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/reports/finance")
    public String financialReport(
            @RequestParam(required = false) String monthKey,
            Model model) {
        YearMonth selectedMonth;

        if (monthKey == null || monthKey.isBlank()) {
            selectedMonth = YearMonth.now();
        } else {
            selectedMonth = YearMonth.parse(monthKey);
        }

        String month = selectedMonth.format(DateTimeFormatter.ofPattern("MM/yyyy"));

        LocalDate startDate = selectedMonth.atDay(1);
        LocalDate endDate = selectedMonth.atEndOfMonth();

        Long totalAmount = paymentService.getTotalAmountByMonth(month);
        Long paidAmount = paymentService.getPaidAmountByMonth(month);
        Long unpaidAmount = paymentService.getUnpaidAmountByMonth(month);
        Long expenseAmount = expenseService.getTotalExpenseAmountBetween(startDate, endDate);

        Long balance = paidAmount - expenseAmount;

        model.addAttribute("monthKey", selectedMonth.toString());
        model.addAttribute("month", month);

        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("paidAmount", paidAmount);
        model.addAttribute("unpaidAmount", unpaidAmount);
        model.addAttribute("expenseAmount", expenseAmount);
        model.addAttribute("balance", balance);

        model.addAttribute("paidCount", paymentService.countPaidByMonth(month));
        model.addAttribute("unpaidCount", paymentService.countUnpaidByMonth(month));

        model.addAttribute("batches", paymentBatchService.getBatchesByMonth(month));
        model.addAttribute("expenses", expenseService.getExpensesBetween(startDate, endDate));

        return "reports/finance";
    }
}