package com.kaiwaru.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationSettingsDto {
    private String organizationName;
    private String organizationEmail;
    private String language;
    private String currency;
    private String timezone;
    private String dateFormat;
    private String ticketPrefix;
    private Boolean enableEmailNotifications;
    private Boolean enableSmsNotifications;
    private Boolean enableAutoBackup;
    private String backupFrequency;
    private Boolean maintenanceMode;
}