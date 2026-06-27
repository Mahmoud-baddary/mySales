package com.baddary.mysales.util;

public class TokenStore {
    private static String authToken;
    private static Long currentUserId;
    private static String currentUserName;

    // Store token and user info after login
    public static void setToken(String token) {
        authToken = token;
    }

    public static String getToken() {
        return authToken;
    }

    public static void setCurrentUser(Long userId, String userName) {
        currentUserId = userId;
        currentUserName = userName;
    }

    public static Long getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserName() {
        return currentUserName;
    }

    // Clear all data (e.g., on logout)
    public static void clear() {
        authToken = null;
        currentUserId = null;
        currentUserName = null;
    }

    // Check if user is logged in
    public static boolean isLoggedIn() {
        return authToken != null && !authToken.isEmpty();
    }
}