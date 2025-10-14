package com.example.banking_system.controller.admin;

import com.example.banking_system.model.Admin;
import com.example.banking_system.model.User;
import com.example.banking_system.repository.AdminRepository;
import com.example.banking_system.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:8000")
@RequestMapping("/api/auth")
public class AuthController {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AuthController(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        String role = loginRequest.get("role"); // "admin" or "user"

        Map<String, Object> response = new HashMap<>();

        if ("admin".equals(role)) {
            Optional<Admin> adminOpt = adminRepository.findByUsername(username);
            if (adminOpt.isPresent() && adminOpt.get().getPassword().equals(password)) {
                response.put("success", true);
                response.put("role", "admin");
                response.put("message", "Admin login successful");
                return ResponseEntity.ok(response);
            }
        } else if ("user".equals(role)) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
                response.put("success", true);
                response.put("role", "user");
                response.put("userId", userOpt.get().getUserId());
                response.put("message", "User login successful");
                return ResponseEntity.ok(response);
            }
        }

        response.put("success", false);
        response.put("message", "Invalid credentials");
        return ResponseEntity.status(401).body(response);
    }

    @PostMapping("/register")
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminRepository.save(admin);
    }
}
