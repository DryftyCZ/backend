package com.kaiwaru.ticketing.repository;

import com.kaiwaru.ticketing.model.EmbedVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EmbedVisitRepository extends JpaRepository<EmbedVisit, Long> {

    @Query("SELECT COUNT(ev) FROM EmbedVisit ev WHERE ev.eventId = :eventId")
    Long countVisitsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(ev) FROM EmbedVisit ev WHERE ev.eventId = :eventId AND ev.converted = true")
    Long countConversionsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(ev) FROM EmbedVisit ev WHERE ev.eventId = :eventId AND ev.timestamp >= :start AND ev.timestamp <= :end")
    Long countVisitsByEventIdAndDateRange(@Param("eventId") Long eventId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(ev) FROM EmbedVisit ev WHERE ev.eventId = :eventId AND ev.converted = true AND ev.timestamp >= :start AND ev.timestamp <= :end")
    Long countConversionsByEventIdAndDateRange(@Param("eventId") Long eventId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(ev) FROM EmbedVisit ev")
    Long countAllVisits();

    @Query("SELECT COUNT(ev) FROM EmbedVisit ev WHERE ev.converted = true")
    Long countAllConversions();

    @Query("SELECT COUNT(ev) FROM EmbedVisit ev WHERE ev.customerId = :customerId")
    Long countVisitsByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT COUNT(ev) FROM EmbedVisit ev WHERE ev.customerId = :customerId AND ev.converted = true")
    Long countConversionsByCustomerId(@Param("customerId") String customerId);
}