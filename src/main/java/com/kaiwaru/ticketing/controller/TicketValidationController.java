package com.kaiwaru.ticketing.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.security.UserPrincipal;

@RestController
@RequestMapping("/api/tickets")
public class TicketValidationController {

    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping("/validate/{qrCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('WORKER')")
    public ResponseEntity<Map<String, Object>> validateTicket(@PathVariable String qrCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find ticket by QR code
            Optional<Ticket> ticketOpt = ticketRepository.findByQrCode(qrCode);
            
            if (ticketOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Ticket nebyl nalezen");
                response.put("status", "NOT_FOUND");
                return ResponseEntity.ok(response);
            }
            
            Ticket ticket = ticketOpt.get();
            
            // Check if ticket is already used
            if (ticket.isUsed()) {
                response.put("success", false);
                response.put("message", "Ticket již byl použit");
                response.put("status", "ALREADY_USED");
                response.put("usedDate", ticket.getUsedDate());
                response.put("ticket", createTicketInfo(ticket));
                return ResponseEntity.ok(response);
            }
            
            // Mark ticket as used
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            ticket.setUsed(true);
            ticket.setUsedDate(LocalDateTime.now());
            ticketRepository.save(ticket);
            
            response.put("success", true);
            response.put("message", "Ticket úspěšně validován");
            response.put("status", "VALIDATED");
            response.put("validatedBy", userPrincipal.getUsername());
            response.put("validatedAt", LocalDateTime.now());
            response.put("ticket", createTicketInfo(ticket));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Chyba při validaci ticketu: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    private Map<String, Object> createTicketInfo(Ticket ticket) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", ticket.getId());
        info.put("ticketNumber", ticket.getTicketNumber());
        info.put("customerName", ticket.getCustomerName());
        info.put("customerEmail", ticket.getCustomerEmail());
        info.put("eventName", ticket.getEvent().getName());
        info.put("purchaseDate", ticket.getPurchaseDate());
        info.put("used", ticket.isUsed());
        info.put("usedDate", ticket.getUsedDate());
        if (ticket.getTicketType() != null) {
            info.put("ticketType", ticket.getTicketType().getName());
            info.put("price", ticket.getTicketType().getPrice());
        }
        return info;
    }
}