package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Expense;
import com.badmintonclub.clubmanagement.service.ExpenseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@Controller
public class ExpenseController {

    private static final int PAGE_SIZE = 5;

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/expenses")
    public String listExpenses(
            @RequestParam(defaultValue = "") String monthKey,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String paymentMethod,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        if (page < 0) {
            page = 0;
        }

        keyword = keyword != null ? keyword.trim() : "";
        paymentMethod = normalizePaymentMethod(paymentMethod);

        LocalDate startDate = null;
        LocalDate endDate = null;

        YearMonth selectedMonth = parseMonthKey(monthKey);

        if (selectedMonth != null) {
            startDate = selectedMonth.atDay(1);
            endDate = selectedMonth.atEndOfMonth();
        } else {
            monthKey = "";
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        Page<Expense> expensePage = expenseService.searchExpenses(
                startDate,
                endDate,
                keyword,
                paymentMethod,
                pageable);

        Long totalExpenseAmount = expenseService.getFilteredExpenseAmount(
                startDate,
                endDate,
                keyword,
                paymentMethod);

        model.addAttribute("expensePage", expensePage);
        model.addAttribute("expenses", expensePage.getContent());

        model.addAttribute("totalExpenseAmount", safeLong(totalExpenseAmount));

        model.addAttribute("monthKey", monthKey);
        model.addAttribute("keyword", keyword);
        model.addAttribute("paymentMethod", paymentMethod);

        model.addAttribute("currentPage", expensePage.getNumber());
        model.addAttribute("totalPages", expensePage.getTotalPages());
        model.addAttribute("totalItems", expensePage.getTotalElements());
        model.addAttribute("size", PAGE_SIZE);

        return "expenses/list";
    }

    @GetMapping("/expenses/create")
    public String showCreateForm(Model model) {

        model.addAttribute("expense", new Expense());

        return "expenses/create";
    }

    @PostMapping("/expenses/save")
    public String saveExpense(@ModelAttribute Expense expense) {

        expenseService.saveExpense(expense);

        return "redirect:/expenses";
    }

    @GetMapping("/expenses/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Expense expense = expenseService.getExpenseById(id);

        if (expense == null) {
            return "redirect:/expenses";
        }

        model.addAttribute("expense", expense);

        return "expenses/edit";
    }

    @GetMapping("/expenses/delete/{id}")
    public String deleteExpense(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String monthKey,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String paymentMethod,
            @RequestParam(defaultValue = "0") int page) {

        expenseService.deleteExpense(id);

        return buildRedirect(monthKey, keyword, paymentMethod, page);
    }

    private YearMonth parseMonthKey(String monthKey) {
        if (monthKey == null || monthKey.isBlank()) {
            return null;
        }

        try {
            return YearMonth.parse(monthKey);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String normalizePaymentMethod(String paymentMethod) {
        if ("Tiền mặt".equals(paymentMethod) || "Chuyển khoản".equals(paymentMethod)) {
            return paymentMethod;
        }

        return "";
    }

    private String buildRedirect(
            String monthKey,
            String keyword,
            String paymentMethod,
            int page) {

        if (page < 0) {
            page = 0;
        }

        StringBuilder redirect = new StringBuilder("redirect:/expenses?page=");
        redirect.append(page);

        if (monthKey != null && !monthKey.isBlank()) {
            redirect.append("&monthKey=").append(monthKey);
        }

        if (keyword != null && !keyword.isBlank()) {
            redirect.append("&keyword=").append(keyword);
        }

        if (paymentMethod != null && !paymentMethod.isBlank()) {
            redirect.append("&paymentMethod=").append(paymentMethod);
        }

        return redirect.toString();
    }

    private Long safeLong(Long value) {
        return value != null ? value : 0L;
    }
}