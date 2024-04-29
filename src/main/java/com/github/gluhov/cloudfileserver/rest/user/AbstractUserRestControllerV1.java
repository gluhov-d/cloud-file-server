package com.github.gluhov.cloudfileserver.rest.user;

import com.github.gluhov.cloudfileserver.dto.UserDto;
import com.github.gluhov.cloudfileserver.model.Status;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import com.github.gluhov.cloudfileserver.security.CustomPrincipal;
import com.github.gluhov.cloudfileserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
public abstract class AbstractUserRestControllerV1 {
    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    public Mono<ResponseEntity<UserDto>> get(long id) {
        log.info("get user with id {}", id);
        return userService.getUserById(id)
                .map(userDto -> ResponseEntity.ok().body(userDto));
    }

    public Mono<ResponseEntity<Void>> delete(long id, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        log.info("delete user with id {}", id);
        return userRepository.findById(id)
                .flatMap(user -> {
                    user.setEnabled(false);
                    user.setStatus(Status.DELETED);
                    user.setUpdatedAt(LocalDateTime.now());
                    user.setModifiedBy(String.valueOf(customPrincipal.getId()));
                    return userRepository.save(user).then();
                })
                .map(resp -> ResponseEntity.noContent().build());
    }
}