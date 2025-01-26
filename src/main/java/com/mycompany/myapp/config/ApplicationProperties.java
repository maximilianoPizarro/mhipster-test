package com.mycompany.myapp.config;

import io.micronaut.context.annotation.ConfigurationProperties;

/**
 * Properties specific to Jhipster Sample Application.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link com.mycompany.myapp.util.JHipsterProperties} for a good example.
 */
@ConfigurationProperties("application")
public class ApplicationProperties {}
