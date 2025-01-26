package com.mycompany.myapp.security;

import io.micronaut.security.authentication.AuthenticationFailed;
import java.util.Optional;

public class NotAuthenticatedResponse extends AuthenticationFailed {

    private final String message;

    public NotAuthenticatedResponse(String message) {
        this.message = message;
    }

    @Override
    public Optional<String> getMessage() {
        return Optional.of(message);
    }
}
