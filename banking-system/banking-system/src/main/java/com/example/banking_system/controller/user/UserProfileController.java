package com.example.banking_system.controller.user;

import com.example.banking_system.dto.UserUpdateRequest;
import com.example.banking_system.model.User;
import com.example.banking_system.repository.UserRepository;
import com.example.banking_system.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserProfileController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public User getMyProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PatchMapping
    public User updateMyProfile(@RequestBody UserUpdateRequest update) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userService.partialUpdateUser(currentUser.getUserId(), update);
    }
}