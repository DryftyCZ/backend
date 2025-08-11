package com.kaiwaru.ticketing.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.model.TicketType;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.TicketTypeRepository;
import com.kaiwaru.ticketing.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TicketPurchaseTrackingService ticketPurchaseTrackingService;
    
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    
    @Autowired
    private UserRepository userRepository;

   @Transactional
    public List<Ticket> generateTicketsForEvent(Event event, int count) {
        List<Ticket> tickets = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Ticket ticket = new Ticket();
            ticket.setEvent(event);
            ticket.setQrCode(qrCodeService.generateUniqueTicketCode());
            ticket.setTicketNumber(qrCodeService.generateTicketNumber());
            ticket.setUsed(false);

            tickets.add(ticketRepository.save(ticket));
        }
        return tickets;
    }

    @Transactional
    public Ticket purchaseTicket(Event event, String customerName, String customerEmail, String ipAddress) {
        Optional<Ticket> availableTicketOpt = ticketRepository.findFirstByEventAndCustomerEmailIsNull(event);
        if (availableTicketOpt.isEmpty()) {
            throw new RuntimeException("Žádné dostupné vstupenky pro tuto akci");
        }

        Ticket ticket = availableTicketOpt.get();
        ticket.setCustomerName(customerName);
        ticket.setCustomerEmail(customerEmail);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setIpAddress(ipAddress);

        ticket = ticketRepository.save(ticket);

        try {
            String qrCodeImage = qrCodeService.generateQRCode(ticket.getQrCode());
            //emailService.sendTicketEmail(ticket, qrCodeImage);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při generování nebo odeslání QR kódu: " + e.getMessage(), e);
        }

        return ticket;
    }
    
    @Transactional
    public Ticket purchaseTicket(Event event, String customerName, String customerEmail, HttpServletRequest request) {
        Optional<Ticket> availableTicketOpt = ticketRepository.findFirstByEventAndCustomerEmailIsNull(event);
        if (availableTicketOpt.isEmpty()) {
            throw new RuntimeException("Žádné dostupné vstupenky pro tuto akci");
        }

        Ticket ticket = availableTicketOpt.get();
        ticket.setCustomerName(customerName);
        ticket.setCustomerEmail(customerEmail);
        ticket.setPurchaseDate(LocalDateTime.now());

        ticket = ticketRepository.save(ticket);
        
        // Track the purchase with visitor IP and city information
        ticketPurchaseTrackingService.trackTicketPurchase(ticket, request);

        try {
            String qrCodeImage = qrCodeService.generateQRCode(ticket.getQrCode());
            //emailService.sendTicketEmail(ticket, qrCodeImage);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při generování nebo odeslání QR kódu: " + e.getMessage(), e);
        }

        return ticket;
    }

    @Transactional
    public Ticket validateAndUseTicket(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Neplatný QR kód"));

        if (ticket.isUsed()) {
            throw new RuntimeException("Vstupenka již byla použita");
        }

        if (ticket.getCustomerEmail() == null) {
            throw new RuntimeException("Vstupenka nebyla zakoupena");
        }

        ticket.setUsed(true);
        ticket.setUsedDate(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getTicketsByEvent(Event event) {
        return ticketRepository.findByEvent(event);
    }
    
    /**
     * Purchase tickets for a specific ticket type
     * This creates new tickets on demand and associates them with the ticket type
     */
    @Transactional
    public List<Ticket> purchaseTicketsWithType(Long ticketTypeId, String customerName, String customerEmail, 
                                                 Integer quantity, HttpServletRequest request, User customer) {
        // Find the ticket type
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new RuntimeException("Typ vstupenky nebyl nalezen"));
        
        // Check availability
        if (ticketType.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Nedostatek dostupných vstupenek. Zbývá pouze: " + ticketType.getAvailableQuantity());
        }
        
        List<Ticket> purchasedTickets = new ArrayList<>();
        
        // Create tickets for this purchase
        for (int i = 0; i < quantity; i++) {
            Ticket ticket = new Ticket();
            ticket.setEvent(ticketType.getEvent());
            ticket.setTicketType(ticketType);
            ticket.setQrCode(qrCodeService.generateUniqueTicketCode());
            ticket.setTicketNumber(qrCodeService.generateTicketNumber());
            ticket.setCustomerName(customerName);
            ticket.setCustomerEmail(customerEmail);
            ticket.setCustomer(customer);
            ticket.setPurchaseDate(LocalDateTime.now());
            ticket.setUsed(false);
            
            ticket = ticketRepository.save(ticket);
            purchasedTickets.add(ticket);
            
            // Track the purchase
            ticketPurchaseTrackingService.trackTicketPurchase(ticket, request);
        }
        
        // Update available quantity
        ticketType.setAvailableQuantity(ticketType.getAvailableQuantity() - quantity);
        ticketTypeRepository.save(ticketType);
        
        // Send emails
        try {
            for (Ticket ticket : purchasedTickets) {
                String qrCodeImage = qrCodeService.generateQRCode(ticket.getQrCode());
                //emailService.sendTicketEmail(ticket, qrCodeImage);
            }
        } catch (Exception e) {
            // Log error but don't fail the purchase
            System.err.println("Error sending ticket emails: " + e.getMessage());
        }
        
        return purchasedTickets;
    }
}

