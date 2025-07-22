package com.kaiwaru.ticketing.repository;

import com.kaiwaru.ticketing.model.IpProcessingQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IpProcessingQueueRepository extends JpaRepository<IpProcessingQueue, Long> {
    
    Optional<IpProcessingQueue> findByIpAddress(String ipAddress);
    
    @Query("SELECT ipq FROM IpProcessingQueue ipq WHERE ipq.processingStatus = 'PENDING' ORDER BY ipq.priority ASC, ipq.createdAt ASC")
    List<IpProcessingQueue> findPendingOrderedByPriority();
    
    @Query("SELECT ipq FROM IpProcessingQueue ipq WHERE ipq.processingStatus = 'PENDING' ORDER BY ipq.priority ASC, ipq.createdAt ASC LIMIT :limit")
    List<IpProcessingQueue> findPendingOrderedByPriorityWithLimit(@Param("limit") int limit);
    
    List<IpProcessingQueue> findByProcessingStatus(IpProcessingQueue.ProcessingStatus status);
    
    @Query("SELECT COUNT(ipq) FROM IpProcessingQueue ipq WHERE ipq.processingStatus = :status")
    long countByStatus(@Param("status") IpProcessingQueue.ProcessingStatus status);
    
    @Query("SELECT ipq FROM IpProcessingQueue ipq WHERE ipq.processingStatus = 'PROCESSING' AND ipq.processedAt < :timeout")
    List<IpProcessingQueue> findStuckProcessing(@Param("timeout") LocalDateTime timeout);
    
    @Modifying
    @Query("UPDATE IpProcessingQueue ipq SET ipq.processingStatus = 'PENDING' WHERE ipq.processingStatus = 'PROCESSING' AND ipq.processedAt < :timeout")
    int resetStuckProcessing(@Param("timeout") LocalDateTime timeout);
    
    @Modifying
    @Query("DELETE FROM IpProcessingQueue ipq WHERE ipq.processingStatus IN ('COMPLETED', 'FAILED', 'SKIPPED') AND ipq.processedAt < :before")
    int deleteOldProcessedEntries(@Param("before") LocalDateTime before);
    
    @Query("SELECT ipq FROM IpProcessingQueue ipq WHERE ipq.processingStatus = 'FAILED' AND ipq.retryCount < 3")
    List<IpProcessingQueue> findFailedForRetry();
    
    @Query("SELECT ipq.processingStatus as status, COUNT(ipq) as count FROM IpProcessingQueue ipq GROUP BY ipq.processingStatus")
    List<Object[]> getStatusStatistics();
}