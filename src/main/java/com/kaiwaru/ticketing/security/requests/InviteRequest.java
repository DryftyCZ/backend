package com.kaiwaru.ticketing.security.requests;

import com.kaiwaru.ticketing.model.Auth.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InviteRequest {
    @NotBlank
    @Email
    private String email;

    @NotNull
    private Role.RoleName targetRole;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role.RoleName getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(Role.RoleName targetRole) {
        this.targetRole = targetRole;
    }
}