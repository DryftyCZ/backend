package com.kaiwaru.ticketing.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.RealTimeStats;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.repository.RealTimeStatsRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.VisitorSessionRepository;
import com.kaiwaru.ticketing.service.CurrencyFormatService;

@Service
public class RealTimeStatsService {
    private static final Logger logger = LoggerFactory.getLogger(RealTimeStatsService.class);

    @Autowired
    private RealTimeStatsRepository realTimeStatsRepository;

    @Autowired
    private VisitorSessionRepository visitorSessionRepository;
    
    @Autowired
    private CurrencyFormatService currencyFormatService;

    @Autowired
    private TicketRepository ticketRepository;

    @Transactional
    public RealTimeStats generateRealTimeStats() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneMinuteAgo = now.minusMinutes(1);
            LocalDateTime oneHourAgo = now.minusHours(1);
            LocalDateTime fiveMinutesAgo = now.minusMinutes(5);

            logger.debug("Generating real-time stats for {}", now);

            // Generate global stats
            RealTimeStats globalStats = new RealTimeStats();
            globalStats.setTimestamp(now);

            // Count active visitors (last 5 minutes)
            Long activeVisitors = visitorSessionRepository.countUniqueVisitors(fiveMinutesAgo, now);
            globalStats.setActiveVisitors(activeVisitors.intValue());

            // Count sales in last minute and hour - using purchaseDate
            Long salesLastMinute = ticketRepository.countTicketsPurchasedBetween(oneMinuteAgo, now);
            Long salesLastHour = ticketRepository.countTicketsPurchasedBetween(oneHourAgo, now);

            globalStats.setSalesLastMinute(salesLastMinute.intValue());
            globalStats.setSalesLastHour(salesLastHour.intValue());

            // Calculate revenue using ticket prices
            BigDecimal revenueLastMinute = ticketRepository.sumRevenueBetween(oneMinuteAgo, now);
            BigDecimal revenueLastHour = ticketRepository.sumRevenueBetween(oneHourAgo, now);

            globalStats.setRevenueLastMinute(revenueLastMinute != null ? revenueLastMinute : BigDecimal.ZERO);
            globalStats.setRevenueLastHour(revenueLastHour != null ? revenueLastHour : BigDecimal.ZERO);

            // Calculate conversion rate
            Long totalVisitors = visitorSessionRepository.countTotalVisits(oneHourAgo, now);
            Long conversions = visitorSessionRepository.countConversions(oneHourAgo, now);
            
            if (totalVisitors > 0) {
                double conversionRate = (conversions.doubleValue() / totalVisitors.doubleValue()) * 100;
                globalStats.setConversionRate(conversionRate);
            }

            // Calculate bounce rate
            // Calculate bounce rate (safe)
            Long bounces = visitorSessionRepository.countBounces(oneHourAgo, now);
            Long totalSessions = visitorSessionRepository.countTotalSessions(oneHourAgo, now);

            if (totalSessions != null && totalSessions > 0) {
                double bounceRate = (bounces.doubleValue() / totalSessions.doubleValue()) * 100.0;
                globalStats.setBounceRate(bounceRate);
            } else {
                globalStats.setBounceRate(0.0);
            }

            // Calculate average session duration
            Double avgDuration = visitorSessionRepository.getAverageSessionDuration(oneHourAgo, now);
            globalStats.setAverageSessionDuration(avgDuration != null ? avgDuration : 0.0);

            // Calculate pages per session
            Double pagesPerSession = visitorSessionRepository.getAveragePagesPerSession(oneHourAgo, now);
            globalStats.setPagesPerSession(pagesPerSession != null ? pagesPerSession : 0.0);

            realTimeStatsRepository.save(globalStats);
            
            logger.debug("Generated real-time stats: {} active visitors, {} sales/min, {} {}/min", 
                activeVisitors, salesLastMinute, revenueLastMinute, currencyFormatService.getCurrencyCode());

            // Clean up old stats (keep last 24 hours)
            LocalDateTime cleanupBefore = now.minusHours(24);
            realTimeStatsRepository.deleteByTimestampBefore(cleanupBefore);

            return globalStats;
        } catch (Exception e) {
            logger.error("Error generating real-time stats: {}", e.getMessage(), e);
            return null;
        }
    }

    public List<RealTimeStats> getRecentStats(int limit) {
        // Ensure we have fresh stats first
        getLatestStats();
        
        List<RealTimeStats> stats = realTimeStatsRepository.findAllOrderByTimestampDesc();
        return stats.size() > limit ? stats.subList(0, limit) : stats;
    }

    public List<RealTimeStats> getStatsForEvent(Event event, int limit) {
        List<RealTimeStats> stats = realTimeStatsRepository.findByEventOrderByTimestampDesc(event);
        return stats.size() > limit ? stats.subList(0, limit) : stats;
    }

    public RealTimeStats getLatestStats() {
        // Generate fresh stats when requested (only when real-time tab is accessed)
        RealTimeStats latestFromDb = realTimeStatsRepository.findTopByOrderByTimestampDesc().orElse(null);
        
        // If no stats or stats are older than 2 minutes, generate fresh ones
        if (latestFromDb == null || latestFromDb.getTimestamp().isBefore(LocalDateTime.now().minusMinutes(2))) {
            return generateRealTimeStats();
        }
        
        return latestFromDb;
    }

    public RealTimeStats getLatestStatsForEvent(Event event) {
        return realTimeStatsRepository.findTopByEventOrderByTimestampDesc(event).orElse(null);
    }

    @Transactional
    public void recordTicketPurchase(Ticket ticket) {
        try {
            // This method can be called from ticket purchase flow
            // to trigger immediate stats update if needed
            logger.debug("Recorded ticket purchase: {} for event {}", 
                ticket.getId(), ticket.getEvent().getName());
        } catch (Exception e) {
            logger.error("Error recording ticket purchase: {}", e.getMessage(), e);
        }
    }
}