package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.enums.ScheduleStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

        // Tìm 5 lịch đánh gần nhất
        List<Schedule> findTop5ByOrderByPlayTimeDesc();

        // Lịch đánh trong một khoảng thời gian
        List<Schedule> findByPlayTimeBetweenOrderByPlayTimeAsc(
                        LocalDateTime startDateTime,
                        LocalDateTime endDateTime);

        // Lịch sắp tới, từ một thời điểm trở đi
        List<Schedule> findByPlayTimeGreaterThanEqualOrderByPlayTimeAsc(
                        LocalDateTime startDateTime);

        // Tất cả lịch, sắp xếp lịch mới nhất lên trước
        List<Schedule> findAllByOrderByPlayTimeDesc();

        // kiểm tra play time và tên sân
        boolean existsByCourtNameIgnoreCaseAndPlayTimeAndStatusNot(
                        String courtName,
                        LocalDateTime playTime,
                        ScheduleStatus status);

        boolean existsByCourtNameIgnoreCaseAndPlayTimeAndStatusNotAndIdNot(
                        String courtName,
                        LocalDateTime playTime,
                        ScheduleStatus status,
                        Long id);
}