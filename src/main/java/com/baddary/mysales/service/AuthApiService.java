package com.baddary.mysales.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.baddary.mysales.dto.LoginRequestDTO;
import com.baddary.mysales.dto.LoginResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.concurrent.Task;

public class AuthApiService {
    private static final String BASE_URL = "http://localhost:8080/auth";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // Login
    public Task<LoginResponseDTO> loginAsync(String username, String password) {
        return new Task<>() {
            @Override
            protected LoginResponseDTO call() throws Exception {
                LoginRequestDTO request = new LoginRequestDTO(username, password);
                String json = objectMapper.writeValueAsString(request);
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), LoginResponseDTO.class);
                } else {
                    throw new RuntimeException("Login failed: HTTP " + response.statusCode() + response.body());
                }
            }
        };
    }
}
