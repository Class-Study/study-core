package com.example.studycore.infrastructure.config.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    @Value("${app.security.cookie.secure:false}")
    private boolean secureCookie;

    @Value("${app.security.cookie.same-site:Lax}")
    private String sameSite;

    @Value("${jwt.refresh-token.expiration-days:30}")
    private int refreshTokenExpirationDays;

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpirationDays * 24 * 60 * 60);
        response.addCookie(cookie);

        String cookieHeader = String.format("%s=%s; Path=/; Max-Age=%d; HttpOnly; %s SameSite=%s",
                REFRESH_TOKEN_COOKIE,
                refreshToken,
                refreshTokenExpirationDays * 24 * 60 * 60,
                secureCookie ? "Secure;" : "",
                sameSite);
        response.setHeader("Set-Cookie", cookieHeader);
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete cookie
        response.addCookie(cookie);
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public String getRefreshTokenFromCookieOrRequest(HttpServletRequest request, String fallbackToken) {
        String tokenFromCookie = getRefreshTokenFromCookie(request);
        return tokenFromCookie != null ? tokenFromCookie : fallbackToken;
    }
}
