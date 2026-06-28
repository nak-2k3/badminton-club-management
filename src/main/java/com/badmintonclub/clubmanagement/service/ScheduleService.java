package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.enums.ScheduleStatus;
import com.badmintonclub.clubmanagement.repository.ScheduleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAllByOrderByPlayTimeDesc();
    }

    public List<Schedule> getTodaySchedules() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        return scheduleRepository.findByPlayTimeBetweenOrderByPlayTimeAsc(
                startOfDay,
                endOfDay);
    }

    public List<Schedule> getUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();

        return scheduleRepository.findByPlayTimeGreaterThanEqualOrderByPlayTimeAsc(
                now);
    }

    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public long countSchedules() {
        return scheduleRepository.count();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public void cancelSchedule(Long id) {
        Schedule schedule = getScheduleById(id);

        if (schedule != null) {
            schedule.setStatus(ScheduleStatus.CANCELLED);
            scheduleRepository.save(schedule);
        }
    }

    public void lockSchedule(Long id) {
        Schedule schedule = getScheduleById(id);

        if (schedule != null) {
            schedule.setStatus(ScheduleStatus.LOCKED);
            scheduleRepository.save(schedule);
        }
    }

    public void openSchedule(Long id) {
        Schedule schedule = getScheduleById(id);

        if (schedule != null) {
            schedule.setStatus(ScheduleStatus.OPEN);
            scheduleRepository.save(schedule);
        }
    }

    // Tìm 5 lịch đánh gần nhất
    public List<Schedule> getLatestSchedules() {
        return scheduleRepository.findTop5ByOrderByPlayTimeDesc();
    }
}