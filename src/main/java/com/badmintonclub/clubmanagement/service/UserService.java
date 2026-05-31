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
}