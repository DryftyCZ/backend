package com.kaiwaru.ticketing.service;

import com.kaiwaru.ticketing.dto.EventAnalyticsDto;
import com.kaiwaru.ticketing.dto.EventAnalyticsDto.*;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.model.TicketType;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.UserRepository;
import com.kaiwaru.ticketing.repository.EmbedVisitRepository;
import com.kaiwaru.ticketing.repository.WebVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventAnalyticsService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmbedVisitRepository embedVisitRepository;

    @Autowired
    private WebVisitRepository webVisitRepository;

    public List<EventAnalyticsDto> getAllEventsAnalytics() {
        return getAllEventsAnalytics(null, null);
    }

    public List<EventAnalyticsDto> getAllEventsAnalytics(String startDate, String endDate) {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(event -> getEventAnalytics(event, startDate, endDate))
                .collect(Collectors.toList());
    }

    public EventAnalyticsDto getEventAnalytics(Long eventId) {
        return getEventAnalytics(eventId, null, null);
    }

    public EventAnalyticsDto getEventAnalytics(Long eventId, String startDate, String endDate) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        return getEventAnalytics(event, startDate, endDate);
    }

    private EventAnalyticsDto getEventAnalytics(Event event) {
        return getEventAnalytics(event, null, null);
    }

    private EventAnalyticsDto getEventAnalytics(Event event, String startDate, String endDate) {
        EventAnalyticsDto analytics = new EventAnalyticsDto();
        
        // Basic event info
        analytics.setEventId(event.getId());
        analytics.setEventName(event.getName());
        analytics.setEventDescription(event.getDescription());
        analytics.setEventCity(event.getCity());
        
        // Convert LocalDate to LocalDateTime for consistency
        LocalDateTime eventDateTime = event.getDate().atTime(20, 0); // Default 8 PM start
        LocalDateTime eventEndDateTime = event.getDate().atTime(23, 0); // Default 11 PM end
        
        analytics.setEventDate(eventDateTime);
        analytics.setEndDate(eventEndDateTime);
        
        // Determine real status based on actual dates
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        
        // Real status logic
        if (event.getDate().isAfter(today)) {
            analytics.setEventStatus("UPCOMING");
            analytics.setActive(true);
            analytics.setCompleted(false);
        } else if (event.getDate().isEqual(today)) {
            analytics.setEventStatus("TODAY");
            analytics.setActive(true);
            analytics.setCompleted(false);
        } else {
            analytics.setEventStatus("COMPLETED");
            analytics.setActive(false);
            analytics.setCompleted(true);
        }
        
        // Calculate real days until event
        if (event.getDate().isAfter(today)) {
            analytics.setDaysUntilEvent((int) ChronoUnit.DAYS.between(today, event.getDate()));
        } else if (event.getDate().isEqual(today)) {
            analytics.setDaysUntilEvent(0);
        } else {
            analytics.setDaysUntilEvent((int) ChronoUnit.DAYS.between(event.getDate(), today) * -1);
        }
        
        // Parse date range for filtering
        LocalDateTime startDateTime = parseDate(startDate);
        LocalDateTime endDateTime = parseDate(endDate);
        
        // Calculate REAL sales analytics from actual data
        calculateRealSalesAnalytics(event, analytics, startDateTime, endDateTime);
        
        // Calculate REAL ticket type analytics
        calculateRealTicketTypeAnalytics(event, analytics, startDateTime, endDateTime);
        
        // Calculate REAL customer analytics
        calculateRealCustomerAnalytics(event, analytics, startDateTime, endDateTime);
        
        // Calculate REAL geographic analytics
        calculateRealGeographicAnalytics(event, analytics, startDateTime, endDateTime);
        
        // Calculate REAL time-based analytics
        calculateRealTimeBasedAnalytics(event, analytics, startDateTime, endDateTime);
        
        return analytics;
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            // Parse ISO date format (YYYY-MM-DD)
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isTicketInDateRange(Ticket ticket, LocalDateTime startDate, LocalDateTime endDate) {
        if (ticket.getPurchaseDate() == null) {
            return false; // Skip tickets without purchase date
        }
        
        LocalDateTime purchaseDate = ticket.getPurchaseDate();
        
        if (startDate != null && purchaseDate.isBefore(startDate)) {
            return false;
        }
        
        if (endDate != null && purchaseDate.isAfter(endDate.plusDays(1).minusSeconds(1))) {
            return false;
        }
        
        return true;
    }

    private void calculateRealSalesAnalytics(Event event, EventAnalyticsDto analytics) {
        calculateRealSalesAnalytics(event, analytics, null, null);
    }

    private void calculateRealSalesAnalytics(Event event, EventAnalyticsDto analytics, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalTicketsSold = 0;
        int totalTicketsAvailable = 0;
        BigDecimal highestPrice = BigDecimal.ZERO;
        BigDecimal lowestPrice = null;
        
        // Get actual sold tickets from database
        List<Ticket> allSoldTickets = ticketRepository.findByEvent(event);
        
        // Filter tickets by date range if specified
        List<Ticket> soldTickets = allSoldTickets.stream()
                .filter(ticket -> isTicketInDateRange(ticket, startDate, endDate))
                .collect(Collectors.toList());
        
        if (event.getTicketTypes() != null) {
            for (TicketType ticketType : event.getTicketTypes()) {
                // Count REAL sold tickets for this ticket type
                int soldForThisType = (int) soldTickets.stream()
                    .filter(ticket -> ticket.getTicketType().equals(ticketType))
                    .count();
                
                // Calculate REAL revenue from actual sold tickets
                BigDecimal revenueForThisType = ticketType.getPrice().multiply(BigDecimal.valueOf(soldForThisType));
                
                totalRevenue = totalRevenue.add(revenueForThisType);
                totalTicketsSold += soldForThisType;
                totalTicketsAvailable += ticketType.getQuantity();
                
                // Find real highest and lowest prices
                if (ticketType.getPrice().compareTo(highestPrice) > 0) {
                    highestPrice = ticketType.getPrice();
                }
                if (lowestPrice == null || ticketType.getPrice().compareTo(lowestPrice) < 0) {
                    lowestPrice = ticketType.getPrice();
                }
            }
        }
        
        analytics.setTotalRevenue(totalRevenue);
        analytics.setTotalTicketsSold(totalTicketsSold);
        analytics.setTotalTicketsAvailable(totalTicketsAvailable);
        analytics.setTicketsRemaining(totalTicketsAvailable - totalTicketsSold);
        analytics.setSalesPercentage(totalTicketsAvailable > 0 ? (double) totalTicketsSold / totalTicketsAvailable * 100 : 0.0);
        analytics.setHighestTicketPrice(highestPrice);
        analytics.setLowestTicketPrice(lowestPrice != null ? lowestPrice : BigDecimal.ZERO);
        
        // Calculate REAL average ticket price from actual sales
        if (totalTicketsSold > 0) {
            analytics.setAverageTicketPrice(totalRevenue.divide(BigDecimal.valueOf(totalTicketsSold), 2, RoundingMode.HALF_UP));
        } else {
            analytics.setAverageTicketPrice(BigDecimal.ZERO);
        }
        
        // Calculate REAL revenue growth by comparing with previous similar events
        calculateRealRevenueGrowth(event, analytics);
        
        // Calculate REAL conversion rate based on embed visits
        analytics.setConversionRate(calculateEmbedConversionRate(event.getId()));
        
        // Determine REAL sales trend based on actual data
        determineSalesTrend(event, analytics, soldTickets);
    }

    private void calculateRealRevenueGrowth(Event event, EventAnalyticsDto analytics) {
        // Find similar events by same city in the past
        List<Event> similarPastEvents = eventRepository.findAll().stream()
            .filter(e -> e.getCity() != null && event.getCity() != null && e.getCity().equals(event.getCity()))
            .filter(e -> e.getDate().isBefore(event.getDate()))
            .filter(e -> !e.getId().equals(event.getId()))
            .collect(Collectors.toList());
        
        if (!similarPastEvents.isEmpty()) {
            // Calculate average revenue of similar past events
            BigDecimal totalPastRevenue = BigDecimal.ZERO;
            int pastEventCount = 0;
            
            for (Event pastEvent : similarPastEvents) {
                List<Ticket> pastTickets = ticketRepository.findByEvent(pastEvent);
                for (Ticket ticket : pastTickets) {
                    totalPastRevenue = totalPastRevenue.add(ticket.getTicketType().getPrice());
                }
                pastEventCount++;
            }
            
            if (pastEventCount > 0) {
                BigDecimal avgPastRevenue = totalPastRevenue.divide(BigDecimal.valueOf(pastEventCount), 2, RoundingMode.HALF_UP);
                if (avgPastRevenue.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal growthRate = analytics.getTotalRevenue()
                        .subtract(avgPastRevenue)
                        .divide(avgPastRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                    analytics.setRevenueGrowth(growthRate);
                } else {
                    analytics.setRevenueGrowth(BigDecimal.ZERO);
                }
            } else {
                analytics.setRevenueGrowth(BigDecimal.ZERO);
            }
        } else {
            analytics.setRevenueGrowth(BigDecimal.ZERO); // No past events to compare
        }
    }

    private void determineSalesTrend(Event event, EventAnalyticsDto analytics, List<Ticket> soldTickets) {
        // Analyze sales trend based on when tickets were actually sold
        if (soldTickets.isEmpty()) {
            analytics.setSalesTrend(0); // No sales
            return;
        }
        
        // Sort tickets by purchase date (when they were sold)
        List<Ticket> sortedTickets = soldTickets.stream()
            .filter(ticket -> ticket.getPurchaseDate() != null)
            .sorted(Comparator.comparing(Ticket::getPurchaseDate))
            .collect(Collectors.toList());
        
        if (sortedTickets.size() < 2) {
            analytics.setSalesTrend(0); // Not enough data
            return;
        }
        
        // Get first and last sale dates
        LocalDateTime firstSale = sortedTickets.get(0).getPurchaseDate();
        LocalDateTime lastSale = sortedTickets.get(sortedTickets.size() - 1).getPurchaseDate();
        
        analytics.setFirstSaleDate(firstSale);
        analytics.setLastSaleDate(lastSale);
        
        // Calculate trend based on recent sales activity
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        long recentSales = sortedTickets.stream()
            .filter(ticket -> ticket.getPurchaseDate() != null && ticket.getPurchaseDate().isAfter(oneDayAgo))
            .count();
        
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        long previousDaySales = sortedTickets.stream()
            .filter(ticket -> ticket.getPurchaseDate() != null && ticket.getPurchaseDate().isAfter(twoDaysAgo) && ticket.getPurchaseDate().isBefore(oneDayAgo))
            .count();
        
        if (recentSales > previousDaySales) {
            analytics.setSalesTrend(1); // Increasing
        } else if (recentSales < previousDaySales) {
            analytics.setSalesTrend(-1); // Decreasing
        } else {
            analytics.setSalesTrend(0); // Stable
        }
    }

    private void calculateRealTicketTypeAnalytics(Event event, EventAnalyticsDto analytics) {
        calculateRealTicketTypeAnalytics(event, analytics, null, null);
    }

    private void calculateRealTicketTypeAnalytics(Event event, EventAnalyticsDto analytics, LocalDateTime startDate, LocalDateTime endDate) {
        List<TicketTypeAnalyticsDto> ticketTypeAnalytics = new ArrayList<>();
        
        if (event.getTicketTypes() != null) {
            for (TicketType ticketType : event.getTicketTypes()) {
                // Count REAL sold tickets for this type (filtered by date range)
                List<Ticket> allSoldTickets = ticketRepository.findByEvent(event);
                int sold = (int) allSoldTickets.stream()
                    .filter(ticket -> ticket.getTicketType().equals(ticketType))
                    .filter(ticket -> isTicketInDateRange(ticket, startDate, endDate))
                    .count();
                
                int available = ticketType.getQuantity() - sold;
                BigDecimal revenue = ticketType.getPrice().multiply(BigDecimal.valueOf(sold));
                double salesPercentage = ticketType.getQuantity() > 0 ? (double) sold / ticketType.getQuantity() * 100 : 0.0;
                
                ticketTypeAnalytics.add(new TicketTypeAnalyticsDto(
                    ticketType.getName(),
                    ticketType.getPrice(),
                    ticketType.getQuantity(),
                    sold,
                    available,
                    revenue,
                    salesPercentage
                ));
            }
        }
        
        analytics.setTicketTypeAnalytics(ticketTypeAnalytics);
    }

    /**
     * Calculate conversion rate based on embed widget visits
     */
    private double calculateEmbedConversionRate(Long eventId) {
        try {
            Long totalVisits = embedVisitRepository.countVisitsByEventId(eventId);
            Long conversions = embedVisitRepository.countConversionsByEventId(eventId);
            
            if (totalVisits > 0) {
                return (conversions.doubleValue() / totalVisits.doubleValue()) * 100.0;
            }
            
            return 0.0;
        } catch (Exception e) {
            // Fallback to old calculation if embed tracking fails
            System.err.println("Embed conversion calculation failed: " + e.getMessage());
            return 0.0;
        }
    }

    private void calculateRealCustomerAnalytics(Event event, EventAnalyticsDto analytics) {
        calculateRealCustomerAnalytics(event, analytics, null, null);
    }

    private void calculateRealCustomerAnalytics(Event event, EventAnalyticsDto analytics, LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> allSoldTickets = ticketRepository.findByEvent(event);
        
        // Filter tickets by date range
        List<Ticket> soldTickets = allSoldTickets.stream()
                .filter(ticket -> isTicketInDateRange(ticket, startDate, endDate))
                .collect(Collectors.toList());
        
        // Count REAL unique customers
        Set<Long> uniqueCustomerIds = soldTickets.stream()
            .map(ticket -> ticket.getCustomer().getId())
            .collect(Collectors.toSet());
        
        int uniqueCustomers = uniqueCustomerIds.size();
        int totalTicketsSold = soldTickets.size();
        int repeatCustomers = totalTicketsSold - uniqueCustomers;
        
        analytics.setUniqueCustomers(uniqueCustomers);
        analytics.setRepeatCustomers(Math.max(0, repeatCustomers));
        analytics.setRepeatCustomerRate(totalTicketsSold > 0 ? (double) repeatCustomers / totalTicketsSold * 100 : 0.0);
        
        // Set REAL total visitors based on embed visits (stránky s lístky)
        Long totalVisitors = embedVisitRepository.countVisitsByEventId(event.getId());
        analytics.setTotalVisitors(totalVisitors.intValue());
        
        // Calculate REAL customer segments based on actual spending
        calculateRealCustomerSegments(event, analytics, soldTickets);
    }

    private void calculateRealCustomerSegments(Event event, EventAnalyticsDto analytics, List<Ticket> soldTickets) {
        // Group tickets by customer and calculate spending
        Map<Long, List<Ticket>> ticketsByCustomer = soldTickets.stream()
            .collect(Collectors.groupingBy(ticket -> ticket.getCustomer().getId()));
        
        Map<String, Integer> segmentCounts = new HashMap<>();
        Map<String, BigDecimal> segmentTotals = new HashMap<>();
        
        for (Map.Entry<Long, List<Ticket>> entry : ticketsByCustomer.entrySet()) {
            List<Ticket> customerTickets = entry.getValue();
            BigDecimal customerSpending = customerTickets.stream()
                .map(ticket -> ticket.getTicketType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            String segment;
            if (customerSpending.compareTo(BigDecimal.valueOf(2000)) >= 0) {
                segment = "VIP";
            } else if (customerSpending.compareTo(BigDecimal.valueOf(500)) >= 0) {
                segment = "Premium";
            } else {
                segment = "Standard";
            }
            
            segmentCounts.put(segment, segmentCounts.getOrDefault(segment, 0) + 1);
            segmentTotals.put(segment, segmentTotals.getOrDefault(segment, BigDecimal.ZERO).add(customerSpending));
        }
        
        List<CustomerSegmentDto> segments = new ArrayList<>();
        for (String segmentName : segmentCounts.keySet()) {
            int count = segmentCounts.get(segmentName);
            BigDecimal total = segmentTotals.get(segmentName);
            double average = count > 0 ? total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP).doubleValue() : 0.0;
            
            segments.add(new CustomerSegmentDto(segmentName, count, total, average));
        }
        
        analytics.setCustomerSegments(segments);
    }

    private void calculateRealGeographicAnalytics(Event event, EventAnalyticsDto analytics) {
        calculateRealGeographicAnalytics(event, analytics, null, null);
    }

    private void calculateRealGeographicAnalytics(Event event, EventAnalyticsDto analytics, LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> allSoldTickets = ticketRepository.findByEvent(event);
        
        // Filter tickets by date range
        List<Ticket> soldTickets = allSoldTickets.stream()
                .filter(ticket -> isTicketInDateRange(ticket, startDate, endDate))
                .collect(Collectors.toList());
        
        // Group by ticket's city (if available in ticket data)
        Map<String, List<Ticket>> ticketsByCity = soldTickets.stream()
            .filter(ticket -> ticket.getCity() != null && !ticket.getCity().isEmpty())
            .collect(Collectors.groupingBy(Ticket::getCity));
        
        List<GeographicSalesDto> geographicSales = new ArrayList<>();
        int totalSales = soldTickets.size();
        
        for (Map.Entry<String, List<Ticket>> entry : ticketsByCity.entrySet()) {
            String city = entry.getKey();
            List<Ticket> cityTickets = entry.getValue();
            int sales = cityTickets.size();
            
            BigDecimal revenue = cityTickets.stream()
                .map(ticket -> ticket.getTicketType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            double percentage = totalSales > 0 ? (double) sales / totalSales * 100 : 0.0;
            
            geographicSales.add(new GeographicSalesDto(city, "CZ", sales, revenue, percentage));
        }
        
        // If no address data available, just show event city
        if (geographicSales.isEmpty()) {
            BigDecimal totalRevenue = analytics.getTotalRevenue();
            geographicSales.add(new GeographicSalesDto(event.getCity(), "CZ", totalSales, totalRevenue, 100.0));
        }
        
        analytics.setGeographicSales(geographicSales);
    }

    private void calculateRealTimeBasedAnalytics(Event event, EventAnalyticsDto analytics) {
        calculateRealTimeBasedAnalytics(event, analytics, null, null);
    }

    private void calculateRealTimeBasedAnalytics(Event event, EventAnalyticsDto analytics, LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> allSoldTickets = ticketRepository.findByEvent(event);
        
        // Filter tickets by date range
        List<Ticket> soldTickets = allSoldTickets.stream()
                .filter(ticket -> isTicketInDateRange(ticket, startDate, endDate))
                .collect(Collectors.toList());
        
        // Generate REAL daily sales data based on actual ticket sales
        Map<String, List<Ticket>> ticketsByDay = soldTickets.stream()
            .filter(ticket -> ticket.getPurchaseDate() != null)
            .collect(Collectors.groupingBy(ticket -> 
                ticket.getPurchaseDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            ));
        
        List<DailyRevenueDto> dailyRevenue = new ArrayList<>();
        List<DailyTicketSalesDto> dailyTicketSales = new ArrayList<>();
        
        // Get last 30 days including today
        LocalDate chartStartDate = LocalDate.now().minusDays(29);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 0; i < 30; i++) {
            LocalDate date = chartStartDate.plusDays(i);
            String dateStr = date.format(formatter);
            
            List<Ticket> dayTickets = ticketsByDay.getOrDefault(dateStr, new ArrayList<>());
            int dailySales = dayTickets.size();
            BigDecimal dailyRev = dayTickets.stream()
                .map(ticket -> ticket.getTicketType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            dailyRevenue.add(new DailyRevenueDto(dateStr, dailyRev, dailySales));
            dailyTicketSales.add(new DailyTicketSalesDto(dateStr, dailySales, analytics.getTotalTicketsAvailable()));
        }
        
        analytics.setDailyRevenue(dailyRevenue);
        analytics.setDailyTicketSales(dailyTicketSales);
        
        // Generate REAL hourly analytics (use the already filtered soldTickets)
        Map<Integer, List<Ticket>> ticketsByHour = soldTickets.stream()
            .filter(ticket -> ticket.getPurchaseDate() != null)
            .collect(Collectors.groupingBy(ticket -> ticket.getPurchaseDate().getHour()));
        
        List<HourlyAnalyticsDto> hourlyAnalytics = new ArrayList<>();
        
        for (int hour = 0; hour < 24; hour++) {
            List<Ticket> hourTickets = ticketsByHour.getOrDefault(hour, new ArrayList<>());
            int hourlySales = hourTickets.size();
            BigDecimal hourlyRevenue = hourTickets.stream()
                .map(ticket -> ticket.getTicketType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            hourlyAnalytics.add(new HourlyAnalyticsDto(hour, hourlySales, hourlyRevenue));
        }
        
        analytics.setHourlyAnalytics(hourlyAnalytics);
    }
}