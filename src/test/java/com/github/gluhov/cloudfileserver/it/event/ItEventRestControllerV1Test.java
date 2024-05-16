package com.github.gluhov.cloudfileserver.it.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.cloudfileserver.config.MySqlTestContainerConfig;
import com.github.gluhov.cloudfileserver.dto.AuthRequestDto;
import com.github.gluhov.cloudfileserver.dto.EventDto;
import com.github.gluhov.cloudfileserver.it.AbstractRestControllerTest;
import com.github.gluhov.cloudfileserver.model.Status;
import com.github.gluhov.cloudfileserver.rest.AuthRestControllerV1;
import com.github.gluhov.cloudfileserver.rest.EventRestControllerV1;
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

import static com.github.gluhov.cloudfileserver.rest.event.EventTestData.EVENT_NOT_FOUND_ID;
import static com.github.gluhov.cloudfileserver.rest.event.EventTestData.eventUser;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({MySqlTestContainerConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
public class ItEventRestControllerV1Test extends AbstractRestControllerTest {

    private final String REST_URL = EventRestControllerV1.REST_URL;

    private final String MODERATOR_REST_URL = EventRestControllerV1.MODERATOR_REST_URL;

    private final String ADMIN_REST_URL = EventRestControllerV1.ADMIN_REST_URL;

    private static final String LOGIN_URL = AuthRestControllerV1.REST_URL;

    @Autowired
    private WebTestClient webTestClient;

    private String token;
    private String adminToken;
    private String moderatorToken;

    @BeforeEach
    public void setUp() {
        if (token == null) {
            token = getToken(user.getUsername(), user.getPassword());
        }
        if (moderatorToken == null) {
            moderatorToken = getToken(moderator.getUsername(), moderator.getPassword());
        }
        if (adminToken == null) {
            adminToken = getToken(admin.getUsername(), admin.getPassword());
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
    @DisplayName("Test get event by user functionality")
    void givenEventId_whenGetEvent_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/" + eventUser.getId())
                .headers(headers -> headers.setBearerAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::print)
                .jsonPath("$.body.id").isEqualTo(eventUser.getId())
                .jsonPath("$.body.user_id").isEqualTo(eventUser.getUserId())
                .jsonPath("$.body.file_id").isEqualTo(eventUser.getFileId());
    }

    @Test
    @DisplayName("Test event get not found id info functionality")
    public void givenEventId_whenGetEvent_thenNotFoundResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/" + EVENT_NOT_FOUND_ID)
                .headers(headers -> headers.setBearerAuth(token))
                .exchange();

        resp.expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    @DisplayName("Test get all events functionality")
    public void givenPrincipals_whenGetAllEvents_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL)
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.*").isArray();
    }

    @Test
    @DisplayName("Test get all events for user by adminfunctionality")
    public void givenUserId_whenGetAllFilesByAdmin_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(ADMIN_REST_URL + "/" + user.getId() + "/all")
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.*").isArray();
    }

    @Test
    @DisplayName("Test get all events for user by moderator functionality")
    public void givenUserId_whenGetAllFilesByModerator_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(MODERATOR_REST_URL + "/" + admin.getId() + "/all")
                .headers(headers -> headers.setBearerAuth(moderatorToken))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.*").isArray();
    }

    @Test
    @DisplayName("Test delete event by moderator functionality")
    public void givenEventId_whenDeleteEventByModerator_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.delete()
                .uri(MODERATOR_REST_URL + "/" + eventUser.getId())
                .headers(headers -> headers.setBearerAuth(moderatorToken))
                .exchange();

        resp.expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Test delete event by admin functionality")
    public void givenEventId_whenDeleteEventByAdmin_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.delete()
                .uri(ADMIN_REST_URL + "/" + eventUser.getId())
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange();

        resp.expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Test update event by moderator functionality")
    public void givenEventDto_whenUpdateEventByModerator_thenSuccessResponse() {
        EventDto eventDto = new EventDto();
        eventDto.setStatus(Status.DELETED);
        WebTestClient.ResponseSpec resp = webTestClient.put()
                .uri(MODERATOR_REST_URL + "/" + eventUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(moderatorToken))
                .bodyValue(eventDto)
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.body.status").isEqualTo("DELETED")
                .jsonPath("$.body.modified_by").isEqualTo(moderator.getId());
    }

}
