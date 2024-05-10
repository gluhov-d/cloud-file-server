package com.github.gluhov.cloudfileserver.rest.user;

import com.github.gluhov.cloudfileserver.mapper.UserMapper;
import com.github.gluhov.cloudfileserver.security.CustomPrincipal;
import com.github.gluhov.cloudfileserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractUserRestControllerV1 {
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserMapper userMapper;

    public Mono<?> get(long id) {
        log.info("get user with id {}", id);
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok().body(userMapper.map(user)));
    }

    public Mono<?> delete(long id, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        log.info("delete user with id {}", id);
        return userService.deleteById(id, customPrincipal.getId())
                .map(resp -> ResponseEntity.noContent().build());
    }
}