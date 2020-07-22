package group.msg.at.cloud.common.rest.tracing;

import group.msg.at.cloud.common.rest.internal.json.SimpleJsonBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * {@code JAX-RS ContainerRequestFilter} which traces inbound requests from upstream services or frontends.
 * <p>
 * In contrast to ClientRequestFilters both CDI injection and automatic detection works well for ContainerRequestFilters
 * on all MicroProfile capable application servers.
 * </p>
 */
@Provider
public class TracingContainerRequestFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.TRACE_LOGGER_NAME);

    @Inject
    @ConfigProperty(name = Constants.ENABLED_CONFIG_KEY, defaultValue = Constants.ENABLED_DEFAULT_VALUE)
    boolean enabled;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (shouldFilter(requestContext)) {
            SimpleJsonBuilder builder = new SimpleJsonBuilder();
            traceRequest(builder, requestContext);
            LOGGER.info("\"receivedInboundRequest\" : { {} }", builder.build());
        }
    }

    private boolean shouldFilter(ContainerRequestContext requestContext) {
        return enabled && LOGGER.isInfoEnabled() && !requestContext.getUriInfo().getPath().contains("probes");
    }

    private void traceRequest(SimpleJsonBuilder builder, ContainerRequestContext request) {
        builder.startObject("request");
        builder.add("uri", request.getUriInfo().getRequestUri().toString());
        builder.add("method", request.getMethod());
        builder.startMap("headers");
        MultivaluedMap<String, String> headers = request.getHeaders();
        headers.forEach((k, v) -> builder.add(k, v));
        builder.stopMap("headers");
        builder.stopObject("request");
    }
}
