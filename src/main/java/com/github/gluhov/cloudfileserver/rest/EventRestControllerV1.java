package com.github.gluhov.cloudfileserver.rest;

import com.github.gluhov.cloudfileserver.dto.EventDto;
import com.github.gluhov.cloudfileserver.mapper.EventMapper;
import com.github.gluhov.cloudfileserver.security.CustomPrincipal;
import com.github.gluhov.cloudfileserver.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EventRestControllerV1 {
    public static final String REST_URL = "/api/v1/events";
    public static final String MODERATOR_REST_URL = "/api/v1/moderator/events";
    public static final String ADMIN_REST_URL = "/api/v1/admin/events";

    private final EventService eventService;

    private final EventMapper eventMapper;

    @GetMapping(value = REST_URL)
    public Mono<?> getAll(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return eventService.getAllByUserId(customPrincipal.getId())
                .map(eventMapper::map)
                .collectList()
                .flatMap(eventDtos -> Mono.just(ResponseEntity.ok().body(eventDtos)));
    }

    @GetMapping(value = REST_URL + "/{id}")
    public Mono<?> getById(@PathVariable long id) { return eventService.getById(id).map(event -> ResponseEntity.ok().body(eventMapper.map(event)));}

    @GetMapping(value = {ADMIN_REST_URL + "/{id}/all", MODERATOR_REST_URL + "/{id}/all"})
    public Mono<?> getAllById(@PathVariable long id) {
        return eventService.getAllByUserId(id)
                .map(eventMapper::map)
                .collectList()
                .flatMap(eventDtos ->Mono.just(ResponseEntity.ok().body(eventDtos)));


    }

    @DeleteMapping(value = { MODERATOR_REST_URL + "/{id}", ADMIN_REST_URL + "/{id}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<?> delete(@PathVariable long id, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return eventService.delete(id, customPrincipal.getId());
    }

    @PutMapping(value = MODERATOR_REST_URL + "/{id}")
    public Mono<?> update(@RequestBody EventDto eventDto, @PathVariable Long id, Authentication authentication) {
        eventDto.setId(id);
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return eventService.update(eventMapper.map(eventDto), customPrincipal.getId())
                .map(event -> ResponseEntity.ok().body(eventMapper.map(event)));
    }
}