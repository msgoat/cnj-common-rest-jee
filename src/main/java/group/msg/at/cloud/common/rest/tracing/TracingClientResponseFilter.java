package group.msg.at.cloud.common.rest.tracing;

import group.msg.at.cloud.common.rest.internal.json.SimpleJsonBuilder;
import org.eclipse.microprofile.config.ConfigProvider;
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
 * <p>
 * Since Payara (5.2020) does not handle CDI-Injections into ClientResponseFilters registered via {@code @RegisterProvider}
 * on MicroProfile REST clients very well, the actual MicroProfile configuration values must be looked up programmatically.
 * </p>
 * <p>
 * Quarkus (1.5) complains about CDI injection into JAX-RS providers but supports it nevertheless.
 * </p>
 * <p>
 * <strong>Attention:</strong> With Payara, this {@code ClientResponseFilter} has to be registered explicitly on
 * MicroProfile REST clients to be actually applied to REST client invocations. Quarkus picks all ClientResponseFilters
 * automatically.
 * </p>
 */
@Provider
@Priority(Priorities.USER)
public class TracingClientResponseFilter implements ClientResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.TRACE_LOGGER_NAME);

    Boolean enabled;

    private boolean shouldFilter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        return isEnabled() && LOGGER.isInfoEnabled();
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

    private boolean isEnabled() {
        if (enabled == null) {
            ConfigProvider.getConfig().getOptionalValue(Constants.ENABLED_CONFIG_KEY, Boolean.class).ifPresentOrElse(v -> enabled = v, () -> enabled = Boolean.FALSE);
        }
        return enabled;
    }

}
