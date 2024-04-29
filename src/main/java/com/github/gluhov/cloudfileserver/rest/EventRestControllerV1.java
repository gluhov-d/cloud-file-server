package com.github.gluhov.cloudfileserver.rest;

import com.github.gluhov.cloudfileserver.dto.EventDto;
import com.github.gluhov.cloudfileserver.model.Event;
import com.github.gluhov.cloudfileserver.security.CustomPrincipal;
import com.github.gluhov.cloudfileserver.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EventRestControllerV1 {
    static final String REST_URL = "/api/v1/events";
    static final String MODERATOR_REST_URL = "/api/v1/moderator/events";
    static final String ADMIN_REST_URL = "/api/v1/admin/events";

    private final EventService eventService;

    @GetMapping(value = REST_URL)
    public Flux<Event> getAll(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return eventService.getAllByUserId(customPrincipal.getId());
    }

    @GetMapping(value = REST_URL + "/{id}")
    public Mono<ResponseEntity<EventDto>> getById(@PathVariable long id) { return eventService.getById(id).map(eventDto -> ResponseEntity.ok().body(eventDto));}

    @GetMapping(value = ADMIN_REST_URL + "/{id}")
    public Flux<Event> getAllById(@PathVariable long id) {
        return eventService.getAllByUserId(id);
    }

    @GetMapping(value = MODERATOR_REST_URL + "/{id}")
    public Flux<Event> getAllByUserId(@PathVariable long id) {
        return eventService.getAllByUserId(id);
    }

    @DeleteMapping(value = MODERATOR_REST_URL + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable long id, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return eventService.delete(id, customPrincipal.getId());
    }

    @DeleteMapping(value = ADMIN_REST_URL + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable long id, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return eventService.delete(id, customPrincipal.getId());
    }

    @PutMapping(value = MODERATOR_REST_URL + "/{id}")
    public Mono<Event> update(@RequestBody EventDto eventDto, @PathVariable Long id, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return eventService.update(eventDto, customPrincipal.getId());
    }
}