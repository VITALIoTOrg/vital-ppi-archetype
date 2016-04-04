package eu.vital.ppi.utils;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by f.deceglia on 24/03/2015.
 * Updated by l.bracco on 14/10/2015.
 */

@Provider
public class RequestEvent implements ContainerRequestFilter {

    private Logger logger = LogManager.getLogger(RequestEvent.class);

    @Override
    public void filter(ContainerRequestContext arg0) throws IOException {
        logger.info("Request " + StatCounter.getRequestNumber().addAndGet(1) + " started [" + arg0.getUriInfo().getPath() + "].");
        EventHelper eventHelper = new EventHelper(StatCounter.getRequestNumber().addAndGet(1), arg0.getUriInfo().getPath());
        StatCounter.addEventHelper(eventHelper);
    }
}
