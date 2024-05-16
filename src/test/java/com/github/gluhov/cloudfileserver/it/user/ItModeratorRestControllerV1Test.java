package com.github.gluhov.cloudfileserver.it.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.cloudfileserver.config.MySqlTestContainerConfig;
import com.github.gluhov.cloudfileserver.dto.AuthRequestDto;
import com.github.gluhov.cloudfileserver.rest.AuthRestControllerV1;
import com.github.gluhov.cloudfileserver.rest.user.ModeratorRestControllerV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.moderator;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.user;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({MySqlTestContainerConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
public class ItModeratorRestControllerV1Test {

    private final String REST_URL = ModeratorRestControllerV1.REST_URL;

    private static final String LOGIN_URL = AuthRestControllerV1.REST_URL;

    @Autowired
    private WebTestClient webTestClient;

    private String token;

    @BeforeEach
    public void setUp() {
        if (token == null) {
            token = getToken(moderator.getUsername(), moderator.getPassword());
        }
    }

    private String getToken(String username, String password) {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setPassword(password);
        dto.setUsername(username);

        String resp = webTestClient.post()
                .uri(LOGIN_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), AuthRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(resp);
            JsonNode tokenNode = rootNode.path("body").path("token");
            if (tokenNode.isMissingNode()) {
                throw new RuntimeException("Token not found in response");
            }
            return tokenNode.asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parsing token exception");
        }
    }

    @Test
    @DisplayName("Test get user profile by moderator functionality")
    void givenUserId_whenGetUserByModerator_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/" + user.getId())
                .headers(headers -> headers.setBearerAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.id").isEqualTo(user.getId())
                .jsonPath("$.body.username").isEqualTo(user.getUsername())
                .jsonPath("$.body.first_name").isEqualTo(user.getFirstName())
                .jsonPath("$.body.last_name").isEqualTo(user.getLastName());
    }

    @Test
    @DisplayName("Test get all users by moderator functionality")
    void givenModerator_whenGetAllUsersByModerator_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL)
                .headers(headers -> headers.setBearerAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.*").isArray();
    }

    @Test
    @DisplayName("Test get all active users by moderator functionality")
    void givenModerator_whenGetAllActiveUsersByModerator_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/active")
                .headers(headers -> headers.setBearerAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.*").isArray();
    }
}
