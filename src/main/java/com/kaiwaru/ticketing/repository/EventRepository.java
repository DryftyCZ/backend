package com.kaiwaru.ticketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaiwaru.ticketing.model.Event;
import com.kaiwaru.ticketing.model.Auth.User;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByName(String name);
    List<Event> findByOrganizer(User organizer);
}
