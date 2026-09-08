package com.baddary.mysales.service;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.baddary.mysales.dto.CashDTO;
import com.baddary.mysales.dto.CustomerDTO;
import com.baddary.mysales.exception.UnauthorizedException;
import com.baddary.mysales.util.TokenStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.concurrent.Task;

public class CashApiService {
    private static final String BASE_URL = "http://localhost:8080/cash";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CashApiService() {
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

    public Task<Void> depositCashToSafeAsync(CashDTO cashDTO) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String json = objectMapper.writeValueAsString(cashDTO);
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL + "/deposit"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                    return null;
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Deposit failed: " + response.statusCode() + response.body());

            }
        };
    }

    public Task<Void> receiveCashFromSafeAsync(CashDTO cashDTO) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String json = objectMapper.writeValueAsString(cashDTO);
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL + "/receive"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                    return null;
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Receiving failed: " + response.statusCode() + response.body());

            }
        };
    }

    public Task<BigDecimal> currentAmountAsync(Long userId) {
        return new Task<>() {
            @Override
            protected BigDecimal call() throws Exception {
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL + "/" + userId))
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
