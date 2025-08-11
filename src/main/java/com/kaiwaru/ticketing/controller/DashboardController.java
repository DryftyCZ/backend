package com.kaiwaru.ticketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.security.UserPrincipal;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.repository.UserRepository;
import com.kaiwaru.ticketing.service.DashboardService;
import com.kaiwaru.ticketing.service.RealTimeStatsService;
import com.kaiwaru.ticketing.service.IpGeolocationBatchService;
import com.kaiwaru.ticketing.dto.DashboardStatsDto;
import com.kaiwaru.ticketing.dto.TodayAgendaDto;
import com.kaiwaru.ticketing.dto.NextMonthCashFlowDto;
import com.kaiwaru.ticketing.model.RealTimeStats;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private RealTimeStatsService realTimeStatsService;

    @Autowired
    private IpGeolocationBatchService ipGeolocationBatchService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<?> getDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("welcome", "Welcome to SmartTickets Dashboard!");
        dashboardData.put("user", Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail(),
            "roles", user.getRoles().stream().map(role -> role.getName()).toList()
        ));
        dashboardData.put("message", "You are successfully logged in!");
        dashboardData.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('WORKER')")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        DashboardStatsDto stats = dashboardService.getDashboardStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/realtime")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('WORKER')")
    public ResponseEntity<RealTimeStats> getRealTimeStats() {
        RealTimeStats stats = realTimeStatsService.getLatestStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/realtime/recent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('WORKER')")
    public ResponseEntity<java.util.List<RealTimeStats>> getRecentRealTimeStats(
            @RequestParam(value = "limit", defaultValue = "30") int limit) {
        java.util.List<RealTimeStats> stats = realTimeStatsService.getRecentStats(limit);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/ip-queue-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<Map<String, Object>> getIpQueueStatus() {
        try {
            var status = ipGeolocationBatchService.getQueueStatus();
            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("message", "IP geolocation queue is " + status.toString().toLowerCase());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get queue status");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/today-agenda")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('WORKER')")
    public ResponseEntity<List<TodayAgendaDto>> getTodayAgenda() {
        List<TodayAgendaDto> agenda = dashboardService.getTodayAgenda();
        return ResponseEntity.ok(agenda);
    }

    @GetMapping("/next-month-cashflow")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasRole('WORKER')")
    public ResponseEntity<NextMonthCashFlowDto> getNextMonthCashFlow() {
        NextMonthCashFlowDto cashFlow = dashboardService.getNextMonthCashFlow();
        return ResponseEntity.ok(cashFlow);
    }
}