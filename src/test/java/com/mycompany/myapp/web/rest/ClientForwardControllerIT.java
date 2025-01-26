package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.MhipsterTestApp;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.rxjava3.http.client.Rx3HttpClient;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the {@link com.mycompany.myapp.web.rest.ClientForwardController} REST controller.
 */
@MicronautTest(application = MhipsterTestApp.class)
@Property(name = "spec.name", value = "ClientForwardControllerIT")
public class ClientForwardControllerIT {

    @Inject
    @Client("/")
    Rx3HttpClient client;

    @Test
    public void getBackendEndpoint() throws Exception {
        HttpResponse<String> response = client.exchange(HttpRequest.GET("/test"), String.class).blockingFirst();

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.header("Content-Type")).isEqualTo(MediaType.TEXT_PLAIN);
        assertThat(response.body()).isEqualTo("test");
    }

    //    @Test
    //    public void getClientEndpoint() throws Exception {
    //        HttpResponse response = client.exchange(HttpRequest.GET("/non-existant-mapping")).blockingFirst();
    //
    //        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
    //        assertThat(response.header("Content-Type")).isEqualTo(MediaType.TEXT_HTML);
    //    }

    //    @Test
    //    public void getNestedClientEndpoint() throws Exception {
    //        HttpResponse response = client.exchange(HttpRequest.GET("/admin/user-management")).blockingFirst();
    //
    //        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
    //        assertThat(response.header("Content-Type")).isEqualTo(MediaType.TEXT_HTML);
    //    }

    @Controller
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Requires(property = "spec.name", value = "ClientForwardControllerIT")
    public static class TestController {

        @Get(value = "/test", produces = MediaType.TEXT_PLAIN)
        public String test() {
            return "test";
        }
    }
}
