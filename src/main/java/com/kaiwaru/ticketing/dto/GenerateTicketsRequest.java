package com.kaiwaru.ticketing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GenerateTicketsRequest {
    @NotNull
    private Long eventId;

    @NotNull
    @Min(1)
    private Integer count;

    // gettery/settery
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
