package com.kaiwaru.ticketing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "web_visits")
public class WebVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "page_url", length = 500)
    private String pageUrl;

    @Column(name = "visitor_ip")
    private String visitorIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "referrer_url", length = 500)
    private String referrerUrl;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "event_related")
    private Boolean eventRelated = false;

    @Column(name = "event_id")
    private Long eventId;

    // Constructors
    public WebVisit() {}

    public WebVisit(String customerId, String pageUrl, String visitorIp, String userAgent, String sessionId, String referrerUrl) {
        this.customerId = customerId;
        this.pageUrl = pageUrl;
        this.visitorIp = visitorIp;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.referrerUrl = referrerUrl;
        this.timestamp = LocalDateTime.now();
        this.eventRelated = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }

    public String getVisitorIp() { return visitorIp; }
    public void setVisitorIp(String visitorIp) { this.visitorIp = visitorIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getReferrerUrl() { return referrerUrl; }
    public void setReferrerUrl(String referrerUrl) { this.referrerUrl = referrerUrl; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Boolean getEventRelated() { return eventRelated; }
    public void setEventRelated(Boolean eventRelated) { this.eventRelated = eventRelated; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
}