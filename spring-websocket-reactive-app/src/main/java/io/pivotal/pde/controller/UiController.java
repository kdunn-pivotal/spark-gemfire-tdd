package io.pivotal.pde.controller;

import io.pivotal.pde.model.Order;
import io.pivotal.pde.support.WebSocketConfiguration;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.CqAttributes;
import org.apache.geode.cache.query.CqAttributesFactory;
import org.apache.geode.cache.query.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import io.pivotal.pde.db.OrderRepository;
import io.pivotal.pde.db.OrderUpdateCqListener;

import javax.servlet.http.HttpServletRequest;

@Controller
class UiController {

    private static final Logger LOG = LoggerFactory.getLogger(UiController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private Region<String, Order> orderRegion;

    @Autowired
    private WebSocketConfiguration webSocketConfiguration;

    @GetMapping("/ui")
    public String index(Model model) {
        //model.addAttribute("user", authentication.getName());

        /* static/templates/index.ftl */
        return "index";
    }

    @RequestMapping(value = "/order", method = GET)
    public String order(@RequestParam(value = "id") String orderId, HttpServletRequest request, Model model) {
        model.addAttribute("orderId", orderId);

        request.getSession().setAttribute("orderId", orderId);

        LOG.info("added new orderId : " + orderId + " to model.");

        // set up the new websocket endpoint
        webSocketConfiguration.addNewBroker(orderId);

        LOG.info("created websocket broker for id : " + orderId);

        // Create CqAttribute using CqAttributeFactory
        CqAttributesFactory cqf = new CqAttributesFactory();

        // specify the callback for the continuous query results
        cqf.addCqListener(new OrderUpdateCqListener());

        CqAttributes cqa = cqf.create();

        // Name of the CQ and its query
        String cqName = "orderTracker-" + orderId;

        try {

            orderRegion.registerInterest(orderId);

            LOG.info("registered interest for id : " + orderId);
        }
        catch (Exception ex) {
            LOG.error(ex.getMessage());
            //ex.printStackTrace();
        }


        /* static/templates/order.ftl */
        return "order";
    }

}