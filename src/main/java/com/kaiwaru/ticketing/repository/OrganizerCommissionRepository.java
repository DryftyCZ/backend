package com.kaiwaru.ticketing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kaiwaru.ticketing.model.OrganizerCommission;
import com.kaiwaru.ticketing.model.Auth.User;

public interface OrganizerCommissionRepository extends JpaRepository<OrganizerCommission, Long> {
    
    Optional<OrganizerCommission> findByOrganizer(User organizer);
    
    Optional<OrganizerCommission> findByOrganizerId(Long organizerId);
    
    @Query("SELECT oc FROM OrganizerCommission oc WHERE oc.organizer.id IN " +
           "(SELECT u.id FROM User u JOIN u.roles r WHERE r.name = 'ORGANIZER')")
    List<OrganizerCommission> findAllOrganizerCommissions();
    
    @Query("SELECT CASE WHEN COUNT(oc) > 0 THEN true ELSE false END " +
           "FROM OrganizerCommission oc WHERE oc.organizer.id = :organizerId")
    boolean existsByOrganizerId(@Param("organizerId") Long organizerId);
}