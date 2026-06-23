package com.badmintonclub.clubmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fee_settings")
@Getter
@Setter
public class FeeSetting { // Cấu hình phí mặc định

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer maleMonthlyFee;

    private Integer femaleMonthlyFee;

    private Integer guestSessionFee;
}