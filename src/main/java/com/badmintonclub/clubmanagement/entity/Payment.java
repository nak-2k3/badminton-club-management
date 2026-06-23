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

    // Thuộc đợt thu nào
    @ManyToOne
    @JoinColumn(name = "batch_id")
    private PaymentBatch batch;

    // Thành viên phải đóng
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "Số tiền không được để trống")
    @Min(value = 1000, message = "Số tiền phải lớn hơn 0")
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.UNPAID;

    private LocalDate paidDate;

    private String paymentMethod;

    @Column(length = 500)
    private String note;
}