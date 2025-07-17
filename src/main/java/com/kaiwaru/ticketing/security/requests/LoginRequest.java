package com.kaiwaru.ticketing.security.requests;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String identifier; // username or email

    @NotBlank
    private String password;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}