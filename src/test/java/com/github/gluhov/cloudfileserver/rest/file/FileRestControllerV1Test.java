package com.github.gluhov.cloudfileserver.rest.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.cloudfileserver.dto.FileEntityDto;
import com.github.gluhov.cloudfileserver.mapper.FileEntityMapper;
import com.github.gluhov.cloudfileserver.mapper.UserMapper;
import com.github.gluhov.cloudfileserver.rest.AuthRestControllerV1;
import com.github.gluhov.cloudfileserver.rest.FileRestControllerV1;
import com.github.gluhov.cloudfileserver.security.SecurityService;
import com.github.gluhov.cloudfileserver.service.FileEntityService;
import com.github.gluhov.cloudfileserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.gluhov.cloudfileserver.rest.file.FileEntityTestData.fileUser;

@ComponentScan({"com.github.gluhov.cloudfileserver.errorhandling"})
@ActiveProfiles("test")
@WebFluxTest(controllers = {FileRestControllerV1.class, AuthRestControllerV1.class})
@TestPropertySource("classpath:application-test.yaml")
class FileRestControllerV1Test {

    private static final String REST_URL = FileRestControllerV1.REST_URL;

    private static final String LOGIN_URL = "/api/v1/auth/login";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private FileEntityMapper fileEntityMapper;

    @MockBean
    private FileEntityService fileEntityService;

    private String token;

    @BeforeEach
    public void setUp() {
        if ( token == null) {
            String resp = webTestClient.post()
                    .uri(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{ \"username\": \"moderator_user\", \"password\": \"12354\" }")
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
    }

    @Test
    public void get() {
        FileEntityDto fileEntityDto = new FileEntityDto(fileUser);

        webTestClient.get()
                .uri(REST_URL + "/1")
                .headers(headers -> headers.setBearerAuth(token))
                .exchange()
                .expectStatus().isOk()
                .expectBody(FileEntityDto.class)
                .isEqualTo(fileEntityDto);
    }
}