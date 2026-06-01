package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.Payment;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUser(User user);

    long countByStatus(PaymentStatus status);
}