package com.baddary.mysales.service;

import com.baddary.mysales.dto.CustomerDTO;
import com.baddary.mysales.exception.UnauthorizedException;
import com.baddary.mysales.util.TokenStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.concurrent.Task;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerApiService {
    private static final String BASE_URL = "http://localhost:8080/customers";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CustomerApiService() {
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

    // Fetch all customers
    public Task<List<CustomerDTO>> findAllAsync() {
        return new Task<>() {
            @Override
            protected List<CustomerDTO> call() throws Exception {
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(),
                            new com.fasterxml.jackson.core.type.TypeReference<List<CustomerDTO>>() {
                            });
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("HTTP " + response.statusCode() + response.body());
            }
        };
    }

    public Task<List<CustomerDTO>> searchByNameAndBalanceStatusAsync(String name, String balanceStatus) {
        return new Task<List<CustomerDTO>>() {

            @Override
            protected List<CustomerDTO> call() throws Exception {
                String url = BASE_URL + "/search-by-balance?name="
                        + java.net.URLEncoder.encode(name, "UTF-8") + "&balanceStatus=" + balanceStatus;
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(url))
                        .GET().build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(),
                            new com.fasterxml.jackson.core.type.TypeReference<List<CustomerDTO>>() {
                            });
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("HTTP " + response.statusCode() + response.body());
            }

        };
    }

    public Task<CustomerDTO> settleCustomerBalanceAsync(long customerId, double amount) {
    return new Task<>() {
        @Override
        protected CustomerDTO call() throws Exception {
            // Create a Map for the JSON body
            Map<String, BigDecimal> payload = new HashMap<>();
            payload.put("amount", BigDecimal.valueOf(amount));

            // Convert map to JSON string
            String jsonBody = objectMapper.writeValueAsString(payload);

            // Build the request: PATCH /customers/{id}/balance
            HttpRequest request = addAuthHeader(HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + customerId + "/settle"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), CustomerDTO.class);
            } else {
                throw new RuntimeException("Update balance failed: HTTP " + response.statusCode() + " - " + response.body());
            }
        }
    };
}

    public Task<List<CustomerDTO>> searchByName(String name) {
        return new Task<>() {
            @Override
            protected List<CustomerDTO> call() throws Exception {
                String url = BASE_URL + "/search?name=" + java.net.URLEncoder.encode(name, "UTF-8");
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(),
                            new com.fasterxml.jackson.core.type.TypeReference<List<CustomerDTO>>() {
                            });
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("HTTP " + response.statusCode() + response.body());
            }
        };
    }

    public Task<Optional<CustomerDTO>> findByExactNameAsync(String name) {
        return new Task<>() {
            @Override
            protected Optional<CustomerDTO> call() throws Exception {
                String url = BASE_URL + "/by-name?name=" + java.net.URLEncoder.encode(name, "UTF-8");
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return Optional.of(objectMapper.readValue(response.body(), CustomerDTO.class));
                } else if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    return Optional.empty();
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("HTTP " + response.statusCode() + response.body());
            }
        };
    }

    public Task<Optional<CustomerDTO>> findByIdAsync(long id) {
        return new Task<>() {
            @Override
            protected Optional<CustomerDTO> call() throws Exception {
                String url = BASE_URL + "/" + id;
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return Optional.of(objectMapper.readValue(response.body(), CustomerDTO.class));
                } else if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    return Optional.empty();
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("HTTP " + response.statusCode() + response.body());
            }
        };
    }

    public Task<Optional<CustomerDTO>> findByPhoneAsync(String phoneNum) {
        return new Task<>() {
            @Override
            protected Optional<CustomerDTO> call() throws Exception {
                String url = BASE_URL + "/by-phone?phoneNum=" + java.net.URLEncoder.encode(phoneNum, "UTF-8");
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Response status: " + response.statusCode());
                System.out.println("Response body: " + response.body());
                if (response.statusCode() == 200) {
                    return Optional.of(objectMapper.readValue(response.body(), CustomerDTO.class));
                } else if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    return Optional.empty();
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                return Optional.empty();
            }
        };
    }

    public Task<CustomerDTO> addCustomerAsync(CustomerDTO customer) {
        return new Task<>() {
            @Override
            protected CustomerDTO call() throws Exception {
                String json = objectMapper.writeValueAsString(customer);
                System.out.println("Sending JSON: " + json);
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Response status: " + response.statusCode());
                System.out.println("Response body: " + response.body());
                if (response.statusCode() == 201) {
                    return objectMapper.readValue(response.body(), CustomerDTO.class);
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Add failed: " + response.statusCode() + response.body());
            }
        };
    }

    public Task<CustomerDTO> updateCustomerAsync(long id, CustomerDTO customer) {
        return new Task<>() {
            @Override
            protected CustomerDTO call() throws Exception {
                String json = objectMapper.writeValueAsString(customer);
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL + "/" + id))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), CustomerDTO.class);
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Update failed: " + response.statusCode() + response.body());
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
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                return 0L;
            }
        };
    }

    public Task<List<String>> findAllNamesAsync() {
        return new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                        .uri(URI.create(BASE_URL + "/names"))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), new TypeReference<List<String>>() {
                    });
                }
                if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new UnauthorizedException("Token expired or invalid");
                }
                throw new RuntimeException("Failed to fetch names: " + response.statusCode() + response.body());
            }
        };
    }
}