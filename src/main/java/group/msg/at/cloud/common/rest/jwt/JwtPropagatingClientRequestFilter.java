package group.msg.at.cloud.common.rest.jwt;

import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.Priority;
import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * {@code JAX-RS ClientRequestFilter} that propagates an existing {@link JsonWebToken} to a downstream service.
 * <p>
 * Since Payara (5.2020) does not handle CDI-Injections into ClientRequestFilters registered via {@code @RegisterProvider}
 * on MicroProfile REST clients very well, the actual {@code JsonWebToken} object must be looked up programmatically.
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
@Priority(Priorities.HEADER_DECORATOR)
public class JwtPropagatingClientRequestFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        JsonWebToken jwt = CDI.current().select(JsonWebToken.class).get();
        if (jwt != null) {
            propagateAuthorizationHeader(requestContext, jwt);
        }
    }

    private void propagateAuthorizationHeader(ClientRequestContext requestContext, JsonWebToken jwt) {
        requestContext.getHeaders().putSingle("Authorization", "Bearer " + jwt.getRawToken());
    }
}
