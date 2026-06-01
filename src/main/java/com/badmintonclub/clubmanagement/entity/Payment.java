package com.badmintonclub.clubmanagement.entity;

import com.badmintonclub.clubmanagement.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thành viên đóng phí
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Tháng thu phí, ví dụ: 06/2026
    private String month;

    @NotNull(message = "Số tiền không được để trống")
    @Min(value = 1000, message = "Số tiền phải lớn hơn 0")
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.UNPAID;

    private LocalDate paidDate;

    @Column(length = 500)
    private String note;
}