package stroom.proxy.app.jersey;

import stroom.dropwizard.common.AbstractJerseyClientFactory;
import stroom.proxy.app.Config;
import stroom.util.io.PathCreator;
import stroom.util.shared.BuildInfo;
import stroom.util.shared.NullSafe;

import io.dropwizard.core.setup.Environment;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

@SuppressWarnings("unused")
@Singleton // Caches shared jersey clients
class ProxyJerseyClientFactoryImpl extends AbstractJerseyClientFactory {

    // This name is used by dropwizard metrics
    private static final String JERSEY_CLIENT_NAME_PREFIX = "stroom-proxy_jersey_client_";
    private static final String JERSEY_CLIENT_USER_AGENT_PREFIX = "stroom-proxy/";

    @SuppressWarnings("unused")
    @Inject
    public ProxyJerseyClientFactoryImpl(final Config config,
                                        final Provider<BuildInfo> buildInfoProvider,
                                        final Environment environment,
                                        final PathCreator pathCreator) {
        super(buildInfoProvider,
                environment,
                pathCreator,
                NullSafe.map(config.getJerseyClients()));
    }

    public String getJerseyClientNamePrefix() {
        return JERSEY_CLIENT_NAME_PREFIX;
    }

    public String getJerseyClientUserAgentPrefix() {
        return JERSEY_CLIENT_USER_AGENT_PREFIX;
    }
}
