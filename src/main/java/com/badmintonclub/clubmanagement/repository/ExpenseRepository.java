package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.Expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByOrderByExpenseDateDesc();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    Long sumAllAmount();

    // chi thu theo tháng
    List<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(
            LocalDate startDate,
            LocalDate endDate);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    Long sumAmountBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}