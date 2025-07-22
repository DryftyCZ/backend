package com.kaiwaru.ticketing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.dto.LocationSuggestion;
import com.kaiwaru.ticketing.service.VenueService;

@RestController
@RequestMapping("/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping("/search")
    public ResponseEntity<List<LocationSuggestion>> searchLocations(
            @RequestParam(required = false) String query) {
        List<LocationSuggestion> suggestions = venueService.searchLocations(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        List<String> cities = venueService.getCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/by-city")
    public ResponseEntity<List<LocationSuggestion>> getVenuesByCity(
            @RequestParam String city) {
        List<LocationSuggestion> venues = venueService.getVenuesByCity(city);
        return ResponseEntity.ok(venues);
    }
}