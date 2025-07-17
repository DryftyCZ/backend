package com.kaiwaru.ticketing.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kaiwaru.ticketing.security.service.InviteTokenService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private InviteTokenService inviteTokenService;

    // Run every day at 2 AM to clean up expired invite tokens
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredInviteTokens() {
         try {
            inviteTokenService.cleanupExpiredTokens();
            log.info("Cleaned up expired invite tokens");
        } catch (Exception e) {
            log.error("Error during cleanup of expired invite tokens", e);
        }
    }
}

