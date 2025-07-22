package com.kaiwaru.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LocationSuggestion {
    private String city;
    private String region;
    private String venueName;
    private String address;
    
    public String getFullLocation() {
        return String.format("%s, %s", venueName, city);
    }
    
    public String getDisplayText() {
        return String.format("%s - %s", venueName, address);
    }
}