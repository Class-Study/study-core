package com.example.studycore.infrastructure.api.controllers.auth.response;

/**
 * Response for authentication endpoints when tokens are managed via headers.
 * Token data is returned in HTTP headers:
 * - X-Access-Token: JWT access token
 * - X-Refresh-Token: Refresh token value
 * - X-Expires-In: Token expiration time in seconds
 *
 * Refresh token is also set as HttpOnly cookie for enhanced security.
 */
public record AuthResponse(
        String message,
        UserInfo user
) {
    public record UserInfo(
            String id,
            String name,
            String email,
            String role,
            String preferenceTheme
    ) {
    }

    public static AuthResponse loginSuccess(UserInfo user) {
        return new AuthResponse("Login successful", user);
    }

    public static AuthResponse refreshSuccess() {
        return new AuthResponse("Token refreshed successfully", null);
    }

    public static AuthResponse logoutSuccess() {
        return new AuthResponse("Logout successful", null);
    }
}
