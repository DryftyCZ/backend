package com.kaiwaru.ticketing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organization_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String organizationName = "SmartTicket s.r.o.";

    @Column(nullable = false)
    private String organizationEmail = "info@smartticket.cz";

    @Column(nullable = false)
    private String language = "cs";

    @Column(nullable = false)
    private String currency = "CZK";

    @Column(nullable = false)
    private String timezone = "Europe/Prague";

    @Column(nullable = false)
    private String dateFormat = "DD.MM.YYYY";

    @Column(nullable = false)
    private String ticketPrefix = "ST";

    private Boolean enableEmailNotifications = true;
    private Boolean enableSmsNotifications = false;
    private Boolean enableAutoBackup = true;
    private String backupFrequency = "daily";
    private Boolean maintenanceMode = false;
}