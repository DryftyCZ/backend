package com.kaiwaru.ticketing.dto;

import java.time.LocalDateTime;

public class TodayAgendaDto {
    private String type; // "event", "order", "system"
    private String title;
    private String subtitle;
    private String time;
    private String icon;
    private Long eventId;
    private Integer count;

    public TodayAgendaDto() {}

    public TodayAgendaDto(String type, String title, String subtitle, String time, String icon) {
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.icon = icon;
    }

    public TodayAgendaDto(String type, String title, String subtitle, String time, String icon, Long eventId, Integer count) {
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.icon = icon;
        this.eventId = eventId;
        this.count = count;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}