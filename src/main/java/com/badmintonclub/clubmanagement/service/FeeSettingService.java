package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.FeeSetting;
import com.badmintonclub.clubmanagement.repository.FeeSettingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeeSettingService {

    @Autowired
    private FeeSettingRepository feeSettingRepository;

    // lấy cấu hình phí hiện tại. Nếu chưa có dữ liệu, hệ thống sẽ tự tạo mặc định
    public FeeSetting getCurrentSetting() {
        FeeSetting setting = feeSettingRepository.findTopByOrderByIdDesc();

        if (setting == null) {
            setting = new FeeSetting();

            setting.setMaleMonthlyFee(300000);
            setting.setFemaleMonthlyFee(200000);
            setting.setGuestSessionFee(50000);

            setting = feeSettingRepository.save(setting);
        }

        return setting;
    }

    public FeeSetting saveSetting(FeeSetting setting) {
        return feeSettingRepository.save(setting);
    }
}