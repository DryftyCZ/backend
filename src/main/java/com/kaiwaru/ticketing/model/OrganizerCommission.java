package com.kaiwaru.ticketing.model;

import java.math.BigDecimal;

import com.kaiwaru.ticketing.model.Auth.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizer_commissions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrganizerCommission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "organizer_id", unique = true, nullable = false)
    private User organizer;

    @Column(name = "commission_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionPercentage = BigDecimal.valueOf(20.0); // Default 20%

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}