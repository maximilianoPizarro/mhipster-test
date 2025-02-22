package com.mycompany.myapp.web.rest.errors.handlers;

import com.mycompany.myapp.web.rest.errors.ErrorConstants;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.violations.ConstraintViolationProblem;

public class ProblemHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ProblemHandler.class);

    protected static final String FIELD_ERRORS_KEY = "fieldErrors";
    protected static final String MESSAGE_KEY = "message";
    protected static final String PATH_KEY = "path";
    protected static final String VIOLATIONS_KEY = "violations";

    public static final MediaType PROBLEM = MediaType.of("application/problem+json");
    public static final MediaType X_PROBLEM = MediaType.of("application/x.problem+json");

    private static void log(HttpStatus status, Exception exception) {
        if (status.getCode() >= 400 && status.getCode() <= 499) {
            LOG.warn("{}: {}", status.getReason(), exception.getMessage());
        } else if (status.getCode() >= 500) {
            LOG.error(status.getReason(), exception);
        }
    }

    /**
     * Post-process the Problem payload to add the message key for the front-end if needed.
     */
    private static Problem process(@Nullable Problem problem, HttpRequest request) {
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return problem;
        }
        ProblemBuilder builder = Problem.builder()
            .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? ErrorConstants.DEFAULT_TYPE : problem.getType())
            .withStatus(problem.getStatus())
            .withTitle(problem.getTitle())
            .with(PATH_KEY, request.getUri());

        if (problem instanceof ConstraintViolationProblem) {
            builder
                .with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations())
                .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION);
        } else {
            builder.withCause(((DefaultProblem) problem).getCause()).withDetail(problem.getDetail()).withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);
            if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
                builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return builder.build();
    }

    private static MediaType getProblemMediaType(final HttpRequest request) {
        List<MediaType> mediaTypes = Arrays.asList(
            MediaType.of(request.getHeaders().getAll(HttpHeaders.ACCEPT).toArray(new CharSequence[] {}))
        );

        if (mediaTypes.contains(X_PROBLEM)) {
            return X_PROBLEM;
        } else {
            return PROBLEM;
        }
    }

    public static MutableHttpResponse<Problem> create(Problem problem, HttpRequest request, @Nullable Exception exception) {
        problem = process(problem, request);

        HttpStatus status = HttpStatus.valueOf(
            Optional.ofNullable(problem.getStatus()).orElse(org.zalando.problem.Status.INTERNAL_SERVER_ERROR).getStatusCode()
        );

        if (exception != null) {
            log(status, exception);
        }

        MediaType mediaType = getProblemMediaType(request);

        return HttpResponse.<Problem>status(status).contentType(mediaType).body(problem);
    }
}
