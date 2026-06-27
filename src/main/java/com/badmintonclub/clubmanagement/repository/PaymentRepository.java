package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.Payment;
import com.badmintonclub.clubmanagement.entity.PaymentBatch;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUser(User user);

    List<Payment> findByBatch(PaymentBatch batch);

    long countByStatus(PaymentStatus status);

    long countByBatch(PaymentBatch batch);

    long countByBatchAndStatus(PaymentBatch batch, PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.batch = :batch")
    Long sumAmountByBatch(@Param("batch") PaymentBatch batch);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.batch = :batch AND p.status = :status")
    Long sumAmountByBatchAndStatus(
            @Param("batch") PaymentBatch batch,
            @Param("status") PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p")
    Long sumAllAmount();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    Long sumAmountByStatus(@Param("status") PaymentStatus status);

    // báo cáo chi thu theo tháng
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.batch.month = :month")
    Long sumAmountByMonth(@Param("month") String month);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.batch.month = :month AND p.status = :status")
    Long sumAmountByMonthAndStatus(
            @Param("month") String month,
            @Param("status") PaymentStatus status);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.batch.month = :month AND p.status = :status")
    long countByMonthAndStatus(
            @Param("month") String month,
            @Param("status") PaymentStatus status);
}