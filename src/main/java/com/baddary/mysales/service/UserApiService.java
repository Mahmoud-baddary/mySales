package com.baddary.mysales.service;

import com.baddary.mysales.dto.UserDTO;
import com.baddary.mysales.exception.UnauthorizedException;
import com.baddary.mysales.util.TokenStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class UserApiService {
    private static final String BASE_URL = "http://localhost:8080/users";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    private HttpRequest.Builder addAuthHeader(HttpRequest.Builder builder) {
        String token = TokenStore.getToken();
        if (token != null && !token.isEmpty()) {
            return builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    // Register new user
    public Task<UserDTO> registerAsync(UserDTO user) {
        return new Task<>() {
            @Override
            protected UserDTO call() throws Exception {
                String json = objectMapper.writeValueAsString(user);
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 201) {
                    return objectMapper.readValue(response.body(), UserDTO.class);
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Registration failed: HTTP " + response.statusCode() + response.body());
            }
        };
    }

    public Task<UserDTO> registerFirstAsync(UserDTO user) {
        return new Task<>() {
            @Override
            protected UserDTO call() throws Exception {
                String json = objectMapper.writeValueAsString(user);
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL + "/first"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 201) {
                    return objectMapper.readValue(response.body(), UserDTO.class);
                }
                throw new RuntimeException("Registration failed: HTTP " + response.statusCode() + response.body());
            }
        };
    }

    // Find user by name (case-insensitive)
    public Task<Optional<UserDTO>> findByNameAsync(String name) {
        return new Task<>() {
            @Override
            protected Optional<UserDTO> call() throws Exception {
                String url = BASE_URL + "/search?name=" + java.net.URLEncoder.encode(name, "UTF-8");
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(url)).GET().build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return Optional.of(objectMapper.readValue(response.body(), UserDTO.class));
                } else if (response.statusCode() == 404) {
                    return Optional.empty();
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Find user failed: HTTP " + response.statusCode() + response.body());
            }
        };
    }

    public Task<Long> countAsync() {
        return new Task<>() {
            @Override
            protected Long call() throws Exception {
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL + "/count"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), Long.class);
                }
                return 0L;
            }
        };
    }
}