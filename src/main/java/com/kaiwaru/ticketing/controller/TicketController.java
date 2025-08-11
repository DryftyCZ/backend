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
import com.kaiwaru.ticketing.model.TicketType;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.TicketTypeRepository;
import com.kaiwaru.ticketing.service.TicketService;
import com.kaiwaru.ticketing.service.EventService;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.kaiwaru.ticketing.repository.UserRepository;

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
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public List<Ticket> getAllTickets() {
        User currentUser = getCurrentUser();
        
        // Admins see all tickets
        if (isAdmin(currentUser)) {
            return ticketRepository.findAll();
        }
        
        // Organizers see only tickets from their events
        List<Event> userEvents = eventRepository.findByOrganizer(currentUser);
        return userEvents.stream()
                .flatMap(event -> ticketRepository.findByEvent(event).stream())
                .collect(Collectors.toList());
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
            // Use EventService which checks permissions
            Event event = eventService.getEventById(request.getEventId());

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
            // Validate event exists
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Event s ID " + request.getEventId() + " nebyl nalezen"));

            // Get current user if authenticated, otherwise create a guest user
            User customer = null;
            try {
                customer = getCurrentUser();
            } catch (Exception e) {
                // For public purchases, customer can be null
            }

            // Use the new method that supports ticket types
            List<Ticket> tickets = ticketService.purchaseTicketsWithType(
                    request.getTicketTypeId(),
                    request.getCustomerName(),
                    request.getCustomerEmail(),
                    request.getQuantity(),
                    httpRequest,
                    customer
            );

            return ResponseEntity.ok(Map.of(
                "message", "Vstupenky byly úspěšně zakoupeny",
                "tickets", tickets,
                "totalTickets", tickets.size()
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
            // Use EventService which checks permissions
            Event event = eventService.getEventById(eventId);

            List<Ticket> tickets = ticketService.getTicketsByEvent(event);

            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/stats/countries")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public Map<String, Long> statsByCountry() {
        User currentUser = getCurrentUser();
        List<Ticket> tickets;
        
        if (isAdmin(currentUser)) {
            tickets = ticketRepository.findAll();
        } else {
            // Get only tickets from organizer's events
            List<Event> userEvents = eventRepository.findByOrganizer(currentUser);
            tickets = userEvents.stream()
                    .flatMap(event -> ticketRepository.findByEvent(event).stream())
                    .collect(Collectors.toList());
        }
        
        return tickets.stream()
                .filter(t -> t.getCountry() != null)
                .collect(Collectors.groupingBy(Ticket::getCountry, Collectors.counting()));
    }
    
    @GetMapping("/stats/cities")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public Map<String, Long> statsByCity() {
        User currentUser = getCurrentUser();
        List<Ticket> tickets;
        
        if (isAdmin(currentUser)) {
            tickets = ticketRepository.findAll();
        } else {
            // Get only tickets from organizer's events
            List<Event> userEvents = eventRepository.findByOrganizer(currentUser);
            tickets = userEvents.stream()
                    .flatMap(event -> ticketRepository.findByEvent(event).stream())
                    .collect(Collectors.toList());
        }
        
        return tickets.stream()
            .filter(t -> t.getCity() != null && !t.getCity().isBlank())
            .collect(Collectors.groupingBy(
                t -> t.getCity().trim(),
                Collectors.counting()
            ));
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new RuntimeException("Uživatel nebyl nalezen"));
        }
        throw new RuntimeException("Uživatel není přihlášen");
    }
    
    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }
}
