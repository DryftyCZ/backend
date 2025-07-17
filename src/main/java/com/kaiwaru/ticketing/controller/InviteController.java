package com.kaiwaru.ticketing.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.model.Auth.InviteToken;
import com.kaiwaru.ticketing.model.Auth.Role;
import com.kaiwaru.ticketing.security.requests.InviteRequest;
import com.kaiwaru.ticketing.security.response.InviteResponse;
import com.kaiwaru.ticketing.security.response.MessageResponse;
import com.kaiwaru.ticketing.security.service.InviteTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/invite")
public class InviteController {

    @Autowired
    private InviteTokenService inviteTokenService;

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendInvite(@Valid @RequestBody InviteRequest inviteRequest) {
        // Only allow inviting ORGANIZER role for now
        if (inviteRequest.getTargetRole() != Role.RoleName.ORGANIZER) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Only ORGANIZER invites are supported currently"));
        }

        // Check if there's already a valid invite for this email
        if (inviteTokenService.hasValidInviteForEmail(inviteRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Valid invite already exists for this email"));
        }

        InviteToken inviteToken = inviteTokenService.createInviteToken(
                inviteRequest.getEmail(),
                inviteRequest.getTargetRole()
        );

        String inviteUrl = "http://localhost:3000/register?token=" + inviteToken.getToken();

        return ResponseEntity.ok(new InviteResponse(
                "Invite sent successfully",
                inviteUrl,
                inviteToken.getToken()
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateInvite(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();

        Optional<InviteToken> inviteTokenOpt = inviteTokenService.findByToken(token);
        if (inviteTokenOpt.isEmpty()) {
            response.put("valid", false);
            response.put("message", "Invite token not found");
            return ResponseEntity.badRequest().body(response);
        }

        InviteToken inviteToken = inviteTokenOpt.get();
        boolean isValid = inviteTokenService.isValidInviteToken(token);

        response.put("valid", isValid);
        if (isValid) {
            response.put("email", inviteToken.getEmail());
            response.put("targetRole", inviteToken.getTargetRole());
            response.put("expiryDate", inviteToken.getExpiryDate());
            response.put("message", "Valid invite token");
        } else {
            if (inviteToken.isUsed()) {
                response.put("message", "Invite token has already been used");
            } else {
                response.put("message", "Invite token has expired");
            }
        }

        return ResponseEntity.ok(response);
    }
}
