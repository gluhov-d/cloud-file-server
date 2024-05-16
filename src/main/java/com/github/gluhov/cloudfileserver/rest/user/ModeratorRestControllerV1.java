package com.github.gluhov.cloudfileserver.rest.user;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.user.ModeratorRestControllerV1.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ModeratorRestControllerV1 extends AbstractUserRestControllerV1 {
    public static final String REST_URL = "/api/v1/moderator/users";

    @GetMapping(value = "/{id}")
    @Override
    public Mono<?> get(@PathVariable long id) {
        return super.get(id);
    }

    @GetMapping
    public Mono<?> getAll() {
        return userService.getAll()
                .map(userMapper::map)
                .collectList()
                .flatMap(userDtos -> Mono.just(ResponseEntity.ok().body(userDtos)));
    }

    @GetMapping("/active")
    public Mono<?> getAllActive() {
        return userService.findAllActive()
                .map(userMapper::map)
                .collectList()
                .flatMap(userDtos -> Mono.just(ResponseEntity.ok().body(userDtos)));
    }
}