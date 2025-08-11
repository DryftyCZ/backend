package com.kaiwaru.ticketing.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.kaiwaru.ticketing.dto.ChangePasswordRequest;
import com.kaiwaru.ticketing.dto.UpdateProfileRequest;
import com.kaiwaru.ticketing.dto.UserProfileDto;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.repository.UserRepository;
import com.kaiwaru.ticketing.security.UserPrincipal;
import com.kaiwaru.ticketing.security.response.MessageResponse;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('MANAGER')")
    public ResponseEntity<UserProfileDto> getUserProfile(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDto profile = convertToDto(user);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> updateUserProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update profile fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setPosition(request.getPosition());
        user.setDepartment(request.getDepartment());
        user.setBio(request.getBio());
        user.setLanguage(request.getLanguage());
        user.setTimezone(request.getTimezone());
        user.setTheme(request.getTheme());
        user.setEmailNotifications(request.getEmailNotifications());
        user.setPushNotifications(request.getPushNotifications());
        user.setWeeklyReports(request.getWeeklyReports());

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Profile updated successfully!"));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Current password is incorrect"));
        }

        // Update password
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }

    @GetMapping("/team")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<UserProfileDto>> getTeamMembers() {
        List<User> users = userRepository.findAll();
        List<UserProfileDto> teamMembers = users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teamMembers);
    }

    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> updateTeamMemberProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update profile fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setPosition(request.getPosition());
        user.setDepartment(request.getDepartment());
        user.setBio(request.getBio());

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Team member profile updated successfully!"));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }

    private UserProfileDto convertToDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setPosition(user.getPosition());
        dto.setDepartment(user.getDepartment());
        dto.setBio(user.getBio());
        dto.setLanguage(user.getLanguage());
        dto.setTimezone(user.getTimezone());
        dto.setTheme(user.getTheme());
        dto.setEmailNotifications(user.getEmailNotifications());
        dto.setPushNotifications(user.getPushNotifications());
        dto.setWeeklyReports(user.getWeeklyReports());
        return dto;
    }
}