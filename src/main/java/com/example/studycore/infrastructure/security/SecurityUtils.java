package com.example.studycore.infrastructure.security;

import java.lang.reflect.Method;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Try to extract the authenticated user's UUID from the current SecurityContext.
     *
     * Supports the common cases used in the project:
     * - principal is a String containing the UUID
     * - principal implements UserDetails (uses getUsername())
     * - principal is an OAuth2 Jwt (reflection used to avoid a hard compile-time dependency)
     * - fallback to Authentication.getName()
     *
     * Throws IllegalStateException when it cannot extract a UUID.
     */
    public static UUID getAuthenticatedUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        final Object principal = authentication.getPrincipal();

        // common: principal is a String with the UUID
        if (principal instanceof String) {
            final String s = (String) principal;
            try {
                return UUID.fromString(s);
            } catch (IllegalArgumentException ex) {
                // continue to other attempts
            }
        }

        // common: principal implements UserDetails -> use username
        if (principal instanceof UserDetails) {
            final String username = ((UserDetails) principal).getUsername();
            try {
                return UUID.fromString(username);
            } catch (IllegalArgumentException ex) {
                // continue
            }
        }

        // If the project uses Jwt as principal, avoid hard dependency: use reflection
        if (principal != null) {
            final Class<?> cls = principal.getClass();
            if ("org.springframework.security.oauth2.jwt.Jwt".equals(cls.getName())) {
                try {
                    final Method getClaim = cls.getMethod("getClaim", String.class);
                    Object sub = getClaim.invoke(principal, "sub");
                    if (sub != null) return UUID.fromString(sub.toString());
                    Object userId = getClaim.invoke(principal, "userId");
                    if (userId != null) return UUID.fromString(userId.toString());
                } catch (Exception e) {
                    // ignore and fallback
                }
            }
        }

        // fallback to authentication.getName()
        final String name = authentication.getName();
        if (name != null) {
            try {
                return UUID.fromString(name);
            } catch (IllegalArgumentException ex) {
                // pass to error
            }
        }

        throw new IllegalStateException("Cannot extract authenticated user id from security context. Principal type: " +
                (principal == null ? "null" : principal.getClass().getName()));
    }
}

