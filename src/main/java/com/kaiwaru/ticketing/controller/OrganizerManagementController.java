package com.kaiwaru.ticketing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaiwaru.ticketing.dto.OrganizerCommissionDto;
import com.kaiwaru.ticketing.dto.UpdateCommissionRequest;
import com.kaiwaru.ticketing.service.OrganizerCommissionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/organizers")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class OrganizerManagementController {

    @Autowired
    private OrganizerCommissionService commissionService;

    @GetMapping
    public ResponseEntity<List<OrganizerCommissionDto>> getAllOrganizers() {
        List<OrganizerCommissionDto> organizers = commissionService.getAllOrganizersWithCommissions();
        return ResponseEntity.ok(organizers);
    }

    @PutMapping("/commission")
    public ResponseEntity<?> updateCommission(@Valid @RequestBody UpdateCommissionRequest request) {
        try {
            OrganizerCommissionDto updated = commissionService.updateCommission(request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}