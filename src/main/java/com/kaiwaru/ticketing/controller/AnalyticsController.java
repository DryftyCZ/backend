package com.kaiwaru.ticketing.controller;

import com.kaiwaru.ticketing.dto.EventAnalyticsDto;
import com.kaiwaru.ticketing.service.EventAnalyticsService;
import com.kaiwaru.ticketing.service.VisitorTrackingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private VisitorTrackingService visitorTrackingService;

    @Autowired
    private EventAnalyticsService eventAnalyticsService;

    @PostMapping("/conversion")
    public ResponseEntity<?> recordConversion(
            @RequestBody Map<String, Object> conversionData,
            HttpServletRequest request) {
        
        try {
            String sessionId = (String) conversionData.get("sessionId");
            Integer ticketsPurchased = (Integer) conversionData.get("ticketsPurchased");
            Double revenue = (Double) conversionData.get("revenue");
            
            if (sessionId == null) {
                // Try to get session ID from request
                sessionId = request.getSession().getId();
            }
            
            if (sessionId != null && ticketsPurchased != null && revenue != null) {
                visitorTrackingService.recordConversion(sessionId, ticketsPurchased, revenue);
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/session-duration")
    public ResponseEntity<?> updateSessionDuration(
            @RequestBody Map<String, Object> sessionData,
            HttpServletRequest request) {
        
        try {
            String sessionId = (String) sessionData.get("sessionId");
            Long duration = ((Number) sessionData.get("duration")).longValue();
            
            if (sessionId == null) {
                sessionId = request.getSession().getId();
            }
            
            if (sessionId != null && duration != null) {
                visitorTrackingService.updateSessionDuration(sessionId, duration);
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/events")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('WORKER')")
    public ResponseEntity<List<EventAnalyticsDto>> getAllEventsAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<EventAnalyticsDto> analytics = eventAnalyticsService.getAllEventsAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('WORKER')")
    public ResponseEntity<EventAnalyticsDto> getEventAnalytics(
            @PathVariable Long eventId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        EventAnalyticsDto analytics = eventAnalyticsService.getEventAnalytics(eventId, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }
}