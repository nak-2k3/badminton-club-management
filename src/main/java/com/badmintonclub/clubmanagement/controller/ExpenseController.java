package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Expense;
import com.badmintonclub.clubmanagement.service.ExpenseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/expenses")
    public String listExpenses(Model model) {

        model.addAttribute("expenses", expenseService.getAllExpenses());
        model.addAttribute("totalExpenseAmount", expenseService.getTotalExpenseAmount());

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
    public String deleteExpense(@PathVariable Long id) {

        expenseService.deleteExpense(id);

        return "redirect:/expenses";
    }
}