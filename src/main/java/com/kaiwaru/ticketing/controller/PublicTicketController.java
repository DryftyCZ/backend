package com.kaiwaru.ticketing.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.dto.PurchaseTicketRequest;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.model.TicketType;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.service.TicketService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/public/tickets")
@CrossOrigin(origins = "*")
public class PublicTicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private EventRepository eventRepository;

    /**
     * Public endpoint to get ticket types for an event
     */
    @GetMapping("/event/{eventId}/types")
    public ResponseEntity<?> getEventTicketTypes(@PathVariable Long eventId) {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event nebyl nalezen"));
            
            return ResponseEntity.ok(Map.of(
                "event", event,
                "ticketTypes", event.getTicketTypes()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Public endpoint to purchase tickets
     */
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTickets(@RequestBody @Valid PurchaseTicketRequest request, 
                                           HttpServletRequest httpRequest) {
        try {
            // Validate event exists
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Event s ID " + request.getEventId() + " nebyl nalezen"));

            // Purchase tickets without authentication (customer = null)
            List<Ticket> tickets = ticketService.purchaseTicketsWithType(
                    request.getTicketTypeId(),
                    request.getCustomerName(),
                    request.getCustomerEmail(),
                    request.getQuantity() != null ? request.getQuantity() : 1,
                    httpRequest,
                    null // No authenticated user for public purchases
            );

            // Calculate total price
            double totalPrice = 0;
            if (!tickets.isEmpty() && tickets.get(0).getTicketType() != null) {
                totalPrice = tickets.get(0).getTicketType().getPrice().doubleValue() * tickets.size();
            }

            return ResponseEntity.ok(Map.of(
                "message", "Vstupenky byly úspěšně zakoupeny",
                "tickets", tickets,
                "totalTickets", tickets.size(),
                "totalPrice", totalPrice
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}