package com.github.gluhov.cloudfileserver.rest.user;

import com.github.gluhov.cloudfileserver.rest.AuthRestControllerV1;
import com.github.gluhov.cloudfileserver.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AuthRestControllerV1Test {

    private static final String REST_URL = AuthRestControllerV1.REST_URL;
    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private UserService userService;

    @Test
    void register() {
    }

    @Test
    void login() {
    }
}