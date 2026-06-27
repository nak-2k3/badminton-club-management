package com.badmintonclub.clubmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên khoản chi không được để trống")
    private String title;

    @NotNull(message = "Số tiền không được để trống")
    @Min(value = 1000, message = "Số tiền phải lớn hơn 0")
    private Integer amount;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate expenseDate;

    private String paymentMethod;

    @Column(length = 500)
    private String note;
}