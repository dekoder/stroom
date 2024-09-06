package stroom.proxy.app.servlet;

import stroom.util.shared.BuildInfo;
import stroom.util.shared.IsServlet;
import stroom.util.shared.ResourcePaths;
import stroom.util.shared.Unauthenticated;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Public un-authenticated servlet for client systems to monitor stroom-proxy's availability.
 */
@Unauthenticated
public class ProxyStatusServlet extends HttpServlet implements IsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyStatusServlet.class);

    public static final String PATH_PART = "/status";
    private static final Set<String> PATH_SPECS = Set.of(
            PATH_PART,
            ResourcePaths.addLegacyUnauthenticatedServletPrefix(PATH_PART));

    private final Provider<BuildInfo> buildInfoProvider;

    @Inject
    public ProxyStatusServlet(final Provider<BuildInfo> buildInfoProvider) {
        this.buildInfoProvider = buildInfoProvider;
    }

    @Override
    public void init() throws ServletException {
        LOGGER.info("Initialising Status Servlet");
        super.init();
        LOGGER.info("Initialised Status Servlet");
    }

    @Override
    public void destroy() {
        LOGGER.info("Destroying Status Servlet");
        super.destroy();
        LOGGER.info("Destroyed Status Servlet");
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        try (final PrintWriter printWriter = response.getWriter()) {
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(200);

            new ObjectMapper().writeValue(printWriter, buildInfoProvider.get());
        } catch (final IOException e) {
            LOGGER.error("Error retrieving stroom status", e);
            throw new ServletException("Error retrieving stroom status");
        }
    }

    /**
     * @return The part of the path that will be in addition to any base path,
     * e.g. "/datafeed".
     */
    @Override
    public Set<String> getPathSpecs() {
        return PATH_SPECS;
    }
}
