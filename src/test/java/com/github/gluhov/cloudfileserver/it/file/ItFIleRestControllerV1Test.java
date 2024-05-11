package com.github.gluhov.cloudfileserver.it.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.cloudfileserver.config.MySqlTestContainerConfig;
import com.github.gluhov.cloudfileserver.dto.AuthRequestDto;
import com.github.gluhov.cloudfileserver.it.AbstractRestControllerTest;
import com.github.gluhov.cloudfileserver.rest.AuthRestControllerV1;
import com.github.gluhov.cloudfileserver.rest.FileRestControllerV1;
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

import static com.github.gluhov.cloudfileserver.rest.file.FileEntityTestData.fileUser;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.user;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({MySqlTestContainerConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
public class ItFIleRestControllerV1Test extends AbstractRestControllerTest {

    private static final String REST_URL = FileRestControllerV1.REST_URL;
    private static final String LOGIN_URL = AuthRestControllerV1.REST_URL;

    @Autowired
    private WebTestClient webTestClient;

    private String token;

    @BeforeEach
    public void setUp() {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setPassword("12354");
        dto.setUsername(user.getUsername());

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
            token = tokenNode.asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parsing token exception");
        }
    }

    @Test
    @DisplayName("Test file entity get info functionality")
    public void givenFileId_whenGetFileEntity_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/" + fileUser.getId())
                .headers(headers -> headers.setBearerAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.id").isEqualTo(fileUser.getId())
                .jsonPath("$.body.created_by").isEqualTo(fileUser.getCreatedBy())
                .jsonPath("$.body.modified_by").isEqualTo(fileUser.getModifiedBy())
                .jsonPath("$.body.location").isEqualTo(fileUser.getLocation())
                .jsonPath("$.body.name").isEqualTo(fileUser.getName())
                .jsonPath("$.body.user_id").isEqualTo(fileUser.getUserId());
    }
}
