package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.*;
import com.badmintonclub.clubmanagement.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    public boolean register(
            User user,
            Schedule schedule) {

        // khóa lịch
        if (schedule.getLocked()) {
            return false;
        }

        // full slot
        int currentPlayers = registrationRepository.countBySchedule(schedule);

        if (currentPlayers >= schedule.getMaxPlayers()) {
            return false;
        }

        // đăng ký trùng
        boolean alreadyRegistered = registrationRepository
                .existsByUserAndSchedule(
                        user,
                        schedule);

        if (alreadyRegistered) {
            return false;
        }

        Registration registration = new Registration();

        registration.setUser(user);

        registration.setSchedule(schedule);

        registrationRepository.save(registration);

        return true;
    }

    public int countParticipants(
            Schedule schedule) {

        return registrationRepository
                .countBySchedule(schedule);
    }

    public List<Registration> getRegistrationsBySchedule(
            Schedule schedule) {

        return registrationRepository
                .findBySchedule(schedule);
    }
}