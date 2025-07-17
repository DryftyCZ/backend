package com.kaiwaru.ticketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.security.UserPrincipal;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('WORKER') or hasRole('VISITOR')")
    public ResponseEntity<?> getDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("welcome", "Welcome to SmartTicket Dashboard!");
        dashboardData.put("user", Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail(),
            "roles", user.getRoles().stream().map(role -> role.getName()).toList()
        ));
        dashboardData.put("message", "You are successfully logged in!");
        dashboardData.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(dashboardData);
    }
}