package com.kaiwaru.ticketing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaiwaru.ticketing.dto.OrganizerCommissionDto;
import com.kaiwaru.ticketing.dto.UpdateCommissionRequest;
import com.kaiwaru.ticketing.exception.EntityNotFoundException;
import com.kaiwaru.ticketing.model.OrganizerCommission;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.OrganizerCommissionRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.UserRepository;

@Service
public class OrganizerCommissionService {

    @Autowired
    private OrganizerCommissionRepository commissionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private TicketRepository ticketRepository;

    public List<OrganizerCommissionDto> getAllOrganizersWithCommissions() {
        // Get all users with ORGANIZER role
        List<User> organizers = userRepository.findByRolesName("ORGANIZER");
        
        return organizers.stream().map(organizer -> {
            OrganizerCommission commission = commissionRepository.findByOrganizer(organizer)
                    .orElse(createDefaultCommission(organizer));
            
            // Calculate statistics
            Integer totalEvents = eventRepository.findByOrganizer(organizer).size();
            BigDecimal totalRevenue = calculateTotalRevenue(organizer);
            BigDecimal totalCommission = totalRevenue.multiply(commission.getCommissionPercentage())
                    .divide(BigDecimal.valueOf(100));
            
            return new OrganizerCommissionDto(
                commission.getId(),
                organizer.getId(),
                organizer.getUsername(),
                organizer.getEmail(),
                commission.getCommissionPercentage(),
                commission.getIsActive(),
                totalEvents,
                totalRevenue,
                totalCommission
            );
        }).collect(Collectors.toList());
    }
    
    @Transactional
    public OrganizerCommissionDto updateCommission(UpdateCommissionRequest request) {
        User organizer = userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizátor s ID " + request.getOrganizerId() + " nebyl nalezen"));
        
        // Check if user has ORGANIZER role
        boolean isOrganizer = organizer.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ORGANIZER"));
        
        if (!isOrganizer) {
            throw new IllegalArgumentException("Uživatel není organizátor");
        }
        
        OrganizerCommission commission = commissionRepository.findByOrganizer(organizer)
                .orElse(new OrganizerCommission());
        
        commission.setOrganizer(organizer);
        commission.setCommissionPercentage(request.getCommissionPercentage());
        commission.setIsActive(request.getIsActive());
        
        commission = commissionRepository.save(commission);
        
        // Calculate statistics for response
        Integer totalEvents = eventRepository.findByOrganizer(organizer).size();
        BigDecimal totalRevenue = calculateTotalRevenue(organizer);
        BigDecimal totalCommission = totalRevenue.multiply(commission.getCommissionPercentage())
                .divide(BigDecimal.valueOf(100));
        
        return new OrganizerCommissionDto(
            commission.getId(),
            organizer.getId(),
            organizer.getUsername(),
            organizer.getEmail(),
            commission.getCommissionPercentage(),
            commission.getIsActive(),
            totalEvents,
            totalRevenue,
            totalCommission
        );
    }
    
    public BigDecimal getOrganizerCommissionPercentage(User organizer) {
        return commissionRepository.findByOrganizer(organizer)
                .map(OrganizerCommission::getCommissionPercentage)
                .orElse(BigDecimal.valueOf(20.0)); // Default 20%
    }
    
    private OrganizerCommission createDefaultCommission(User organizer) {
        if (commissionRepository.existsByOrganizerId(organizer.getId())) {
            return commissionRepository.findByOrganizer(organizer).get();
        }
        
        OrganizerCommission commission = new OrganizerCommission();
        commission.setOrganizer(organizer);
        commission.setCommissionPercentage(BigDecimal.valueOf(20.0)); // Default 20%
        commission.setIsActive(true);
        return commissionRepository.save(commission);
    }
    
    private BigDecimal calculateTotalRevenue(User organizer) {
        return eventRepository.findByOrganizer(organizer).stream()
                .flatMap(event -> ticketRepository.findByEvent(event).stream())
                .filter(ticket -> ticket.getTicketType() != null)
                .map(ticket -> ticket.getTicketType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}