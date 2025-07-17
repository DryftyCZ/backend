package com.kaiwaru.ticketing.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InviteResponse {
    private String message;
    private String inviteUrl;
    private String token;
}