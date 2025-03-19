package stroom.config.app;

import stroom.search.elastic.CryptoConfig;
import stroom.security.common.impl.ContentSecurityConfig;
import stroom.security.identity.config.IdentityConfig;
import stroom.security.impl.AuthenticationConfig;
import stroom.security.impl.AuthorisationConfig;
import stroom.util.shared.AbstractConfig;
import stroom.util.shared.IsStroomConfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder(alphabetic = true)
public class SecurityConfig extends AbstractConfig implements IsStroomConfig {

    public static final String PROP_NAME_IDENTITY = "identity";

    private final AuthenticationConfig authenticationConfig;
    private final AuthorisationConfig authorisationConfig;
    private final ContentSecurityConfig contentSecurityConfig;
    private final CryptoConfig cryptoConfig;
    private final IdentityConfig identityConfig;

    public SecurityConfig() {
        authenticationConfig = new AuthenticationConfig();
        authorisationConfig = new AuthorisationConfig();
        contentSecurityConfig = new ContentSecurityConfig();
        cryptoConfig = new CryptoConfig();
        identityConfig = new IdentityConfig();
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public SecurityConfig(@JsonProperty("authentication") final AuthenticationConfig authenticationConfig,
                          @JsonProperty("authorisation") final AuthorisationConfig authorisationConfig,
                          @JsonProperty("webContent") final ContentSecurityConfig contentSecurityConfig,
                          @JsonProperty("crypto") final CryptoConfig cryptoConfig,
                          @JsonProperty(PROP_NAME_IDENTITY) final IdentityConfig identityConfig) {
        this.authenticationConfig = authenticationConfig;
        this.authorisationConfig = authorisationConfig;
        this.contentSecurityConfig = contentSecurityConfig;
        this.cryptoConfig = cryptoConfig;
        this.identityConfig = identityConfig;
    }

    @JsonProperty("authentication")
    public AuthenticationConfig getAuthenticationConfig() {
        return authenticationConfig;
    }

    @JsonProperty("authorisation")
    public AuthorisationConfig getAuthorisationConfig() {
        return authorisationConfig;
    }

    @JsonProperty("webContent")
    public ContentSecurityConfig getContentSecurityConfig() {
        return contentSecurityConfig;
    }

    @JsonProperty("crypto")
    public CryptoConfig getCryptoConfig() {
        return cryptoConfig;
    }

    @JsonProperty(PROP_NAME_IDENTITY)
    public IdentityConfig getIdentityConfig() {
        return identityConfig;
    }

    @Override
    public String toString() {
        return "SecurityConfig{" +
                "authenticationConfig=" + identityConfig +
                ", authorisationConfig=" + authorisationConfig +
                ", contentSecurityConfig=" + contentSecurityConfig +
                ", cryptoConfig=" + cryptoConfig +
                ", identityConfig=" + identityConfig +
                '}';
    }
}
