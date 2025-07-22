package com.kaiwaru.ticketing.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateCommissionRequest {
    
    @NotNull(message = "ID organizátora je povinné")
    private Long organizerId;
    
    @NotNull(message = "Procento provize je povinné")
    @DecimalMin(value = "0.0", message = "Procento provize musí být minimálně 0%")
    @DecimalMax(value = "100.0", message = "Procento provize může být maximálně 100%")
    private BigDecimal commissionPercentage;
    
    private Boolean isActive = true;
}