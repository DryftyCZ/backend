package com.kaiwaru.ticketing.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kaiwaru.ticketing.model.Auth.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String qrCode;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private String ticketNumber;

    @Column(nullable = true)
    private String customerEmail;

    @Column(nullable = true)
    private String customerName;

    private LocalDateTime purchaseDate;
    private LocalDateTime usedDate;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Event event;

    @ManyToOne(optional = true)
    @JsonIgnore
    private User customer;

    @Column(nullable = true)
    private String country;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String ipAddress;

}
