package com.badmintonclub.clubmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import com.badmintonclub.clubmanagement.entity.enums.Gender;
import com.badmintonclub.clubmanagement.entity.enums.Level;
import com.badmintonclub.clubmanagement.entity.enums.Role;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Họ tên
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    // Email
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Column(unique = true)
    private String email;

    // Mật khẩu
    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password;

    // Số điện thoại
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    // Giới tính
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // Ngày sinh
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate birthDate;

    // Địa chỉ
    private String address;

    // Vai trò
    @Enumerated(EnumType.STRING)
    private Role role;

    // Trình độ
    @Enumerated(EnumType.STRING)
    private Level level;

    // Ngày tham gia CLB
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate joinDate;

    // Trạng thái tài khoản
    private boolean enabled = true;
}