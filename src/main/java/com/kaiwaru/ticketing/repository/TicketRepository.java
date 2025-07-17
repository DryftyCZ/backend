package com.kaiwaru.ticketing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Ticket;

import jakarta.persistence.LockModeType;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Ticket> findFirstByEventAndCustomerEmailIsNull(Event event);
    Optional<Ticket> findByQrCode(String qrCode);
    List<Ticket> findByEvent(Event event);
    List<Ticket> findByCustomerEmail(String customerEmail);

    long countByEventAndUsed(Event event, boolean used);
    Optional<Ticket> findFirstByCountryIsNullAndIpAddressIsNotNullOrderByPurchaseDateAsc();
}