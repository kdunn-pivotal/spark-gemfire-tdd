package io.pivotal.pde;

import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;

import io.pivotal.pde.model.Order;
import io.pivotal.pde.model.OrderLineItem;

public class TrickleMergeListener implements AsyncEventListener, Declarable {

    private static final Logger LOG = LoggerFactory.getLogger(TrickleMergeListener.class);

    private Cache cache;

    private Region<String, Order> orderRegion;

    private Region<BigInteger, PdxInstanceImpl> orderLineItemRegion;

    public TrickleMergeListener() {
        this.cache = CacheFactory.getAnyInstance();
    }

    @Override
    public void init(Properties arg0) {

    }

    public boolean processEvents(List<AsyncEvent> list) {

        try {
            list.forEach(item -> {
                LOG.info("Executing server-side merging function");

                orderRegion = cache.getRegion("Orders");

                orderLineItemRegion = cache.getRegion("OrderLineItems");

                Object li = orderLineItemRegion.get(item.getKey());

                OrderLineItem lineItem = null;
                if (li instanceof PdxInstance) {
                    PdxInstance lineItemPdxInstance = (PdxInstance)li;

                    lineItem = (OrderLineItem)lineItemPdxInstance.getObject();
                }
                else if (li instanceof OrderLineItem) {
                    lineItem = (OrderLineItem)li;
                }
                else {
                    LOG.error("Could not handle OrderLineItem object type " + li.getClass());
                }

                if (lineItem != null) {
                    String orderId = lineItem.getOrder_id();

                    LOG.info("Received a new transaction for order " + orderId);

                    Object o = orderRegion.get(orderId);

                    Order order = null;
                    if (o instanceof PdxInstance) {
                        PdxInstance orderPdxInstance = (PdxInstance)o;

                        order = (Order)orderPdxInstance.getObject();
                    }
                    else if (o instanceof Order) {
                        order = (Order)o;
                    }
                    else if (o == null) {
                        LOG.info("Order " + orderId + " does not yet exist, creating it.");
                        order = new Order(orderId);
                    }
                    else {
                        LOG.error("Could not handle Order object type " + o.getClass());

                    }

                    if (order != null) {
                        order.addItem(lineItem);

                        LOG.info("Adding the line item: " + lineItem.getProduct_name() + " to order " + orderId + " .");

                        orderRegion.put(orderId, order);
                    }

                }

            });
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return true;
    }

    @Override
    public void close() {

    }

}
