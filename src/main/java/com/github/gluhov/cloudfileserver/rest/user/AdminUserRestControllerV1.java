package com.github.gluhov.cloudfileserver.rest.user;

import com.github.gluhov.cloudfileserver.dto.UserDto;
import com.github.gluhov.cloudfileserver.mapper.UserMapper;
import com.github.gluhov.cloudfileserver.security.CustomPrincipal;
import com.github.gluhov.cloudfileserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.user.AdminUserRestControllerV1.REST_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUserRestControllerV1 extends AbstractUserRestControllerV1 {

    private final UserService userService;
    static final String REST_URL = "/api/v1/admin/users";

    private final UserMapper userMapper;

    @GetMapping(value = "/{id}")
    @Override
    public Mono<?> get(@PathVariable long id) {
        return super.get(id);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public Mono<?> delete(@PathVariable long id, Authentication authentication) {
        return super.delete(id, authentication);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<?> update(@RequestBody UserDto userDto, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.update(userMapper.map(userDto), customPrincipal.getId()).map(user -> ResponseEntity.ok().body(userMapper.map(user)));
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