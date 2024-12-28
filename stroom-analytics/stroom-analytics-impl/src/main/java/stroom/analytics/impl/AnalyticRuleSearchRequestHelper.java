package stroom.analytics.impl;

import stroom.analytics.shared.AbstractAnalyticRuleDoc;
import stroom.expression.api.DateTimeSettings;
import stroom.query.api.v2.Query;
import stroom.query.api.v2.QueryKey;
import stroom.query.api.v2.SearchRequest;
import stroom.query.api.v2.SearchRequestSource;
import stroom.query.api.v2.SearchRequestSource.SourceType;
import stroom.query.common.v2.ExpressionContextFactory;
import stroom.query.language.SearchRequestFactory;
import stroom.query.language.functions.ExpressionContext;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;
import stroom.util.logging.LogUtil;

import jakarta.inject.Inject;

public class AnalyticRuleSearchRequestHelper {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(AnalyticRuleSearchRequestHelper.class);

    private final SearchRequestFactory searchRequestFactory;
    private final ExpressionContextFactory expressionContextFactory;

    @Inject
    public AnalyticRuleSearchRequestHelper(final SearchRequestFactory searchRequestFactory,
                                           final ExpressionContextFactory expressionContextFactory) {
        this.searchRequestFactory = searchRequestFactory;
        this.expressionContextFactory = expressionContextFactory;
    }

    public SearchRequest create(final AbstractAnalyticRuleDoc doc) {
        try {
            // Map the rule query
            Query sampleQuery = Query.builder().build();
            final QueryKey queryKey = new QueryKey(doc.getUuid() +
                                                   " - " +
                                                   doc.getName());
            SearchRequest sampleRequest = new SearchRequest(
                    SearchRequestSource.builder().sourceType(SourceType.TABLE_BUILDER_ANALYTIC).build(),
                    queryKey,
                    sampleQuery,
                    null,
                    DateTimeSettings.builder().build(),
                    false);
            final ExpressionContext expressionContext = expressionContextFactory.createContext(sampleRequest);
            return searchRequestFactory
                    .create(doc.getQuery(), sampleRequest, expressionContext);
        } catch (final RuntimeException e) {
            LOGGER.debug(() ->
                    LogUtil.message(
                            "Error creating search request for analytic rule - {}",
                            RuleUtil.getRuleIdentity(doc)), e);
            throw e;
        }
    }
}
