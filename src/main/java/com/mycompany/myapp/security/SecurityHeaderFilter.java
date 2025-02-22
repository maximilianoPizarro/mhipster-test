package com.mycompany.myapp.security;

import static io.micronaut.http.annotation.Filter.MATCH_ALL_PATTERN;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.rxjava3.core.Flowable;
import org.reactivestreams.Publisher;

@Filter(patterns = { MATCH_ALL_PATTERN })
public class SecurityHeaderFilter implements HttpServerFilter {

    private static final String X_FRAME_OPTIONS_HEADER = "X-Frame-Options";
    private static final String X_CONTENT_TYPE_OPTIONS_HEADER = "X-Content-Type-Options";
    private static final String X_XSS_PROTECTION_HEADER = "X-XSS-Protection";
    private static final String REFERRER_POLICY_HEADER = "Referrer-Policy";
    private static final String FEATURE_POLICY_HEADER = "Feature-Policy";
    private static final String CONTENT_SECURITY_POLICY_HEADER = "Content-Security-Policy";

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        return Flowable.fromPublisher(chain.proceed(request)).map(mutableHttpResponse -> {
            addSecurityHeaders(mutableHttpResponse);
            return mutableHttpResponse;
        });
    }

    protected void addSecurityHeaders(MutableHttpResponse<?> response) {
        response.header(X_FRAME_OPTIONS_HEADER, "DENY");
        response.header(X_CONTENT_TYPE_OPTIONS_HEADER, "nosniff");
        response.header(X_XSS_PROTECTION_HEADER, "1; mode=block");
        response.header(REFERRER_POLICY_HEADER, "strict-origin-when-cross-origin");
        response.header(
            FEATURE_POLICY_HEADER,
            "geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; fullscreen 'self'; payment 'none'"
        );
        response.header(
            CONTENT_SECURITY_POLICY_HEADER,
            "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:"
        );
    }
}
