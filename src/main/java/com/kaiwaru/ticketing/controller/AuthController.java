package com.kaiwaru.ticketing.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.exception.InvalidInviteTokenException;
import com.kaiwaru.ticketing.exception.TokenRefreshException;
import com.kaiwaru.ticketing.model.Auth.InviteToken;
import com.kaiwaru.ticketing.model.Auth.RefreshToken;
import com.kaiwaru.ticketing.model.Auth.Role;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.repository.RoleRepository;
import com.kaiwaru.ticketing.repository.UserRepository;
import com.kaiwaru.ticketing.security.UserPrincipal;
import com.kaiwaru.ticketing.security.requests.LoginRequest;
import com.kaiwaru.ticketing.security.requests.SignupRequest;
import com.kaiwaru.ticketing.security.requests.TokenRefreshRequest;
import com.kaiwaru.ticketing.security.response.JwtResponse;
import com.kaiwaru.ticketing.security.response.MessageResponse;
import com.kaiwaru.ticketing.security.response.TokenRefreshResponse;
import com.kaiwaru.ticketing.security.service.InviteTokenService;
import com.kaiwaru.ticketing.security.service.RefreshTokenService;
import com.kaiwaru.ticketing.security.utils.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    InviteTokenService inviteTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getIdentifier(), 
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, 
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(null,
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                new HashSet<>(),
                null,
                null,
                null);

        Set<Role> roles = new HashSet<>();

        // Check if invite token provided for organizer registration
        if (signUpRequest.getInviteToken() != null && !signUpRequest.getInviteToken().isEmpty()) {
            if (!inviteTokenService.isValidInviteToken(signUpRequest.getInviteToken())) {
                throw new InvalidInviteTokenException("Invalid or expired invite token");
            }

            Optional<InviteToken> inviteTokenOpt = inviteTokenService.findByToken(signUpRequest.getInviteToken());
            if (inviteTokenOpt.isPresent()) {
                InviteToken inviteToken = inviteTokenOpt.get();
                
                // Verify email matches
                if (!inviteToken.getEmail().equals(signUpRequest.getEmail())) {
                    throw new InvalidInviteTokenException("Email doesn't match invite token");
                }

                // Set role based on invite
                Role targetRole = roleRepository.findByName(inviteToken.getTargetRole().name())
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(targetRole);

                // Mark invite token as used
                inviteTokenService.markAsUsed(signUpRequest.getInviteToken());
            }
        } else {
            // Default registration - only VISITOR role
            Role visitorRole = roleRepository.findByName(Role.RoleName.VISITOR.name())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(visitorRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByUser(userRepository.findById(userId).get());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}
