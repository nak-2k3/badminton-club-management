package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEnabledTrue();

    User findByEmail(String email);

    // đếm tài khoản hoạt động
    long countByEnabledTrue();

    long countByEnabledFalse();

}
