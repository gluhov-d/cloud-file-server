package com.github.gluhov.cloudfileserver.repository;

import com.github.gluhov.cloudfileserver.model.Event;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface EventRepository extends R2dbcRepository<Event, Long> {
    @Query("SELECT e FROM events e WHERE e.status = 'ACTIVE' AND e.user_id = ?1")
    Flux<Event> getAllByUserId(Long id);
}