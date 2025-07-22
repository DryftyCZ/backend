package com.kaiwaru.ticketing.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrganizerCommissionDto {
    private Long id;
    private Long organizerId;
    private String organizerUsername;
    private String organizerEmail;
    private BigDecimal commissionPercentage;
    private Boolean isActive;
    private Integer totalEvents;
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
}