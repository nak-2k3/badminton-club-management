package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository
                extends JpaRepository<Schedule, Long> {
        // tìm lịch đánh gần nhất
        List<Schedule> findTop5ByOrderByPlayTimeDesc();
}