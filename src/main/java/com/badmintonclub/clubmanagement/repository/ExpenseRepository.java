package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.Expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

        List<Expense> findAllByOrderByExpenseDateDesc();

        @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
        Long sumAllAmount();

        // Thu chi theo tháng
        List<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(
                        LocalDate startDate,
                        LocalDate endDate);

        @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
        Long sumAmountBetween(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query(value = """
                         SELECT e
                         FROM Expense e
                         WHERE (:startDate IS NULL OR e.expenseDate >= :startDate)
                         AND (:endDate IS NULL OR e.expenseDate <= :endDate)
                         AND (:keyword IS NULL OR :keyword = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                         AND (:paymentMethod IS NULL OR :paymentMethod = '' OR e.paymentMethod = :paymentMethod)
                         ORDER BY e.expenseDate DESC, e.id DESC
                        """, countQuery = """
                         SELECT COUNT(e)
                         FROM Expense e
                         WHERE (:startDate IS NULL OR e.expenseDate >= :startDate)
                         AND (:endDate IS NULL OR e.expenseDate <= :endDate)
                         AND (:keyword IS NULL OR :keyword = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                         AND (:paymentMethod IS NULL OR :paymentMethod = '' OR e.paymentMethod = :paymentMethod)
                        """)
        Page<Expense> searchExpenses(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("keyword") String keyword,
                        @Param("paymentMethod") String paymentMethod,
                        Pageable pageable);

        @Query("""
                         SELECT COALESCE(SUM(e.amount), 0)
                         FROM Expense e
                         WHERE (:startDate IS NULL OR e.expenseDate >= :startDate)
                         AND (:endDate IS NULL OR e.expenseDate <= :endDate)
                         AND (:keyword IS NULL OR :keyword = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                         AND (:paymentMethod IS NULL OR :paymentMethod = '' OR e.paymentMethod = :paymentMethod)
                        """)
        Long sumFilteredAmount(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("keyword") String keyword,
                        @Param("paymentMethod") String paymentMethod);
}