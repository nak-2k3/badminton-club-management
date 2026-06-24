package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.FeeSetting;
import com.badmintonclub.clubmanagement.entity.Payment;
import com.badmintonclub.clubmanagement.entity.PaymentBatch;
import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.Gender;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;
import com.badmintonclub.clubmanagement.repository.PaymentRepository;
import com.badmintonclub.clubmanagement.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUser(user);
    }

    public List<Payment> getPaymentsByBatch(PaymentBatch batch) {
        return paymentRepository.findByBatch(batch);
    }

    public void markAsPaid(Long id) {
        markAsPaid(id, "Tiền mặt");
    }

    public void markAsPaid(Long id, String paymentMethod) {
        Payment payment = getPaymentById(id);

        if (payment != null) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidDate(LocalDate.now());
            payment.setPaymentMethod(paymentMethod);

            paymentRepository.save(payment);
        }
    }

    public void markAsUnpaid(Long id) {
        Payment payment = getPaymentById(id);

        if (payment != null) {
            payment.setStatus(PaymentStatus.UNPAID);
            payment.setPaidDate(null);
            payment.setPaymentMethod(null);

            paymentRepository.save(payment);
        }
    }

    public void createMonthlyPaymentsByGender(
            PaymentBatch batch,
            FeeSetting setting) {
        List<User> users = userRepository.findByEnabledTrue();

        for (User user : users) {
            Payment payment = new Payment();

            payment.setBatch(batch);
            payment.setUser(user);
            payment.setAmount(getMonthlyFeeByGender(user, setting));
            payment.setStatus(PaymentStatus.UNPAID);
            payment.setPaidDate(null);
            payment.setPaymentMethod(null);
            payment.setNote(batch.getNote());

            paymentRepository.save(payment);
        }
    }

    private Integer getMonthlyFeeByGender(
            User user,
            FeeSetting setting) {
        if (user.getGender() == Gender.FEMALE) {
            return setting.getFemaleMonthlyFee();
        }

        return setting.getMaleMonthlyFee();
    }

    public long countPaid() {
        return paymentRepository.countByStatus(PaymentStatus.PAID);
    }

    public long countUnpaid() {
        return paymentRepository.countByStatus(PaymentStatus.UNPAID);
    }

    public long countByBatch(PaymentBatch batch) {
        return paymentRepository.countByBatch(batch);
    }

    public long countPaidByBatch(PaymentBatch batch) {
        return paymentRepository.countByBatchAndStatus(
                batch,
                PaymentStatus.PAID);
    }

    public long countUnpaidByBatch(PaymentBatch batch) {
        return paymentRepository.countByBatchAndStatus(
                batch,
                PaymentStatus.UNPAID);
    }

    public Long getTotalAmountByBatch(PaymentBatch batch) {
        return paymentRepository.sumAmountByBatch(batch);
    }

    public Long getPaidAmountByBatch(PaymentBatch batch) {
        return paymentRepository.sumAmountByBatchAndStatus(
                batch,
                PaymentStatus.PAID);
    }

    public Long getUnpaidAmountByBatch(PaymentBatch batch) {
        return getTotalAmountByBatch(batch) - getPaidAmountByBatch(batch);
    }

    public int getProgressPercent(PaymentBatch batch) {
        Long total = getTotalAmountByBatch(batch);
        Long paid = getPaidAmountByBatch(batch);

        if (total == null || total == 0) {
            return 0;
        }

        return (int) Math.round((paid * 100.0) / total);
    }

    public void createEventPayments(
            PaymentBatch batch,
            Integer amount,
            List<Long> userIds) {
        List<User> users;

        if (userIds == null || userIds.isEmpty()) {
            users = userRepository.findByEnabledTrue();
        } else {
            users = userRepository.findAllById(userIds);
        }

        for (User user : users) {
            if (!user.isEnabled()) {
                continue;
            }

            Payment payment = new Payment();

            payment.setBatch(batch);
            payment.setUser(user);
            payment.setAmount(amount);
            payment.setStatus(PaymentStatus.UNPAID);
            payment.setPaidDate(null);
            payment.setPaymentMethod(null);
            payment.setNote(batch.getNote());

            paymentRepository.save(payment);
        }
    }
}