package stroom.receive.common;

import stroom.meta.api.AttributeMap;
import stroom.security.api.UserIdentity;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles the authentication of HTTP requests into the datafeed API on stroom or stroom-proxy
 */
public interface RequestAuthenticator {

    /**
     * Authenticate an inbound request
     * @return
     */
    UserIdentity authenticate(final HttpServletRequest request,
                              final AttributeMap attributeMap);
//
//    /**
//     * Check for presence of tokens/certs on an inbound request that determines if authentication
//     * is possible.
//     * @return True if the request has the required heaader(s) for authentication.
//     */
//    boolean hasAuthenticationToken(final HttpServletRequest request);
//
//    /**
//     * Remove any headers relating to authorisations, e.g. 'Authorisation',
//     * from the passed map
//     */
//    void removeAuthorisationEntries(final Map<String, String> headers);
//
//    /**
//     * @return The authentication/authorisation headers to enable authentication with this user
//     */
//    Map<String, String> getAuthHeaders(final UserIdentity userIdentity);
//
//    /**
//     * @return The authentication/authorisation headers to enable authentication with the service
//     * account user
//     */
//    Map<String, String> getServiceUserAuthHeaders();
}
