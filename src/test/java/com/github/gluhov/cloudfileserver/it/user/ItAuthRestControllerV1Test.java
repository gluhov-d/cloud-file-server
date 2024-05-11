package com.github.gluhov.cloudfileserver.it.user;

import com.github.gluhov.cloudfileserver.config.MySqlTestContainerConfig;
import com.github.gluhov.cloudfileserver.dto.AuthRequestDto;
import com.github.gluhov.cloudfileserver.dto.UserDto;
import com.github.gluhov.cloudfileserver.it.AbstractRestControllerTest;
import com.github.gluhov.cloudfileserver.rest.AuthRestControllerV1;
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

import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.user;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({MySqlTestContainerConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
public class ItAuthRestControllerV1Test extends AbstractRestControllerTest {

    private static final String REST_URL = AuthRestControllerV1.REST_URL;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Test create user functionality")
    public void givenUserDto_whenCreateUser_thenSuccessResponse() {
        UserDto userDto = new UserDto(user);
        userDto.setPassword("12354");

        WebTestClient.ResponseSpec resp = webTestClient.post()
                .uri(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"user_two\", \"password\":\"12354\",\"first_name\":\"User\",\"last_name\":\"Two\"}")
                .exchange();

        resp.expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.id").isEqualTo(4L)
                .jsonPath("$.body.username").isEqualTo("user_two")
                .jsonPath("$.body.first_name").isEqualTo("User")
                .jsonPath("$.body.last_name").isEqualTo("Two");
    }

    @Test
    @DisplayName("Test user authentication functionality")
    public void givenAuthRequestDto_whenLogin_thenSuccessResponse() {
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername(user.getUsername());
        authRequestDto.setPassword(user.getPassword());

        WebTestClient.ResponseSpec resp = webTestClient.post()
                .uri(REST_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authRequestDto)
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.user_id").isEqualTo(user.getId())
                .jsonPath("$.body.token").isNotEmpty()
                .jsonPath("$.body.expires_at").isNotEmpty()
                .jsonPath("$.body.issued_at").isNotEmpty();
    }
}
