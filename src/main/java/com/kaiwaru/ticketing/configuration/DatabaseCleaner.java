package com.kaiwaru.ticketing.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.RefreshTokenRepository;
import com.kaiwaru.ticketing.repository.RoleRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "app.clean-db-on-startup", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleaner implements CommandLineRunner {
    // in production, it must not working

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void run(String... args) throws Exception {
        log.warn("🧹 Cleaning db data...");

        ticketRepository.deleteAll();
        eventRepository.deleteAll();
        refreshTokenRepository.deleteAll();
 

        log.info("✅ Db cleaned");
    }
}