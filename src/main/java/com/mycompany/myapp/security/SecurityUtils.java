package com.mycompany.myapp.security;

import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.security.authentication.Authentication;
import java.util.Collection;
import java.util.Optional;

/**
 * Utility class for Micronaut Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        return ServerRequestContext.currentRequest()
            .flatMap(request -> request.getUserPrincipal(Authentication.class))
            .map(Authentication::getName);
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    public static boolean isAuthenticated() {
        return ServerRequestContext.currentRequest().flatMap(request -> request.getUserPrincipal(Authentication.class)).isPresent();
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the {@code isUserInRole()} method in the Servlet API.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    @SuppressWarnings("unchecked")
    public static boolean isCurrentUserInRole(String authority) {
        return ServerRequestContext.currentRequest()
            .flatMap(request -> request.getUserPrincipal(Authentication.class))
            .map(authentication -> authentication.getAttributes().get("roles"))
            .filter(Collection.class::isInstance)
            .map(Collection.class::cast)
            .map(roles -> roles.stream().anyMatch(role -> role.equals(authority)))
            .orElse(false);
    }
}
