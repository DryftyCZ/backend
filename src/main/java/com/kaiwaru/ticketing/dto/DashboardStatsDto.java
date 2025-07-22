package com.kaiwaru.ticketing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardStatsDto {
    private BigDecimal totalRevenue;
    private BigDecimal previousMonthRevenue;
    private Double revenueGrowth;
    private Integer totalTicketsSold;
    private Double ticketsGrowth;
    private Double conversionRate;
    private Double conversionGrowth;
    private Integer activeEvents;
    private Integer totalCustomers;
    private Integer newCustomersThisMonth;
    private BigDecimal averageTicketPrice;
    private Double averageTicketPriceGrowth;
    private Double repeatCustomerRate;
    private Double customerSatisfactionScore;
    private Double averageProcessingTime;
    private Double processingTimeImprovement;
    private List<RevenueByDateDto> revenueByDate;
    private List<SalesByCategoryDto> salesByCategory;
    private List<UpcomingEventDto> upcomingEvents;
    private List<RecentTransactionDto> recentTransactions;
    private List<GeolocationStatsDto> geolocationStats;
    private List<RealTimeStatsDto> realTimeStats;
    private List<TrafficSourceDto> trafficSources;
    private List<CitySalesDto> citySales;

    // Constructors
    public DashboardStatsDto() {}

    // Getters and Setters
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getPreviousMonthRevenue() {
        return previousMonthRevenue;
    }

    public void setPreviousMonthRevenue(BigDecimal previousMonthRevenue) {
        this.previousMonthRevenue = previousMonthRevenue;
    }

    public Double getRevenueGrowth() {
        return revenueGrowth;
    }

    public void setRevenueGrowth(Double revenueGrowth) {
        this.revenueGrowth = revenueGrowth;
    }

    public Integer getTotalTicketsSold() {
        return totalTicketsSold;
    }

    public void setTotalTicketsSold(Integer totalTicketsSold) {
        this.totalTicketsSold = totalTicketsSold;
    }

    public Double getTicketsGrowth() {
        return ticketsGrowth;
    }

    public void setTicketsGrowth(Double ticketsGrowth) {
        this.ticketsGrowth = ticketsGrowth;
    }

    public Double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public Double getConversionGrowth() {
        return conversionGrowth;
    }

    public void setConversionGrowth(Double conversionGrowth) {
        this.conversionGrowth = conversionGrowth;
    }

    public Integer getActiveEvents() {
        return activeEvents;
    }

    public void setActiveEvents(Integer activeEvents) {
        this.activeEvents = activeEvents;
    }

    public Integer getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Integer totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public Integer getNewCustomersThisMonth() {
        return newCustomersThisMonth;
    }

    public void setNewCustomersThisMonth(Integer newCustomersThisMonth) {
        this.newCustomersThisMonth = newCustomersThisMonth;
    }

    public BigDecimal getAverageTicketPrice() {
        return averageTicketPrice;
    }

    public void setAverageTicketPrice(BigDecimal averageTicketPrice) {
        this.averageTicketPrice = averageTicketPrice;
    }

    public Double getAverageTicketPriceGrowth() {
        return averageTicketPriceGrowth;
    }

    public void setAverageTicketPriceGrowth(Double averageTicketPriceGrowth) {
        this.averageTicketPriceGrowth = averageTicketPriceGrowth;
    }

    public Double getRepeatCustomerRate() {
        return repeatCustomerRate;
    }

    public void setRepeatCustomerRate(Double repeatCustomerRate) {
        this.repeatCustomerRate = repeatCustomerRate;
    }

    public Double getCustomerSatisfactionScore() {
        return customerSatisfactionScore;
    }

    public void setCustomerSatisfactionScore(Double customerSatisfactionScore) {
        this.customerSatisfactionScore = customerSatisfactionScore;
    }

    public Double getAverageProcessingTime() {
        return averageProcessingTime;
    }

    public void setAverageProcessingTime(Double averageProcessingTime) {
        this.averageProcessingTime = averageProcessingTime;
    }

    public Double getProcessingTimeImprovement() {
        return processingTimeImprovement;
    }

    public void setProcessingTimeImprovement(Double processingTimeImprovement) {
        this.processingTimeImprovement = processingTimeImprovement;
    }

    public List<RevenueByDateDto> getRevenueByDate() {
        return revenueByDate;
    }

    public void setRevenueByDate(List<RevenueByDateDto> revenueByDate) {
        this.revenueByDate = revenueByDate;
    }

    public List<SalesByCategoryDto> getSalesByCategory() {
        return salesByCategory;
    }

    public void setSalesByCategory(List<SalesByCategoryDto> salesByCategory) {
        this.salesByCategory = salesByCategory;
    }

    public List<UpcomingEventDto> getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(List<UpcomingEventDto> upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public List<RecentTransactionDto> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<RecentTransactionDto> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public List<GeolocationStatsDto> getGeolocationStats() {
        return geolocationStats;
    }

    public void setGeolocationStats(List<GeolocationStatsDto> geolocationStats) {
        this.geolocationStats = geolocationStats;
    }

    public List<RealTimeStatsDto> getRealTimeStats() {
        return realTimeStats;
    }

    public void setRealTimeStats(List<RealTimeStatsDto> realTimeStats) {
        this.realTimeStats = realTimeStats;
    }

    public List<TrafficSourceDto> getTrafficSources() {
        return trafficSources;
    }

    public void setTrafficSources(List<TrafficSourceDto> trafficSources) {
        this.trafficSources = trafficSources;
    }

    public List<CitySalesDto> getCitySales() {
        return citySales;
    }

    public void setCitySales(List<CitySalesDto> citySales) {
        this.citySales = citySales;
    }

    // Nested DTOs
    public static class RevenueByDateDto {
        private String date;
        private BigDecimal revenue;

        public RevenueByDateDto() {}

        public RevenueByDateDto(String date, BigDecimal revenue) {
            this.date = date;
            this.revenue = revenue;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(BigDecimal revenue) {
            this.revenue = revenue;
        }
    }

    public static class SalesByCategoryDto {
        private String category;
        private Integer sales;

        public SalesByCategoryDto() {}

        public SalesByCategoryDto(String category, Integer sales) {
            this.category = category;
            this.sales = sales;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Integer getSales() {
            return sales;
        }

        public void setSales(Integer sales) {
            this.sales = sales;
        }
    }

    public static class UpcomingEventDto {
        private Long id;
        private String name;
        private String description;
        private String address;
        private String city;
        private LocalDateTime date;
        private Integer totalTickets;
        private Integer soldTickets;

        public UpcomingEventDto() {}

        public UpcomingEventDto(Long id, String name, String description, String address, String city, LocalDateTime date, Integer totalTickets, Integer soldTickets) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.address = address;
            this.city = city;
            this.date = date;
            this.totalTickets = totalTickets;
            this.soldTickets = soldTickets;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }

        public Integer getTotalTickets() {
            return totalTickets;
        }

        public void setTotalTickets(Integer totalTickets) {
            this.totalTickets = totalTickets;
        }

        public Integer getSoldTickets() {
            return soldTickets;
        }

        public void setSoldTickets(Integer soldTickets) {
            this.soldTickets = soldTickets;
        }
    }

    public static class RecentTransactionDto {
        private Long id;
        private String eventName;
        private String customerName;
        private BigDecimal amount;
        private LocalDateTime date;
        private String status;

        public RecentTransactionDto() {}

        public RecentTransactionDto(Long id, String eventName, String customerName, BigDecimal amount, LocalDateTime date, String status) {
            this.id = id;
            this.eventName = eventName;
            this.customerName = customerName;
            this.amount = amount;
            this.date = date;
            this.status = status;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class GeolocationStatsDto {
        private String country;
        private String countryCode;
        private Integer sales;
        private BigDecimal revenue;
        private Double percentage;
        private String flag;

        public GeolocationStatsDto() {}

        public GeolocationStatsDto(String country, String countryCode, Integer sales, BigDecimal revenue, Double percentage, String flag) {
            this.country = country;
            this.countryCode = countryCode;
            this.sales = sales;
            this.revenue = revenue;
            this.percentage = percentage;
            this.flag = flag;
        }

        // Getters and Setters
        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public Integer getSales() {
            return sales;
        }

        public void setSales(Integer sales) {
            this.sales = sales;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(BigDecimal revenue) {
            this.revenue = revenue;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }
    }

    public static class RealTimeStatsDto {
        private String timestamp;
        private Integer activeSales;
        private Integer activeVisitors;
        private BigDecimal currentRevenue;
        private Double conversionRate;

        public RealTimeStatsDto() {}

        public RealTimeStatsDto(String timestamp, Integer activeSales, Integer activeVisitors, BigDecimal currentRevenue, Double conversionRate) {
            this.timestamp = timestamp;
            this.activeSales = activeSales;
            this.activeVisitors = activeVisitors;
            this.currentRevenue = currentRevenue;
            this.conversionRate = conversionRate;
        }

        // Getters and Setters
        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public Integer getActiveSales() {
            return activeSales;
        }

        public void setActiveSales(Integer activeSales) {
            this.activeSales = activeSales;
        }

        public Integer getActiveVisitors() {
            return activeVisitors;
        }

        public void setActiveVisitors(Integer activeVisitors) {
            this.activeVisitors = activeVisitors;
        }

        public BigDecimal getCurrentRevenue() {
            return currentRevenue;
        }

        public void setCurrentRevenue(BigDecimal currentRevenue) {
            this.currentRevenue = currentRevenue;
        }

        public Double getConversionRate() {
            return conversionRate;
        }

        public void setConversionRate(Double conversionRate) {
            this.conversionRate = conversionRate;
        }
    }

    public static class TrafficSourceDto {
        private String source;
        private Double percentage;
        private Integer visitors;
        private Integer conversions;
        private Double conversionRate;

        public TrafficSourceDto() {}

        public TrafficSourceDto(String source, Double percentage, Integer visitors, Integer conversions, Double conversionRate) {
            this.source = source;
            this.percentage = percentage;
            this.visitors = visitors;
            this.conversions = conversions;
            this.conversionRate = conversionRate;
        }

        // Getters and Setters
        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }

        public Integer getVisitors() {
            return visitors;
        }

        public void setVisitors(Integer visitors) {
            this.visitors = visitors;
        }

        public Integer getConversions() {
            return conversions;
        }

        public void setConversions(Integer conversions) {
            this.conversions = conversions;
        }

        public Double getConversionRate() {
            return conversionRate;
        }

        public void setConversionRate(Double conversionRate) {
            this.conversionRate = conversionRate;
        }
    }

    public static class CitySalesDto {
        private String city;
        private String country;
        private Integer ticketsSold;
        private BigDecimal revenue;
        private Double percentage;

        public CitySalesDto() {}

        public CitySalesDto(String city, String country, Integer ticketsSold, BigDecimal revenue, Double percentage) {
            this.city = city;
            this.country = country;
            this.ticketsSold = ticketsSold;
            this.revenue = revenue;
            this.percentage = percentage;
        }

        // Getters and Setters
        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Integer getTicketsSold() {
            return ticketsSold;
        }

        public void setTicketsSold(Integer ticketsSold) {
            this.ticketsSold = ticketsSold;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(BigDecimal revenue) {
            this.revenue = revenue;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }
}