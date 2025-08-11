package com.kaiwaru.ticketing.service;

import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.model.VisitorSession;
import com.kaiwaru.ticketing.repository.VisitorSessionRepository;
import com.kaiwaru.ticketing.service.CurrencyFormatService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class VisitorTrackingService {
    private static final Logger logger = LoggerFactory.getLogger(VisitorTrackingService.class);

    @Autowired
    private VisitorSessionRepository visitorSessionRepository;
    
    @Autowired
    private CurrencyFormatService currencyFormatService;

    @Autowired
    private IpGeolocationBatchService ipGeolocationBatchService;

    @Async
    @Transactional
    public CompletableFuture<VisitorSession> trackVisitor(HttpServletRequest request, Event event, User user) {
        try {
            String sessionId = getOrCreateSessionId(request);
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            String referer = request.getHeader("Referer");

            // Check if session already exists
            VisitorSession session = visitorSessionRepository.findBySessionId(sessionId)
                .orElse(null);

            if (session == null) {
                // Create new session
                session = new VisitorSession(sessionId, ipAddress, userAgent, referer);
                session.setEvent(event);
                session.setUser(user);
                session.setTrafficSource(determineTrafficSource(referer));

                // Save session first to get ID
                session = visitorSessionRepository.save(session);
                
                // Queue IP for geolocation processing (will be processed in batches)
                Integer priority = determineGeolocationPriority(event, user);
                ipGeolocationBatchService.queueIpForProcessing(ipAddress, session.getId(), priority);

                logger.info("Created new visitor session: {} from {} (IP queued for geolocation)", 
                    sessionId, ipAddress);
            } else {
                // Update existing session
                session.setPageViews(session.getPageViews() + 1);
                session.setTimestamp(LocalDateTime.now());
                if (event != null && session.getEvent() == null) {
                    session.setEvent(event);
                }
                if (user != null && session.getUser() == null) {
                    session.setUser(user);
                }
                session = visitorSessionRepository.save(session);
                logger.debug("Updated visitor session: {} (page views: {})", 
                    sessionId, session.getPageViews());
            }

            return CompletableFuture.completedFuture(session);
        } catch (Exception e) {
            logger.error("Error tracking visitor: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Transactional
    public void recordConversion(String sessionId, int ticketsPurchased, double revenue) {
        try {
            visitorSessionRepository.findBySessionId(sessionId).ifPresent(session -> {
                session.setConverted(true);
                session.setTicketsPurchased(ticketsPurchased);
                session.setRevenueGenerated(revenue);
                visitorSessionRepository.save(session);
                logger.info("Recorded conversion for session {}: {} tickets, {} {}", 
                    sessionId, ticketsPurchased, revenue, currencyFormatService.getCurrencyCode());
            });
        } catch (Exception e) {
            logger.error("Error recording conversion for session {}: {}", sessionId, e.getMessage(), e);
        }
    }

    @Transactional
    public void updateSessionDuration(String sessionId, long durationSeconds) {
        try {
            visitorSessionRepository.findBySessionId(sessionId).ifPresent(session -> {
                session.setDurationSeconds(durationSeconds);
                visitorSessionRepository.save(session);
            });
        } catch (Exception e) {
            logger.error("Error updating session duration for {}: {}", sessionId, e.getMessage(), e);
        }
    }

    private String getOrCreateSessionId(HttpServletRequest request) {
        // Try to get session ID from various sources
        String sessionId = request.getParameter("sessionId");
        if (sessionId == null) {
            sessionId = request.getHeader("X-Session-ID");
        }
        if (sessionId == null && request.getSession(false) != null) {
            sessionId = request.getSession().getId();
        }
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
        }
        return sessionId;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Get first IP if multiple
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        String ip = request.getRemoteAddr();
        return ip != null ? ip : "127.0.0.1";
    }

    private String determineTrafficSource(String referer) {
        if (referer == null || referer.isEmpty()) {
            return "Direct";
        }

        String refererLower = referer.toLowerCase();
        
        if (refererLower.contains("google.")) {
            return "Google Search";
        } else if (refererLower.contains("facebook.") || refererLower.contains("fb.")) {
            return "Facebook";
        } else if (refererLower.contains("instagram.")) {
            return "Instagram";
        } else if (refererLower.contains("twitter.") || refererLower.contains("t.co")) {
            return "Twitter";
        } else if (refererLower.contains("linkedin.")) {
            return "LinkedIn";
        } else if (refererLower.contains("youtube.")) {
            return "YouTube";
        } else if (refererLower.contains("seznam.")) {
            return "Seznam";
        } else if (refererLower.contains("bing.")) {
            return "Bing";
        } else if (refererLower.contains("yahoo.")) {
            return "Yahoo";
        } else if (refererLower.contains("email") || refererLower.contains("newsletter")) {
            return "Email";
        } else {
            return "Referral";
        }
    }

    private Integer determineGeolocationPriority(Event event, User user) {
        // Priority 1-10 (1 = highest priority, 10 = lowest)
        
        if (user != null) {
            // Registered users get higher priority
            return 2;
        }
        
        if (event != null) {
            // Event-specific visitors get medium priority
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime eventDate = event.getDate().atStartOfDay();
            
            if (eventDate.isAfter(now) && eventDate.isBefore(now.plusDays(7))) {
                // Events within a week get high priority
                return 3;
            } else if (eventDate.isAfter(now) && eventDate.isBefore(now.plusDays(30))) {
                // Events within a month get medium priority
                return 5;
            }
        }
        
        // General visitors get lower priority
        return 7;
    }
}