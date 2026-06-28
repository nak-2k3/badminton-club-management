package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.Registration;
import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.AttendanceStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

        int countBySchedule(Schedule schedule);

        List<Registration> findByUser(User user);

        @Query("""
                         SELECT r
                         FROM Registration r
                         WHERE r.user = :user
                         ORDER BY r.schedule.playTime DESC
                        """)
        List<Registration> findByUserOrderBySchedulePlayTimeDesc(@Param("user") User user);

        int countByUser(User user);

        long countByUserAndAttendanceStatus(User user, AttendanceStatus attendanceStatus);

        @Query("""
                         SELECT COUNT(r)
                         FROM Registration r
                         WHERE r.user = :user
                         AND (r.attendanceStatus IS NULL OR r.attendanceStatus = :status)
                        """)
        long countNotMarkedOrNullByUser(
                        @Param("user") User user,
                        @Param("status") AttendanceStatus status);

        Registration findByUserAndSchedule(User user, Schedule schedule);

        boolean existsByUserAndSchedule(User user, Schedule schedule);

        List<Registration> findBySchedule(Schedule schedule);
}