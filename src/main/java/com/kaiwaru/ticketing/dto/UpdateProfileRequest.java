package com.kaiwaru.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String position;
    private String department;
    private String bio;
    private String language;
    private String timezone;
    private String theme;
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private Boolean weeklyReports;
}