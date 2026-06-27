package com.baddary.mysales.service;

import com.baddary.mysales.dto.ProductDTO;
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
import java.util.List;
import java.util.Optional;

public class ProductApiService {
    private static final String BASE_URL = "http://localhost:8080/products";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ProductApiService() {
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

    // GET /products
    public Task<List<ProductDTO>> findAllAsync() {
        return new Task<>() {
            @Override
            protected List<ProductDTO> call() throws Exception {
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .GET())
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), 
                        new com.fasterxml.jackson.core.type.TypeReference<List<ProductDTO>>() {});
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Failed to fetch products: " + response.statusCode() + response.body());
            }
        };
    }

    // GET /products/search?name=xxx
    public Task<List<ProductDTO>> searchByNameAsync(String name) {
        return new Task<>() {
            @Override
            protected List<ProductDTO> call() throws Exception {
                String url = BASE_URL + "/search?name=" + java.net.URLEncoder.encode(name, "UTF-8");
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET())
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), 
                        new com.fasterxml.jackson.core.type.TypeReference<List<ProductDTO>>() {});
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Search failed: " + response.statusCode() + response.body());
            }
        };
    }

    // GET /products/by-name?name=xxx
    public Task<Optional<ProductDTO>> findByNameAsync(String name) {
        return new Task<>() {
            @Override
            protected Optional<ProductDTO> call() throws Exception {
                String url = BASE_URL + "/by-name?name=" + java.net.URLEncoder.encode(name, "UTF-8");
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET())
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return Optional.of(objectMapper.readValue(response.body(), ProductDTO.class));
                } else if (response.statusCode() == 404) {
                    return Optional.empty();
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Find by name failed: " + response.statusCode() + response.body());
            }
        };
    }

    // GET /products/{id}
    public Task<Optional<ProductDTO>> findByIdAsync(long id) {
        return new Task<>() {
            @Override
            protected Optional<ProductDTO> call() throws Exception {
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/" + id))
                        .GET())
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return Optional.of(objectMapper.readValue(response.body(), ProductDTO.class));
                } else if (response.statusCode() == 404) {
                    return Optional.empty();
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Find by id failed: " + response.statusCode() + response.body());
            }
        };
    }

    // POST /products
    public Task<ProductDTO> addProductAsync(ProductDTO product) {
        return new Task<>() {
            @Override
            protected ProductDTO call() throws Exception {
                String json = objectMapper.writeValueAsString(product);
                System.out.println("Sending product JSON: " + json);
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json)))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 201) {
                    return objectMapper.readValue(response.body(), ProductDTO.class);
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Add product failed: " + response.statusCode() + " - " + response.body());
            }
        };
    }

    // PUT /products/{id}
    public Task<ProductDTO> updateProductAsync(long id, ProductDTO product) {
        return new Task<>() {
            @Override
            protected ProductDTO call() throws Exception {
                String json = objectMapper.writeValueAsString(product);
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/" + id))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json)))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), ProductDTO.class);
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Update product failed: " + response.statusCode() + " - " + response.body());
            }
        };
    }
}