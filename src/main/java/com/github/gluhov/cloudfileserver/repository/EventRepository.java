package com.github.gluhov.cloudfileserver.repository;

import com.github.gluhov.cloudfileserver.model.Event;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface EventRepository extends R2dbcRepository<Event, Long> {
    Flux<Event> getAllByUserId(Long id);
}