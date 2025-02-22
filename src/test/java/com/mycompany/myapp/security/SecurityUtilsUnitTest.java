package com.mycompany.myapp.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.security.authentication.Authentication;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link SecurityUtils} utility class.
 */
public class SecurityUtilsUnitTest {

    @Test
    public void testGetCurrentUserLogin() {
        HttpRequest request = HttpRequest.GET("/").setAttribute(
            HttpAttributes.PRINCIPAL,
            Authentication.build("admin", Collections.emptyMap())
        );
        ServerRequestContext.with(request, () -> {
            Optional<String> login = SecurityUtils.getCurrentUserLogin();
            assertThat(login).contains("admin");
        });
    }

    @Test
    public void testIsAuthenticated() {
        HttpRequest request = HttpRequest.GET("/").setAttribute(
            HttpAttributes.PRINCIPAL,
            Authentication.build("admin", Collections.emptyMap())
        );
        ServerRequestContext.with(request, () -> {
            boolean isAuthenticated = SecurityUtils.isAuthenticated();
            assertThat(isAuthenticated).isTrue();
        });
    }

    @Test
    public void testAnonymousIsNotAuthenticated() {
        HttpRequest request = HttpRequest.GET("/");
        ServerRequestContext.with(request, () -> {
            boolean isAuthenticated = SecurityUtils.isAuthenticated();
            assertThat(isAuthenticated).isFalse();
        });
    }

    @Test
    public void testIsCurrentUserInRole() {
        HttpRequest request = HttpRequest.GET("/").setAttribute(
            HttpAttributes.PRINCIPAL,
            Authentication.build("user", Collections.singletonMap("roles", Collections.singletonList(AuthoritiesConstants.USER)))
        );

        ServerRequestContext.with(request, () -> {
            assertThat(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.USER)).isTrue();
            assertThat(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)).isFalse();
        });
    }
}
