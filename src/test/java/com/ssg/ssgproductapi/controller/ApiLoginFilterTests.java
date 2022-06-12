package com.ssg.ssgproductapi.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ApiLoginFilterTests {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("토큰없이 api 접근할 때")
    @Test
    void Should_ThrowException_WithNoJwt() {
        webTestClient.post().uri("/api/signin")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().valueEquals("Content-Type", "application/json;charset=utf-8");
    }

    @DisplayName("비밀번호 틀렸을때, [Post]/signin")
    @Test
    void Should_ThrowUnauthorized_WhenInvalidPassword() {
        String postRequestBody = new JSONObject()
                .put("email", "team1@kakao.com")
                .put("password", "wrongPassword")
                .toString();

        webTestClient.post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(postRequestBody)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().valueEquals("Content-Type", "application/json;charset=utf-8");
    }
}

