package io.pivotal.pde;

import io.pivotal.pde.db.OrderUpdateListener;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.pivotal.pde.model.Order;

import java.math.BigInteger;

@EnableScheduling
@Configuration
public class AppConfiguration {

    /*
     * Connection parameter members (TODO - @Profile should adjust these *only*)
     */
    @Value("${gemfire.locator.host:172.16.139.1}")
    private String locatorHost;

    @Value("${gemfire.locator.port:10334}")
    private Integer locatorPort;

    /*
     * Create a connection - client/server topology (TODO - maybe change this to
     * use a connection Pool)
     */
    @Bean
    public ClientCache cache() {
        ClientCacheFactory ccf = new ClientCacheFactory();

        ccf.addPoolLocator(locatorHost, locatorPort);

        // subscription is required for continuous querying
        ccf.setPoolSubscriptionEnabled(true);

        //ccf.setPdxPersistent(false);
        //ccf.setPdxReadSerialized(false);
        ccf.setPdxSerializer(new ReflectionBasedAutoSerializer("io.pivotal.pde.model.*"));

        return ccf.create();
    }

    /*
     * Get a region called "AppLogs", configure as a non-caching proxy
     * (i.e. data remains remote; a pure client-server topology)
     */
    @Bean
    public Region<String, Order> orderRegion(ClientCache cache, SimpMessagingTemplate webSocketMessageTemplate) {
        ClientRegionFactory<String, Order> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

        crf.setConcurrencyChecksEnabled(false);

        crf.addCacheListener(new OrderUpdateListener(webSocketMessageTemplate));

        return crf.create("Orders");
    }

    @Bean
    public QueryService queryService(ClientCache cache) {
        return cache.getQueryService();
    }

}
