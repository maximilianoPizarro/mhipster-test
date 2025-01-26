package com.mycompany.myapp.config;

import com.mycompany.myapp.util.JHipsterProperties;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import javax.cache.CacheManager;

@Factory
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {}

    @Singleton
    public CacheManager cacheManager(ApplicationContext applicationContext) {
        customizeCacheManager(cacheManager);
        return cacheManager;
    }

    private void customizeCacheManager(CacheManager cm) {}

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cm.destroyCache(cacheName);
        }
        cm.createCache(cacheName, jcacheConfiguration);
    }
}
