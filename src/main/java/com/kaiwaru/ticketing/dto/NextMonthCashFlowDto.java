package com.kaiwaru.ticketing.dto;

import java.math.BigDecimal;
import java.util.List;

public class NextMonthCashFlowDto {
    private BigDecimal expectedRevenue;
    private BigDecimal potentialRevenue; // Total value of all available tickets
    private Integer upcomingEvents;
    private Integer ticketsSoldForNextMonth;
    private List<UpcomingEventDto> upcomingEventDetails;

    public NextMonthCashFlowDto() {}

    public NextMonthCashFlowDto(BigDecimal expectedRevenue, BigDecimal potentialRevenue, Integer upcomingEvents, Integer ticketsSoldForNextMonth, List<UpcomingEventDto> upcomingEventDetails) {
        this.expectedRevenue = expectedRevenue;
        this.potentialRevenue = potentialRevenue;
        this.upcomingEvents = upcomingEvents;
        this.ticketsSoldForNextMonth = ticketsSoldForNextMonth;
        this.upcomingEventDetails = upcomingEventDetails;
    }

    // Getters and Setters
    public BigDecimal getExpectedRevenue() {
        return expectedRevenue;
    }

    public void setExpectedRevenue(BigDecimal expectedRevenue) {
        this.expectedRevenue = expectedRevenue;
    }

    public BigDecimal getPotentialRevenue() {
        return potentialRevenue;
    }

    public void setPotentialRevenue(BigDecimal potentialRevenue) {
        this.potentialRevenue = potentialRevenue;
    }

    public Integer getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(Integer upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public Integer getTicketsSoldForNextMonth() {
        return ticketsSoldForNextMonth;
    }

    public void setTicketsSoldForNextMonth(Integer ticketsSoldForNextMonth) {
        this.ticketsSoldForNextMonth = ticketsSoldForNextMonth;
    }

    public List<UpcomingEventDto> getUpcomingEventDetails() {
        return upcomingEventDetails;
    }

    public void setUpcomingEventDetails(List<UpcomingEventDto> upcomingEventDetails) {
        this.upcomingEventDetails = upcomingEventDetails;
    }

    public static class UpcomingEventDto {
        private Long eventId;
        private String eventName;
        private String eventDate;
        private BigDecimal expectedRevenue;
        private Integer ticketsSold;
        private Integer totalTickets;

        public UpcomingEventDto() {}

        public UpcomingEventDto(Long eventId, String eventName, String eventDate, BigDecimal expectedRevenue, Integer ticketsSold, Integer totalTickets) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.eventDate = eventDate;
            this.expectedRevenue = expectedRevenue;
            this.ticketsSold = ticketsSold;
            this.totalTickets = totalTickets;
        }

        // Getters and Setters
        public Long getEventId() {
            return eventId;
        }

        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getEventDate() {
            return eventDate;
        }

        public void setEventDate(String eventDate) {
            this.eventDate = eventDate;
        }

        public BigDecimal getExpectedRevenue() {
            return expectedRevenue;
        }

        public void setExpectedRevenue(BigDecimal expectedRevenue) {
            this.expectedRevenue = expectedRevenue;
        }

        public Integer getTicketsSold() {
            return ticketsSold;
        }

        public void setTicketsSold(Integer ticketsSold) {
            this.ticketsSold = ticketsSold;
        }

        public Integer getTotalTickets() {
            return totalTickets;
        }

        public void setTotalTickets(Integer totalTickets) {
            this.totalTickets = totalTickets;
        }
    }
}