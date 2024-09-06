package stroom.index.impl;

import stroom.config.common.AbstractDbConfig;
import stroom.config.common.ConnectionConfig;
import stroom.config.common.ConnectionPoolConfig;
import stroom.config.common.HasDbConfig;
import stroom.util.shared.AbstractConfig;
import stroom.util.shared.BootStrapConfig;
import stroom.util.shared.IsStroomConfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(alphabetic = true)
public class IndexFieldDbConfig extends AbstractConfig implements IsStroomConfig, HasDbConfig {

    private final IndexDatasourceDbConfig dbConfig;

    public IndexFieldDbConfig() {
        dbConfig = new IndexDatasourceDbConfig();
    }

    @JsonCreator
    public IndexFieldDbConfig(@JsonProperty("db") final IndexDatasourceDbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    @JsonProperty("db")
    public IndexDatasourceDbConfig getDbConfig() {
        return dbConfig;
    }

    @BootStrapConfig
    public static class IndexDatasourceDbConfig extends AbstractDbConfig {

        public IndexDatasourceDbConfig() {
            super();
        }

        @SuppressWarnings("unused")
        @JsonCreator
        public IndexDatasourceDbConfig(
                @JsonProperty(PROP_NAME_CONNECTION) final ConnectionConfig connectionConfig,
                @JsonProperty(PROP_NAME_CONNECTION_POOL) final ConnectionPoolConfig connectionPoolConfig) {
            super(connectionConfig, connectionPoolConfig);
        }
    }
}
