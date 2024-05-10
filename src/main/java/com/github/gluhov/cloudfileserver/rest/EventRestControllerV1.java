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

    private final EventMapper eventMapper;

    // TO-DO convert to ResponseEntity
    @GetMapping(value = REST_URL)
    public Flux<?> getAll(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return eventService.getAllByUserId(customPrincipal.getId())
                .flatMap(event -> Mono.just(eventMapper.map(event)));
    }

    @GetMapping(value = REST_URL + "/{id}")
    public Mono<?> getById(@PathVariable long id) { return eventService.getById(id).map(event -> ResponseEntity.ok().body(eventMapper.map(event)));}

    @GetMapping(value = {ADMIN_REST_URL, MODERATOR_REST_URL})
    public Flux<?> getAllById(@RequestParam(value = "id", defaultValue = "0") Long id) {
        if (id != 0) {
            return eventService.getAllByUserId(id)
                    .flatMap(event -> Mono.just(eventMapper.map(event)));
        } else {
            return Flux.error(new RuntimeException("CFS_BAD_ID"));
        }

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