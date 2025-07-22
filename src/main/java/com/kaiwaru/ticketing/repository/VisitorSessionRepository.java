package com.kaiwaru.ticketing.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.VisitorSession;

@Repository
public interface VisitorSessionRepository extends JpaRepository<VisitorSession, Long> {
    
    Optional<VisitorSession> findBySessionId(String sessionId);
    
    @Query("SELECT COUNT(DISTINCT vs.sessionId) FROM VisitorSession vs WHERE vs.timestamp >= :start AND vs.timestamp <= :end")
    Long countUniqueVisitors(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(DISTINCT vs.sessionId) FROM VisitorSession vs WHERE vs.event = :event AND vs.timestamp >= :start AND vs.timestamp <= :end")
    Long countUniqueVisitorsByEvent(@Param("event") Event event, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(vs) FROM VisitorSession vs WHERE vs.timestamp >= :start AND vs.timestamp <= :end")
    Long countTotalVisits(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(vs) FROM VisitorSession vs WHERE vs.converted = true AND vs.timestamp >= :start AND vs.timestamp <= :end")
    Long countConversions(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT vs.country, vs.countryCode, COUNT(vs), SUM(vs.revenueGenerated) " +
           "FROM VisitorSession vs " +
           "WHERE vs.timestamp >= :start AND vs.timestamp <= :end " +
           "GROUP BY vs.country, vs.countryCode " +
           "ORDER BY COUNT(vs) DESC")
    List<Object[]> getGeolocationStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT vs.trafficSource, COUNT(vs), COUNT(CASE WHEN vs.converted = true THEN 1 END) " +
           "FROM VisitorSession vs " +
           "WHERE vs.timestamp >= :start AND vs.timestamp <= :end " +
           "GROUP BY vs.trafficSource " +
           "ORDER BY COUNT(vs) DESC")
    List<Object[]> getTrafficSourceStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT vs FROM VisitorSession vs WHERE vs.timestamp >= :since")
    List<VisitorSession> findActiveVisitors(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(vs.durationSeconds) FROM VisitorSession vs WHERE vs.durationSeconds IS NOT NULL AND vs.timestamp >= :start AND vs.timestamp <= :end")
    Double getAverageSessionDuration(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT AVG(vs.pageViews) FROM VisitorSession vs WHERE vs.timestamp >= :start AND vs.timestamp <= :end")
    Double getAveragePagesPerSession(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(vs) * 100.0 / (SELECT COUNT(all_vs) FROM VisitorSession all_vs WHERE all_vs.timestamp >= :start AND all_vs.timestamp <= :end) " +
           "FROM VisitorSession vs " +
           "WHERE vs.pageViews = 1 AND vs.durationSeconds < 30 AND vs.timestamp >= :start AND vs.timestamp <= :end")
    Double getBounceRate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(vs) FROM VisitorSession vs " +
                  "WHERE vs.pageViews = 1 AND vs.durationSeconds < 30 " +
                  "AND vs.timestamp >= :start AND vs.timestamp <= :end")
    Long countBounces(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(vs) FROM VisitorSession vs " +
                  "WHERE vs.timestamp >= :start AND vs.timestamp <= :end")
    Long countTotalSessions(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT DATE(vs.timestamp), COUNT(vs), SUM(vs.revenueGenerated) " +
           "FROM VisitorSession vs " +
           "WHERE vs.timestamp >= :start AND vs.timestamp <= :end " +
           "GROUP BY DATE(vs.timestamp) " +
           "ORDER BY DATE(vs.timestamp)")
    List<Object[]> getDailyStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}