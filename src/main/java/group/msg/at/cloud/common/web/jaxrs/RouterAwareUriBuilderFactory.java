package group.msg.at.cloud.common.web.jaxrs;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * {@code Factory} for {@code UriBuilder}s which are aware of routers that forwarded the current request.
 * <p>
 * Use this class instead of the factory methods provided by {@link UriInfo} or {@link UriBuilder}, if your service
 * is running behind a router that uses path-based routing.
 * </p>
 *
 * @TODO: clarify if this factory can be transformed into a @RequestScoped bean with injectable UriInfo and HttpHeaders
 */
public class RouterAwareUriBuilderFactory {

    public static UriBuilder from(UriInfo uriInfo, HttpHeaders httpHeaders) {
        URI actualUri = null;
        try {
            actualUri = new URI(getScheme(uriInfo, httpHeaders), getAuthority(uriInfo, httpHeaders), getPath(uriInfo, httpHeaders), uriInfo.getRequestUri().getQuery(), uriInfo.getRequestUri().getFragment());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(String.format("failed to create a external URI for request [%s]", uriInfo.getRequestUri()), ex);
        }
        return UriBuilder.fromUri(actualUri);
    }

    private static String getScheme(UriInfo uriInfo, HttpHeaders headers) {
        Optional<String> forwardedPrefix = getHeaderByNameIgnoringCase(headers, "X-Forwarded-Proto");
        return forwardedPrefix.orElse(uriInfo.getRequestUri().getScheme());
    }

    private static String getAuthority(UriInfo uriInfo, HttpHeaders headers) {
        Optional<String> forwardedPrefix = getHeaderByNameIgnoringCase(headers, "X-Forwarded-Host");
        return forwardedPrefix.orElse(uriInfo.getRequestUri().getAuthority());
    }

    private static String getPath(UriInfo uriInfo, HttpHeaders headers) {
        Optional<String> forwardedPrefix = getHeaderByNameIgnoringCase(headers, "X-Forwarded-Prefix");
        return forwardedPrefix.orElse("") + uriInfo.getRequestUri().getPath();
    }

    private static Optional<String> getHeaderByNameIgnoringCase(HttpHeaders headers, String name) {
        String result = null;
        for (MultivaluedMap.Entry<String, List<String>> current : headers.getRequestHeaders().entrySet()) {
            if (current.getKey().equalsIgnoreCase(name)) {
                result = current.getValue() != null && !current.getValue().isEmpty() ? current.getValue().get(0) : null;
            }
        }
        return Optional.ofNullable(result);
    }
}
