package com.kaiwaru.ticketing.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kaiwaru.ticketing.exception.EntityNotFoundException;
import com.kaiwaru.ticketing.model.TicketType;
import com.kaiwaru.ticketing.repository.TicketTypeRepository;

@Service
public class TicketTypeService {
    
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    
    public TicketType getTicketTypeById(Long id) {
        return ticketTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Typ vstupenky s ID " + id + " nebyl nalezen"));
    }
    
    public List<TicketType> getTicketTypesByEventId(Long eventId) {
        return ticketTypeRepository.findByEventId(eventId);
    }
    
    public List<TicketType> getAvailableTicketTypesByEventId(Long eventId) {
        return ticketTypeRepository.findByEventIdAndAvailableQuantityGreaterThan(eventId, 0);
    }
    
    public TicketType saveTicketType(TicketType ticketType) {
        return ticketTypeRepository.save(ticketType);
    }
}