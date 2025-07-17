package com.kaiwaru.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public class ValidateTicketRequest {
    @NotBlank
    private String qrCode;

    // getter/setter
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}