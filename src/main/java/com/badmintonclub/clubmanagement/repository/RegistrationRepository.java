package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.Registration;
import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {

    int countBySchedule(Schedule schedule);

    boolean existsByUserAndSchedule(
            User user,
            Schedule schedule);

    List<Registration> findBySchedule(Schedule schedule);
}