package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.GuestPayment;
import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GuestPaymentRepository extends JpaRepository<GuestPayment, Long> {

        List<GuestPayment> findAllByOrderByIdDesc();

        List<GuestPayment> findBySchedule(Schedule schedule);

        long countByStatus(PaymentStatus status);

        int countBySchedule(Schedule schedule);

        @Query("SELECT COALESCE(SUM(g.amount), 0) FROM GuestPayment g")
        Long sumAllAmount();

        @Query("SELECT COALESCE(SUM(g.amount), 0) FROM GuestPayment g WHERE g.status = :status")
        Long sumAmountByStatus(@Param("status") PaymentStatus status);

        @Query("""
                         SELECT COALESCE(SUM(g.amount), 0)
                         FROM GuestPayment g
                         WHERE g.schedule.playTime >= :startDateTime
                         AND g.schedule.playTime < :endDateTime
                        """)
        Long sumAmountByScheduleTimeBetween(
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime);

        @Query("""
                         SELECT COALESCE(SUM(g.amount), 0)
                         FROM GuestPayment g
                         WHERE g.schedule.playTime >= :startDateTime
                         AND g.schedule.playTime < :endDateTime
                         AND g.status = :status
                        """)
        Long sumAmountByScheduleTimeBetweenAndStatus(
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime,
                        @Param("status") PaymentStatus status);

        @Query("""
                         SELECT COUNT(g)
                         FROM GuestPayment g
                         WHERE g.schedule.playTime >= :startDateTime
                         AND g.schedule.playTime < :endDateTime
                         AND g.status = :status
                        """)
        long countByScheduleTimeBetweenAndStatus(
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime,
                        @Param("status") PaymentStatus status);

        @Query("""
                         SELECT g
                         FROM GuestPayment g
                         WHERE g.schedule.playTime >= :startDateTime
                         AND g.schedule.playTime < :endDateTime
                         ORDER BY g.schedule.playTime DESC
                        """)
        List<GuestPayment> findByScheduleTimeBetween(
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime);
}