package io.pivotal.pde.support;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import io.pivotal.pde.model.Order;
import org.apache.geode.cache.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.geode.cache.query.QueryService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionManager implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);

    @Autowired
    private Region<String, Order> orderRegion;

    private static int totalActiveSessions;

    public static int getTotalActiveSession(){
        return totalActiveSessions;
    }

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        totalActiveSessions++;
        LOG.info("sessionCreated - add one session into counter");

        //printCounter(sessionEvent);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        totalActiveSessions--;
        LOG.info("sessionDestroyed - deduct one session from counter");
        cancelContinuousQuery(sessionEvent);
    }

    private void cancelContinuousQuery(HttpSessionEvent sessionEvent){

        HttpSession session = sessionEvent.getSession();

        String orderId = (String)session.getAttribute("orderId");

        try {
            LOG.info("Removing interest for order " + orderId);
            orderRegion.unregisterInterest(orderId);
        }
        catch (Exception ex) {
            LOG.info("FAILED removing interest for order " + orderId);
            ex.printStackTrace();
        }
    }
}