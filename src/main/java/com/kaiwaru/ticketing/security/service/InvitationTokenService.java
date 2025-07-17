package com.kaiwaru.ticketing.security.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.kaiwaru.ticketing.model.invitation.InvitationToken;
import com.kaiwaru.ticketing.repository.InvitationRepository;

@Service
public class InvitationTokenService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Value("${app.url}")
    private String url;

    public String generateInvitationLink() {
        String token = UUID.randomUUID().toString();

        InvitationToken invitation = new InvitationToken();
        invitation.setToken(token);
        invitation.setExpiryDate(LocalDateTime.now().plusDays(3));
        invitation.setUsed(false);

        invitationRepository.save(invitation);

        return UriComponentsBuilder.fromUriString(url)
        .path("/register")
        .queryParam("token", token)
        .toUriString();
    }
    
}
