package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.User;
import com.badmintonclub.clubmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lưu user
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Tìm user theo id
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Xóa user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}