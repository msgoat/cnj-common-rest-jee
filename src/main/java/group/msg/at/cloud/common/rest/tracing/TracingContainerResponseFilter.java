package group.msg.at.cloud.common.rest.tracing;

import group.msg.at.cloud.common.rest.internal.json.SimpleJsonBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * {@code JAX-RS ContainerResponseFilter} which traces outbound responses to upstream services or frontends.
 * <p>
 * In contrast to ClientRequestFilters both CDI injection and automatic detection works well for ContainerRequestFilters
 * on all MicroProfile capable application servers.
 * </p>
 */
@Provider
public class TracingContainerResponseFilter implements ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.TRACE_LOGGER_NAME);

    @Inject
    @ConfigProperty(name = Constants.ENABLED_CONFIG_KEY, defaultValue = Constants.ENABLED_DEFAULT_VALUE)
    boolean enabled;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (shouldFilter(requestContext, responseContext)) {
            SimpleJsonBuilder builder = new SimpleJsonBuilder();
            traceRequest(builder, requestContext);
            traceResponse(builder, responseContext);
            LOGGER.info("\"sentOutboundResponse\" : { {} }", builder.build());
        }
    }

    private boolean shouldFilter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        return enabled && LOGGER.isInfoEnabled() && !requestContext.getUriInfo().getPath().contains("probes");
    }

    private void traceRequest(SimpleJsonBuilder builder, ContainerRequestContext requestContext) {
        builder.startObject("request");
        builder.add("uri", requestContext.getUriInfo().getRequestUri().toString());
        builder.add("method", requestContext.getMethod());
        builder.stopObject("request");
    }

    private void traceResponse(SimpleJsonBuilder builder, ContainerResponseContext responseContext) {
        builder.startObject("response");
        builder.add("status", responseContext.getStatus());
        builder.stopObject("response");
    }

}
