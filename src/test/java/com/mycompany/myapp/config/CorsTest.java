package com.mycompany.myapp.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.rxjava3.http.client.Rx3HttpClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CorsTest implements TestPropertyProvider {

    @Inject
    @Client("/")
    Rx3HttpClient client;

    @Property(name = "micronaut.server.cors.enabled", value = "true")
    @Test
    public void testCorsFilterOnApiPath() {
        HttpResponse<?> response = client
            .exchange(
                HttpRequest.OPTIONS("/api/test-cors")
                    .header(HttpHeaders.ORIGIN, "other.domain.com")
                    .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
            )
            .blockingFirst();

        assertThat(response.getStatus().getCode()).isEqualTo(200);
        assertThat(response.header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo("other.domain.com");
        assertThat(response.header(HttpHeaders.VARY)).isEqualTo("Origin");
        assertThat(response.header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS)).isEqualTo("POST");
        assertThat(response.header(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
        assertThat(response.header(HttpHeaders.ACCESS_CONTROL_MAX_AGE)).isEqualTo("1800");
    }

    @Test
    public void testCorsFilterDeactivated() throws Exception {
        HttpResponse<?> response = client
            .exchange(
                HttpRequest.GET("/test/test-cors")
                    .header(HttpHeaders.ORIGIN, "other.domain.com")
                    .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
            )
            .blockingFirst();

        assertThat(response.getStatus().getCode()).isEqualTo(200);
        assertThat(response.getHeaders().contains(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isFalse();
    }

    @Override
    public Map<String, String> getProperties() {
        return CollectionUtils.mapOf(
            "consul.client.registration.enabled",
            "false",
            "consul.client.config.enabled",
            "false",
            "micronaut.server.cors.single-header",
            "true",
            "micronaut.server.cors.localhost-pass-through",
            "true",
            "micronaut.server.cors.configurations.default.allowed-methods[0]",
            "GET",
            "micronaut.server.cors.configurations.default.allowed-methods[1]",
            "POST",
            "micronaut.server.cors.configurations.default.allowed-methods[2]",
            "PUT",
            "micronaut.server.cors.configurations.default.allowed-methods[3]",
            "DELETE",
            "micronaut.server.cors.configurations.default.max-age",
            "1800L",
            "micronaut.server.cors.configurations.default.allow-credentials",
            "true"
        );
    }
}
