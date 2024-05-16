package com.github.gluhov.cloudfileserver.service;

import com.github.gluhov.cloudfileserver.exception.EntityNotFoundException;
import com.github.gluhov.cloudfileserver.model.Event;
import com.github.gluhov.cloudfileserver.model.Status;
import com.github.gluhov.cloudfileserver.repository.EventRepository;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Flux<Event> getAllByUserId(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found", "CFS_USER_NOT_FOUND")))
                .flatMapMany(u -> eventRepository.getAllActiveByUserId(u.getId()));
    }

    public Mono<Event> getById(Long id) {
        return eventRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Event not found", "CFS_EVENT_NOT_FOUND")));
    }

    public Mono<Void> delete(Long id, Long modifiedById) {
        return eventRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Event not found", "CFS_EVENT_NOT_FOUND")))
                .flatMap(event -> {
                    event.setStatus(Status.DELETED);
                    event.setModifiedBy(String.valueOf(modifiedById));
                    event.setUpdatedAt(LocalDateTime.now());
                    return eventRepository.save(event).then();
                });
    }

    public Mono<Event> update(Event event, Long modifiedById) {
        return eventRepository.findById(event.getId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Event not found", "CFS_EVENT_NOT_FOUND")))
                .flatMap(e -> {
                    e.setUpdatedAt(LocalDateTime.now());
                    e.setModifiedBy(String.valueOf(modifiedById));
                    e.setStatus(event.getStatus());
                    return eventRepository.save(e);
                })
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
    }
}