package com.github.gluhov.cloudfileserver.rest.user;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.user.ModeratorRestControllerV1.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ModeratorRestControllerV1 extends AbstractUserRestControllerV1 {
    static final String REST_URL = "/api/v1/moderator/users";

    @GetMapping(value = "/{id}")
    @Override
    public Mono<?> get(@PathVariable long id) {
        return super.get(id);
    }

    @GetMapping
    public Flux<?> getAll() {
        return userService.getAll()
                .flatMap(user -> Mono.just(userMapper.map(user)));
    }

    @GetMapping("/active")
    public Flux<?> getAllActive() {
        return userService.findAllActive()
                .flatMap(user -> Mono.just(userMapper.map(user)));
    }
}