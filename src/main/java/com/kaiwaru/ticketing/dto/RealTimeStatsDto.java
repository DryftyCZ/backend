package com.kaiwaru.ticketing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RealTimeStatsDto {
    private List<MetricDto> metrics;
    private List<GeographicDataDto> geographicData;
    private List<ActivityDto> recentActivity;
    private List<AlertDto> alerts;

    public RealTimeStatsDto() {}

    // Getters and Setters
    public List<MetricDto> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<MetricDto> metrics) {
        this.metrics = metrics;
    }

    public List<GeographicDataDto> getGeographicData() {
        return geographicData;
    }

    public void setGeographicData(List<GeographicDataDto> geographicData) {
        this.geographicData = geographicData;
    }

    public List<ActivityDto> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<ActivityDto> recentActivity) {
        this.recentActivity = recentActivity;
    }

    public List<AlertDto> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertDto> alerts) {
        this.alerts = alerts;
    }

    // Nested DTOs
    public static class MetricDto {
        private String title;
        private String value;
        private String unit;
        private Double change;
        private String changeLabel;
        private String color;
        private String icon;

        public MetricDto() {}

        public MetricDto(String title, String value, String unit, Double change, String changeLabel, String color, String icon) {
            this.title = title;
            this.value = value;
            this.unit = unit;
            this.change = change;
            this.changeLabel = changeLabel;
            this.color = color;
            this.icon = icon;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public Double getChange() {
            return change;
        }

        public void setChange(Double change) {
            this.change = change;
        }

        public String getChangeLabel() {
            return changeLabel;
        }

        public void setChangeLabel(String changeLabel) {
            this.changeLabel = changeLabel;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class GeographicDataDto {
        private String city;
        private Integer sales;
        private Double growth;
        private String region;

        public GeographicDataDto() {}

        public GeographicDataDto(String city, Integer sales, Double growth, String region) {
            this.city = city;
            this.sales = sales;
            this.growth = growth;
            this.region = region;
        }

        // Getters and Setters
        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Integer getSales() {
            return sales;
        }

        public void setSales(Integer sales) {
            this.sales = sales;
        }

        public Double getGrowth() {
            return growth;
        }

        public void setGrowth(Double growth) {
            this.growth = growth;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }

    public static class ActivityDto {
        private String type;
        private String message;
        private LocalDateTime timestamp;
        private String severity;
        private String icon;

        public ActivityDto() {}

        public ActivityDto(String type, String message, LocalDateTime timestamp, String severity, String icon) {
            this.type = type;
            this.message = message;
            this.timestamp = timestamp;
            this.severity = severity;
            this.icon = icon;
        }

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class AlertDto {
        private String title;
        private String description;
        private String type;
        private String severity;
        private LocalDateTime timestamp;
        private String action;
        private String value;
        private String color;

        public AlertDto() {}

        public AlertDto(String title, String description, String type, String severity, LocalDateTime timestamp, String action, String value, String color) {
            this.title = title;
            this.description = description;
            this.type = type;
            this.severity = severity;
            this.timestamp = timestamp;
            this.action = action;
            this.value = value;
            this.color = color;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}