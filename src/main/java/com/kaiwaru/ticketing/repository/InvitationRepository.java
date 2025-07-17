package com.kaiwaru.ticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaiwaru.ticketing.model.invitation.InvitationToken;

public interface InvitationRepository extends JpaRepository<InvitationToken, Long>{
    
}
