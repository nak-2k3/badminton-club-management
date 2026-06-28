package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.GuestPayment;
import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GuestPaymentRepository extends JpaRepository<GuestPayment, Long> {

        List<GuestPayment> findAllByOrderByIdDesc();

        List<GuestPayment> findBySchedule(Schedule schedule);

        int countBySchedule(Schedule schedule);

        long countByStatus(PaymentStatus status);

        Page<GuestPayment> findByScheduleOrderByIdDesc(Schedule schedule, Pageable pageable);

        @Query(value = """
                         SELECT g
                         FROM GuestPayment g
                         LEFT JOIN g.schedule s
                         ORDER BY s.playTime DESC, g.id DESC
                        """, countQuery = """
                         SELECT COUNT(g)
                         FROM GuestPayment g
                        """)
        Page<GuestPayment> findAllForPage(Pageable pageable);

        @Query(value = """
                         SELECT g
                         FROM GuestPayment g
                         WHERE g.schedule.playTime >= :startDateTime
                         AND g.schedule.playTime < :endDateTime
                         ORDER BY g.schedule.playTime DESC, g.id DESC
                        """, countQuery = """
                         SELECT COUNT(g)
                         FROM GuestPayment g
                         WHERE g.schedule.playTime >= :startDateTime
                         AND g.schedule.playTime < :endDateTime
                        """)
        Page<GuestPayment> findPageByScheduleTimeBetween(
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime,
                        Pageable pageable);

        @Query("SELECT COALESCE(SUM(g.amount), 0) FROM GuestPayment g")
        Long sumAllAmount();

        @Query("SELECT COALESCE(SUM(g.amount), 0) FROM GuestPayment g WHERE g.status = :status")
        Long sumAmountByStatus(@Param("status") PaymentStatus status);

        @Query("SELECT COALESCE(SUM(g.amount), 0) FROM GuestPayment g WHERE g.schedule = :schedule")
        Long sumAmountBySchedule(@Param("schedule") Schedule schedule);

        @Query("""
                         SELECT COALESCE(SUM(g.amount), 0)
                         FROM GuestPayment g
                         WHERE g.schedule = :schedule
                         AND g.status = :status
                        """)
        Long sumAmountByScheduleAndStatus(
                        @Param("schedule") Schedule schedule,
                        @Param("status") PaymentStatus status);

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