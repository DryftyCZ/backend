package com.kaiwaru.ticketing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EventAnalyticsDto {
    private Long eventId;
    private String eventName;
    private String eventDescription;
    private String eventCity;
    private String eventStatus;
    private LocalDateTime eventDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private boolean isCompleted;
    
    // Sales Analytics
    private BigDecimal totalRevenue;
    private BigDecimal revenueGrowth;
    private Integer totalTicketsSold;
    private Integer totalTicketsAvailable;
    private Integer ticketsRemaining;
    private Double salesPercentage;
    private BigDecimal averageTicketPrice;
    private BigDecimal highestTicketPrice;
    private BigDecimal lowestTicketPrice;
    
    // Time-based Analytics
    private List<DailyRevenueDto> dailyRevenue;
    private List<DailyTicketSalesDto> dailyTicketSales;
    private List<TicketTypeAnalyticsDto> ticketTypeAnalytics;
    
    // Customer Analytics
    private Integer uniqueCustomers;
    private Integer repeatCustomers;
    private Double repeatCustomerRate;
    private Integer totalVisitors; // Total visitors to ticket pages
    private List<CustomerSegmentDto> customerSegments;
    
    // Performance Metrics
    private Double conversionRate;
    private LocalDateTime firstSaleDate;
    private LocalDateTime lastSaleDate;
    private Integer daysUntilEvent;
    private Integer salesTrend; // -1 decreasing, 0 stable, 1 increasing
    
    // Geographic Analytics
    private List<GeographicSalesDto> geographicSales;
    
    // Hourly Analytics
    private List<HourlyAnalyticsDto> hourlyAnalytics;
    
    // Nested DTOs
    public static class DailyRevenueDto {
        private String date;
        private BigDecimal revenue;
        private Integer ticketsSold;
        
        public DailyRevenueDto(String date, BigDecimal revenue, Integer ticketsSold) {
            this.date = date;
            this.revenue = revenue;
            this.ticketsSold = ticketsSold;
        }
        
        // Getters and setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Integer getTicketsSold() { return ticketsSold; }
        public void setTicketsSold(Integer ticketsSold) { this.ticketsSold = ticketsSold; }
    }
    
    public static class DailyTicketSalesDto {
        private String date;
        private Integer sales;
        private Integer capacity;
        
        public DailyTicketSalesDto(String date, Integer sales, Integer capacity) {
            this.date = date;
            this.sales = sales;
            this.capacity = capacity;
        }
        
        // Getters and setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Integer getSales() { return sales; }
        public void setSales(Integer sales) { this.sales = sales; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
    }
    
    public static class TicketTypeAnalyticsDto {
        private String ticketTypeName;
        private BigDecimal price;
        private Integer quantity;
        private Integer sold;
        private Integer available;
        private BigDecimal revenue;
        private Double salesPercentage;
        
        public TicketTypeAnalyticsDto(String ticketTypeName, BigDecimal price, Integer quantity, Integer sold, Integer available, BigDecimal revenue, Double salesPercentage) {
            this.ticketTypeName = ticketTypeName;
            this.price = price;
            this.quantity = quantity;
            this.sold = sold;
            this.available = available;
            this.revenue = revenue;
            this.salesPercentage = salesPercentage;
        }
        
        // Getters and setters
        public String getTicketTypeName() { return ticketTypeName; }
        public void setTicketTypeName(String ticketTypeName) { this.ticketTypeName = ticketTypeName; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Integer getSold() { return sold; }
        public void setSold(Integer sold) { this.sold = sold; }
        public Integer getAvailable() { return available; }
        public void setAvailable(Integer available) { this.available = available; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Double getSalesPercentage() { return salesPercentage; }
        public void setSalesPercentage(Double salesPercentage) { this.salesPercentage = salesPercentage; }
    }
    
    public static class CustomerSegmentDto {
        private String segmentName;
        private Integer customerCount;
        private BigDecimal totalSpent;
        private Double averageSpent;
        
        public CustomerSegmentDto(String segmentName, Integer customerCount, BigDecimal totalSpent, Double averageSpent) {
            this.segmentName = segmentName;
            this.customerCount = customerCount;
            this.totalSpent = totalSpent;
            this.averageSpent = averageSpent;
        }
        
        // Getters and setters
        public String getSegmentName() { return segmentName; }
        public void setSegmentName(String segmentName) { this.segmentName = segmentName; }
        public Integer getCustomerCount() { return customerCount; }
        public void setCustomerCount(Integer customerCount) { this.customerCount = customerCount; }
        public BigDecimal getTotalSpent() { return totalSpent; }
        public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
        public Double getAverageSpent() { return averageSpent; }
        public void setAverageSpent(Double averageSpent) { this.averageSpent = averageSpent; }
    }
    
    public static class GeographicSalesDto {
        private String city;
        private String country;
        private Integer sales;
        private BigDecimal revenue;
        private Double percentage;
        
        public GeographicSalesDto(String city, String country, Integer sales, BigDecimal revenue, Double percentage) {
            this.city = city;
            this.country = country;
            this.sales = sales;
            this.revenue = revenue;
            this.percentage = percentage;
        }
        
        // Getters and setters
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public Integer getSales() { return sales; }
        public void setSales(Integer sales) { this.sales = sales; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Double getPercentage() { return percentage; }
        public void setPercentage(Double percentage) { this.percentage = percentage; }
    }
    
    public static class HourlyAnalyticsDto {
        private Integer hour;
        private Integer sales;
        private BigDecimal revenue;
        
        public HourlyAnalyticsDto(Integer hour, Integer sales, BigDecimal revenue) {
            this.hour = hour;
            this.sales = sales;
            this.revenue = revenue;
        }
        
        // Getters and setters
        public Integer getHour() { return hour; }
        public void setHour(Integer hour) { this.hour = hour; }
        public Integer getSales() { return sales; }
        public void setSales(Integer sales) { this.sales = sales; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    }
    
    // Main class getters and setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    
    public String getEventCity() { return eventCity; }
    public void setEventCity(String eventCity) { this.eventCity = eventCity; }
    
    public String getEventStatus() { return eventStatus; }
    public void setEventStatus(String eventStatus) { this.eventStatus = eventStatus; }
    
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public BigDecimal getRevenueGrowth() { return revenueGrowth; }
    public void setRevenueGrowth(BigDecimal revenueGrowth) { this.revenueGrowth = revenueGrowth; }
    
    public Integer getTotalTicketsSold() { return totalTicketsSold; }
    public void setTotalTicketsSold(Integer totalTicketsSold) { this.totalTicketsSold = totalTicketsSold; }
    
    public Integer getTotalTicketsAvailable() { return totalTicketsAvailable; }
    public void setTotalTicketsAvailable(Integer totalTicketsAvailable) { this.totalTicketsAvailable = totalTicketsAvailable; }
    
    public Integer getTicketsRemaining() { return ticketsRemaining; }
    public void setTicketsRemaining(Integer ticketsRemaining) { this.ticketsRemaining = ticketsRemaining; }
    
    public Double getSalesPercentage() { return salesPercentage; }
    public void setSalesPercentage(Double salesPercentage) { this.salesPercentage = salesPercentage; }
    
    public BigDecimal getAverageTicketPrice() { return averageTicketPrice; }
    public void setAverageTicketPrice(BigDecimal averageTicketPrice) { this.averageTicketPrice = averageTicketPrice; }
    
    public BigDecimal getHighestTicketPrice() { return highestTicketPrice; }
    public void setHighestTicketPrice(BigDecimal highestTicketPrice) { this.highestTicketPrice = highestTicketPrice; }
    
    public BigDecimal getLowestTicketPrice() { return lowestTicketPrice; }
    public void setLowestTicketPrice(BigDecimal lowestTicketPrice) { this.lowestTicketPrice = lowestTicketPrice; }
    
    public List<DailyRevenueDto> getDailyRevenue() { return dailyRevenue; }
    public void setDailyRevenue(List<DailyRevenueDto> dailyRevenue) { this.dailyRevenue = dailyRevenue; }
    
    public List<DailyTicketSalesDto> getDailyTicketSales() { return dailyTicketSales; }
    public void setDailyTicketSales(List<DailyTicketSalesDto> dailyTicketSales) { this.dailyTicketSales = dailyTicketSales; }
    
    public List<TicketTypeAnalyticsDto> getTicketTypeAnalytics() { return ticketTypeAnalytics; }
    public void setTicketTypeAnalytics(List<TicketTypeAnalyticsDto> ticketTypeAnalytics) { this.ticketTypeAnalytics = ticketTypeAnalytics; }
    
    public Integer getUniqueCustomers() { return uniqueCustomers; }
    public void setUniqueCustomers(Integer uniqueCustomers) { this.uniqueCustomers = uniqueCustomers; }
    
    public Integer getTotalVisitors() { return totalVisitors; }
    public void setTotalVisitors(Integer totalVisitors) { this.totalVisitors = totalVisitors; }
    
    public Integer getRepeatCustomers() { return repeatCustomers; }
    public void setRepeatCustomers(Integer repeatCustomers) { this.repeatCustomers = repeatCustomers; }
    
    public Double getRepeatCustomerRate() { return repeatCustomerRate; }
    public void setRepeatCustomerRate(Double repeatCustomerRate) { this.repeatCustomerRate = repeatCustomerRate; }
    
    public List<CustomerSegmentDto> getCustomerSegments() { return customerSegments; }
    public void setCustomerSegments(List<CustomerSegmentDto> customerSegments) { this.customerSegments = customerSegments; }
    
    public Double getConversionRate() { return conversionRate; }
    public void setConversionRate(Double conversionRate) { this.conversionRate = conversionRate; }
    
    public LocalDateTime getFirstSaleDate() { return firstSaleDate; }
    public void setFirstSaleDate(LocalDateTime firstSaleDate) { this.firstSaleDate = firstSaleDate; }
    
    public LocalDateTime getLastSaleDate() { return lastSaleDate; }
    public void setLastSaleDate(LocalDateTime lastSaleDate) { this.lastSaleDate = lastSaleDate; }
    
    public Integer getDaysUntilEvent() { return daysUntilEvent; }
    public void setDaysUntilEvent(Integer daysUntilEvent) { this.daysUntilEvent = daysUntilEvent; }
    
    public Integer getSalesTrend() { return salesTrend; }
    public void setSalesTrend(Integer salesTrend) { this.salesTrend = salesTrend; }
    
    public List<GeographicSalesDto> getGeographicSales() { return geographicSales; }
    public void setGeographicSales(List<GeographicSalesDto> geographicSales) { this.geographicSales = geographicSales; }
    
    public List<HourlyAnalyticsDto> getHourlyAnalytics() { return hourlyAnalytics; }
    public void setHourlyAnalytics(List<HourlyAnalyticsDto> hourlyAnalytics) { this.hourlyAnalytics = hourlyAnalytics; }
}