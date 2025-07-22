package com.kaiwaru.ticketing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "embed_visits")
public class EmbedVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "referrer_url", length = 500)
    private String referrerUrl;

    @Column(name = "visitor_ip")
    private String visitorIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "converted", nullable = false)
    private Boolean converted = false;

    // Constructors
    public EmbedVisit() {}

    public EmbedVisit(Long eventId, String customerId, String referrerUrl, String visitorIp, String userAgent, String sessionId) {
        this.eventId = eventId;
        this.customerId = customerId;
        this.referrerUrl = referrerUrl;
        this.visitorIp = visitorIp;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.timestamp = LocalDateTime.now();
        this.converted = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getReferrerUrl() { return referrerUrl; }
    public void setReferrerUrl(String referrerUrl) { this.referrerUrl = referrerUrl; }

    public String getVisitorIp() { return visitorIp; }
    public void setVisitorIp(String visitorIp) { this.visitorIp = visitorIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Boolean getConverted() { return converted; }
    public void setConverted(Boolean converted) { this.converted = converted; }
}