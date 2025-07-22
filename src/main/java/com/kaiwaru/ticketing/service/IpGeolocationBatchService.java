package com.kaiwaru.ticketing.service;

import com.kaiwaru.ticketing.dto.GeolocationResponse;
import com.kaiwaru.ticketing.model.IpProcessingQueue;
import com.kaiwaru.ticketing.model.VisitorSession;
import com.kaiwaru.ticketing.repository.IpProcessingQueueRepository;
import com.kaiwaru.ticketing.repository.VisitorSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IpGeolocationBatchService {
    private static final Logger logger = LoggerFactory.getLogger(IpGeolocationBatchService.class);
    
    // IP-API.com free tier allows 45 requests per minute
    // We'll use 40 to be safe and leave some buffer
    private static final int MAX_REQUESTS_PER_MINUTE = 40;
    private static final int PROCESSING_BATCH_SIZE = 40;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    @Autowired
    private IpProcessingQueueRepository ipQueueRepository;
    
    @Autowired
    private VisitorSessionRepository visitorSessionRepository;
    
    @Autowired
    private GeolocationService geolocationService;
    
    private int requestsThisMinute = 0;
    private LocalDateTime lastMinuteReset = LocalDateTime.now();

    @Scheduled(fixedRate = 15000) // Run every 15 seconds (4 times per minute)
    @Transactional
    public void processBatchOfIpAddresses() {
        try {
            resetRequestCounterIfNeeded();
            
            // Calculate how many requests we can make in this batch
            int requestsAvailable = MAX_REQUESTS_PER_MINUTE - requestsThisMinute;
            if (requestsAvailable <= 0) {
                logger.debug("Rate limit reached for this minute, waiting...");
                return;
            }
            
            // Process up to 10 IPs per batch (40/4 = 10 per 15-second interval)
            int batchSize = Math.min(10, requestsAvailable);
            
            List<IpProcessingQueue> pendingIps = ipQueueRepository.findPendingOrderedByPriorityWithLimit(batchSize);
            
            if (pendingIps.isEmpty()) {
                logger.debug("No pending IP addresses to process");
                return;
            }
            
            logger.info("Processing batch of {} IP addresses. Requests used this minute: {}/{}", 
                pendingIps.size(), requestsThisMinute, MAX_REQUESTS_PER_MINUTE);
            
            for (IpProcessingQueue ipEntry : pendingIps) {
                try {
                    processIpAddress(ipEntry);
                    requestsThisMinute++;
                } catch (Exception e) {
                    logger.error("Error processing IP {}: {}", ipEntry.getIpAddress(), e.getMessage(), e);
                    handleProcessingError(ipEntry, e.getMessage());
                }
                
                // Safety check to prevent rate limit exceeded
                if (requestsThisMinute >= MAX_REQUESTS_PER_MINUTE) {
                    logger.warn("Reached rate limit, stopping batch processing");
                    break;
                }
            }
            
        } catch (Exception e) {
            logger.error("Error in IP batch processing: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void resetStuckProcessingEntries() {
        try {
            LocalDateTime timeout = LocalDateTime.now().minusMinutes(10);
            int resetCount = ipQueueRepository.resetStuckProcessing(timeout);
            if (resetCount > 0) {
                logger.info("Reset {} stuck processing entries", resetCount);
            }
        } catch (Exception e) {
            logger.error("Error resetting stuck processing entries: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(fixedRate = 3600000) // Every hour
    @Transactional
    public void cleanupOldEntries() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
            int deletedCount = ipQueueRepository.deleteOldProcessedEntries(cutoff);
            if (deletedCount > 0) {
                logger.info("Cleaned up {} old processed IP entries", deletedCount);
            }
        } catch (Exception e) {
            logger.error("Error cleaning up old entries: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(fixedRate = 1800000) // Every 30 minutes
    @Transactional
    public void retryFailedEntries() {
        try {
            List<IpProcessingQueue> failedEntries = ipQueueRepository.findFailedForRetry();
            for (IpProcessingQueue entry : failedEntries) {
                entry.setProcessingStatus(IpProcessingQueue.ProcessingStatus.PENDING);
                entry.incrementRetryCount();
                ipQueueRepository.save(entry);
            }
            
            if (!failedEntries.isEmpty()) {
                logger.info("Queued {} failed entries for retry", failedEntries.size());
            }
        } catch (Exception e) {
            logger.error("Error retrying failed entries: {}", e.getMessage(), e);
        }
    }

    public void queueIpForProcessing(String ipAddress, Long visitorSessionId, Integer priority) {
        try {
            // Skip if IP already exists in queue
            Optional<IpProcessingQueue> existing = ipQueueRepository.findByIpAddress(ipAddress);
            if (existing.isPresent()) {
                logger.debug("IP {} already in queue with status: {}", ipAddress, existing.get().getProcessingStatus());
                return;
            }
            
            // Skip local/private IPs
            if (isLocalOrPrivateIP(ipAddress)) {
                logger.debug("Skipping local/private IP: {}", ipAddress);
                IpProcessingQueue entry = new IpProcessingQueue(ipAddress, visitorSessionId, priority);
                entry.markAsSkipped();
                ipQueueRepository.save(entry);
                return;
            }
            
            IpProcessingQueue entry = new IpProcessingQueue(ipAddress, visitorSessionId, priority);
            ipQueueRepository.save(entry);
            
            logger.debug("Queued IP {} for processing with priority {}", ipAddress, priority);
            
        } catch (Exception e) {
            logger.error("Error queueing IP {} for processing: {}", ipAddress, e.getMessage(), e);
        }
    }
    
    private void processIpAddress(IpProcessingQueue ipEntry) {
        try {
            ipEntry.markAsProcessing();
            ipQueueRepository.save(ipEntry);
            
            GeolocationResponse geoResponse = geolocationService.getGeolocation(ipEntry.getIpAddress());
            
            if (geoResponse != null && geoResponse.isSuccess()) {
                // Update the queue entry with geolocation data
                ipEntry.setCountry(geoResponse.getCountry());
                ipEntry.setCountryCode(geoResponse.getCountryCode());
                ipEntry.setRegion(geoResponse.getRegionName());
                ipEntry.setCity(geoResponse.getCity());
                ipEntry.setLatitude(geoResponse.getLatitude());
                ipEntry.setLongitude(geoResponse.getLongitude());
                ipEntry.setTimezone(geoResponse.getTimezone());
                ipEntry.setIsp(geoResponse.getIsp());
                ipEntry.markAsCompleted();
                
                // Update the related visitor session if it exists
                if (ipEntry.getVisitorSessionId() != null) {
                    updateVisitorSession(ipEntry);
                }
                
                logger.debug("Successfully processed IP {}: {}, {}", 
                    ipEntry.getIpAddress(), geoResponse.getCountry(), geoResponse.getCity());
                
            } else {
                throw new RuntimeException("Failed to get geolocation data");
            }
            
            ipQueueRepository.save(ipEntry);
            
        } catch (Exception e) {
            handleProcessingError(ipEntry, e.getMessage());
            throw e;
        }
    }
    
    private void updateVisitorSession(IpProcessingQueue ipEntry) {
        try {
            Optional<VisitorSession> sessionOpt = visitorSessionRepository.findById(ipEntry.getVisitorSessionId());
            if (sessionOpt.isPresent()) {
                VisitorSession session = sessionOpt.get();
                session.setCountry(ipEntry.getCountry());
                session.setCountryCode(ipEntry.getCountryCode());
                session.setRegion(ipEntry.getRegion());
                session.setCity(ipEntry.getCity());
                session.setLatitude(ipEntry.getLatitude());
                session.setLongitude(ipEntry.getLongitude());
                session.setTimezone(ipEntry.getTimezone());
                session.setIsp(ipEntry.getIsp());
                visitorSessionRepository.save(session);
                
                logger.debug("Updated visitor session {} with geolocation data", session.getId());
            }
        } catch (Exception e) {
            logger.error("Error updating visitor session {}: {}", ipEntry.getVisitorSessionId(), e.getMessage());
        }
    }
    
    private void handleProcessingError(IpProcessingQueue ipEntry, String error) {
        try {
            ipEntry.incrementRetryCount();
            
            if (ipEntry.hasReachedMaxRetries()) {
                ipEntry.markAsFailed(error);
                logger.warn("IP {} failed after {} attempts: {}", 
                    ipEntry.getIpAddress(), ipEntry.getRetryCount(), error);
            } else {
                ipEntry.setProcessingStatus(IpProcessingQueue.ProcessingStatus.PENDING);
                ipEntry.setLastError(error);
                logger.debug("IP {} failed, will retry (attempt {}): {}", 
                    ipEntry.getIpAddress(), ipEntry.getRetryCount(), error);
            }
            
            ipQueueRepository.save(ipEntry);
        } catch (Exception e) {
            logger.error("Error handling processing error for IP {}: {}", ipEntry.getIpAddress(), e.getMessage());
        }
    }
    
    private void resetRequestCounterIfNeeded() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getMinute() != lastMinuteReset.getMinute() || now.getHour() != lastMinuteReset.getHour()) {
            requestsThisMinute = 0;
            lastMinuteReset = now;
            logger.debug("Reset request counter for new minute");
        }
    }
    
    private boolean isLocalOrPrivateIP(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return true;
        }
        
        return ipAddress.equals("127.0.0.1") ||
               ipAddress.equals("::1") ||
               ipAddress.equals("0:0:0:0:0:0:0:1") ||
               ipAddress.startsWith("192.168.") ||
               ipAddress.startsWith("10.") ||
               ipAddress.startsWith("172.16.") ||
               ipAddress.startsWith("172.17.") ||
               ipAddress.startsWith("172.18.") ||
               ipAddress.startsWith("172.19.") ||
               ipAddress.matches("172\\.(2[0-9]|3[01])\\..*") ||
               ipAddress.equals("localhost");
    }
    
    public IpProcessingQueue.ProcessingStatus getQueueStatus() {
        try {
            List<Object[]> stats = ipQueueRepository.getStatusStatistics();
            long pending = 0, processing = 0, completed = 0, failed = 0;
            
            for (Object[] stat : stats) {
                IpProcessingQueue.ProcessingStatus status = (IpProcessingQueue.ProcessingStatus) stat[0];
                Long count = (Long) stat[1];
                
                switch (status) {
                    case PENDING -> pending = count;
                    case PROCESSING -> processing = count;
                    case COMPLETED -> completed = count;
                    case FAILED -> failed = count;
                }
            }
            
            logger.info("IP Queue Status - Pending: {}, Processing: {}, Completed: {}, Failed: {}, Requests this minute: {}/{}", 
                pending, processing, completed, failed, requestsThisMinute, MAX_REQUESTS_PER_MINUTE);
            
            return pending > 0 ? IpProcessingQueue.ProcessingStatus.PENDING : IpProcessingQueue.ProcessingStatus.COMPLETED;
        } catch (Exception e) {
            logger.error("Error getting queue status: {}", e.getMessage());
            return IpProcessingQueue.ProcessingStatus.FAILED;
        }
    }
}