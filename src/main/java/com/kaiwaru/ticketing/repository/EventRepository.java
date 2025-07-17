package com.kaiwaru.ticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaiwaru.ticketing.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByName(String name);
}
