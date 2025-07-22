package com.kaiwaru.ticketing.model;

import com.kaiwaru.ticketing.model.Auth.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_sessions", indexes = {
    @Index(name = "idx_visitor_ip", columnList = "ip_address"),
    @Index(name = "idx_visitor_timestamp", columnList = "timestamp"),
    @Index(name = "idx_visitor_event", columnList = "event_id")
})
public class VisitorSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "referer")
    private String referer;

    @Column(name = "traffic_source")
    private String trafficSource;

    @Column(name = "country")
    private String country;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "region")
    private String region;

    @Column(name = "city")
    private String city;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "isp")
    private String isp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "page_views")
    private Integer pageViews = 1;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "converted")
    private Boolean converted = false;

    @Column(name = "tickets_purchased")
    private Integer ticketsPurchased = 0;

    @Column(name = "revenue_generated")
    private Double revenueGenerated = 0.0;

    // Constructors
    public VisitorSession() {}

    public VisitorSession(String sessionId, String ipAddress, String userAgent, String referer) {
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referer = referer;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getTrafficSource() {
        return trafficSource;
    }

    public void setTrafficSource(String trafficSource) {
        this.trafficSource = trafficSource;
    }

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getPageViews() {
        return pageViews;
    }

    public void setPageViews(Integer pageViews) {
        this.pageViews = pageViews;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Boolean getConverted() {
        return converted;
    }

    public void setConverted(Boolean converted) {
        this.converted = converted;
    }

    public Integer getTicketsPurchased() {
        return ticketsPurchased;
    }

    public void setTicketsPurchased(Integer ticketsPurchased) {
        this.ticketsPurchased = ticketsPurchased;
    }

    public Double getRevenueGenerated() {
        return revenueGenerated;
    }

    public void setRevenueGenerated(Double revenueGenerated) {
        this.revenueGenerated = revenueGenerated;
    }
}