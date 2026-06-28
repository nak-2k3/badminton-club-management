package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.entity.enums.Level;
import com.badmintonclub.clubmanagement.entity.enums.Role;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEnabledTrue();

    User findByEmail(String email);

    long countByEnabledTrue();

    long countByEnabledFalse();

    @Query(value = """
             SELECT u
             FROM User u
             WHERE (:keyword IS NULL OR :keyword = ''
                 OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR u.phone LIKE CONCAT('%', :keyword, '%'))
             AND (:role IS NULL OR u.role = :role)
             AND (:level IS NULL OR u.level = :level)
             AND (:enabled IS NULL OR u.enabled = :enabled)
             ORDER BY u.id DESC
            """, countQuery = """
             SELECT COUNT(u)
             FROM User u
             WHERE (:keyword IS NULL OR :keyword = ''
                 OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR u.phone LIKE CONCAT('%', :keyword, '%'))
             AND (:role IS NULL OR u.role = :role)
             AND (:level IS NULL OR u.level = :level)
             AND (:enabled IS NULL OR u.enabled = :enabled)
            """)
    Page<User> searchUsers(
            @Param("keyword") String keyword,
            @Param("role") Role role,
            @Param("level") Level level,
            @Param("enabled") Boolean enabled,
            Pageable pageable);
}