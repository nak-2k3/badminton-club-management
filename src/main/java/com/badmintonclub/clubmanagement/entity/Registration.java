package com.badmintonclub.clubmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "registrations")
@Getter
@Setter
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User đăng ký
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Lịch đánh
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
}