package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.GuestPayment;
import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;
import com.badmintonclub.clubmanagement.repository.GuestPaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GuestPaymentService {

    @Autowired
    private GuestPaymentRepository guestPaymentRepository;

    public List<GuestPayment> getAllGuestPayments() {
        return guestPaymentRepository.findAllByOrderByIdDesc();
    }

    public GuestPayment getGuestPaymentById(Long id) {
        return guestPaymentRepository.findById(id).orElse(null);
    }

    public GuestPayment saveGuestPayment(GuestPayment guestPayment) {
        return guestPaymentRepository.save(guestPayment);
    }

    public List<GuestPayment> getGuestPaymentsBySchedule(Schedule schedule) {
        return guestPaymentRepository.findBySchedule(schedule);
    }

    public void markAsPaid(Long id, String paymentMethod) {
        GuestPayment guestPayment = getGuestPaymentById(id);

        if (guestPayment != null) {
            guestPayment.setStatus(PaymentStatus.PAID);
            guestPayment.setPaidDate(LocalDate.now());

            if (paymentMethod == null || paymentMethod.isBlank()) {
                guestPayment.setPaymentMethod("Tiền mặt");
            } else {
                guestPayment.setPaymentMethod(paymentMethod);
            }

            guestPaymentRepository.save(guestPayment);
        }
    }

    public void markAsUnpaid(Long id) {
        GuestPayment guestPayment = getGuestPaymentById(id);

        if (guestPayment != null) {
            guestPayment.setStatus(PaymentStatus.UNPAID);
            guestPayment.setPaidDate(null);
            guestPayment.setPaymentMethod(null);

            guestPaymentRepository.save(guestPayment);
        }
    }

    public void deleteGuestPayment(Long id) {
        guestPaymentRepository.deleteById(id);
    }

    public long countPaid() {
        return guestPaymentRepository.countByStatus(PaymentStatus.PAID);
    }

    public long countUnpaid() {
        return guestPaymentRepository.countByStatus(PaymentStatus.UNPAID);
    }

    public Long getTotalGuestAmount() {
        return guestPaymentRepository.sumAllAmount();
    }

    public Long getPaidGuestAmount() {
        return guestPaymentRepository.sumAmountByStatus(PaymentStatus.PAID);
    }

    public Long getUnpaidGuestAmount() {
        return guestPaymentRepository.sumAmountByStatus(PaymentStatus.UNPAID);
    }

    public Long getTotalGuestAmountBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return guestPaymentRepository.sumAmountByScheduleTimeBetween(
                startDateTime,
                endDateTime);
    }

    public Long getPaidGuestAmountBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return guestPaymentRepository.sumAmountByScheduleTimeBetweenAndStatus(
                startDateTime,
                endDateTime,
                PaymentStatus.PAID);
    }

    public Long getUnpaidGuestAmountBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return guestPaymentRepository.sumAmountByScheduleTimeBetweenAndStatus(
                startDateTime,
                endDateTime,
                PaymentStatus.UNPAID);
    }

    public long countPaidGuestBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return guestPaymentRepository.countByScheduleTimeBetweenAndStatus(
                startDateTime,
                endDateTime,
                PaymentStatus.PAID);
    }

    public long countUnpaidGuestBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return guestPaymentRepository.countByScheduleTimeBetweenAndStatus(
                startDateTime,
                endDateTime,
                PaymentStatus.UNPAID);
    }

    public List<GuestPayment> getGuestPaymentsBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return guestPaymentRepository.findByScheduleTimeBetween(
                startDateTime,
                endDateTime);
    }
}