package com.kaiwaru.ticketing.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kaiwaru.ticketing.dto.GenerateTicketsRequest;
import com.kaiwaru.ticketing.dto.PurchaseTicketRequest;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.model.Auth.Role;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.UserRepository;
import com.kaiwaru.ticketing.service.TicketService;
import com.kaiwaru.ticketing.service.EventService;
import com.kaiwaru.ticketing.security.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private EventService eventService;
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketController ticketController;

    private Event sampleEvent;
    private Ticket sampleTicket;

    @BeforeEach
    void setUp() {
        sampleEvent = new Event();
        sampleEvent.setId(1L);

        sampleTicket = new Ticket();
        sampleTicket.setQrCode("sampleQR");
        sampleTicket.setEvent(sampleEvent);
        sampleTicket.setIpAddress("127.0.0.1");
    }

    @Test
    void testGetAllTickets() {
        // Setup security context for this test
        User sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@example.com");
        sampleUser.setPassword("password");
        
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        sampleUser.setRoles(roles);
        
        UserPrincipal userPrincipal = UserPrincipal.create(sampleUser);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        
        when(ticketRepository.findAll()).thenReturn(List.of(sampleTicket));

        List<Ticket> tickets = ticketController.getAllTickets();

        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        verify(ticketRepository).findAll();
    }

   @Test
    void testValidateTicketSuccess() {
        when(ticketService.validateAndUseTicket("sampleQR")).thenReturn(sampleTicket);

        ResponseEntity<?> response = ticketController.validateTicket("sampleQR");

        assertEquals(200, response.getStatusCodeValue());

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(sampleTicket, body.get("ticket"));
        verify(ticketService).validateAndUseTicket("sampleQR");
    }

    @Test
    void testValidateTicketFailure() {
        when(ticketService.validateAndUseTicket("badQR")).thenThrow(new RuntimeException("Ticket not found"));

        ResponseEntity<?> response = ticketController.validateTicket("badQR");

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Ticket not found", body.get("error"));
        verify(ticketService).validateAndUseTicket("badQR");
    }
    @Test
    void testGenerateTicketsSuccess() {
        GenerateTicketsRequest request = new GenerateTicketsRequest();
        request.setEventId(1L);
        request.setCount(2);

        when(eventService.getEventById(1L)).thenReturn(sampleEvent);
        when(ticketService.generateTicketsForEvent(eq(sampleEvent), eq(2))).thenReturn(List.of(sampleTicket, sampleTicket));

        ResponseEntity<?> response = ticketController.generateTickets(request);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("tickets"));
        assertEquals("Vygenerováno 2 vstupenek", body.get("message"));
        verify(eventService).getEventById(1L);
        verify(ticketService).generateTicketsForEvent(sampleEvent, 2);
    }

    @Test
    void testGenerateTicketsEventNotFound() {
        GenerateTicketsRequest request = new GenerateTicketsRequest();
        request.setEventId(999L);
        request.setCount(2);

        when(eventService.getEventById(999L)).thenThrow(new IllegalArgumentException("Event s ID 999 nebyl nalezen"));

        ResponseEntity<?> response = ticketController.generateTickets(request);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("999"));
        verify(eventService).getEventById(999L);
    }

    @Test
    void testPurchaseTicketSuccess() {
        PurchaseTicketRequest request = new PurchaseTicketRequest();
        request.setEventId(1L);
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(ticketService.purchaseTicket(eq(sampleEvent), eq("John Doe"), eq("john@example.com"), eq(httpServletRequest)))
            .thenReturn(sampleTicket);

        ResponseEntity<?> response = ticketController.purchaseTicket(request, httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Vstupenka byla úspěšně zakoupena", body.get("message"));
        assertEquals(sampleTicket, body.get("ticket"));
        verify(eventRepository).findById(1L);
        verify(ticketService).purchaseTicket(sampleEvent, "John Doe", "john@example.com", httpServletRequest);
    }

@Test
    void testPurchaseTicketEventNotFound() {
        PurchaseTicketRequest request = new PurchaseTicketRequest();
        request.setEventId(999L);
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");

        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        ResponseEntity<?> response = ticketController.purchaseTicket(request, httpServletRequest);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("999"));
        verify(eventRepository).findById(999L);
    }


    @Test
    void testGetEventTicketsSuccess() {
        when(eventService.getEventById(1L)).thenReturn(sampleEvent);
        when(ticketService.getTicketsByEvent(sampleEvent)).thenReturn(List.of(sampleTicket));

        ResponseEntity<?> response = ticketController.getEventTickets(1L);

        assertEquals(200, response.getStatusCodeValue());
        List<?> tickets = (List<?>) response.getBody();
        assertEquals(1, tickets.size());
        verify(eventService).getEventById(1L);
        verify(ticketService).getTicketsByEvent(sampleEvent);
    }

    @Test
    void testGetEventTicketsEventNotFound() {
        when(eventService.getEventById(999L)).thenThrow(new IllegalArgumentException("Event s ID 999 nebyl nalezen"));

        ResponseEntity<?> response = ticketController.getEventTickets(999L);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("999"));
        verify(eventService).getEventById(999L);
    }
}