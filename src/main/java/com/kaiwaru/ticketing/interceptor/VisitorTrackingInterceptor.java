package com.kaiwaru.ticketing.interceptor;

import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.service.VisitorTrackingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class VisitorTrackingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(VisitorTrackingInterceptor.class);

    @Autowired
    private VisitorTrackingService visitorTrackingService;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // Only track GET requests to avoid tracking API calls
            if (!"GET".equals(request.getMethod())) {
                return true;
            }

            // Skip tracking for certain paths
            String path = request.getRequestURI();
            if (shouldSkipTracking(path)) {
                return true;
            }

            // Get current user if authenticated
            User currentUser = getCurrentUser();

            // Try to extract event from URL
            Event currentEvent = extractEventFromPath(path);

            // Track visitor asynchronously
            visitorTrackingService.trackVisitor(request, currentEvent, currentUser);

        } catch (Exception e) {
            logger.error("Error in visitor tracking interceptor: {}", e.getMessage(), e);
            // Don't fail the request if tracking fails
        }

        return true;
    }

    private boolean shouldSkipTracking(String path) {
        return path.startsWith("/api/") ||
               path.startsWith("/admin/") ||
               path.startsWith("/static/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/favicon.ico") ||
               path.startsWith("/actuator/");
    }

    private User getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                // Extract user from authentication
                // This depends on your security configuration
                return null; // TODO: Implement based on your User model
            }
        } catch (Exception e) {
            logger.debug("Could not get current user: {}", e.getMessage());
        }
        return null;
    }

    private Event extractEventFromPath(String path) {
        try {
            // Try to extract event ID from URLs like /events/{id} or /event/{id}
            if (path.matches(".*/(events?|udalosti?)/\\d+.*")) {
                String[] parts = path.split("/");
                for (int i = 0; i < parts.length - 1; i++) {
                    if (parts[i].matches("events?|udalosti?") && i + 1 < parts.length) {
                        try {
                            Long eventId = Long.parseLong(parts[i + 1]);
                            Optional<Event> event = eventRepository.findById(eventId);
                            if (event.isPresent()) {
                                return event.get();
                            }
                        } catch (NumberFormatException e) {
                            // Not a valid event ID
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Could not extract event from path {}: {}", path, e.getMessage());
        }
        return null;
    }
}