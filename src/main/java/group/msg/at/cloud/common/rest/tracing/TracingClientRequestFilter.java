package group.msg.at.cloud.common.rest.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * {@code JAX-RS ClientRequestFilter} which traces outbound requests to downstream services.
 */
@Provider
public class TracingClientRequestFilter implements ClientRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.TRACE_LOGGER_NAME);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (shouldFilter(requestContext)) {
            LOGGER.info("{} OUT sending request to URI [{}]", Constants.TRACE_EYE_CATCHER, requestContext.getUri());
        }
    }

    private boolean shouldFilter(ClientRequestContext requestContext) {
        return LOGGER.isInfoEnabled();
    }
}
