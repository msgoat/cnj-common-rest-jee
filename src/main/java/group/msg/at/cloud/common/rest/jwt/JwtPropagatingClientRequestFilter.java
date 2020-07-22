package group.msg.at.cloud.common.rest.jwt;

import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.USER)
public class JwtPropagatingClientRequestFilter implements ClientRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (jwt != null) {
            propagateAuthorizationHeader(requestContext);
        }
    }

    private void propagateAuthorizationHeader(ClientRequestContext requestContext) {
        requestContext.getHeaders().putSingle("Authorization", "Bearer " + jwt.getRawToken());
    }
}
