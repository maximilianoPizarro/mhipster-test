package com.mycompany.myapp.config;

import java.util.*;
import tech.jhipster.config.JHipsterConstants;

/**
 * Utility class to load a Spring profile to be used as default
 * when there is no {@code spring.profiles.active} set in the environment or as command line argument.
 * If the value is not available in {@code application.yml} then {@code dev} profile will be used as default.
 */
public final class DefaultProfileUtil {

    private DefaultProfileUtil() {}

    /**
     * Set a default to use when no profile is configured.
     */
    public static List<String> getDefaultEnvironments() {
        return Collections.singletonList(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT);
    }
}
