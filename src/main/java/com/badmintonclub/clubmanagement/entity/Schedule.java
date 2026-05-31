package com.badmintonclub.clubmanagement.entity;

import com.badmintonclub.clubmanagement.entity.enums.ScheduleStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Tên sân không được để trống")
    private String courtName;

    @NotNull(message = "Thời gian không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime playTime;

    @NotNull(message = "Giới hạn người không được để trống")
    @Min(value = 1, message = "Giới hạn người phải lớn hơn 0")
    private Integer maxPlayers;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status = ScheduleStatus.OPEN;

    @Transient
    private int currentPlayers;
    // biến tạm đăng ký tham gia
    @Transient
    private boolean registered;
}