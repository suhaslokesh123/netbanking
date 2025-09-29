package com.example.NetBanking.service;

import com.example.NetBanking.model.User;
import com.example.NetBanking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public User addUser(User user) {
        // 🔐 hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Integer id, User updatedUser) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(updatedUser.getUsername());
                    existing.setEmail(updatedUser.getEmail());
                    existing.setPhone(updatedUser.getPhone());
                    existing.setStatus(updatedUser.getStatus());

                    // Hash only if password is updated
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
