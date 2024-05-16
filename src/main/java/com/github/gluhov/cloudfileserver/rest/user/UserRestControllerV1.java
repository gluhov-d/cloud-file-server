package com.github.gluhov.cloudfileserver.rest.user;

import com.github.gluhov.cloudfileserver.security.CustomPrincipal;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.user.UserRestControllerV1.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestControllerV1 extends AbstractUserRestControllerV1 {
    public static final String REST_URL = "/api/v1/profile";

    @GetMapping
    public Mono<?> get(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return super.get(customPrincipal.getId());
    }
}