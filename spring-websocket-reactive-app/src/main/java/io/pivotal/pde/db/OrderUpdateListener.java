package io.pivotal.pde.db;

import com.google.gson.Gson;
import io.pivotal.pde.model.Order;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.pdx.PdxInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Properties;

public class OrderUpdateListener extends CacheListenerAdapter<String, Order> implements Declarable {

    private static final Logger LOG = LoggerFactory.getLogger(OrderUpdateListener.class);

    private Cache cache;

    private SimpMessagingTemplate webSocketMessageTemplate;

    public OrderUpdateListener(SimpMessagingTemplate webSocketMessageTemplate) {
        this.cache = CacheFactory.getAnyInstance();

        this.webSocketMessageTemplate = webSocketMessageTemplate;
    }

    public void afterCreate(EntryEvent<String, Order> event) {

        Object key = event.getKey();

        Object o = event.getNewValue();

        Order updatedOrder = null;
        if (o instanceof PdxInstance) {
            PdxInstance orderPdxInstance = (PdxInstance) o;

            updatedOrder = (Order) orderPdxInstance.getObject();
        }
        else if (o instanceof Order) {
            updatedOrder = (Order)o;
        }

        LOG.info("Word on the street is " + updatedOrder.getOrder_id() + " got an update, sending along...");

        String jsonString = new Gson().toJson(updatedOrder);

        webSocketMessageTemplate.convertAndSend("/orders/" + updatedOrder.getOrder_id(), jsonString);

    }

    public void init(Properties p) {

    }
}

