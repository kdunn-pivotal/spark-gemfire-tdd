package io.pivotal.pde;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Value("${locator-host:localhost}")
    private String locatorHost;

    @Value("${locator-port:20334}")
    private int locatorPort;

    @Value("${cache-server.port:40404}")
    private int port;

    @Bean
    public Cache createCache() throws Exception {
        CacheFactory cf = new CacheFactory();
        cf.set("name", "ServerFunctionTdd");
        cf.set("start-locator", locatorHost +
                "[" + locatorPort + "]");

        // PDX serialization is necessary when using complex types
        cf.setPdxSerializer(new ReflectionBasedAutoSerializer("io.pivotal.pde.*"));

        Cache c = cf.create();
        CacheServer cs = c.addCacheServer();
        cs.setPort(port);
        cs.start();

        return c;
    }

}
