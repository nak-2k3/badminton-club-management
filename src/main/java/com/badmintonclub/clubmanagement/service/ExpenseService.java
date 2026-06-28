package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.Expense;
import com.badmintonclub.clubmanagement.repository.ExpenseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Expense> searchExpenses(
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            String paymentMethod,
            Pageable pageable) {
        return expenseRepository.searchExpenses(
                startDate,
                endDate,
                keyword,
                paymentMethod,
                pageable);
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

    public Long getFilteredExpenseAmount(
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            String paymentMethod) {
        return expenseRepository.sumFilteredAmount(
                startDate,
                endDate,
                keyword,
                paymentMethod);
    }

    // Đếm khoản chi
    public long countExpenses() {
        return expenseRepository.count();
    }

    // Thu chi theo tháng
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