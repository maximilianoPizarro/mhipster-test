package com.mycompany.myapp;

import com.mycompany.myapp.config.DefaultProfileUtil;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.server.EmbeddedServer;
import java.lang.reflect.Method;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhipster.config.JHipsterConstants;

public class MhipsterTestApp {

    private static final Logger log = LoggerFactory.getLogger(MhipsterTestApp.class);

    /**
     * Initializes MhipsterTestApp.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    public static void checkProfiles(Environment env) {
        Collection<String> activeProfiles = env.getActiveNames();
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
        ) {
            log.error(
                "You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time."
            );
        }
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)
        ) {
            log.error(
                "You have misconfigured your application! It should not " + "run with both the 'dev' and 'cloud' profiles at the same time."
            );
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        List<String> environments;
        if (System.getenv(Environment.ENVIRONMENTS_ENV) != null) {
            environments = Arrays.asList(System.getenv(Environment.ENVIRONMENTS_ENV).split(","));
        } else if (System.getProperty(Environment.ENVIRONMENTS_PROPERTY) != null) {
            environments = Arrays.asList(System.getProperty(Environment.ENVIRONMENTS_PROPERTY).split(","));
        } else {
            environments = DefaultProfileUtil.getDefaultEnvironments();
        }

        ApplicationContext context = Micronaut.build(args)
            .mainClass(MhipsterTestApp.class)
            .deduceEnvironment(false)
            .environments(environments.toArray(new String[0]))
            .start();

        checkProfiles(context.getEnvironment());

        logApplicationStartup(context);

        if (environments.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)) {
            startH2WebConsole();
        }
    }

    private static void logApplicationStartup(ApplicationContext context) {
        EmbeddedServer server = context.getBean(EmbeddedServer.class);
        ApplicationConfiguration application = context.getBean(ApplicationConfiguration.class);
        String protocol = server.getScheme();
        int serverPort = server.getPort();
        String hostAddress = server.getHost();

        log.info(
            "\n----------------------------------------------------------\n\t" +
            "Application '{}' is running! Access URLs:\n\t" +
            "Local: \t\t\t{}://localhost:{}\n\t" +
            "External: \t\t{}://{}:{}\n\t" +
            "Environment(s): \t{}\n----------------------------------------------------------",
            application.getName().orElse(null),
            protocol,
            serverPort,
            protocol,
            hostAddress,
            serverPort,
            context.getEnvironment().getActiveNames()
        );
    }

    private static void startH2WebConsole() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> serverClass = Class.forName("org.h2.tools.Server", true, loader);
            Method createWebServer = serverClass.getMethod("createWebServer", String[].class);
            Method start = serverClass.getMethod("start");

            Object server = createWebServer.invoke(null, new Object[] { new String[] { "-properties", "src/main/resources/" } });
            start.invoke(server);
        } catch (Exception e) {
            log.trace("Unable to start H2 console.", e);
        }
    }
}
