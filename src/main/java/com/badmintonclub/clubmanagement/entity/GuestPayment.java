package com.badmintonclub.clubmanagement.entity;

import com.badmintonclub.clubmanagement.entity.enums.AttendanceStatus;
import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "guest_payments")
@Getter
@Setter
public class GuestPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Khách vãng lai thuộc buổi đánh nào
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @NotBlank(message = "Tên khách không được để trống")
    private String guestName;

    private String phone;

    @NotNull(message = "Số tiền không được để trống")
    @Min(value = 1000, message = "Số tiền phải lớn hơn 0")
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.UNPAID;

    private LocalDate paidDate;

    private String paymentMethod;

    // Trạng thái điểm danh
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus = AttendanceStatus.NOT_MARKED;

    @Column(length = 500)
    private String note;
}