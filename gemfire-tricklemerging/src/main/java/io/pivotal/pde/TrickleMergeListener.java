package io.pivotal.pde;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.util.CacheListenerAdapter;

import io.pivotal.pde.model.Order;
import io.pivotal.pde.model.OrderLineItem;

public class TrickleMergeListener extends CacheListenerAdapter implements Declarable {

    private static final Logger LOG = LoggerFactory.getLogger(TrickleMergeListener.class);

    @Override
    public void init(Properties arg0) {
        // TODO Auto-generated method stub

    }

    /** Processes an afterCreate event.
     * @param event The afterCreate EntryEvent received
     */
    public void afterCreate(EntryEvent event) {
        LOG.info("Executing server-side merging function");

        OrderLineItem orderItem = (OrderLineItem)event.getNewValue();
        Integer orderKey = orderItem.getOrder_id();


        LOG.info("Received a new transaction for order " + orderKey);

        // Get a handle on the server cache object
        Cache cache = CacheFactory.getAnyInstance();

        // Get a handle on the Order region object
        Region<Integer, Order> orderRegion = cache.getRegion("Orders");

        Order o = orderRegion.get(orderKey);

        if (o == null) {
            LOG.info("Order " + orderKey + " does not yet exist, creating it.");
            o = new Order(orderKey);
        }

        o.addItem(orderItem);

        LOG.info("Adding the line item: " + orderItem.getProduct_name() + " to order " + orderKey + " .");

        orderRegion.put(orderKey, o);
    }

    public void execute(FunctionContext context) {
        if (!(context instanceof RegionFunctionContext)) {
            throw new FunctionException(
                    "This is a data aware function, and has to be called using FunctionService.onRegion.");
        }
        RegionFunctionContext regionFunctionContext = (RegionFunctionContext) context;

        Map<String, Object> arguments = (Map<String, Object>) regionFunctionContext.getArguments();

        LOG.debug("Executing server-side merging function");

        // Get a handle on the server cache object
        Cache cache = CacheFactory.getAnyInstance();

        // Get handles on the live and export region objects
        //Region<String, Customer> customerRegion = cache.getRegion("Customer");

        LOG.debug(arguments.values().toString());

        List<String> keysToModify = (List<String>) arguments.get("keys");

        LOG.debug(keysToModify.toString());

        Integer numProcessedObjects = 0;
        for (Iterator<String> iter = keysToModify.iterator(); iter.hasNext();) {
            String thisKey = (String) iter.next();

            //customerRegion.put(thisKey, upperCaseFields(customerRegion.get(thisKey)));

            //LOG.debug(customerRegion.get(thisKey).getFirstName());

            ++numProcessedObjects;
        }

        context.getResultSender().lastResult(numProcessedObjects);
    }

}
