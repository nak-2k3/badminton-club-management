package com.badmintonclub.clubmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "payment_batches")
@Getter
@Setter
public class PaymentBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String month;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;

    @Column(length = 500)
    private String note;
}