package com.baddary.mysales.service;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.baddary.mysales.dto.OrderDTO;
import com.baddary.mysales.enums.OrderType;
import com.baddary.mysales.exception.UnauthorizedException;
import com.baddary.mysales.util.TokenStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.concurrent.Task;

public class OrderApiService {
    private static final String BASE_URL = "http://localhost:8080/orders";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OrderApiService() {
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

    public Task<OrderDTO> addOrderAsync(OrderDTO order) {
        return new Task<>() {
            @Override
            protected OrderDTO call() throws Exception {
                String json = objectMapper.writeValueAsString(order);
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Response status: " + response.statusCode());
                System.out.println("Response body: " + response.body());
                if (response.statusCode() == 201) {
                    return objectMapper.readValue(response.body(), OrderDTO.class);
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Add failed: " + response.statusCode() + response.body());
            }
        };
    }

    public Task<List<OrderDTO>> searchOrdersAsync(String customerName, String productName,
            String userName, LocalDate fromDate,
            LocalDate toDate, OrderType orderType) {
        return new Task<>() {
            @Override
            protected List<OrderDTO> call() throws Exception {
                // Build URL with optional parameters
                StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/search?");
                boolean first = true;

                if (customerName != null && !customerName.isBlank()) {
                    urlBuilder.append("customerName=").append(java.net.URLEncoder.encode(customerName, "UTF-8"));
                    first = false;
                }
                if (productName != null && !productName.isBlank()) {
                    if (!first)
                        urlBuilder.append("&");
                    urlBuilder.append("productName=").append(java.net.URLEncoder.encode(productName, "UTF-8"));
                    first = false;
                }
                if (userName != null && !userName.isBlank()) {
                    if (!first)
                        urlBuilder.append("&");
                    urlBuilder.append("userName=").append(java.net.URLEncoder.encode(userName, "UTF-8"));
                    first = false;
                }
                if (fromDate != null) {
                    if (!first)
                        urlBuilder.append("&");
                    urlBuilder.append("fromDate=").append(fromDate.toString());
                    first = false;
                }
                if (toDate != null) {
                    if (!first)
                        urlBuilder.append("&");
                    urlBuilder.append("toDate=").append(toDate.toString());
                    first = false;
                }
                if (orderType != null) {
                    if (!first)
                        urlBuilder.append("&");
                    urlBuilder.append("orderType=").append(orderType.name());
                }

                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(urlBuilder.toString()))
                        .GET())
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(),
                            new TypeReference<List<OrderDTO>>() {
                            });
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }

                throw new RuntimeException("Search failed: HTTP " + response.statusCode());

            }
        };
    }

    public Task<Optional<OrderDTO>> findByIdAsync(long id) {
        return new Task<>() {
            @Override
            protected Optional<OrderDTO> call() throws Exception {
                String url = BASE_URL + "/" + id;
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return Optional.of(objectMapper.readValue(response.body(), OrderDTO.class));
                }else if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND){
                    return Optional.empty();
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("HTTP " + response.statusCode() + response.body());
            }
        };
    }
}
