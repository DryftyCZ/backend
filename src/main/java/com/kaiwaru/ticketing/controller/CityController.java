package com.kaiwaru.ticketing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.service.CityService;

@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchCities(
            @RequestParam(required = false) String query) {
        List<String> cities = cityService.searchCities(query);
        return ResponseEntity.ok(cities);
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllCities() {
        List<String> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }
}