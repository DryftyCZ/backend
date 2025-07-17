package com.kaiwaru.ticketing.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.kaiwaru.ticketing.dto.GenerateTicketsRequest;
import com.kaiwaru.ticketing.dto.PurchaseTicketRequest;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.service.TicketService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

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

        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(ticketService.generateTicketsForEvent(eq(sampleEvent), eq(2))).thenReturn(List.of(sampleTicket, sampleTicket));

        ResponseEntity<?> response = ticketController.generateTickets(request);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("tickets"));
        assertEquals("Vygenerováno 2 vstupenek", body.get("message"));
        verify(eventRepository).findById(1L);
        verify(ticketService).generateTicketsForEvent(sampleEvent, 2);
    }

    @Test
    void testGenerateTicketsEventNotFound() {
        GenerateTicketsRequest request = new GenerateTicketsRequest();
        request.setEventId(999L);
        request.setCount(2);

        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = ticketController.generateTickets(request);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("999"));
        verify(eventRepository).findById(999L);
    }

    @Test
    void testPurchaseTicketSuccess() {
        PurchaseTicketRequest request = new PurchaseTicketRequest();
        request.setEventId(1L);
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(ticketService.purchaseTicket(eq(sampleEvent), eq("John Doe"), eq("john@example.com"), eq("127.0.0.1")))
            .thenReturn(sampleTicket);

        ResponseEntity<?> response = ticketController.purchaseTicket(request, httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Vstupenka byla úspěšně zakoupena", body.get("message"));
        assertEquals(sampleTicket, body.get("ticket"));
        verify(eventRepository).findById(1L);
        verify(ticketService).purchaseTicket(sampleEvent, "John Doe", "john@example.com", "127.0.0.1");
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
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(ticketService.getTicketsByEvent(sampleEvent)).thenReturn(List.of(sampleTicket));

        ResponseEntity<?> response = ticketController.getEventTickets(1L);

        assertEquals(200, response.getStatusCodeValue());
        List<?> tickets = (List<?>) response.getBody();
        assertEquals(1, tickets.size());
        verify(eventRepository).findById(1L);
        verify(ticketService).getTicketsByEvent(sampleEvent);
    }

    @Test
    void testGetEventTicketsEventNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = ticketController.getEventTickets(999L);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("999"));
        verify(eventRepository).findById(999L);
    }
}
