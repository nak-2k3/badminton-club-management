package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.FeeSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeSettingRepository extends JpaRepository<FeeSetting, Long> {
    // lấy cấu hình phí mới nhất
    FeeSetting findTopByOrderByIdDesc();
}