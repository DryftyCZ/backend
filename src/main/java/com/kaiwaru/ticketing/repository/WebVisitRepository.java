package com.kaiwaru.ticketing.repository;

import com.kaiwaru.ticketing.model.WebVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface WebVisitRepository extends JpaRepository<WebVisit, Long> {

    @Query("SELECT COUNT(wv) FROM WebVisit wv WHERE wv.customerId = :customerId")
    Long countVisitsByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT COUNT(wv) FROM WebVisit wv WHERE wv.customerId = :customerId AND wv.timestamp >= :start AND wv.timestamp <= :end")
    Long countVisitsByCustomerIdAndDateRange(@Param("customerId") String customerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(wv) FROM WebVisit wv WHERE wv.eventRelated = true AND wv.eventId = :eventId")
    Long countEventRelatedVisits(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(wv) FROM WebVisit wv WHERE wv.eventRelated = true AND wv.eventId = :eventId AND wv.timestamp >= :start AND wv.timestamp <= :end")
    Long countEventRelatedVisitsByDateRange(@Param("eventId") Long eventId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(wv) FROM WebVisit wv")
    Long countAllVisits();

    @Query("SELECT COUNT(wv) FROM WebVisit wv WHERE wv.timestamp >= :start AND wv.timestamp <= :end")
    Long countAllVisitsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT wv.sessionId) FROM WebVisit wv WHERE wv.customerId = :customerId")
    Long countUniqueVisitorsByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT COUNT(DISTINCT wv.sessionId) FROM WebVisit wv WHERE wv.eventRelated = true AND wv.eventId = :eventId")
    Long countUniqueVisitorsForEvent(@Param("eventId") Long eventId);
}