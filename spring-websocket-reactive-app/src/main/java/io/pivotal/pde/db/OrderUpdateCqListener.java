package io.pivotal.pde.db;

import org.apache.geode.cache.Operation;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.query.CqStatusListener;

import io.pivotal.pde.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class OrderUpdateCqListener implements CqStatusListener {

    @Autowired
    private SimpMessagingTemplate webSocketMessageTemplate;

    public void onEvent(CqEvent cqEvent) {
        // org.apache.geode.cache Operation associated with the query op
        Operation queryOperation = cqEvent.getQueryOperation();

        // key and new value from the event
        Object key = cqEvent.getKey();
        Order updatedOrder = (Order)cqEvent.getNewValue();

        if (queryOperation.isUpdate()) {
            // update line items on the screen for the order . . .
            webSocketMessageTemplate.convertAndSend("/orders/" + updatedOrder.getOrder_id(), updatedOrder.getItems());
        }
        else if (queryOperation.isCreate()) {
            // add the order to the screen . . .
        }
        else if (queryOperation.isDestroy()) {
            // remove the order from the screen . . .
        }
    }

    public void onError(CqEvent cqEvent) {
        // handle the error
    }

    // From CacheCallback
    public void close() {
        // close the output screen for the orders . . .
    }

    public void onCqConnected() {
        //Display connected symbol
    }

    public void onCqDisconnected() {
        //Display disconnected symbol
    }
}
