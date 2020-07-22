package group.msg.at.cloud.common.rest.tracing;

import group.msg.at.cloud.common.rest.internal.json.SimpleJsonBuilder;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
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
 * <p>
 * Since Payara (5.2020) does not handle CDI-Injections into ClientRequestFilters registered via {@code @RegisterProvider}
 * on MicroProfile REST clients very well, the actual MicroProfile configuration values must be looked up programmatically.
 * </p>
 * <p>
 * Quarkus (1.5) complains about CDI injection into JAX-RS providers but supports it nevertheless.
 * </p>
 * <p>
 * <strong>Attention:</strong> With Payara, this {@code ClientRequestFilter} has to be registered explicitly on
 * MicroProfile REST clients to be actually applied to REST client invocations. Quarkus picks all ClientRequestFilters
 * automatically.
 * </p>
 */
@Provider
@Priority(Priorities.USER)
public class TracingClientRequestFilter implements ClientRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.TRACE_LOGGER_NAME);

    Boolean enabled;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (shouldFilter(requestContext)) {
            SimpleJsonBuilder builder = new SimpleJsonBuilder();
            traceRequest(builder, requestContext);
            LOGGER.info("\"sentOutboundRequest\" : { {} }", builder.build());
        }
    }

    private boolean shouldFilter(ClientRequestContext requestContext) {
        return isEnabled() && LOGGER.isInfoEnabled();
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

    private boolean isEnabled() {
        if (enabled == null) {
            ConfigProvider.getConfig().getOptionalValue(Constants.ENABLED_CONFIG_KEY, Boolean.class).ifPresentOrElse(v -> enabled = v, () -> enabled = Boolean.FALSE);
        }
        return enabled;
    }
}
