package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.Payment;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;
import com.badmintonclub.clubmanagement.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void markAsPaid(Long id) {
        Payment payment = getPaymentById(id);

        if (payment != null) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidDate(LocalDate.now());
            paymentRepository.save(payment);
        }
    }

    public void markAsUnpaid(Long id) {
        Payment payment = getPaymentById(id);

        if (payment != null) {
            payment.setStatus(PaymentStatus.UNPAID);
            payment.setPaidDate(null);
            paymentRepository.save(payment);
        }
    }

    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUser(user);
    }

    public long countPaid() {
        return paymentRepository.countByStatus(PaymentStatus.PAID);
    }

    public long countUnpaid() {
        return paymentRepository.countByStatus(PaymentStatus.UNPAID);
    }
}