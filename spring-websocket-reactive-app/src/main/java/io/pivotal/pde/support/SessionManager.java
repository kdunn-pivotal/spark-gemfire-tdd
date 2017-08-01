package io.pivotal.pde.support;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.geode.cache.query.QueryService;

public class SessionManager implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);

    @Autowired
    QueryService queryService;

    @Autowired
    private WebSocketConfiguration webSocketConfiguration;

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
            LOG.info("Tearing down CQ for order " + orderId);
            queryService.getCq("orderTracker-" + orderId).close();
        }
        catch (Exception ex) {
            LOG.info("FAILED tearing down CQ for order " + orderId);
            ex.printStackTrace();
        }
    }
}