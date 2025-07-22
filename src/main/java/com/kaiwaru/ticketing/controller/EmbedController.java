package com.kaiwaru.ticketing.controller;

import com.kaiwaru.ticketing.model.EmbedVisit;
import com.kaiwaru.ticketing.repository.EmbedVisitRepository;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/embed")
@CrossOrigin(origins = "*") // Povolit všechny domény pro iframe
public class EmbedController {

    @Autowired
    private EmbedVisitRepository embedVisitRepository;

    @Autowired
    private EventRepository eventRepository;

    /**
     * Endpoint pro iframe widget
     * URL: /embed/event/{eventId}?customer={customerId}
     */
    @GetMapping("/event/{eventId}")
    public String embedEvent(@PathVariable Long eventId, 
                           @RequestParam(required = false) String customer,
                           HttpServletRequest request,
                           HttpSession session,
                           Model model) {
        
        // Najít event
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            model.addAttribute("error", "Event not found");
            return "embed/error";
        }

        // Vytvořit nebo získat session ID
        String sessionId = (String) session.getAttribute("embedSessionId");
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            session.setAttribute("embedSessionId", sessionId);
        }

        // Log návštěvy
        logVisit(eventId, customer, request, sessionId);

        // Přidat data do modelu pro Thymeleaf template
        model.addAttribute("event", event);
        model.addAttribute("eventId", eventId);
        model.addAttribute("customerId", customer);
        model.addAttribute("sessionId", sessionId);

        return "embed/event-widget"; // Thymeleaf template
    }

    /**
     * API endpoint pro tracking návštěv
     */
    @PostMapping("/api/track-visit")
    @ResponseBody
    public ResponseEntity<?> trackVisit(@RequestBody Map<String, Object> payload,
                                      HttpServletRequest request) {
        try {
            Long eventId = Long.valueOf(payload.get("eventId").toString());
            String customerId = (String) payload.get("customerId");
            String sessionId = (String) payload.get("sessionId");

            logVisit(eventId, customerId, request, sessionId);

            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * API endpoint pro tracking konverze
     */
    @PostMapping("/api/track-conversion")
    @ResponseBody
    public ResponseEntity<?> trackConversion(@RequestBody Map<String, Object> payload) {
        try {
            String sessionId = (String) payload.get("sessionId");
            
            // Najít visit podle session ID a označit jako converted
            embedVisitRepository.findAll().stream()
                .filter(visit -> sessionId.equals(visit.getSessionId()))
                .forEach(visit -> {
                    visit.setConverted(true);
                    embedVisitRepository.save(visit);
                });

            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private void logVisit(Long eventId, String customerId, HttpServletRequest request, String sessionId) {
        try {
            String referrerUrl = request.getHeader("Referer");
            String visitorIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            EmbedVisit visit = new EmbedVisit(eventId, customerId, referrerUrl, visitorIp, userAgent, sessionId);
            embedVisitRepository.save(visit);
        } catch (Exception e) {
            // Log error but don't fail the request
            e.printStackTrace();
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
}