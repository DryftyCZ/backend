package com.kaiwaru.ticketing.service;

import com.kaiwaru.ticketing.model.IpProcessingQueue;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.model.VisitorSession;
import com.kaiwaru.ticketing.repository.IpProcessingQueueRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.VisitorSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TicketPurchaseTrackingService {
    private static final Logger logger = LoggerFactory.getLogger(TicketPurchaseTrackingService.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VisitorSessionRepository visitorSessionRepository;

    @Autowired
    private IpProcessingQueueRepository ipProcessingQueueRepository;

    @Autowired
    private VisitorTrackingService visitorTrackingService;

    @Async
    @Transactional
    public void trackTicketPurchase(Ticket ticket, HttpServletRequest request) {
        try {
            String ipAddress = getClientIpAddress(request);
            String sessionId = getSessionId(request);
            
            // Set IP address on ticket
            ticket.setIpAddress(ipAddress);
            
            // Try to get city from current session first
            String city = getCityFromSession(sessionId);
            String country = getCountryFromSession(sessionId);
            
            if (city == null) {
                // Try to get city from processed IP queue
                city = getCityFromIpQueue(ipAddress);
                country = getCountryFromIpQueue(ipAddress);
            }
            
            if (city != null) {
                ticket.setCity(city);
                ticket.setCountry(country);
                logger.info("Tracked ticket purchase from city: {} for ticket {}", city, ticket.getId());
            } else {
                logger.debug("City not yet available for ticket {}, IP: {}", ticket.getId(), ipAddress);
            }
            
            ticketRepository.save(ticket);
            
            // Record conversion in visitor tracking
            if (sessionId != null) {
                double revenue = ticket.getTicketType() != null ? 
                    ticket.getTicketType().getPrice().doubleValue() : 0.0;
                visitorTrackingService.recordConversion(sessionId, 1, revenue);
            }
            
        } catch (Exception e) {
            logger.error("Error tracking ticket purchase for ticket {}: {}", 
                ticket.getId(), e.getMessage(), e);
        }
    }

    private String getCityFromSession(String sessionId) {
        if (sessionId == null) return null;
        
        try {
            Optional<VisitorSession> session = visitorSessionRepository.findBySessionId(sessionId);
            if (session.isPresent() && session.get().getCity() != null) {
                return session.get().getCity();
            }
        } catch (Exception e) {
            logger.debug("Error getting city from session {}: {}", sessionId, e.getMessage());
        }
        return null;
    }

    private String getCountryFromSession(String sessionId) {
        if (sessionId == null) return null;
        
        try {
            Optional<VisitorSession> session = visitorSessionRepository.findBySessionId(sessionId);
            if (session.isPresent() && session.get().getCountry() != null) {
                return session.get().getCountry();
            }
        } catch (Exception e) {
            logger.debug("Error getting country from session {}: {}", sessionId, e.getMessage());
        }
        return null;
    }

    private String getCityFromIpQueue(String ipAddress) {
        if (ipAddress == null) return null;
        
        try {
            Optional<IpProcessingQueue> ipEntry = ipProcessingQueueRepository.findByIpAddress(ipAddress);
            if (ipEntry.isPresent() && 
                ipEntry.get().getProcessingStatus() == IpProcessingQueue.ProcessingStatus.COMPLETED &&
                ipEntry.get().getCity() != null) {
                return ipEntry.get().getCity();
            }
        } catch (Exception e) {
            logger.debug("Error getting city from IP queue for {}: {}", ipAddress, e.getMessage());
        }
        return null;
    }

    private String getCountryFromIpQueue(String ipAddress) {
        if (ipAddress == null) return null;
        
        try {
            Optional<IpProcessingQueue> ipEntry = ipProcessingQueueRepository.findByIpAddress(ipAddress);
            if (ipEntry.isPresent() && 
                ipEntry.get().getProcessingStatus() == IpProcessingQueue.ProcessingStatus.COMPLETED &&
                ipEntry.get().getCountry() != null) {
                return ipEntry.get().getCountry();
            }
        } catch (Exception e) {
            logger.debug("Error getting country from IP queue for {}: {}", ipAddress, e.getMessage());
        }
        return null;
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
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        String ip = request.getRemoteAddr();
        return ip != null ? ip : "127.0.0.1";
    }

    private String getSessionId(HttpServletRequest request) {
        try {
            String sessionId = request.getParameter("sessionId");
            if (sessionId == null) {
                sessionId = request.getHeader("X-Session-ID");
            }
            if (sessionId == null && request.getSession(false) != null) {
                sessionId = request.getSession().getId();
            }
            return sessionId;
        } catch (Exception e) {
            logger.debug("Error getting session ID: {}", e.getMessage());
            return null;
        }
    }

    @Async
    @Transactional
    public void updateTicketCityFromQueue() {
        try {
            // Find tickets without city that have processed IP addresses
            Optional<Ticket> ticketOpt = ticketRepository.findFirstByCountryIsNullAndIpAddressIsNotNullOrderByPurchaseDateAsc();
            
            if (ticketOpt.isPresent()) {
                Ticket ticket = ticketOpt.get();
                String city = getCityFromIpQueue(ticket.getIpAddress());
                String country = getCountryFromIpQueue(ticket.getIpAddress());
                
                if (city != null) {
                    ticket.setCity(city);
                    ticket.setCountry(country);
                    ticketRepository.save(ticket);
                    
                    logger.info("Updated ticket {} with city: {}", ticket.getId(), city);
                }
            }
        } catch (Exception e) {
            logger.error("Error updating ticket city from queue: {}", e.getMessage(), e);
        }
    }
}