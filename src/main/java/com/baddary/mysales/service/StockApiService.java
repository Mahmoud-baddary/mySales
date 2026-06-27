package com.baddary.mysales.service;

import com.baddary.mysales.dto.StockDTO;
import com.baddary.mysales.exception.UnauthorizedException;
import com.baddary.mysales.util.TokenStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.concurrent.Task;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StockApiService {
    private static final String BASE_URL = "http://localhost:8080/stocks";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public StockApiService() {
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

    // GET /stocks/{productId}
    public Task<List<StockDTO>> findStocksAsync(long productId) {
        return new Task<>() {
            @Override
            protected List<StockDTO> call() throws Exception {
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/" + productId))
                        .GET())
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(),
                            new com.fasterxml.jackson.core.type.TypeReference<List<StockDTO>>() {
                            });
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Failed to fetch stocks: " + response.statusCode()
                        + response.body());
            }
        };
    }
    // GET /stocks/{productId}
    public Task<List<StockDTO>> findStocksAsync(String productName) {
        return new Task<>() {
            @Override
            protected List<StockDTO> call() throws Exception {
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/by-product-name?productName=" + productName))
                        .GET())
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(),
                            new com.fasterxml.jackson.core.type.TypeReference<List<StockDTO>>() {
                            });
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Failed to fetch stocks: " + response.statusCode()
                        + response.body());
            }
        };
    }

    // GET /stocks/by-product-expire?productId=...&expire=...
    public Task<Optional<StockDTO>> findStockByProductAndExpireAsync(long productId, LocalDate expire) {
        return new Task<>() {
            @Override
            protected Optional<StockDTO> call() throws Exception {
                String url = BASE_URL + "/by-product-expire?productId=" + productId + "&expire=" + expire.toString();
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET())
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return Optional.of(objectMapper.readValue(response.body(), StockDTO.class));
                } else if (response.statusCode() == 404) {
                    return Optional.empty();
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Failed to fetch stock: " + response.statusCode() + response.body());
            }
        };
    }
}