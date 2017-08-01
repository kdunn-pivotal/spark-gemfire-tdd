package io.pivotal.pde.controller;

import io.pivotal.pde.model.Order;
import io.pivotal.pde.support.WebSocketConfiguration;
import org.apache.geode.cache.query.CqAttributes;
import org.apache.geode.cache.query.CqAttributesFactory;
import org.apache.geode.cache.query.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import io.pivotal.pde.db.OrderRepository;
import io.pivotal.pde.support.OrderUpdateCqListener;

import javax.servlet.http.HttpServletRequest;

@Controller
class UiController {

    @Autowired
    private SimpMessagingTemplate webSocketMessageTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private QueryService queryService;

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

        // set up the new websocket endpoint
        webSocketConfiguration.addNewBroker(orderId);

        // Create CqAttribute using CqAttributeFactory
        CqAttributesFactory cqf = new CqAttributesFactory();

        // specify the callback for the continuous query results
        cqf.addCqListener(new OrderUpdateCqListener());

        CqAttributes cqa = cqf.create();

        // Name of the CQ and its query
        String cqName = "orderTracker-" + orderId;

        try {
            // register the continuous query
            queryService.newCq("SELECT * FROM /Orders WHERE id = " + orderId, cqa);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


        /* static/templates/order.ftl */
        return "order";
    }
}