package com.kaiwaru.ticketing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ip_processing_queue", indexes = {
    @Index(name = "idx_ip_status", columnList = "processing_status"),
    @Index(name = "idx_ip_created", columnList = "created_at"),
    @Index(name = "idx_ip_attempts", columnList = "retry_count")
})
public class IpProcessingQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", nullable = false, unique = true)
    private String ipAddress;

    @Column(name = "visitor_session_id")
    private Long visitorSessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false)
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "priority")
    private Integer priority = 5; // 1 = highest, 10 = lowest

    // Geolocation data (will be filled after processing)
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

    public enum ProcessingStatus {
        PENDING,     // Waiting to be processed
        PROCESSING,  // Currently being processed
        COMPLETED,   // Successfully processed
        FAILED,      // Failed after retries
        SKIPPED      // Skipped (local IP, etc.)
    }

    // Constructors
    public IpProcessingQueue() {
        this.createdAt = LocalDateTime.now();
    }

    public IpProcessingQueue(String ipAddress, Long visitorSessionId) {
        this();
        this.ipAddress = ipAddress;
        this.visitorSessionId = visitorSessionId;
    }

    public IpProcessingQueue(String ipAddress, Long visitorSessionId, Integer priority) {
        this(ipAddress, visitorSessionId);
        this.priority = priority;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getVisitorSessionId() {
        return visitorSessionId;
    }

    public void setVisitorSessionId(Long visitorSessionId) {
        this.visitorSessionId = visitorSessionId;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    // Helper methods
    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean hasReachedMaxRetries() {
        return this.retryCount >= 3;
    }

    public void markAsProcessing() {
        this.processingStatus = ProcessingStatus.PROCESSING;
    }

    public void markAsCompleted() {
        this.processingStatus = ProcessingStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String error) {
        this.processingStatus = ProcessingStatus.FAILED;
        this.lastError = error;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsSkipped() {
        this.processingStatus = ProcessingStatus.SKIPPED;
        this.processedAt = LocalDateTime.now();
    }
}