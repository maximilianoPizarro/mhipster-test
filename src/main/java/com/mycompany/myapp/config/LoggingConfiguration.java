package com.mycompany.myapp.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.ContextAwareBase;
import com.mycompany.myapp.util.JHipsterProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Property;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Singleton;
import java.net.InetSocketAddress;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.composite.ContextJsonProvider;
import net.logstash.logback.composite.GlobalCustomFieldsJsonProvider;
import net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider;
import net.logstash.logback.composite.loggingevent.LogLevelJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggerNameJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders;
import net.logstash.logback.composite.loggingevent.LoggingEventPatternJsonProvider;
import net.logstash.logback.composite.loggingevent.MdcJsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.composite.loggingevent.StackTraceJsonProvider;
import net.logstash.logback.composite.loggingevent.ThreadNameJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Context
public class LoggingConfiguration {

    private static final String CONSOLE_APPENDER_NAME = "CONSOLE";

    private static final String LOGSTASH_APPENDER_NAME = "LOGSTASH";

    private static final String ASYNC_LOGSTASH_APPENDER_NAME = "ASYNC_LOGSTASH";

    private final Logger log = LoggerFactory.getLogger(LoggingConfiguration.class);

    private LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    private final String appName;

    private final String serverPort;

    private final JHipsterProperties jHipsterProperties;

    public LoggingConfiguration(
        @Property(name = "micronaut.application.name") String appName,
        EmbeddedServer embeddedServer,
        JHipsterProperties jHipsterProperties
    ) {
        this.appName = appName;
        this.serverPort = String.valueOf(embeddedServer.getPort());
        this.jHipsterProperties = jHipsterProperties;
        if (this.jHipsterProperties.getLogging().isUseJsonFormat()) {
            addJsonConsoleAppender(context);
        }
        if (this.jHipsterProperties.getLogging().getLogstash().isEnabled()) {
            addLogstashTcpSocketAppender(context);
        }
        if (this.jHipsterProperties.getLogging().isUseJsonFormat() || this.jHipsterProperties.getLogging().getLogstash().isEnabled()) {
            addContextListener(context);
        }
    }

    private void addJsonConsoleAppender(LoggerContext context) {
        log.info("Initializing Console logging");

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setEncoder(compositeJsonEncoder(context));
        consoleAppender.setName(CONSOLE_APPENDER_NAME);
        consoleAppender.start();

        context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).detachAppender(CONSOLE_APPENDER_NAME);
        context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(consoleAppender);
    }

    private void addLogstashTcpSocketAppender(LoggerContext context) {
        log.info("Initializing Logstash logging");

        // More documentation is available at: https://github.com/logstash/logstash-logback-encoder
        LogstashTcpSocketAppender logstashAppender = new LogstashTcpSocketAppender();
        logstashAppender.addDestinations(
            new InetSocketAddress(
                this.jHipsterProperties.getLogging().getLogstash().getHost(),
                this.jHipsterProperties.getLogging().getLogstash().getPort()
            )
        );
        logstashAppender.setContext(context);
        logstashAppender.setEncoder(logstashEncoder());
        logstashAppender.setName(LOGSTASH_APPENDER_NAME);
        logstashAppender.start();

        // Wrap the appender in an Async appender for performance
        AsyncAppender asyncLogstashAppender = new AsyncAppender();
        asyncLogstashAppender.setContext(context);
        asyncLogstashAppender.setName(ASYNC_LOGSTASH_APPENDER_NAME);
        asyncLogstashAppender.setQueueSize(this.jHipsterProperties.getLogging().getLogstash().getQueueSize());
        asyncLogstashAppender.addAppender(logstashAppender);
        asyncLogstashAppender.start();

        context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(asyncLogstashAppender);
    }

    private LoggingEventCompositeJsonEncoder compositeJsonEncoder(LoggerContext context) {
        final LoggingEventCompositeJsonEncoder compositeJsonEncoder = new LoggingEventCompositeJsonEncoder();
        compositeJsonEncoder.setContext(context);
        compositeJsonEncoder.setProviders(jsonProviders(context));
        compositeJsonEncoder.start();
        return compositeJsonEncoder;
    }

    private LogstashEncoder logstashEncoder() {
        final LogstashEncoder logstashEncoder = new LogstashEncoder();
        logstashEncoder.setThrowableConverter(throwableConverter());
        logstashEncoder.setCustomFields(customFields());
        return logstashEncoder;
    }

    private LoggingEventJsonProviders jsonProviders(LoggerContext context) {
        final LoggingEventJsonProviders jsonProviders = new LoggingEventJsonProviders();
        jsonProviders.addArguments(new ArgumentsJsonProvider());
        jsonProviders.addContext(new ContextJsonProvider<>());
        jsonProviders.addGlobalCustomFields(customFieldsJsonProvider());
        jsonProviders.addLogLevel(new LogLevelJsonProvider());
        jsonProviders.addLoggerName(loggerNameJsonProvider());
        jsonProviders.addMdc(new MdcJsonProvider());
        jsonProviders.addMessage(new MessageJsonProvider());
        jsonProviders.addPattern(new LoggingEventPatternJsonProvider());
        jsonProviders.addStackTrace(stackTraceJsonProvider());
        jsonProviders.addThreadName(new ThreadNameJsonProvider());
        jsonProviders.addTimestamp(timestampJsonProvider());
        jsonProviders.setContext(context);
        return jsonProviders;
    }

    private GlobalCustomFieldsJsonProvider<ILoggingEvent> customFieldsJsonProvider() {
        final GlobalCustomFieldsJsonProvider<ILoggingEvent> customFieldsJsonProvider = new GlobalCustomFieldsJsonProvider<>();
        customFieldsJsonProvider.setCustomFields(customFields());
        return customFieldsJsonProvider;
    }

    private String customFields() {
        StringBuilder customFields = new StringBuilder();
        customFields.append("{");
        customFields.append("\"app_name\":\"").append(this.appName).append("\"");
        customFields.append(",").append("\"app_port\":\"").append(this.serverPort).append("\"");
        customFields.append("}");
        return customFields.toString();
    }

    private LoggerNameJsonProvider loggerNameJsonProvider() {
        final LoggerNameJsonProvider loggerNameJsonProvider = new LoggerNameJsonProvider();
        loggerNameJsonProvider.setShortenedLoggerNameLength(20);
        return loggerNameJsonProvider;
    }

    private StackTraceJsonProvider stackTraceJsonProvider() {
        StackTraceJsonProvider stackTraceJsonProvider = new StackTraceJsonProvider();
        stackTraceJsonProvider.setThrowableConverter(throwableConverter());
        return stackTraceJsonProvider;
    }

    private ShortenedThrowableConverter throwableConverter() {
        final ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();
        throwableConverter.setRootCauseFirst(true);
        return throwableConverter;
    }

    private LoggingEventFormattedTimestampJsonProvider timestampJsonProvider() {
        final LoggingEventFormattedTimestampJsonProvider timestampJsonProvider = new LoggingEventFormattedTimestampJsonProvider();
        timestampJsonProvider.setTimeZone("UTC");
        timestampJsonProvider.setFieldName("timestamp");
        return timestampJsonProvider;
    }

    private void addContextListener(LoggerContext context) {
        LogbackLoggerContextListener loggerContextListener = new LogbackLoggerContextListener(this.jHipsterProperties);
        loggerContextListener.setContext(context);
        context.addListener(loggerContextListener);
    }

    /**
     * Logback configuration is achieved by configuration file and API.
     * When configuration file change is detected, the configuration is reset.
     * This listener ensures that the programmatic configuration is also re-applied after reset.
     */
    class LogbackLoggerContextListener extends ContextAwareBase implements LoggerContextListener {

        private final JHipsterProperties jHipsterProperties;

        private LogbackLoggerContextListener(JHipsterProperties jHipsterProperties) {
            this.jHipsterProperties = jHipsterProperties;
        }

        @Override
        public boolean isResetResistant() {
            return true;
        }

        @Override
        public void onStart(LoggerContext context) {
            if (this.jHipsterProperties.getLogging().isUseJsonFormat()) {
                addJsonConsoleAppender(context);
            }
            if (this.jHipsterProperties.getLogging().getLogstash().isEnabled()) {
                addLogstashTcpSocketAppender(context);
            }
        }

        @Override
        public void onReset(LoggerContext context) {
            if (this.jHipsterProperties.getLogging().isUseJsonFormat()) {
                addJsonConsoleAppender(context);
            }
            if (this.jHipsterProperties.getLogging().getLogstash().isEnabled()) {
                addLogstashTcpSocketAppender(context);
            }
        }

        @Override
        public void onStop(LoggerContext context) {
            // Nothing to do.
        }

        @Override
        public void onLevelChange(ch.qos.logback.classic.Logger logger, Level level) {
            // Nothing to do.
        }
    }
}
