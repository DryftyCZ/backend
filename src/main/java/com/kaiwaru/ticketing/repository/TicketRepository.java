package com.kaiwaru.ticketing.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // Real-time analytics queries
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.purchaseDate >= :start AND t.purchaseDate <= :end")
    Long countTicketsPurchasedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(tt.price) FROM Ticket t JOIN t.ticketType tt WHERE t.purchaseDate >= :start AND t.purchaseDate <= :end")
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT t FROM Ticket t WHERE t.purchaseDate >= :start AND t.purchaseDate <= :end ORDER BY t.purchaseDate DESC")
    List<Ticket> findTicketsPurchasedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event = :event AND t.purchaseDate >= :start AND t.purchaseDate <= :end")
    Long countTicketsPurchasedForEventBetween(@Param("event") Event event, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(tt.price) FROM Ticket t JOIN t.ticketType tt WHERE t.event = :event AND t.purchaseDate >= :start AND t.purchaseDate <= :end")
    BigDecimal sumRevenueForEventBetween(@Param("event") Event event, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // City-based sales statistics
    @Query("SELECT t.city, t.country, COUNT(t), SUM(tt.price) " +
           "FROM Ticket t JOIN t.ticketType tt " +
           "WHERE t.city IS NOT NULL AND t.purchaseDate >= :start AND t.purchaseDate <= :end " +
           "GROUP BY t.city, t.country " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getCitySalesStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.city IS NOT NULL AND t.purchaseDate >= :start AND t.purchaseDate <= :end")
    Long getTotalTicketsWithCity(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}