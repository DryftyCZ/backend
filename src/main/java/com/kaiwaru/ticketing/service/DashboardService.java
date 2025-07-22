package com.kaiwaru.ticketing.service;

import com.kaiwaru.ticketing.dto.DashboardStatsDto;
import com.kaiwaru.ticketing.dto.TodayAgendaDto;
import com.kaiwaru.ticketing.dto.NextMonthCashFlowDto;
import com.kaiwaru.ticketing.dto.NextMonthCashFlowDto.UpcomingEventDto;
import com.kaiwaru.ticketing.repository.EventRepository;
import com.kaiwaru.ticketing.repository.RealTimeStatsRepository;
import com.kaiwaru.ticketing.repository.TicketRepository;
import com.kaiwaru.ticketing.repository.UserRepository;
import com.kaiwaru.ticketing.repository.VisitorSessionRepository;
import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.RealTimeStats;
import com.kaiwaru.ticketing.model.Ticket;
import com.kaiwaru.ticketing.model.TicketType;
import com.kaiwaru.ticketing.service.GeolocationService;
import com.kaiwaru.ticketing.service.OrganizerCommissionService;
import com.kaiwaru.ticketing.model.Auth.User;
import com.kaiwaru.ticketing.security.UserPrincipal;
import com.kaiwaru.ticketing.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VisitorSessionRepository visitorSessionRepository;

    @Autowired
    private RealTimeStatsRepository realTimeStatsRepository;

    @Autowired
    private GeolocationService geolocationService;
    
    @Autowired
    private OrganizerCommissionService commissionService;

    public DashboardStatsDto getDashboardStats(String startDateStr, String endDateStr) {
        DashboardStatsDto stats = new DashboardStatsDto();
        
        // Get current user and check permissions
        User currentUser = getCurrentUser();
        
        // Get events based on user role
        List<Event> allEvents = getEventsForUser(currentUser);
        
        // Parse dates for filtering
        LocalDateTime filterStartDate = null;
        LocalDateTime filterEndDate = null;
        List<Ticket> filteredTickets;
        
        if (startDateStr != null || endDateStr != null) {
            // Use date filtering if at least one date is provided
            filterStartDate = startDateStr != null ? LocalDate.parse(startDateStr).atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
            filterEndDate = endDateStr != null ? LocalDate.parse(endDateStr).atTime(23, 59, 59) : LocalDateTime.now();
            filteredTickets = ticketRepository.findTicketsPurchasedBetween(filterStartDate, filterEndDate);
        } else {
            // No filtering - get all tickets
            filteredTickets = ticketRepository.findAll();
        }
        
        // Filter tickets to only those from events the user has access to
        if (!isAdmin(currentUser)) {
            Set<Long> userEventIds = allEvents.stream()
                    .map(Event::getId)
                    .collect(Collectors.toSet());
            filteredTickets = filteredTickets.stream()
                    .filter(ticket -> userEventIds.contains(ticket.getEvent().getId()))
                    .collect(Collectors.toList());
        }
        
        // Calculate total revenue from filtered tickets
        BigDecimal totalGrossRevenue = filteredTickets.stream()
                .filter(ticket -> ticket.getTicketType() != null)
                .map(ticket -> ticket.getTicketType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate net revenue after commission (only for admins)
        BigDecimal totalRevenue = totalGrossRevenue;
        if (isAdmin(currentUser)) {
            // For admins, subtract commission from organizer events
            BigDecimal totalCommission = filteredTickets.stream()
                    .filter(ticket -> ticket.getTicketType() != null && ticket.getEvent() != null)
                    .map(ticket -> {
                        BigDecimal price = ticket.getTicketType().getPrice();
                        BigDecimal commissionRate = commissionService.getOrganizerCommissionPercentage(ticket.getEvent().getOrganizer());
                        return price.multiply(commissionRate).divide(BigDecimal.valueOf(100));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalRevenue = totalGrossRevenue.subtract(totalCommission);
        }
        
        stats.setTotalRevenue(totalRevenue);
        
        // Calculate total tickets sold from filtered tickets
        Integer totalTicketsSold = filteredTickets.size();
        
        // Calculate growth based on whether date filtering is used
        if (startDateStr != null || endDateStr != null) {
            // Date filtering is used - calculate period-over-period growth
            calculateDateRangeGrowth(stats, startDateStr, endDateStr, totalRevenue, totalTicketsSold);
        } else {
            // No date filtering - use month-over-month growth
            BigDecimal previousMonthRevenue = calculatePreviousMonthRevenue();
            stats.setPreviousMonthRevenue(previousMonthRevenue);
            
            BigDecimal currentMonthRevenue = calculateCurrentMonthRevenue();
            stats.setRevenueGrowth(calculateRealRevenueGrowth(currentMonthRevenue, previousMonthRevenue));
        }
        
        // Set total tickets sold (already calculated above)
        stats.setTotalTicketsSold(totalTicketsSold);
        
        // Calculate tickets growth (only if no date filtering, otherwise it's calculated in calculateDateRangeGrowth)
        if (startDateStr == null && endDateStr == null) {
            stats.setTicketsGrowth(calculateTicketsGrowth(allEvents));
        }
        
        // Calculate conversion rate (only if no date filtering, otherwise it's calculated in calculateDateRangeGrowth)
        if (startDateStr == null && endDateStr == null) {
            stats.setConversionRate(75.0);
            stats.setConversionGrowth(5.2);
        }
        
        // Calculate active events
        Integer activeEvents = countActiveEvents(allEvents);
        stats.setActiveEvents(activeEvents);
        
        // Calculate customer metrics
        Integer totalCustomers = (int) userRepository.count();
        stats.setTotalCustomers(totalCustomers);
        
        // Calculate new customers this month (mock)
        stats.setNewCustomersThisMonth(Math.max(1, totalCustomers / 4));
        
        // Calculate average ticket price
        BigDecimal averageTicketPrice = calculateAverageTicketPrice(allEvents);
        stats.setAverageTicketPrice(averageTicketPrice);
        stats.setAverageTicketPriceGrowth(2.8);
        
        // Calculate repeat customer rate (mock)
        stats.setRepeatCustomerRate(34.6);
        
        // Set customer satisfaction score (mock)
        stats.setCustomerSatisfactionScore(4.7);
        
        // Set average processing time (mock)
        stats.setAverageProcessingTime(1.8);
        stats.setProcessingTimeImprovement(-12.4);
        
        // Set revenue by date (last 7 days)
        stats.setRevenueByDate(generateRevenueByDate(allEvents, startDateStr, endDateStr));
        
        // Set sales by category
        stats.setSalesByCategory(generateSalesByCategory(allEvents));
        
        // Set upcoming events
        stats.setUpcomingEvents(getUpcomingEvents(allEvents));
        
        // Set recent transactions (mock)
        stats.setRecentTransactions(generateRecentTransactions(allEvents));
        
        // Set geolocation stats (real data)
        stats.setGeolocationStats(getGeolocationStats(startDateStr, endDateStr));
        
        // Set real-time stats (real data)
        stats.setRealTimeStats(getRealTimeStats());
        
        // Set traffic sources (real data)
        stats.setTrafficSources(getTrafficSourceStats(startDateStr, endDateStr));
        
        // Set city sales stats (real data)
        stats.setCitySales(getCitySalesStats(startDateStr, endDateStr));
        
        return stats;
    }
    
    private BigDecimal calculateTotalRevenue(List<Ticket> tickets) {
        return tickets.stream()
                .filter(ticket -> ticket.getTicketType() != null)
                .map(ticket -> ticket.getTicketType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calculate current month revenue from actual ticket sales
     */
    private BigDecimal calculateCurrentMonthRevenue() {
        LocalDate now = LocalDate.now();
        LocalDate currentMonthStart = now.withDayOfMonth(1);
        LocalDate currentMonthEnd = now.withDayOfMonth(now.lengthOfMonth());
        
        LocalDateTime startDateTime = currentMonthStart.atStartOfDay();
        LocalDateTime endDateTime = currentMonthEnd.atTime(23, 59, 59);
        
        System.out.println("Calculating current month revenue for: " + currentMonthStart + " to " + currentMonthEnd);
        
        List<Ticket> currentMonthTickets = ticketRepository.findTicketsPurchasedBetween(startDateTime, endDateTime);
        
        BigDecimal currentRevenue = currentMonthTickets.stream()
            .filter(ticket -> ticket.getTicketType() != null)
            .map(ticket -> ticket.getTicketType().getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("Current month tickets: " + currentMonthTickets.size() + ", revenue: " + currentRevenue);
        
        return currentRevenue;
    }
    
    /**
     * Calculate previous month revenue from actual ticket sales
     */
    private BigDecimal calculatePreviousMonthRevenue() {
        LocalDate now = LocalDate.now();
        LocalDate previousMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate previousMonthEnd = previousMonthStart.withDayOfMonth(previousMonthStart.lengthOfMonth());
        
        LocalDateTime startDateTime = previousMonthStart.atStartOfDay();
        LocalDateTime endDateTime = previousMonthEnd.atTime(23, 59, 59);
        
        System.out.println("Calculating previous month revenue for: " + previousMonthStart + " to " + previousMonthEnd);
        
        List<Ticket> previousMonthTickets = ticketRepository.findTicketsPurchasedBetween(startDateTime, endDateTime);
        
        BigDecimal previousRevenue = previousMonthTickets.stream()
            .filter(ticket -> ticket.getTicketType() != null)
            .map(ticket -> ticket.getTicketType().getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("Previous month tickets: " + previousMonthTickets.size() + ", revenue: " + previousRevenue);
        
        return previousRevenue;
    }
    
    /**
     * Calculate real revenue growth based on current vs previous month
     */
    private Double calculateRealRevenueGrowth(BigDecimal currentRevenue, BigDecimal previousRevenue) {
        if (previousRevenue == null || previousRevenue.compareTo(BigDecimal.ZERO) == 0) {
            // If no previous revenue but current revenue exists, that's infinite growth
            // But we'll cap it at a reasonable maximum to avoid UI issues
            if (currentRevenue.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Revenue growth: previous was 0, current is " + currentRevenue + " - showing as 999% (infinite growth)");
                return 999.0; // Show as 999% instead of infinity
            }
            return 0.0; // Both are zero
        }
        
        // Calculate percentage growth: ((current - previous) / previous) * 100
        BigDecimal growth = currentRevenue.subtract(previousRevenue)
            .divide(previousRevenue, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
        
        System.out.println("Revenue growth calculation: current=" + currentRevenue + 
                          ", previous=" + previousRevenue + ", growth=" + growth.doubleValue() + "%");
        
        return growth.doubleValue();
    }
    
    private Integer calculateTotalTicketsSold(List<Ticket> tickets) {
        return tickets.size();
    }
    
    private Double calculateTicketsGrowth(List<Event> events) {
        // Mock calculation - in real implementation would compare with previous period
        if (events.isEmpty()) return 0.0;
        return 8.7; // Mock growth percentage
    }
    
    private Integer countActiveEvents(List<Event> events) {
        LocalDate now = LocalDate.now();
        System.out.println("=== COUNTING ACTIVE EVENTS ===");
        System.out.println("Today's date: " + now);
        System.out.println("Total events to check: " + events.size());
        
        int activeCount = 0;
        for (Event event : events) {
            LocalDate eventDate = event.getDate();
            boolean isActive = false;
            String reason = "";
            
            if (eventDate.equals(now)) {
                isActive = true;
                reason = "today";
            } else if (eventDate.isAfter(now)) {
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(now, eventDate);
                if (daysUntil <= 7) {
                    isActive = true;
                    reason = daysUntil + " days until";
                } else {
                    reason = daysUntil + " days until (too far)";
                }
            } else {
                reason = "past event";
            }
            
            System.out.println("Event: " + event.getName() + " | Date: " + eventDate + " | Active: " + isActive + " | Reason: " + reason);
            
            if (isActive) {
                activeCount++;
            }
        }
        
        System.out.println("Total active events: " + activeCount);
        System.out.println("=== END COUNTING ===");
        
        return activeCount;
    }
    
    private BigDecimal calculateAverageTicketPrice(List<Event> events) {
        if (events.isEmpty()) return BigDecimal.ZERO;
        
        List<BigDecimal> prices = events.stream()
                .flatMap(event -> event.getTicketTypes().stream())
                .map(TicketType::getPrice)
                .collect(Collectors.toList());
        
        if (prices.isEmpty()) return BigDecimal.ZERO;
        
        BigDecimal total = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP);
    }
    
    private List<DashboardStatsDto.RevenueByDateDto> generateRevenueByDate(List<Event> events, String startDateStr, String endDateStr) {
        List<DashboardStatsDto.RevenueByDateDto> revenueByDate = new ArrayList<>();
        
        LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now();
        LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : endDate.minusDays(6);
        
        // Generate dates for the selected period (max 7 days)
        LocalDate currentDate = endDate;
        int daysCount = 0;
        while (!currentDate.isBefore(startDate) && daysCount < 7) {
            String dateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // Mock revenue calculation - in real implementation would use actual transaction data
            BigDecimal dailyRevenue = BigDecimal.valueOf(Math.random() * 5000 + 1000);
            revenueByDate.add(0, new DashboardStatsDto.RevenueByDateDto(dateStr, dailyRevenue));
            
            currentDate = currentDate.minusDays(1);
            daysCount++;
        }
        
        return revenueByDate;
    }
    
    private List<DashboardStatsDto.SalesByCategoryDto> generateSalesByCategory(List<Event> events) {
        Map<String, Integer> salesByCategory = new HashMap<>();
        
        for (Event event : events) {
            for (TicketType ticketType : event.getTicketTypes()) {
                String category = ticketType.getName();
                int soldTickets = ticketType.getQuantity() - (ticketType.getAvailableQuantity() != null ? ticketType.getAvailableQuantity() : ticketType.getQuantity());
                salesByCategory.merge(category, soldTickets, Integer::sum);
            }
        }
        
        return salesByCategory.entrySet().stream()
                .map(entry -> new DashboardStatsDto.SalesByCategoryDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    
    private List<DashboardStatsDto.UpcomingEventDto> getUpcomingEvents(List<Event> events) {
        LocalDate now = LocalDate.now();
        
        List<DashboardStatsDto.UpcomingEventDto> upcomingEvents = new ArrayList<>();
        
        for (Event event : events) {
            if (event.getDate().isAfter(now)) {
                int totalTickets = event.getTicketTypes().stream()
                        .mapToInt(TicketType::getQuantity)
                        .sum();
                int soldTickets = event.getTicketTypes().stream()
                        .mapToInt(ticketType -> ticketType.getQuantity() - (ticketType.getAvailableQuantity() != null ? ticketType.getAvailableQuantity() : ticketType.getQuantity()))
                        .sum();
                
                DashboardStatsDto.UpcomingEventDto dto = new DashboardStatsDto.UpcomingEventDto();
                dto.setId(event.getId());
                dto.setName(event.getName());
                dto.setDescription(event.getDescription());
                dto.setAddress(event.getAddress());
                dto.setCity(event.getCity());
                dto.setDate(event.getDate().atStartOfDay());
                dto.setTotalTickets(totalTickets);
                dto.setSoldTickets(soldTickets);
                
                upcomingEvents.add(dto);
            }
        }
        
        // Sort by date and limit to 5
        upcomingEvents.sort(Comparator.comparing(DashboardStatsDto.UpcomingEventDto::getDate));
        return upcomingEvents.stream().limit(5).collect(Collectors.toList());
    }
    
    private List<DashboardStatsDto.RecentTransactionDto> generateRecentTransactions(List<Event> events) {
        List<DashboardStatsDto.RecentTransactionDto> transactions = new ArrayList<>();
        
        // Mock recent transactions
        String[] customerNames = {"Jan Novák", "Marie Svobodová", "Petr Dvořák", "Anna Krásná", "Pavel Němec"};
        String[] statuses = {"COMPLETED", "PENDING", "COMPLETED", "COMPLETED", "REFUNDED"};
        
        for (int i = 0; i < Math.min(5, events.size()); i++) {
            Event event = events.get(i);
            BigDecimal amount = event.getTicketTypes().isEmpty() ? 
                    BigDecimal.valueOf(500) : 
                    event.getTicketTypes().get(0).getPrice();
            
            DashboardStatsDto.RecentTransactionDto dto = new DashboardStatsDto.RecentTransactionDto();
            dto.setId((long) (i + 1));
            dto.setEventName(event.getName());
            dto.setCustomerName(customerNames[i % customerNames.length]);
            dto.setAmount(amount);
            dto.setDate(LocalDateTime.now().minusHours(i + 1));
            dto.setStatus(statuses[i % statuses.length]);
            transactions.add(dto);
        }
        
        return transactions;
    }
    
    private List<DashboardStatsDto.GeolocationStatsDto> getGeolocationStats(String startDateStr, String endDateStr) {
        List<DashboardStatsDto.GeolocationStatsDto> geoStats = new ArrayList<>();
        
        LocalDateTime endDate;
        LocalDateTime startDate;
        
        if (startDateStr != null || endDateStr != null) {
            endDate = endDateStr != null ? LocalDate.parse(endDateStr).atTime(23, 59, 59) : LocalDateTime.now();
            startDate = startDateStr != null ? LocalDate.parse(startDateStr).atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        } else {
            // No filtering - use wide date range
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
            endDate = LocalDateTime.now();
        }
        
        try {
            List<Object[]> results = visitorSessionRepository.getGeolocationStats(startDate, endDate);
            long totalSales = results.stream().mapToLong(r -> ((Long) r[2]).longValue()).sum();
            
            for (Object[] result : results) {
                String country = (String) result[0];
                String countryCode = (String) result[1];
                Long sales = (Long) result[2];
                Double revenue = (Double) result[3];
                
                if (country != null && countryCode != null && sales != null) {
                    double percentage = totalSales > 0 ? (sales.doubleValue() / totalSales) * 100 : 0;
                    String flag = geolocationService.getCountryFlag(countryCode);
                    
                    geoStats.add(new DashboardStatsDto.GeolocationStatsDto(
                        country, 
                        countryCode, 
                        sales.intValue(), 
                        BigDecimal.valueOf(revenue != null ? revenue : 0), 
                        percentage, 
                        flag
                    ));
                }
            }
        } catch (Exception e) {
            // Fallback if no visitor data available
            geoStats.add(new DashboardStatsDto.GeolocationStatsDto("Czech Republic", "CZ", 0, BigDecimal.ZERO, 100.0, "🇨🇿"));
        }
        
        return geoStats;
    }
    
    private List<DashboardStatsDto.RealTimeStatsDto> getRealTimeStats() {
        List<DashboardStatsDto.RealTimeStatsDto> realTimeStatsList = new ArrayList<>();
        
        List<RealTimeStats> stats = realTimeStatsRepository.findByTimestampAfter(LocalDateTime.now().minusHours(2));
        
        for (RealTimeStats stat : stats) {
            String timestampStr = stat.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            realTimeStatsList.add(new DashboardStatsDto.RealTimeStatsDto(
                timestampStr,
                stat.getSalesLastMinute(),
                stat.getActiveVisitors(),
                stat.getRevenueLastMinute(),
                stat.getConversionRate()
            ));
        }
        
        return realTimeStatsList;
    }
    
    private List<DashboardStatsDto.TrafficSourceDto> getTrafficSourceStats(String startDateStr, String endDateStr) {
        List<DashboardStatsDto.TrafficSourceDto> trafficSources = new ArrayList<>();
        
        LocalDateTime endDate;
        LocalDateTime startDate;
        
        if (startDateStr != null || endDateStr != null) {
            endDate = endDateStr != null ? LocalDate.parse(endDateStr).atTime(23, 59, 59) : LocalDateTime.now();
            startDate = startDateStr != null ? LocalDate.parse(startDateStr).atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        } else {
            // No filtering - use wide date range
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
            endDate = LocalDateTime.now();
        }
        
        try {
            List<Object[]> results = visitorSessionRepository.getTrafficSourceStats(startDate, endDate);
            long totalVisitors = results.stream().mapToLong(r -> ((Long) r[1]).longValue()).sum();
            
            for (Object[] result : results) {
                String source = (String) result[0];
                Long visitors = (Long) result[1];
                Long conversions = (Long) result[2];
                
                if (source != null && visitors != null && conversions != null) {
                    double percentage = totalVisitors > 0 ? (visitors.doubleValue() / totalVisitors) * 100 : 0;
                    double conversionRate = visitors > 0 ? (conversions.doubleValue() / visitors) * 100 : 0;
                    
                    trafficSources.add(new DashboardStatsDto.TrafficSourceDto(
                        source,
                        percentage,
                        visitors.intValue(),
                        conversions.intValue(),
                        conversionRate
                    ));
                }
            }
        } catch (Exception e) {
            // Fallback if no visitor data available
            trafficSources.add(new DashboardStatsDto.TrafficSourceDto("Direct", 100.0, 0, 0, 0.0));
        }
        
        return trafficSources;
    }
    
    private List<DashboardStatsDto.CitySalesDto> getCitySalesStats(String startDateStr, String endDateStr) {
        List<DashboardStatsDto.CitySalesDto> citySales = new ArrayList<>();
        
        LocalDateTime endDate;
        LocalDateTime startDate;
        
        if (startDateStr != null || endDateStr != null) {
            endDate = endDateStr != null ? LocalDate.parse(endDateStr).atTime(23, 59, 59) : LocalDateTime.now();
            startDate = startDateStr != null ? LocalDate.parse(startDateStr).atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        } else {
            // No filtering - use wide date range
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
            endDate = LocalDateTime.now();
        }
        
        try {
            List<Object[]> results = ticketRepository.getCitySalesStats(startDate, endDate);
            Long totalTicketsWithCity = ticketRepository.getTotalTicketsWithCity(startDate, endDate);
            
            if (totalTicketsWithCity == null) {
                totalTicketsWithCity = 0L;
            }
            
            for (Object[] result : results) {
                String city = (String) result[0];
                String country = (String) result[1];
                Long ticketsSold = (Long) result[2];
                BigDecimal revenue = (BigDecimal) result[3];
                
                if (city != null && ticketsSold != null) {
                    double percentage = totalTicketsWithCity > 0 ? 
                        (ticketsSold.doubleValue() / totalTicketsWithCity) * 100 : 0;
                    
                    citySales.add(new DashboardStatsDto.CitySalesDto(
                        city,
                        country,
                        ticketsSold.intValue(),
                        revenue != null ? revenue : BigDecimal.ZERO,
                        percentage
                    ));
                }
            }
        } catch (Exception e) {
            // Fallback if no city data available yet
            citySales.add(new DashboardStatsDto.CitySalesDto(
                "Data se zpracovávají", 
                "CZ", 
                0, 
                BigDecimal.ZERO, 
                0.0
            ));
        }
        
        return citySales;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Uživatel nebyl nalezen"));
        }
        throw new AccessDeniedException("Uživatel není přihlášen");
    }
    
    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }
    
    private List<Event> getEventsForUser(User user) {
        // Admins see all events
        if (isAdmin(user)) {
            return eventRepository.findAll();
        }
        
        // Organizers see only their events
        return eventRepository.findByOrganizer(user);
    }

    /**
     * Get today's agenda with real data - events happening today and orders made today
     */
    public List<TodayAgendaDto> getTodayAgenda() {
        List<TodayAgendaDto> agenda = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // Get current user and their events
        User currentUser = getCurrentUser();
        List<Event> userEvents = getEventsForUser(currentUser);
        
        // 1. Events happening today (filtered by user access)
        List<Event> todayEvents = userEvents.stream()
            .filter(event -> event.getDate().equals(today))
            .collect(Collectors.toList());
        
        for (Event event : todayEvents) {
            // Count real sold tickets for this event
            List<Ticket> eventTickets = ticketRepository.findByEvent(event);
            int totalTickets = event.getTicketTypes().stream()
                .mapToInt(TicketType::getQuantity)
                .sum();
            
            agenda.add(new TodayAgendaDto(
                "event",
                event.getName(),
                eventTickets.size() + "/" + totalTickets + " prodáno",
                "20:00", // Default event time
                "Event",
                event.getId(),
                eventTickets.size()
            ));
        }
        
        // 2. Real orders made today
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);
        
        List<Ticket> todayOrders = ticketRepository.findTicketsPurchasedBetween(todayStart, todayEnd);
        
        // Filter orders to only those from events the user has access to
        if (!isAdmin(currentUser)) {
            Set<Long> userEventIds = userEvents.stream()
                    .map(Event::getId)
                    .collect(Collectors.toSet());
            todayOrders = todayOrders.stream()
                    .filter(ticket -> userEventIds.contains(ticket.getEvent().getId()))
                    .collect(Collectors.toList());
        }
        
        if (!todayOrders.isEmpty()) {
            // Group orders by event to show summary
            Map<Long, List<Ticket>> ordersByEvent = todayOrders.stream()
                .collect(Collectors.groupingBy(ticket -> ticket.getEvent().getId()));
            
            for (Map.Entry<Long, List<Ticket>> entry : ordersByEvent.entrySet()) {
                List<Ticket> eventOrders = entry.getValue();
                Event event = eventOrders.get(0).getEvent();
                
                BigDecimal todayRevenue = eventOrders.stream()
                    .map(ticket -> ticket.getTicketType().getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                agenda.add(new TodayAgendaDto(
                    "order",
                    eventOrders.size() + " nových objednávek",
                    "Za " + formatCurrency(todayRevenue) + " • " + event.getName(),
                    null,
                    "ShoppingCart",
                    event.getId(),
                    eventOrders.size()
                ));
            }
        } else {
            // No orders today
            agenda.add(new TodayAgendaDto(
                "order",
                "Žádné objednávky",
                "Za dnešní den",
                null,
                "ShoppingCart",
                null,
                0
            ));
        }
        
        // 3. System notifications if needed
        if (agenda.isEmpty()) {
            agenda.add(new TodayAgendaDto(
                "system",
                "Žádné události dnes",
                "Poklidný den",
                null,
                "Schedule",
                null,
                0
            ));
        }
        
        return agenda;
    }

    /**
     * Get next month cash flow with REAL data only - no mock data
     * Returns null/zero values if no events next month
     */
    public NextMonthCashFlowDto getNextMonthCashFlow() {
        LocalDate today = LocalDate.now();
        LocalDate nextMonthStart = today.withDayOfMonth(1).plusMonths(1);
        LocalDate nextMonthEnd = nextMonthStart.withDayOfMonth(nextMonthStart.lengthOfMonth());
        
        System.out.println("Checking for events between: " + nextMonthStart + " and " + nextMonthEnd);
        
        // Get current user and their events
        User currentUser = getCurrentUser();
        List<Event> userEvents = getEventsForUser(currentUser);
        
        // Find events happening ONLY next month (strict date range) - filtered by user access
        List<Event> nextMonthEvents = userEvents.stream()
            .filter(event -> {
                LocalDate eventDate = event.getDate();
                boolean isInNextMonth = !eventDate.isBefore(nextMonthStart) && !eventDate.isAfter(nextMonthEnd);
                if (isInNextMonth) {
                    System.out.println("Found next month event: " + event.getName() + " on " + eventDate);
                }
                return isInNextMonth;
            })
            .collect(Collectors.toList());
        
        System.out.println("Total events found for next month: " + nextMonthEvents.size());
        
        // If no events next month, return empty/zero data
        if (nextMonthEvents.isEmpty()) {
            System.out.println("No events next month - returning zero data");
            return new NextMonthCashFlowDto(
                BigDecimal.ZERO,  // No expected revenue
                BigDecimal.ZERO,  // No potential revenue
                0,                // No upcoming events
                0,                // No tickets sold
                new ArrayList<>() // Empty event details
            );
        }
        
        BigDecimal totalExpectedRevenue = BigDecimal.ZERO;
        BigDecimal totalPotentialRevenue = BigDecimal.ZERO;
        int totalTicketsSoldForNextMonth = 0;
        List<UpcomingEventDto> upcomingEventDetails = new ArrayList<>();
        
        for (Event event : nextMonthEvents) {
            // Count ONLY real tickets already sold for this event
            List<Ticket> soldTickets = ticketRepository.findByEvent(event);
            
            System.out.println("Event: " + event.getName() + " - sold tickets: " + soldTickets.size());
            
            // Calculate expected revenue from already sold tickets
            BigDecimal grossEventRevenue = soldTickets.stream()
                .map(ticket -> ticket.getTicketType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Subtract commission if user is admin
            BigDecimal eventRevenue = grossEventRevenue;
            if (isAdmin(currentUser)) {
                BigDecimal commissionRate = commissionService.getOrganizerCommissionPercentage(event.getOrganizer());
                BigDecimal commission = grossEventRevenue.multiply(commissionRate).divide(BigDecimal.valueOf(100));
                eventRevenue = grossEventRevenue.subtract(commission);
            }
            
            // Calculate potential revenue (value of all available tickets)
            int totalCapacity = 0;
            BigDecimal eventPotentialRevenue = BigDecimal.ZERO;
            
            if (event.getTicketTypes() != null && !event.getTicketTypes().isEmpty()) {
                for (TicketType ticketType : event.getTicketTypes()) {
                    totalCapacity += ticketType.getQuantity();
                    // Calculate potential revenue from all tickets of this type
                    BigDecimal typeGrossRevenue = ticketType.getPrice().multiply(BigDecimal.valueOf(ticketType.getQuantity()));
                    
                    // Subtract commission if user is admin
                    BigDecimal typeRevenue = typeGrossRevenue;
                    if (isAdmin(currentUser)) {
                        BigDecimal commissionRate = commissionService.getOrganizerCommissionPercentage(event.getOrganizer());
                        BigDecimal typeCommission = typeGrossRevenue.multiply(commissionRate).divide(BigDecimal.valueOf(100));
                        typeRevenue = typeGrossRevenue.subtract(typeCommission);
                    }
                    
                    eventPotentialRevenue = eventPotentialRevenue.add(typeRevenue);
                }
                
                System.out.println("Event has " + event.getTicketTypes().size() + " ticket types, total capacity: " + totalCapacity);
                System.out.println("Event potential revenue: " + eventPotentialRevenue);
                
                // If no tickets sold yet but event has ticket types, show potential
                if (soldTickets.isEmpty()) {
                    System.out.println("No tickets sold yet for event: " + event.getName() + ", but potential revenue: " + eventPotentialRevenue);
                }
            } else {
                System.out.println("Event " + event.getName() + " has no ticket types defined");
            }
            
            totalExpectedRevenue = totalExpectedRevenue.add(eventRevenue);
            totalPotentialRevenue = totalPotentialRevenue.add(eventPotentialRevenue);
            totalTicketsSoldForNextMonth += soldTickets.size();
            
            upcomingEventDetails.add(new UpcomingEventDto(
                event.getId(),
                event.getName(),
                event.getDate().toString(),
                eventRevenue,
                soldTickets.size(),
                totalCapacity
            ));
        }
        
        System.out.println("Total expected revenue for next month: " + totalExpectedRevenue);
        System.out.println("Total potential revenue for next month: " + totalPotentialRevenue);
        System.out.println("Total tickets sold for next month events: " + totalTicketsSoldForNextMonth);
        
        return new NextMonthCashFlowDto(
            totalExpectedRevenue,
            totalPotentialRevenue,
            nextMonthEvents.size(),
            totalTicketsSoldForNextMonth,
            upcomingEventDetails
        );
    }
    
    /**
     * Calculate period-over-period growth when date range filtering is used
     */
    private void calculateDateRangeGrowth(DashboardStatsDto stats, String startDateStr, String endDateStr, 
                                        BigDecimal currentRevenue, Integer currentTicketsSold) {
        
        // Parse the current period dates
        LocalDate currentStart = startDateStr != null ? LocalDate.parse(startDateStr) : LocalDate.of(2000, 1, 1);
        LocalDate currentEnd = endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now();
        
        // Calculate the length of the current period
        long periodDays = java.time.temporal.ChronoUnit.DAYS.between(currentStart, currentEnd) + 1;
        
        // Calculate the previous period (same length, immediately before current period)
        LocalDate previousStart = currentStart.minusDays(periodDays);
        LocalDate previousEnd = currentStart.minusDays(1);
        
        System.out.println("=== DATE RANGE GROWTH CALCULATION ===");
        System.out.println("Current period: " + currentStart + " to " + currentEnd + " (" + periodDays + " days)");
        System.out.println("Previous period: " + previousStart + " to " + previousEnd);
        
        // Get previous period data
        LocalDateTime prevStartDateTime = previousStart.atStartOfDay();
        LocalDateTime prevEndDateTime = previousEnd.atTime(23, 59, 59);
        
        List<Ticket> previousTickets = ticketRepository.findTicketsPurchasedBetween(prevStartDateTime, prevEndDateTime);
        
        BigDecimal previousRevenue = previousTickets.stream()
            .filter(ticket -> ticket.getTicketType() != null)
            .map(ticket -> ticket.getTicketType().getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Integer previousTicketsSold = previousTickets.size();
        
        // Calculate visitors for both periods (mock data for now - would need real visitor tracking)
        Integer currentVisitors = Math.max(1, currentTicketsSold * 4); // Assume 4 visitors per ticket sold
        Integer previousVisitors = Math.max(1, previousTicketsSold * 4);
        
        // Calculate conversion rates
        Double currentConversion = currentTicketsSold > 0 && currentVisitors > 0 ? 
            (currentTicketsSold.doubleValue() / currentVisitors.doubleValue()) * 100 : 0.0;
        Double previousConversion = previousTicketsSold > 0 && previousVisitors > 0 ? 
            (previousTicketsSold.doubleValue() / previousVisitors.doubleValue()) * 100 : 0.0;
        
        // Set previous period values for comparison
        stats.setPreviousMonthRevenue(previousRevenue);
        
        // Calculate and set growth percentages
        stats.setRevenueGrowth(calculateRealRevenueGrowth(currentRevenue, previousRevenue));
        stats.setTicketsGrowth(calculateRealGrowth(currentTicketsSold.doubleValue(), previousTicketsSold.doubleValue()));
        stats.setConversionGrowth(calculateRealGrowth(currentConversion, previousConversion));
        
        // Set current values
        stats.setConversionRate(currentConversion);
        stats.setTotalCustomers(currentVisitors);
        stats.setNewCustomersThisMonth(Math.max(0, currentVisitors - previousVisitors));
        
        System.out.println("Current revenue: " + currentRevenue + ", Previous: " + previousRevenue + 
                          ", Growth: " + stats.getRevenueGrowth() + "%");
        System.out.println("Current tickets: " + currentTicketsSold + ", Previous: " + previousTicketsSold + 
                          ", Growth: " + stats.getTicketsGrowth() + "%");
        System.out.println("Current conversion: " + currentConversion + "%, Previous: " + previousConversion + 
                          "%, Growth: " + stats.getConversionGrowth() + "%");
        System.out.println("=== END DATE RANGE GROWTH ===");
    }
    
    /**
     * Calculate real percentage growth between two double values
     */
    private Double calculateRealGrowth(Double currentValue, Double previousValue) {
        if (previousValue == null || previousValue == 0.0) {
            return currentValue > 0 ? 999.0 : 0.0; // Cap at 999% for infinite growth
        }
        
        double growth = ((currentValue - previousValue) / previousValue) * 100;
        return growth;
    }
    
    private String formatCurrency(BigDecimal amount) {
        return amount.toString() + " Kč";
    }
}