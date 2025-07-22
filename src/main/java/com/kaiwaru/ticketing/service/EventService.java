package com.kaiwaru.ticketing.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaiwaru.ticketing.dto.EventCreateRequest;
import com.kaiwaru.ticketing.exception.EntityNotFoundException;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.TicketType;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.TicketTypeRepository;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.model.Auth.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.kaiwaru.ticketing.repository.UserRepository;
import com.kaiwaru.ticketing.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Event getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event s ID " + id + " nebyl nalezen"));
        
        // Check if user has access to this event
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser) && !isEventOrganizer(event, currentUser)) {
            throw new AccessDeniedException("Nemáte oprávnění zobrazit tento event");
        }
        
        return event;
    }
    
    public List<Event> getAllEvents() {
        User currentUser = getCurrentUser();
        
        // Admins see all events
        if (isAdmin(currentUser)) {
            return eventRepository.findAll();
        }
        
        // Organizers see only their events
        return eventRepository.findByOrganizer(currentUser);
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

        if (event.getCity() == null || event.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("Město eventu nesmí být prázdné.");
        }
        return eventRepository.save(event);
    }

    @Transactional
    public Event createEventWithTicketTypes(EventCreateRequest request) {
        // Validate event data
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Název eventu nesmí být prázdný.");
        }

        if (request.getDate() == null) {
            throw new IllegalArgumentException("Datum eventu nesmí být prázdné.");
        }

        if (request.getCity() == null || request.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("Město eventu nesmí být prázdné.");
        }

        if (request.getTicketTypes() == null || request.getTicketTypes().isEmpty()) {
            throw new IllegalArgumentException("Musí být definován alespoň jeden typ vstupenky.");
        }

        // Create and save event
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setAddress(request.getAddress());
        event.setCity(request.getCity());
        event.setDate(request.getDate());
        
        // Set the current user as organizer
        User currentUser = getCurrentUser();
        event.setOrganizer(currentUser);
        
        Event savedEvent = eventRepository.save(event);

        // Create ticket types
        List<TicketType> ticketTypes = request.getTicketTypes().stream()
                .map(ticketTypeRequest -> {
                    TicketType ticketType = new TicketType();
                    ticketType.setName(ticketTypeRequest.getName());
                    ticketType.setDescription(ticketTypeRequest.getDescription());
                    ticketType.setPrice(ticketTypeRequest.getPrice());
                    ticketType.setQuantity(ticketTypeRequest.getQuantity());
                    ticketType.setAvailableQuantity(ticketTypeRequest.getQuantity());
                    ticketType.setEvent(savedEvent);
                    return ticketType;
                })
                .collect(Collectors.toList());

        ticketTypeRepository.saveAll(ticketTypes);
        savedEvent.setTicketTypes(ticketTypes);

        return savedEvent;
    }

    public Optional<Event> updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id).map(existingEvent -> {
            // Check if user has access to update this event
            User currentUser = getCurrentUser();
            if (!isAdmin(currentUser) && !isEventOrganizer(existingEvent, currentUser)) {
                throw new AccessDeniedException("Nemáte oprávnění upravit tento event");
            }
            
            existingEvent.setName(updatedEvent.getName());
            existingEvent.setDescription(updatedEvent.getDescription());
            existingEvent.setAddress(updatedEvent.getAddress());
            existingEvent.setCity(updatedEvent.getCity());
            existingEvent.setDate(updatedEvent.getDate());
            return eventRepository.save(existingEvent);
        });
    }

    public boolean deleteEvent(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            
            // Check if user has access to delete this event
            User currentUser = getCurrentUser();
            if (!isAdmin(currentUser) && !isEventOrganizer(event, currentUser)) {
                throw new AccessDeniedException("Nemáte oprávnění smazat tento event");
            }
            
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Uživatel nebyl nalezen"));
        }
        throw new AccessDeniedException("Uživatel není přihlášen");
    }
    
    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }
    
    private boolean isEventOrganizer(Event event, User user) {
        return event.getOrganizer() != null && event.getOrganizer().getId().equals(user.getId());
    }
}
