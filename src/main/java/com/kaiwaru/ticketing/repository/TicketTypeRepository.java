package com.kaiwaru.ticketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kaiwaru.ticketing.model.TicketType;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    List<TicketType> findByEventId(Long eventId);
    List<TicketType> findByEventIdAndAvailableQuantityGreaterThan(Long eventId, Integer quantity);
}