package com.kaiwaru.ticketing.repository;

import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.RealTimeStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RealTimeStatsRepository extends JpaRepository<RealTimeStats, Long> {
    
    @Query("SELECT rts FROM RealTimeStats rts WHERE rts.event = :event ORDER BY rts.timestamp DESC")
    List<RealTimeStats> findByEventOrderByTimestampDesc(@Param("event") Event event);
    
    @Query("SELECT rts FROM RealTimeStats rts WHERE rts.event = :event AND rts.timestamp >= :since ORDER BY rts.timestamp DESC")
    List<RealTimeStats> findByEventAndTimestampAfter(@Param("event") Event event, @Param("since") LocalDateTime since);
    
    @Query("SELECT rts FROM RealTimeStats rts WHERE rts.timestamp >= :since ORDER BY rts.timestamp DESC")
    List<RealTimeStats> findByTimestampAfter(@Param("since") LocalDateTime since);
    
    @Query("SELECT rts FROM RealTimeStats rts ORDER BY rts.timestamp DESC")
    List<RealTimeStats> findAllOrderByTimestampDesc();
    
    Optional<RealTimeStats> findTopByEventOrderByTimestampDesc(@Param("event") Event event);
    
    Optional<RealTimeStats> findTopByOrderByTimestampDesc();
    
    // Use JPA method name for delete operation
    void deleteByTimestampBefore(LocalDateTime before);
}