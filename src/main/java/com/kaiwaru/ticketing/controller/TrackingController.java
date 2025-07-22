package com.kaiwaru.ticketing.controller;

import com.kaiwaru.ticketing.model.WebVisit;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.repository.WebVisitRepository;
import com.kaiwaru.ticketing.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*") // Povolit všechny domény pro tracking
public class TrackingController {

    @Autowired
    private WebVisitRepository webVisitRepository;
    
    @Autowired
    private EventRepository eventRepository;

    /**
     * API endpoint pro tracking návštěv zákazníkových webů
     * URL: POST /api/tracking/visit
     */
    @PostMapping("/visit")
    public ResponseEntity<?> trackVisit(@RequestBody Map<String, Object> payload,
                                      HttpServletRequest request) {
        try {
            String customerId = (String) payload.get("customerId");
            String pageUrl = (String) payload.get("pageUrl");
            String sessionId = (String) payload.get("sessionId");
            
            // Generovat session ID pokud není poskytnut
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }

            // Získat visitor info
            String referrerUrl = request.getHeader("Referer");
            String visitorIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            // Vytvořit web visit
            WebVisit webVisit = new WebVisit(customerId, pageUrl, visitorIp, userAgent, sessionId, referrerUrl);

            // Určit zda je visit related k eventu
            if (payload.containsKey("eventId") && payload.get("eventId") != null) {
                Long eventId = Long.valueOf(payload.get("eventId").toString());
                
                // Check if event is still active for tracking
                if (!isEventActiveForTracking(eventId)) {
                    return ResponseEntity.ok(Map.of(
                        "status", "tracking_stopped",
                        "message", "Event tracking period has ended",
                        "sessionId", sessionId
                    ));
                }
                
                webVisit.setEventRelated(true);
                webVisit.setEventId(eventId);
            }

            webVisitRepository.save(webVisit);

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "sessionId", sessionId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint pro tracking pixel (1x1 transparent GIF)
     * URL: GET /api/tracking/pixel.gif?customer=abc123&page=/akce&event=456
     */
    @GetMapping("/pixel.gif")
    public ResponseEntity<byte[]> trackingPixel(@RequestParam String customer,
                                              @RequestParam(required = false) String page,
                                              @RequestParam(required = false) String event,
                                              HttpServletRequest request) {
        try {
            // Log visit
            String sessionId = UUID.randomUUID().toString();
            String referrerUrl = request.getHeader("Referer");
            String visitorIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            WebVisit webVisit = new WebVisit(customer, page, visitorIp, userAgent, sessionId, referrerUrl);

            if (event != null && !event.isEmpty()) {
                Long eventId = Long.valueOf(event);
                
                // Check if event is still active for tracking
                if (!isEventActiveForTracking(eventId)) {
                    // Still return the pixel but don't save the visit
                    byte[] gifBytes = new byte[]{
                        0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00, 
                        (byte)0x80, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
                        0x00, 0x00, 0x00, 0x21, (byte)0xF9, 0x04, 0x01, 0x00, 0x00, 
                        0x00, 0x00, 0x2C, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 
                        0x00, 0x00, 0x02, 0x02, 0x04, 0x01, 0x00, 0x3B
                    };
                    return ResponseEntity.ok()
                        .header("Content-Type", "image/gif")
                        .header("Cache-Control", "no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(gifBytes);
                }
                
                webVisit.setEventRelated(true);
                webVisit.setEventId(eventId);
            }

            webVisitRepository.save(webVisit);

            // Vrátit 1x1 transparent GIF
            byte[] gifBytes = new byte[]{
                0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00, 
                (byte)0x80, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
                0x00, 0x00, 0x00, 0x21, (byte)0xF9, 0x04, 0x01, 0x00, 0x00, 
                0x00, 0x00, 0x2C, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 
                0x00, 0x00, 0x02, 0x02, 0x04, 0x01, 0x00, 0x3B
            };

            return ResponseEntity.ok()
                .header("Content-Type", "image/gif")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(gifBytes);
        } catch (Exception e) {
            // Vrátit prázdný GIF i při chybě
            byte[] emptyGif = new byte[]{
                0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00, 
                (byte)0x80, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
                0x00, 0x00, 0x00, 0x21, (byte)0xF9, 0x04, 0x01, 0x00, 0x00, 
                0x00, 0x00, 0x2C, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 
                0x00, 0x00, 0x02, 0x02, 0x04, 0x01, 0x00, 0x3B
            };
            return ResponseEntity.ok()
                .header("Content-Type", "image/gif")
                .body(emptyGif);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Check if event is still active for tracking (7 days after event date)
     */
    private boolean isEventActiveForTracking(Long eventId) {
        if (eventId == null) {
            return true; // No event specified, allow tracking
        }
        
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            return false; // Event not found
        }
        
        Event event = eventOpt.get();
        LocalDate eventDate = event.getDate();
        if (eventDate == null) {
            return true; // No date set, allow tracking
        }
        
        LocalDate today = LocalDate.now();
        long daysSinceEvent = ChronoUnit.DAYS.between(eventDate, today);
        
        // Allow tracking up to 7 days after the event
        boolean isActive = daysSinceEvent <= 7;
        
        if (!isActive) {
            System.out.println("Tracking stopped for event " + event.getName() + 
                             " (ended " + daysSinceEvent + " days ago)");
        }
        
        return isActive;
    }
}