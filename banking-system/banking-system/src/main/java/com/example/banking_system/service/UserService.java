package com.example.banking_system.service;

import com.example.banking_system.dto.UserUpdateRequest;
import com.example.banking_system.model.Loan;
import com.example.banking_system.model.User;
import com.example.banking_system.repository.LoanRepository;
import com.example.banking_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    public UserService(UserRepository userRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }
    public Optional<User> getUserById(Integer id) { return userRepository.findById(id); }

    // Save then re-fetch to get DB default values (createdAt)
    public User createUser(User user) {
        User saved = userRepository.save(user);
        return userRepository.findById(saved.getUserId()).orElse(saved);
    }
    public List<Loan> getLoansByUserId(Integer userId) {
        return loanRepository.findByUserUserId(userId);
    }

    public void deleteUser(Integer id) { userRepository.deleteById(id); }

    public User partialUpdateUser(Integer userId, UserUpdateRequest update) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (update.getUsername() != null) user.setUsername(update.getUsername());
        if (update.getEmail() != null) user.setEmail(update.getEmail());
        if (update.getPhone() != null) user.setPhone(update.getPhone());
        if (update.getStatus() != null) user.setStatus(update.getStatus());
        if (update.getPassword() != null) user.setPassword(update.getPassword());
        return userRepository.save(user);
    }
}