package com.baddary.mysales.service;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.baddary.mysales.exception.UnauthorizedException;
import com.baddary.mysales.util.TokenStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.concurrent.Task;

public class SafeApiService {
    private static final String BASE_URL = "http://localhost:8080/safe";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SafeApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private HttpRequest.Builder addAuthHeader(HttpRequest.Builder builder) {
        String token = TokenStore.getToken();
        if (token != null && !token.isEmpty()) {
            return builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    public Task<BigDecimal> totalAmountAsync() {
        return new Task<BigDecimal>() {

            @Override
            protected BigDecimal call() throws Exception {
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL + "/total"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                    return objectMapper.readValue(response.body(), BigDecimal.class);
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Data request failed: " + response.statusCode() + response.body());

            }

        };
    }
}
