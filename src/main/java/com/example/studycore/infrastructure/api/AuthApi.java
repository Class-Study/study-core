package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.auth.request.LoginRequest;
import com.example.studycore.infrastructure.api.controllers.auth.request.RefreshRequest;
import com.example.studycore.infrastructure.api.controllers.auth.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication", description = "Authentication endpoints with token management via headers")
@RequestMapping("/auth")
public interface AuthApi {

    @Operation(summary = "User login",
               description = "Authenticates user and returns tokens via headers and HttpOnly cookie")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                     description = "Login successful",
                     headers = {
                         @Header(name = "X-Access-Token", description = "JWT access token",
                                schema = @Schema(type = "string")),
                         @Header(name = "X-Refresh-Token", description = "Refresh token value",
                                schema = @Schema(type = "string")),
                         @Header(name = "X-Expires-In", description = "Token expiration in seconds",
                                schema = @Schema(type = "integer"))
                     }),
        @ApiResponse(responseCode = "400", description = "Invalid credentials or user blocked")
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "Refresh access token",
               description = "Generates new access token using refresh token from cookie or request body")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                     description = "Token refreshed successfully",
                     headers = {
                         @Header(name = "X-Access-Token", description = "New JWT access token",
                                schema = @Schema(type = "string")),
                         @Header(name = "X-Expires-In", description = "Token expiration in seconds",
                                schema = @Schema(type = "integer"))
                     }),
        @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request);

    @Operation(summary = "User logout",
               description = "Revokes refresh token and clears authentication cookie")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Logout successful"),
        @ApiResponse(responseCode = "400", description = "Invalid refresh token")
    })
    @PostMapping("/logout")
    ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request);
}

