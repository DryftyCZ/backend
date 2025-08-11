package com.kaiwaru.ticketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kaiwaru.ticketing.dto.OrganizationSettingsDto;
import com.kaiwaru.ticketing.model.OrganizationSettings;
import com.kaiwaru.ticketing.repository.OrganizationSettingsRepository;
import com.kaiwaru.ticketing.security.response.MessageResponse;

@RestController
@RequestMapping("/settings")
@CrossOrigin(originPatterns = "*", maxAge = 3600, allowCredentials = "true")
public class SettingsController {
    
    @Autowired
    private OrganizationSettingsRepository settingsRepository;

    @GetMapping("/organization")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    public ResponseEntity<OrganizationSettingsDto> getOrganizationSettings() {
        OrganizationSettings settings = settingsRepository.findAll().stream()
                .findFirst()
                .orElse(new OrganizationSettings());

        OrganizationSettingsDto dto = convertToDto(settings);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/organization")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateOrganizationSettings(
            @RequestBody OrganizationSettingsDto request) {
        OrganizationSettings settings = settingsRepository.findAll().stream()
                .findFirst()
                .orElse(new OrganizationSettings());

        // Update settings
        settings.setOrganizationName(request.getOrganizationName());
        settings.setOrganizationEmail(request.getOrganizationEmail());
        settings.setLanguage(request.getLanguage());
        settings.setCurrency(request.getCurrency());
        settings.setTimezone(request.getTimezone());
        settings.setDateFormat(request.getDateFormat());
        settings.setTicketPrefix(request.getTicketPrefix());
        settings.setEnableEmailNotifications(request.getEnableEmailNotifications());
        settings.setEnableSmsNotifications(request.getEnableSmsNotifications());
        settings.setEnableAutoBackup(request.getEnableAutoBackup());
        settings.setBackupFrequency(request.getBackupFrequency());
        settings.setMaintenanceMode(request.getMaintenanceMode());

        settingsRepository.save(settings);
        return ResponseEntity.ok(new MessageResponse("Organization settings updated successfully!"));
    }

    private OrganizationSettingsDto convertToDto(OrganizationSettings settings) {
        OrganizationSettingsDto dto = new OrganizationSettingsDto();
        dto.setOrganizationName(settings.getOrganizationName());
        dto.setOrganizationEmail(settings.getOrganizationEmail());
        dto.setLanguage(settings.getLanguage());
        dto.setCurrency(settings.getCurrency());
        dto.setTimezone(settings.getTimezone());
        dto.setDateFormat(settings.getDateFormat());
        dto.setTicketPrefix(settings.getTicketPrefix());
        dto.setEnableEmailNotifications(settings.getEnableEmailNotifications());
        dto.setEnableSmsNotifications(settings.getEnableSmsNotifications());
        dto.setEnableAutoBackup(settings.getEnableAutoBackup());
        dto.setBackupFrequency(settings.getBackupFrequency());
        dto.setMaintenanceMode(settings.getMaintenanceMode());
        return dto;
    }
}