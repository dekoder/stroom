/*
 *
 *
 *
 *
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *   Copyright 2017 Crown Copyright
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   See the License for the specific language governing permissions and
 *   Unless required by applicable law or agreed to in writing, software
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   You may obtain a copy of the License at
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   import stroom.util.shared.IsStroomConfig;
 *   limitations under the License.
 *   you may not use this file except in compliance with the License.
 */

package stroom.analytics.impl;

import stroom.util.config.annotations.Password;
import stroom.util.shared.AbstractConfig;
import stroom.util.shared.IsStroomConfig;
import stroom.util.shared.NotInjectableConfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.simplejavamail.api.mailer.config.TransportStrategy;

@NotInjectableConfig
@JsonPropertyOrder(alphabetic = true)
public class SmtpConfig extends AbstractConfig implements IsStroomConfig {

    public static final String DEFAULT_TRANSPORT = "plain";

    @NotNull
    @JsonProperty("host")
    @JsonPropertyDescription("The fully qualified hostname of the SMTP server.")
    private final String host;

    @Min(0)
    @Max(65535)
    @JsonProperty("port")
    @JsonPropertyDescription("The port for the SMTP server.")
    private final int port;

    @NotNull
    @JsonProperty("transport")
    @JsonPropertyDescription("The transport type for communicating with the SMTP server.")
    private final String transport;

    @JsonProperty("username")
    @JsonPropertyDescription("The username to authenticate with on the SMTP server.")
    private final String username;

    @Password
    @JsonProperty("password")
    @JsonPropertyDescription("The password to authenticate with on the SMTP server.")
    private final String password;

    public SmtpConfig() {
        host = "localhost";
        port = 2525;
        transport = DEFAULT_TRANSPORT;
        password = null;
        username = null;
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public SmtpConfig(@JsonProperty("host") final String host,
                      @JsonProperty("port") final int port,
                      @JsonProperty("transport") final String transport,
                      @JsonProperty("username") final String username,
                      @JsonProperty("password") final String password) {
        this.host = host;
        this.port = port;
        this.transport = transport;
        this.username = username;
        this.password = password;
    }

    public static SmtpConfig unauthenticated(final String host,
                                             final int port) {
        return new SmtpConfig(host, port, DEFAULT_TRANSPORT, null, null);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTransport() {
        return transport;
    }

    @JsonIgnore
    public TransportStrategy getTransportStrategy() {
        switch (transport) {
            case "TLS", "SSL":
                return TransportStrategy.SMTP_TLS;
            case DEFAULT_TRANSPORT:
            default:
                return TransportStrategy.SMTP;
        }
    }

    @Override
    public String toString() {
        return "SmtpConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", transport='" + transport + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
