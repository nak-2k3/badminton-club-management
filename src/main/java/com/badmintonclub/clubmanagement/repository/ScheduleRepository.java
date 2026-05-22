package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository
        extends JpaRepository<Schedule, Long> {
}