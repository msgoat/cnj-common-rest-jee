package group.msg.at.cloud.common.rest.tracing;

import group.msg.at.cloud.common.rest.internal.json.SimpleJsonBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * {@code JAX-RS ClientResponseFilter} which traces inbound responses from downstream services.
 */
@Provider
@Priority(Priorities.USER)
public class TracingClientResponseFilter implements ClientResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.TRACE_LOGGER_NAME);

    @Inject
    @ConfigProperty(name = Constants.ENABLED_CONFIG_KEY, defaultValue = Constants.ENABLED_DEFAULT_VALUE)
    boolean enabled;

    private boolean shouldFilter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        return enabled && LOGGER.isInfoEnabled();
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (shouldFilter(requestContext, responseContext)) {
            SimpleJsonBuilder builder = new SimpleJsonBuilder();
            traceRequest(builder, requestContext);
            traceResponse(builder, responseContext);
            LOGGER.info("\"receivedInboundResponse\" : { {} }", builder.build());
        }
    }

    private void traceRequest(SimpleJsonBuilder builder, ClientRequestContext requestContext) {
        builder.startObject("request");
        builder.add("uri", requestContext.getUri().toString());
        builder.add("method", requestContext.getMethod());
        builder.stopObject("request");
    }

    private void traceResponse(SimpleJsonBuilder builder, ClientResponseContext responseContext) {
        builder.startObject("response");
        builder.add("status", responseContext.getStatus());
        builder.stopObject("response");
    }
}
