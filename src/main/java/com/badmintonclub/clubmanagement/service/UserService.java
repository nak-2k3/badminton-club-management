package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long countUsers() {
        return userRepository.count();
    }

    public long countActiveUsers() {
        return userRepository.countByEnabledTrue();
    }

    public long countLockedUsers() {
        return userRepository.countByEnabledFalse();
    }

    // Lưu user
    public User saveUser(User user, String newPassword) {

        if (user.getId() != null) {
            User oldUser = getUserById(user.getId());

            if (oldUser != null) {
                if (newPassword != null && !newPassword.isBlank()) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                } else {
                    user.setPassword(oldUser.getPassword());
                }
            }
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    // Tìm user theo id
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void lockUser(Long id) {
        User user = getUserById(id);
        if (user != null) {
            user.setEnabled(false);
            userRepository.save(user);
        }
    }

    public void unlockUser(Long id) {
        User user = getUserById(id);
        if (user != null) {
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateProfile(Long id, User formUser) {
        User user = getUserById(id);

        if (user != null) {
            user.setFullName(formUser.getFullName());
            user.setPhone(formUser.getPhone());
            user.setGender(formUser.getGender());
            user.setBirthDate(formUser.getBirthDate());
            user.setAddress(formUser.getAddress());

            userRepository.save(user);
        }
    }

    public boolean changePassword(
            Long userId,
            String currentPassword,
            String newPassword,
            String confirmPassword) {
        User user = getUserById(userId);

        if (user == null) {
            return false;
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        if (newPassword == null || newPassword.length() < 6) {
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }
}