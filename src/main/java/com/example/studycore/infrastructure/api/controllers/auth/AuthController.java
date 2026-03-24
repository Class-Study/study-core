package com.example.studycore.infrastructure.api.controllers.auth;

import com.example.studycore.application.usecase.auth.LoginUseCase;
import com.example.studycore.application.usecase.auth.LogoutUseCase;
import com.example.studycore.application.usecase.auth.RefreshTokenUseCase;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.infrastructure.api.AuthApi;
import com.example.studycore.infrastructure.api.controllers.auth.request.LoginRequest;
import com.example.studycore.infrastructure.api.controllers.auth.request.RefreshRequest;
import com.example.studycore.infrastructure.api.controllers.auth.response.AuthResponse;
import com.example.studycore.infrastructure.config.service.CookieService;
import com.example.studycore.infrastructure.mapper.AuthInfraMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private static final AuthInfraMapper AUTH_INFRA_MAPPER = AuthInfraMapper.INSTANCE;
    private static final String AUTHORIZATION = "Authorization";
    private static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";
    private static final String EXPIRES_IN_HEADER = "X-Expires-In";

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final CookieService cookieService;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    @Override
    public ResponseEntity<AuthResponse> loginTeacher(LoginRequest request) {
        return executeLogin(request, UserRole.TEACHER);
    }

    @Override
    public ResponseEntity<AuthResponse> loginStudent(LoginRequest request) {
        return executeLogin(request, UserRole.STUDENT);
    }

    @Override
    public ResponseEntity<AuthResponse> loginAdmin(LoginRequest request) {
        return executeLogin(request, UserRole.ADMIN);
    }

    private ResponseEntity<AuthResponse> executeLogin(LoginRequest request, UserRole expectedRole) {
        final var userAgent = httpServletRequest.getHeader("User-Agent");
        final var input = AUTH_INFRA_MAPPER.toLoginInput(request, userAgent, expectedRole);

        final var output = loginUseCase.execute(input);

        final var headers = createTokenHeaders(output.accessToken(), output.refreshToken(), output.expiresIn());
        cookieService.setRefreshTokenCookie(httpServletResponse, output.refreshToken());

        final var response = AUTH_INFRA_MAPPER.toAuthResponse(output);

        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }

    @Override
    public ResponseEntity<AuthResponse> refresh(RefreshRequest request) {
        final var refreshTokenValue = cookieService.getRefreshTokenFromCookieOrRequest(
                httpServletRequest,
                request.refreshToken()
        );

        final var input = AUTH_INFRA_MAPPER.toRefreshTokenInput(refreshTokenValue);
        final var output = refreshTokenUseCase.execute(input);
        final var headers = createTokenHeaders(output.accessToken(), null, output.expiresIn());
        final var response = AUTH_INFRA_MAPPER.toAuthResponse(output);

        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }

    @Override
    public ResponseEntity<Void> logout(RefreshRequest request) {
        final var refreshTokenValue = cookieService.getRefreshTokenFromCookieOrRequest(
                httpServletRequest,
                request.refreshToken()
        );

        final var input = AUTH_INFRA_MAPPER.toRefreshTokenInput(refreshTokenValue);
        logoutUseCase.execute(input);
        cookieService.clearRefreshTokenCookie(httpServletResponse);

        return ResponseEntity.noContent().build();
    }

    private HttpHeaders createTokenHeaders(String accessToken, String refreshToken, long expiresIn) {
        final var headers = new HttpHeaders();

        headers.set(AUTHORIZATION, "Bearer " + accessToken);
        headers.set(EXPIRES_IN_HEADER, String.valueOf(expiresIn));

        if (refreshToken != null) {
            headers.set(REFRESH_TOKEN_HEADER, refreshToken);
        }

        return headers;
    }
}

