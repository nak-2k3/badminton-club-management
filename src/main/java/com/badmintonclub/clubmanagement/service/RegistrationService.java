package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.Registration;
import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.ScheduleStatus;
import com.badmintonclub.clubmanagement.repository.GuestPaymentRepository;
import com.badmintonclub.clubmanagement.repository.RegistrationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private GuestPaymentRepository guestPaymentRepository;

    public boolean register(User user, Schedule schedule) {

        if (user == null || schedule == null) {
            return false;
        }

        if (!user.isEnabled()) {
            return false;
        }

        if (schedule.getStatus() != ScheduleStatus.OPEN) {
            return false;
        }

        boolean alreadyRegistered = registrationRepository.existsByUserAndSchedule(
                user,
                schedule);

        if (alreadyRegistered) {
            return false;
        }

        if (isScheduleFull(schedule)) {
            return false;
        }

        Registration registration = new Registration();

        registration.setUser(user);
        registration.setSchedule(schedule);

        registrationRepository.save(registration);

        return true;
    }

    public int countParticipants(Schedule schedule) {
        return countMemberParticipants(schedule) + countGuestParticipants(schedule);
    }

    public int countMemberParticipants(Schedule schedule) {
        if (schedule == null) {
            return 0;
        }

        return registrationRepository.countBySchedule(schedule);
    }

    public int countGuestParticipants(Schedule schedule) {
        if (schedule == null) {
            return 0;
        }

        return guestPaymentRepository.countBySchedule(schedule);
    }

    public boolean isScheduleFull(Schedule schedule) {
        if (schedule == null || schedule.getMaxPlayers() == null) {
            return true;
        }

        return countParticipants(schedule) >= schedule.getMaxPlayers();
    }

    public List<Registration> getRegistrationsBySchedule(Schedule schedule) {
        return registrationRepository.findBySchedule(schedule);
    }

    public boolean cancelRegistration(User user, Schedule schedule) {

        if (user == null || schedule == null) {
            return false;
        }

        Registration registration = registrationRepository.findByUserAndSchedule(
                user,
                schedule);

        if (registration == null) {
            return false;
        }

        registrationRepository.delete(registration);

        return true;
    }

    public boolean isRegistered(User user, Schedule schedule) {
        return registrationRepository.existsByUserAndSchedule(user, schedule);
    }

    public long countRegistrations() {
        return registrationRepository.count();
    }

    public List<Registration> getRegistrationsByUser(User user) {
        return registrationRepository.findByUser(user);
    }

    public int countByUser(User user) {
        return registrationRepository.countByUser(user);
    }
}