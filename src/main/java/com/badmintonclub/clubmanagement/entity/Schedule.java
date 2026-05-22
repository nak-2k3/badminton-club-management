package com.badmintonclub.clubmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String title; // tên buổi đánh

    private String courtName; // tên sân

    private LocalDateTime playTime; // thời gian

    private Integer maxPlayers; // giới hạn người

    private Boolean locked = false; // khóa đk

    @Transient // ko lưu trong dt chỉ hiển thị
    private int currentPlayers;
}