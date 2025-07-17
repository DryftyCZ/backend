package com.kaiwaru.ticketing.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaiwaru.ticketing.repository.TicketRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LocationService {

    @Autowired
    private TicketRepository ticketRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedDelay = 1500)
    public void resolveLocations() {
        ticketRepository.findFirstByCountryIsNullAndIpAddressIsNotNullOrderByPurchaseDateAsc()
            .ifPresent(ticket -> {
                try {
                    String ip = ticket.getIpAddress();
                    URL url = new URL("http://ip-api.com/json/" + ip + "?fields=status,country,city");
                    log.info("tohle je url: "+ url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);

                    String json;
                    try (Scanner scanner = new Scanner(conn.getInputStream())) {
                        json = scanner.useDelimiter("\\A").next();
                    }

                    JsonNode root = objectMapper.readTree(json);
                    if ("success".equals(root.get("status").asText())) {
                        log.info("tohle je city:: "+ root.get("city").asText());
                        ticket.setCountry(root.get("country").asText());
                        ticket.setCity(root.get("city").asText());
                        ticketRepository.save(ticket);
                    }
                } catch (Exception e) {
                    log.warn("Nepodařilo se získat lokaci pro IP {} (ticket ID: {}). Důvod: {}", 
                        ticket.getIpAddress(), ticket.getId(), e.getMessage());
                }
            });
    }
}