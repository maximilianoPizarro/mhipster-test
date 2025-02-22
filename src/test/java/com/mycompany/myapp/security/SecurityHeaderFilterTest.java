package com.mycompany.myapp.security;

import static org.assertj.core.api.Assertions.*;

import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.simple.SimpleHttpResponseFactory;
import org.junit.jupiter.api.Test;

class SecurityHeaderFilterTest {

    private final SecurityHeaderFilter subject = new SecurityHeaderFilter();

    @Test
    public void assertSecurityHeadersAreAdded() {
        MutableHttpResponse<Object> response = new SimpleHttpResponseFactory().ok();
        subject.addSecurityHeaders(response);

        assertThat(response.header("X-Frame-Options")).isEqualTo("DENY");
        assertThat(response.header("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(response.header("X-XSS-Protection")).isEqualTo("1; mode=block");
        assertThat(response.header("Referrer-Policy")).isEqualTo("strict-origin-when-cross-origin");
        assertThat(response.header("Feature-Policy")).isEqualTo(
            "geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; fullscreen 'self'; payment 'none'"
        );
        assertThat(response.header("Content-Security-Policy")).isEqualTo(
            "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:"
        );
    }
}
