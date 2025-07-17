package com.kaiwaru.ticketing.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kaiwaru.ticketing.exception.EntityNotFoundException;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.repository.EventRepository;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event s ID " + id + " nebyl nalezen"));
    }
    
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
    
    public Event createEvent(Event event) {
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Název eventu nesmí být prázdný.");
        }

        if (event.getDate() == null) {
            throw new IllegalArgumentException("Datum eventu nesmí být prázdné.");
        }

        if (event.getLocation() == null || event.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Lokace eventu nesmí být prázdná.");
        }
        return eventRepository.save(event);
    }

    public Optional<Event> updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id).map(existingEvent -> {
            existingEvent.setName(updatedEvent.getName());
            existingEvent.setDescription(updatedEvent.getDescription());
            existingEvent.setLocation(updatedEvent.getLocation());
            existingEvent.setDate(updatedEvent.getDate());
            return eventRepository.save(existingEvent);
        });
    }

    public boolean deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
