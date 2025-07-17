package com.kaiwaru.ticketing.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaiwaru.ticketing.model.Auth.InviteToken;

@Repository
public interface InviteTokenRepository extends JpaRepository<InviteToken, Long> {
    Optional<InviteToken> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM InviteToken it WHERE it.expiryDate < :now")
    void deleteExpiredTokens(LocalDateTime now);
    
    boolean existsByEmailAndUsed(String email, boolean used);
}
