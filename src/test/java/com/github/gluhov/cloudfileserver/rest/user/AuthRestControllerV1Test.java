package com.github.gluhov.cloudfileserver.rest.user;

import com.github.gluhov.cloudfileserver.config.WebSecurityConfig;
import com.github.gluhov.cloudfileserver.dto.AuthRequestDto;
import com.github.gluhov.cloudfileserver.dto.UserDto;
import com.github.gluhov.cloudfileserver.mapper.UserMapper;
import com.github.gluhov.cloudfileserver.model.User;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.moderator;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.user;
import static org.mockito.ArgumentMatchers.any;

@ComponentScan({"com.github.gluhov.cloudfileserver.errorhandling"})
@ActiveProfiles("test")
@WebFluxTest(controllers = AuthRestControllerV1.class)
@Import({WebSecurityConfig.class, SecurityService.class, UserService.class, AuthenticationManager.class})
@TestPropertySource("classpath:application-test.yaml")
class AuthRestControllerV1Test {

    private static final String REST_URL = AuthRestControllerV1.REST_URL;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void register() {
        BDDMockito.given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Mono.empty());

        BDDMockito.given(userRepository.save(any()))
                .willReturn(Mono.just(user));

        BDDMockito.given(userMapper.map((UserDto) any()))
                .willReturn(user);

        BDDMockito.given(userMapper.map((User) any()))
                        .willReturn(new UserDto(user));

        BDDMockito.given(passwordEncoder.matches(any(), any()))
                .willReturn(true);

        BDDMockito.given(passwordEncoder.encode(any()))
                .willReturn("12354");

        WebTestClient.ResponseSpec res = webTestClient.post()
                .uri(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"user_one\", \"password\":\"12354\",\"first_name\":\"User\",\"last_name\":\"One\"}")
                .exchange();

        res.expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.id").isEqualTo(user.getId())
                .jsonPath("$.body.username").isEqualTo(user.getUsername())
                .jsonPath("$.body.first_name").isEqualTo(user.getFirstName())
                .jsonPath("$.body.last_name").isEqualTo(user.getLastName());
    }

    @Test
    void login() {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setPassword("12354");
        dto.setUsername("moderator_user");

        BDDMockito.given(userRepository.findByUsername(any()))
                        .willReturn(Mono.just(moderator));
        BDDMockito.given(passwordEncoder.matches(any(), any()))
                .willReturn(true);

        webTestClient.post()
                .uri(REST_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk();
    }
}