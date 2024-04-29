package com.github.gluhov.cloudfileserver.rest.user;

import com.github.gluhov.cloudfileserver.dto.UserDto;
import com.github.gluhov.cloudfileserver.model.User;
import com.github.gluhov.cloudfileserver.security.CustomPrincipal;
import com.github.gluhov.cloudfileserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.user.AdminUserRestControllerV1.REST_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUserRestControllerV1 extends AbstractUserRestControllerV1 {

    private final UserService userService;
    static final String REST_URL = "/api/v1/admin/users";

    @GetMapping(value = "/{id}")
    @Override
    public Mono<ResponseEntity<UserDto>> get(@PathVariable long id) {
        return super.get(id);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public Mono<ResponseEntity<Void>> delete(@PathVariable long id, Authentication authentication) {
        return super.delete(id, authentication);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<User>> update(@RequestBody UserDto userDto, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.update(userDto, customPrincipal.getId()).map(user -> ResponseEntity.ok().body(user));
    }

    @GetMapping
    public Flux<User> getAll() {
        return userService.getAll();
    }
}