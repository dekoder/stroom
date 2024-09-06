package stroom.query.common.v2;

import stroom.datasource.api.v2.DataSourceProvider;
import stroom.datasource.api.v2.FindFieldCriteria;
import stroom.datasource.api.v2.QueryField;
import stroom.docref.DocRef;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;
import stroom.util.shared.ResultPage;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@SuppressWarnings("unused")
public class DataSourceProviderRegistry {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(DataSourceProviderRegistry.class);
    private final Map<String, DataSourceProvider> dataSourceProviders = new ConcurrentHashMap<>();

    @Inject
    public DataSourceProviderRegistry(final Set<DataSourceProvider> providers) {
        for (final DataSourceProvider provider : providers) {
            dataSourceProviders.put(provider.getType(), provider);
        }
    }

    public Optional<DataSourceProvider> getDataSourceProvider(final String type) {
        return Optional.ofNullable(dataSourceProviders.get(type));
    }

    public DocRef fetchDefaultExtractionPipeline(final DocRef dataSourceRef) {
        return getDataSourceProvider(dataSourceRef.getType())
                .map(dsp -> dsp.fetchDefaultExtractionPipeline(dataSourceRef))
                .orElse(null);
    }

    public ResultPage<QueryField> getFieldInfo(final FindFieldCriteria criteria) {
        try {
            return getDataSourceProvider(criteria.getDataSourceRef().getType())
                    .map(dsp -> dsp.getFieldInfo(criteria))
                    .orElseGet(() -> ResultPage.createCriterialBasedList(Collections.emptyList(), criteria));
        } catch (final RuntimeException e) {
            LOGGER.debug(e.getMessage(), e);
            return ResultPage.createCriterialBasedList(Collections.emptyList(), criteria);
        }
    }

    public Optional<String> fetchDocumentation(final DocRef docRef) {
        return getDataSourceProvider(docRef.getType()).flatMap(dsp -> dsp.fetchDocumentation(docRef));
    }

    public Set<String> getTypes() {
        return dataSourceProviders.keySet();
    }

    public List<DocRef> list() {
        return dataSourceProviders.values().stream().map(DataSourceProvider::list).flatMap(List::stream).toList();
    }
}
