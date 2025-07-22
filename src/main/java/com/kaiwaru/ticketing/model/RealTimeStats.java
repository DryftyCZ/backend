package com.kaiwaru.ticketing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "real_time_stats", indexes = {
    @Index(name = "idx_realtime_timestamp", columnList = "timestamp"),
    @Index(name = "idx_realtime_event", columnList = "event_id")
})
public class RealTimeStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "active_visitors")
    private Integer activeVisitors = 0;

    @Column(name = "sales_last_minute")
    private Integer salesLastMinute = 0;

    @Column(name = "sales_last_hour")
    private Integer salesLastHour = 0;

    @Column(name = "revenue_last_minute", precision = 10, scale = 2)
    private BigDecimal revenueLastMinute = BigDecimal.ZERO;

    @Column(name = "revenue_last_hour", precision = 10, scale = 2)
    private BigDecimal revenueLastHour = BigDecimal.ZERO;

    @Column(name = "conversion_rate")
    private Double conversionRate = 0.0;

    @Column(name = "bounce_rate")
    private Double bounceRate = 0.0;

    @Column(name = "average_session_duration")
    private Double averageSessionDuration = 0.0;

    @Column(name = "pages_per_session")
    private Double pagesPerSession = 0.0;

    // Constructors
    public RealTimeStats() {
        this.timestamp = LocalDateTime.now();
    }

    public RealTimeStats(Event event) {
        this.event = event;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getActiveVisitors() {
        return activeVisitors;
    }

    public void setActiveVisitors(Integer activeVisitors) {
        this.activeVisitors = activeVisitors;
    }

    public Integer getSalesLastMinute() {
        return salesLastMinute;
    }

    public void setSalesLastMinute(Integer salesLastMinute) {
        this.salesLastMinute = salesLastMinute;
    }

    public Integer getSalesLastHour() {
        return salesLastHour;
    }

    public void setSalesLastHour(Integer salesLastHour) {
        this.salesLastHour = salesLastHour;
    }

    public BigDecimal getRevenueLastMinute() {
        return revenueLastMinute;
    }

    public void setRevenueLastMinute(BigDecimal revenueLastMinute) {
        this.revenueLastMinute = revenueLastMinute;
    }

    public BigDecimal getRevenueLastHour() {
        return revenueLastHour;
    }

    public void setRevenueLastHour(BigDecimal revenueLastHour) {
        this.revenueLastHour = revenueLastHour;
    }

    public Double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public Double getBounceRate() {
        return bounceRate;
    }

    public void setBounceRate(Double bounceRate) {
        this.bounceRate = bounceRate;
    }

    public Double getAverageSessionDuration() {
        return averageSessionDuration;
    }

    public void setAverageSessionDuration(Double averageSessionDuration) {
        this.averageSessionDuration = averageSessionDuration;
    }

    public Double getPagesPerSession() {
        return pagesPerSession;
    }

    public void setPagesPerSession(Double pagesPerSession) {
        this.pagesPerSession = pagesPerSession;
    }
}