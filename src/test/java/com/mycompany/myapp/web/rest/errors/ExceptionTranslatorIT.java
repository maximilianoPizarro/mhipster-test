package com.mycompany.myapp.web.rest.errors;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.MhipsterTestApp;
import com.mycompany.myapp.web.rest.errors.handlers.ProblemHandler;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.rxjava3.http.client.Rx3HttpClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

/**
 * Integration tests exception conditions.
 */
@MicronautTest(application = MhipsterTestApp.class)
public class ExceptionTranslatorIT {

    @Inject
    @Client("/")
    Rx3HttpClient client;

    @Test
    public void testMethodArgumentNotValid() throws Exception {
        HttpResponse<String> response = client
            .exchange(
                HttpRequest.POST("/test/method-argument", new TestDTO()).contentType(MediaType.APPLICATION_JSON_TYPE),
                Argument.of(String.class),
                Argument.of(Problem.class)
            )
            .onErrorReturn(t -> (HttpResponse<String>) ((HttpClientResponseException) t).getResponse())
            .blockingFirst();
        Problem problem = response.getBody(Problem.class).get();
        List<Map> fieldErrors = (List<Map>) problem.getParameters().get("fieldErrors");

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(response.getContentType()).hasValue(ProblemHandler.PROBLEM);
        assertThat(problem.getParameters().get("message")).isEqualTo(ErrorConstants.ERR_VALIDATION);
        assertThat(fieldErrors.get(0).get("objectName")).isEqualTo("testDTO");
        assertThat(fieldErrors.get(0).get("field")).isEqualTo("test");
        assertThat(fieldErrors.get(0).get("message")).isEqualTo("must not be null");
    }

    @Test
    public void testMissingRequestPartException() throws Exception {
        HttpResponse<String> response = client
            .exchange(HttpRequest.GET("/test/missing-servlet-request-part"), Argument.of(String.class), Argument.of(Problem.class))
            .onErrorReturn(t -> (HttpResponse<String>) ((HttpClientResponseException) t).getResponse())
            .blockingFirst();
        Problem problem = response.getBody(Problem.class).get();

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(response.getContentType()).hasValue(ProblemHandler.PROBLEM);
        assertThat(problem.getParameters().get("message")).isEqualTo("error.http.400");
    }

    @Test
    public void testMissingServletRequestParameterException() throws Exception {
        HttpResponse<String> response = client
            .exchange(HttpRequest.GET("/test/missing-servlet-request-parameter"), Argument.of(String.class), Argument.of(Problem.class))
            .onErrorReturn(t -> (HttpResponse<String>) ((HttpClientResponseException) t).getResponse())
            .blockingFirst();
        Problem problem = response.getBody(Problem.class).get();

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(response.getContentType()).hasValue(ProblemHandler.PROBLEM);
        assertThat(problem.getParameters().get("message")).isEqualTo("error.http.400");
    }

    @Test
    public void testAccessDenied() throws Exception {
        HttpResponse<String> response = client
            .exchange(HttpRequest.GET("/test/access-denied"), Argument.of(String.class), Argument.of(Problem.class))
            .onErrorReturn(t -> (HttpResponse<String>) ((HttpClientResponseException) t).getResponse())
            .blockingFirst();
        Problem problem = response.getBody(Problem.class).get();

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
        assertThat(problem.getParameters().get("message")).isEqualTo("error.http.401");
    }

    @Test
    public void testUnauthorized() throws Exception {
        HttpResponse<String> response = client
            .exchange(HttpRequest.GET("/test/unauthorized"), Argument.of(String.class), Argument.of(Problem.class))
            .onErrorReturn(t -> (HttpResponse<String>) ((HttpClientResponseException) t).getResponse())
            .blockingFirst();
        Problem problem = response.getBody(Problem.class).get();

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
        assertThat(response.getContentType()).hasValue(ProblemHandler.PROBLEM);
        assertThat(problem.getParameters().get("message")).isEqualTo("error.http.401");
        assertThat(problem.getParameters().get("path")).isEqualTo("/test/unauthorized");
    }

    @Test
    public void testMethodNotSupported() throws Exception {
        HttpResponse<String> response = client
            .exchange(HttpRequest.POST("/test/access-denied", ""), Argument.of(String.class), Argument.of(Problem.class))
            .onErrorReturn(t -> (HttpResponse<String>) ((HttpClientResponseException) t).getResponse())
            .blockingFirst();
        Problem problem = response.getBody(Problem.class).get();

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.getCode());
        assertThat(problem.getParameters().get("message"))
            .toString()
            .startsWith("Method [POST] not allowed for URI [/test/access-denied].");
    }

    @Test
    public void testExceptionWithResponseStatus() throws Exception {
        HttpResponse<String> response = client
            .exchange(HttpRequest.GET("/test/response-status"), Argument.of(String.class), Argument.of(Problem.class))
            .onErrorReturn(t -> (HttpResponse<String>) ((HttpClientResponseException) t).getResponse())
            .blockingFirst();
        Problem problem = response.getBody(Problem.class).get();

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(problem.getParameters().get("message")).isEqualTo("error.http.400");
    }

    @Test
    public void testInternalServerError() throws Exception {
        HttpResponse<String> response = client
            .exchange(HttpRequest.GET("/test/internal-server-error"), Argument.of(String.class))
            .onErrorReturn(t -> (HttpResponse<String>) ((HttpClientResponseException) t).getResponse())
            .blockingFirst();
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getCode());
    }
}
