package com.kaiwaru.ticketing.security.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaiwaru.ticketing.model.Auth.InviteToken;
import com.kaiwaru.ticketing.model.Auth.Role;
import com.kaiwaru.ticketing.repository.InviteTokenRepository;

@Service
public class InviteTokenService {
    
    @Value("${app.invite.expiration-days}")
    private int inviteExpirationDays;

    @Autowired
    private InviteTokenRepository inviteTokenRepository;

    public InviteToken createInviteToken(String email, Role.RoleName targetRole) {
        InviteToken inviteToken = new InviteToken();
        inviteToken.setToken(UUID.randomUUID().toString());
        inviteToken.setEmail(email);
        inviteToken.setTargetRole(targetRole);
        inviteToken.setCreatedAt(LocalDateTime.now());
        inviteToken.setExpiryDate(LocalDateTime.now().plusDays(inviteExpirationDays));
        inviteToken.setUsed(false);

        return inviteTokenRepository.save(inviteToken);
    }

    public Optional<InviteToken> findByToken(String token) {
        return inviteTokenRepository.findByToken(token);
    }

    public boolean isValidInviteToken(String token) {
        Optional<InviteToken> inviteTokenOpt = findByToken(token);
        if (inviteTokenOpt.isEmpty()) {
            return false;
        }

        InviteToken inviteToken = inviteTokenOpt.get();
        return !inviteToken.isUsed() && 
               inviteToken.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public InviteToken markAsUsed(String token) {
        Optional<InviteToken> inviteTokenOpt = findByToken(token);
        if (inviteTokenOpt.isPresent()) {
            InviteToken inviteToken = inviteTokenOpt.get();
            inviteToken.setUsed(true);
            return inviteTokenRepository.save(inviteToken);
        }
        return null;
    }

    @Transactional
    public void cleanupExpiredTokens() {
        inviteTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    public boolean hasValidInviteForEmail(String email) {
        return inviteTokenRepository.existsByEmailAndUsed(email, false);
    }
}