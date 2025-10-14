package com.example.banking_system.controller.admin;

import com.example.banking_system.dto.UserUpdateRequest;
import com.example.banking_system.model.Loan;
import com.example.banking_system.model.User;
import com.example.banking_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8000")
@RequestMapping("admin/users")
public class AdminUserController {
    private final UserService userService;
    public AdminUserController(UserService userService) { this.userService = userService; }

    @GetMapping
    public List<User> getAllUsers() { return userService.getAllUsers(); }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Integer userId) { return userService.getUserById(userId).orElseThrow(); }

    @PostMapping
    public User createUser(@RequestBody User user) { return userService.createUser(user); }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) { userService.deleteUser(userId); }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable Integer userId, @RequestBody UserUpdateRequest update) {
        return userService.partialUpdateUser(userId, update);
    }
    // Get loans for a user
    @GetMapping("/{userId}/loans")
    public List<Loan> getLoansByUser(@PathVariable Integer userId) { return userService.getLoansByUserId(userId); }
}