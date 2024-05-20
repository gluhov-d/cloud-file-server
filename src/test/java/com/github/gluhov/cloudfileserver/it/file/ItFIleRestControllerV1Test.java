package com.github.gluhov.cloudfileserver.it.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.cloudfileserver.config.MySqlTestContainerConfig;
import com.github.gluhov.cloudfileserver.dto.AuthRequestDto;
import com.github.gluhov.cloudfileserver.dto.FileEntityDto;
import com.github.gluhov.cloudfileserver.it.AbstractRestControllerTest;
import com.github.gluhov.cloudfileserver.model.Status;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.file.FileEntityTestData.FILE_NOT_FOUND_ID;
import static com.github.gluhov.cloudfileserver.rest.file.FileEntityTestData.fileUser;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({MySqlTestContainerConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
public class ItFIleRestControllerV1Test extends AbstractRestControllerTest {

    private static final String REST_URL = FileRestControllerV1.REST_URL;
    private static final String ADMIN_URL = FileRestControllerV1.ADMIN_REST_URL;
    private static final String MODERATOR_URL = FileRestControllerV1.MODERATOR_REST_URL;
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
    @DisplayName("Test file entity get info functionality")
    public void givenFileId_whenGetFile_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/" + fileUser.getId())
                .headers(headers -> headers.setBearerAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.id").isEqualTo(fileUser.getId())
                .jsonPath("$.body.created_by").isEqualTo(fileUser.getCreatedBy())
                .jsonPath("$.body.location").isEqualTo(fileUser.getLocation())
                .jsonPath("$.body.name").isEqualTo(fileUser.getName())
                .jsonPath("$.body.user_id").isEqualTo(fileUser.getUserId());
    }

    @Test
    @DisplayName("Test file entity get not found id info functionality")
    public void givenFileId_whenGetFile_thenNotFoundResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/" + FILE_NOT_FOUND_ID)
                .headers(headers -> headers.setBearerAuth(token))
                .exchange();

        resp.expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    @DisplayName("Test get all file entity functionality")
    public void givenPrincipals_whenGetAllFiles_thenSuccessResponse() {
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
    @DisplayName("Test get all file entity for user functionality")
    public void givenUserId_whenGetAllFilesByAdmin_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(ADMIN_URL + "/" + user.getId() + "/all")
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.*").isArray();
    }

    @Test
    @DisplayName("Test get all file entity for user by moderator functionality")
    public void givenUserId_whenGetAllFilesByModerator_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(MODERATOR_URL + "/" + admin.getId() + "/all")
                .headers(headers -> headers.setBearerAuth(moderatorToken))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.*").isArray();
    }

    @Test
    @DisplayName("Test delete file by moderator entity functionality")
    public void givenFileId_whenDeleteFileByModerator_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.delete()
                .uri(MODERATOR_URL + "/" + fileUser.getId())
                .headers(headers -> headers.setBearerAuth(moderatorToken))
                .exchange();

        resp.expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Test delete file by admin entity functionality")
    public void givenFileId_whenDeleteFileByAdmin_ThenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.delete()
                .uri(ADMIN_URL + "/" + user.getId())
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange();

        resp.expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Test update file entity by moderator functionality")
    public void givenFileId_whenUpdateFileByModerator_thenSuccessResponse() {
        FileEntityDto fileEntityDto = new FileEntityDto();
        fileEntityDto.setStatus(Status.DELETED);
        WebTestClient.ResponseSpec resp = webTestClient.put()
                .uri(MODERATOR_URL + "/" + fileUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(moderatorToken))
                .bodyValue(fileEntityDto)
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.body.status").isEqualTo("DELETED")
                .jsonPath("$.body.modified_by").isEqualTo(moderator.getId());
    }

    @Test
    @DisplayName("Test update file entity by admin functionality")
    public void givenFileId_whenUpdateFileByAdmin_thenSuccessResponse() {
        FileEntityDto fileEntityDto = new FileEntityDto();
        fileEntityDto.setStatus(Status.DELETED);
        WebTestClient.ResponseSpec resp = webTestClient.put()
                .uri(ADMIN_URL + "/" + fileUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(adminToken))
                .bodyValue(fileEntityDto)
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.body.status").isEqualTo("DELETED")
                .jsonPath("$.body.modified_by").isEqualTo(admin.getId());
    }


    @Test
    @DisplayName("Test uploading file functionality")
    public void givenFile_whenUploadFile_thenSuccessResponse() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("PAN.bmp"))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        long startTime = System.currentTimeMillis();

        WebTestClient.ResponseSpec resp = webTestClient.post()
                .uri(REST_URL + "/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(headers -> headers.setBearerAuth(token))
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::print)
                .jsonPath("$.body.location").isNotEmpty()
                .jsonPath("$.body.userId").isEqualTo(user.getId());

        assertTrue(duration < 5000, "The request should complete within 5 seconds");
    }
}
