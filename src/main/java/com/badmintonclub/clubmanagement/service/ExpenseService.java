package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.Expense;
import com.badmintonclub.clubmanagement.repository.ExpenseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAllByOrderByExpenseDateDesc();
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public Long getTotalExpenseAmount() {
        return expenseRepository.sumAllAmount();
    }

    // đếm khoản chi
    public long countExpenses() {
        return expenseRepository.count();
    }

    // chi thu theo tháng
    public List<Expense> getExpensesBetween(
            LocalDate startDate,
            LocalDate endDate) {
        return expenseRepository.findByExpenseDateBetweenOrderByExpenseDateDesc(
                startDate,
                endDate);
    }

    public Long getTotalExpenseAmountBetween(
            LocalDate startDate,
            LocalDate endDate) {
        return expenseRepository.sumAmountBetween(startDate, endDate);
    }
}