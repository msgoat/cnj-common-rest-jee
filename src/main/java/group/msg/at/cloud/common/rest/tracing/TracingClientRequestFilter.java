package group.msg.at.cloud.common.rest.tracing;

import group.msg.at.cloud.common.rest.internal.json.SimpleJsonBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code JAX-RS ClientRequestFilter} which traces outbound requests to downstream services.
 */
@Provider
@Priority(Priorities.USER)
public class TracingClientRequestFilter implements ClientRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.TRACE_LOGGER_NAME);

    @Inject
    @ConfigProperty(name = Constants.ENABLED_CONFIG_KEY, defaultValue = Constants.ENABLED_DEFAULT_VALUE)
    boolean enabled;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (shouldFilter(requestContext)) {
            SimpleJsonBuilder builder = new SimpleJsonBuilder();
            traceRequest(builder, requestContext);
            LOGGER.info("\"sentOutboundRequest\" : { {} }", builder.build());
        }
    }

    private boolean shouldFilter(ClientRequestContext requestContext) {
        return enabled && LOGGER.isInfoEnabled();
    }

    private void traceRequest(SimpleJsonBuilder builder, ClientRequestContext requestContext) {
        builder.startObject("request");
        builder.add("uri", requestContext.getUri().toString());
        builder.add("method", requestContext.getMethod());
        builder.startMap("headers");
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.forEach((k, v) -> {
            List<String> values = new ArrayList<>();
            v.forEach(e -> values.add(e.toString()));
            builder.add(k, values);
        });
        builder.stopMap("headers");
        builder.stopObject("request");
    }
}
