package io.pivotal.pde.db;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.pivotal.pde.model.Order;

import java.util.List;

@Component
public class OrderRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OrderRepository.class);

    private Region<Integer, Order> orderRegion;
    private QueryService queryService;

    @Autowired
    public OrderRepository(Region<Integer, Order> orderRegion, QueryService queryService) {
        this.orderRegion = orderRegion;
        this.queryService = queryService;
    }

    public Integer getNewRegionEntryCount(Long messagetime) throws Exception {

        Object[] params = new Object[1];
        params[0] = messagetime;

        String queryString = "SELECT COUNT(*) FROM /AppLogs t WHERE t.messageTime > $1";

        Query query = queryService.newQuery(queryString);

        SelectResults results = (SelectResults) query.execute(params);

        if (results.isEmpty()) {
            throw new Exception("Query Result is Empty");
        } else {
            return (Integer) results.asList().get(0);
        }
    }

    public List getRecordsOlderThan(Long ttlInSeconds) throws Exception {
        Long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
        Long lastValidTimestampEpoch = currentTimeInSeconds - ttlInSeconds;

        Object[] params = new Object[1];
        params[0] = lastValidTimestampEpoch;

        String queryString = "SELECT id FROM /AppLogs t WHERE t.messageTime > $1";

        Query query = queryService.newQuery(queryString);

        LOG.info("SELECT id FROM /AppLogs t WHERE t.messageTime > {}", params[0]);

        return ((SelectResults) query.execute(params)).asList();
    }

}