package com.kaiwaru.ticketing.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.dto.GenerateTicketsRequest;
import com.kaiwaru.ticketing.dto.PurchaseTicketRequest;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.service.TicketService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tickets")
@Validated
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // Validace tiketu pomocí qrCode v parametru, označí jako použitý
    @PostMapping("/validate")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN') or hasRole('WORKER')")
    @Transactional
    public ResponseEntity<?> validateTicket(@RequestParam String qrCode) {
        try {
            Ticket ticket = ticketService.validateAndUseTicket(qrCode);
            return ResponseEntity.ok(Map.of(
                "message", "Vstupenka je platná a byla označena jako použitá",
                "ticket", ticket,
                "valid", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "valid", false
            ));
        }
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<?> generateTickets(@RequestBody @Valid GenerateTicketsRequest request) {
        try {
            Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event s ID " + request.getEventId() + " nebyl nalezen"));

            List<Ticket> tickets = ticketService.generateTicketsForEvent(event, request.getCount());

            return ResponseEntity.ok(Map.of(
                "message", "Vygenerováno " + tickets.size() + " vstupenek",
                "tickets", tickets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Nákup tiketu (přiřazení k zákazníkovi a odeslání mailu)
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTicket(@RequestBody @Valid PurchaseTicketRequest request, HttpServletRequest httpRequest) {
        try {
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Event s ID " + request.getEventId() + " nebyl nalezen"));

            String ip = httpRequest.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = httpRequest.getRemoteAddr();
            } else {
                // X-Forwarded-For může obsahovat i víc IP, vezmi první
                ip = ip.split(",")[0].trim();
            }
            Ticket ticket = ticketService.purchaseTicket(event, request.getCustomerName(), request.getCustomerEmail(), ip);

            return ResponseEntity.ok(Map.of(
                "message", "Vstupenka byla úspěšně zakoupena",
                "ticket", ticket
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Získání všech tiketů pro konkrétní event
    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<?> getEventTickets(@PathVariable Long eventId) {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event s ID " + eventId + " nebyl nalezen"));

            List<Ticket> tickets = ticketService.getTicketsByEvent(event);

            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/stats/countries")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public Map<String, Long> statsByCountry() {
        return ticketRepository.findAll().stream()
                .filter(t -> t.getCountry() != null)
                .collect(Collectors.groupingBy(Ticket::getCountry, Collectors.counting()));
    }
    
    @GetMapping("/stats/cities")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public Map<String, Long> statsByCity() {
        return ticketRepository.findAll().stream()
            .filter(t -> t.getCity() != null && !t.getCity().isBlank())
            .collect(Collectors.groupingBy(
                t -> t.getCity().trim(),
                Collectors.counting()
            ));
    }
}
