package io.pivotal.pde;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.pivotal.pde.model.Order;
import io.pivotal.pde.model.OrderLineItem;
import org.junit.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
// Ensure the context is correctly cleaned up between tests
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {io.pivotal.pde.TestConfiguration.class})
public class TrickleMergeTests {

    @Autowired
    Cache cache;

    //@Autowired
    //Region<Integer, Order> orderRegion;

    public HashMap<String, List<BigInteger>> addSomeData(Region<BigInteger, Order> OrderRegion, Region<BigInteger, OrderLineItem> OrderLineItemRegion) {
        Order c1 = new Order(new BigInteger("1"), 1.0f, 1f);

        Order c2 = new Order(new BigInteger("2"), 10.0f, 2f);

        Order c3 = new Order(new BigInteger("3"), 5500f, 3f);

        OrderRegion.put(c1.getOrder_id(), c1);

        OrderRegion.put(c2.getOrder_id(), c3);

        OrderRegion.put(c3.getOrder_id(), c3);

        OrderLineItem c1_l1 = new OrderLineItem(new BigInteger("1"), new BigInteger("99"), "Tickets", 1f, 1.0f);

        OrderLineItem c2_l1 = new OrderLineItem(new BigInteger("2"), new BigInteger("2"), "STB", 1f, 7.5f);
        OrderLineItem c2_l2 = new OrderLineItem(new BigInteger("2"), new BigInteger("1"), "satellite dish", 1f, 2.5f);

        OrderLineItem c3_l1 = new OrderLineItem(new BigInteger("3"), new BigInteger("1000"), "commercial onboarding", 1f, 0f);
        OrderLineItem c3_l2 = new OrderLineItem(new BigInteger("3"), new BigInteger("1001"), "STB", 1f, 500f);
        OrderLineItem c3_l3 = new OrderLineItem(new BigInteger("3"), new BigInteger("0"), "annual service", 1f, 5000f);

        OrderLineItemRegion.put(c1_l1.getOrder_item_id(), c1_l1);

        OrderLineItemRegion.put(c2_l1.getOrder_item_id(), c2_l1);
        OrderLineItemRegion.put(c2_l2.getOrder_item_id(), c2_l2);


        OrderLineItemRegion.put(c3_l1.getOrder_item_id(), c3_l1);
        OrderLineItemRegion.put(c3_l2.getOrder_item_id(), c3_l2);
        OrderLineItemRegion.put(c3_l3.getOrder_item_id(), c3_l3);

        HashMap<String, List<BigInteger>> keys = new HashMap<>();

        List<BigInteger> orderKeys = new ArrayList<>();
        orderKeys.add(c1.getOrder_id());
        orderKeys.add(c2.getOrder_id());
        orderKeys.add(c3.getOrder_id());

        keys.put("orders", orderKeys);

        List<BigInteger> orderItemKeys = new ArrayList<>();
        orderItemKeys.add(c1_l1.getOrder_item_id());
        orderItemKeys.add(c2_l1.getOrder_item_id());
        orderItemKeys.add(c2_l2.getOrder_item_id());
        orderItemKeys.add(c3_l1.getOrder_item_id());
        orderItemKeys.add(c3_l2.getOrder_item_id());
        orderItemKeys.add(c3_l3.getOrder_item_id());

        keys.put("orderItems", orderItemKeys);

        return keys;
    }

    public void cleanupData(Region<BigInteger, Order> OrderRegion, Region<BigInteger, OrderLineItem> OrderLineItemRegion, HashMap<String, List<BigInteger>> keys) {
        OrderRegion.removeAll(keys.get("orders"));
        OrderLineItemRegion.removeAll(keys.get("orderItems"));
    }

    @Test
    public void TrickleMergeTests() throws Exception {
        RegionFactory<BigInteger, Order> regionFactory = cache.createRegionFactory(RegionShortcut.PARTITION);
        Region<BigInteger, Order> orderRegion = regionFactory.create("Orders");

        RegionFactory<Integer, OrderLineItem> lineItemRegionFactor = cache.createRegionFactory(RegionShortcut.PARTITION);
        Region<BigInteger, OrderLineItem> orderLineItemRegion = lineItemRegionFactor
                .addCacheListener(new TrickleMergeListener())
                .create("OrderLineItems");

        HashMap<String, List<BigInteger>> keys = addSomeData(orderRegion, orderLineItemRegion);

        Assert.assertEquals(3, orderRegion.size());

        Assert.assertEquals(6, orderLineItemRegion.size());

        Order o1 = orderRegion.get(keys.get("orders").get(0));

        Assert.assertEquals(1, o1.getItems().size());

        Order o2 = orderRegion.get(keys.get("orders").get(1));

        Assert.assertEquals(2, o2.getItems().size());

        Order o3 = orderRegion.get(keys.get("orders").get(2));

        Assert.assertEquals(3, o3.getItems().size());

        cleanupData(orderRegion, orderLineItemRegion, keys);
        Assert.assertEquals(0, orderRegion.size());
        Assert.assertEquals(0, orderLineItemRegion.size());
    }

}
