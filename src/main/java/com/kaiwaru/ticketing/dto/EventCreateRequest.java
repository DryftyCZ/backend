package com.kaiwaru.ticketing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EventCreateRequest {
    
    @NotBlank(message = "Název události je povinný")
    private String name;
    
    private String description;
    
    private String address;
    
    @NotBlank(message = "Město je povinné")
    private String city;
    
    @NotNull(message = "Datum události je povinné")
    private LocalDate date;
    
    @NotEmpty(message = "Musí být definován alespoň jeden typ vstupenky")
    @Valid
    private List<TicketTypeRequest> ticketTypes;
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class TicketTypeRequest {
        
        @NotBlank(message = "Název typu vstupenky je povinný")
        private String name;
        
        private String description;
        
        @NotNull(message = "Cena je povinná")
        private BigDecimal price;
        
        @NotNull(message = "Množství vstupenek je povinné")
        private Integer quantity;
    }
}