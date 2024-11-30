package com.team25.event.planner.event.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.team25.event.planner.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query("SELECT e FROM Event e WHERE " +
            "(:country IS NULL OR :country = '' OR e.location.country = :country) AND " +
            "(:city IS NULL OR :city = '' OR e.location.city = :city) " +
            "ORDER BY e.createdDate DESC")
    Page<Event> findTopEventsByCountryAndCity(@Param("country") String country,
                                              @Param("city") String city,
                                              Pageable pageable);
}