package com.mycompany.myapp.config;

import io.micronaut.context.env.Environment;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.management.endpoint.info.InfoSource;
import jakarta.inject.Singleton;
import java.util.HashMap;
import org.reactivestreams.Publisher;

@Singleton
public class ActiveProfilesInfoSource implements InfoSource {

    private final Environment environment;

    public ActiveProfilesInfoSource(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Publisher<PropertySource> getSource() {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("active-profiles", environment.getActiveNames());
        return Publishers.just(PropertySource.of("active-profiles", map));
    }
}
