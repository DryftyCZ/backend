package com.kaiwaru.ticketing.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/visitor")
    @PreAuthorize("hasRole('VISITOR') or hasRole('WORKER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public String visitorAccess() {
        return "Visitor Content.";
    }

    @GetMapping("/worker")
    @PreAuthorize("hasRole('WORKER') or hasRole('ORGANIZER') or hasRole('ADMIN')")
    public String workerAccess() {
        return "Worker Board.";
    }

    @GetMapping("/organizer")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public String organizerAccess() {
        return "Organizer Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
