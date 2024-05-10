package com.github.gluhov.cloudfileserver.rest.user;

import com.github.gluhov.cloudfileserver.config.WebSecurityConfig;
import com.github.gluhov.cloudfileserver.dto.AuthRequestDto;
import com.github.gluhov.cloudfileserver.dto.UserDto;
import com.github.gluhov.cloudfileserver.mapper.UserMapper;
import com.github.gluhov.cloudfileserver.rest.AuthRestControllerV1;
import com.github.gluhov.cloudfileserver.security.AuthenticationManager;
import com.github.gluhov.cloudfileserver.security.SecurityService;
import com.github.gluhov.cloudfileserver.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.moderator;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.user;
import static org.mockito.ArgumentMatchers.any;

@ComponentScan({"com.github.gluhov.cloudfileserver.errorhandling"})
@ActiveProfiles("test")
@WebFluxTest(controllers = {AuthRestControllerV1.class})
@Import(WebSecurityConfig.class)
@TestPropertySource("classpath:application-test.yaml")
class AuthRestControllerV1Test {

    private static final String REST_URL = AuthRestControllerV1.REST_URL;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private UserMapper userMapper;

    @Test
    void register() {
    }

    @Test
    void login() {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setPassword("12354");
        dto.setUsername("moderator_user");
        BDDMockito.given(userService.getUserByUserName(any()))
                        .willReturn(Mono.just(moderator));
        webTestClient.post()
                .uri(REST_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .isEqualTo(new UserDto(user));
    }
}