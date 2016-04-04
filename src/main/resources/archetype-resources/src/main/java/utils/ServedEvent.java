package eu.vital.ppi.utils;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by f.deceglia on 24/03/2015.
 * Updated by l.bracco on 14/10/2015.
 */

@Provider
public class ServedEvent implements ContainerResponseFilter {

    private volatile int requestCount = 0;

    private Logger logger = LogManager.getLogger(ServedEvent.class);

    @Override
    public void filter(ContainerRequestContext arg0, ContainerResponseContext arg1) throws IOException {
        requestCount++;
        StatCounter.setRequestedNumber(requestCount);
        if(arg1.getStatus() >= 200 && arg1.getStatus() < 300) {
            logger.info("Request " + requestCount + " served.");
        }
        else {
            logger.error("Request " + requestCount + " failed!");
        }
        EventHelper eventHelperF = new EventHelper(requestCount, arg0.getUriInfo().getPath());
        StatCounter.deleteEventHelper(eventHelperF);
    }
}
