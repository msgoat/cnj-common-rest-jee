package group.msg.at.cloud.common.rest.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * {@code JAX-RS ClientResponseFilter} which traces inbound responses from downstream services.
 */
@Provider
public class TracingClientResponseFilter implements ClientResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.TRACE_LOGGER_NAME);

    private boolean shouldFilter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        return LOGGER.isInfoEnabled();
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (shouldFilter(requestContext, responseContext)) {
            LOGGER.info("{} IN got response for request URI [{}]", Constants.TRACE_EYE_CATCHER, requestContext.getUri());
        }
    }
}
