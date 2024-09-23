package stroom.contentindex;

import stroom.docref.DocContentHighlights;
import stroom.docref.DocContentMatch;
import stroom.docstore.api.ContentIndex;
import stroom.explorer.shared.FetchHighlightsRequest;
import stroom.explorer.shared.FindInContentRequest;
import stroom.index.impl.ContentIndexConfig;
import stroom.util.entityevent.EntityAction;
import stroom.util.entityevent.EntityEvent;
import stroom.util.entityevent.EntityEventHandler;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;
import stroom.util.shared.ResultPage;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

@Singleton
@EntityEventHandler(
        action = {EntityAction.CREATE, EntityAction.UPDATE, EntityAction.DELETE})
public class ContentIndexImpl implements ContentIndex, EntityEvent.Handler {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(ContentIndexImpl.class);

    private final ContentIndex contentIndex;
    private final EntityEvent.Handler handler;


    @Inject
    public ContentIndexImpl(final ContentIndexConfig contentIndexConfig,
                            final Provider<LuceneContentIndex> luceneContentIndexProvider,
                            final Provider<BasicContentIndex> basicContentIndexProvider) {

        if (contentIndexConfig.isEnabled()) {
            final LuceneContentIndex index = luceneContentIndexProvider.get();
            handler = index;
            contentIndex = index;
            contentIndex.reindex();
        } else {
            handler = event -> {
            };
            contentIndex = basicContentIndexProvider.get();
        }
    }

    @Override
    public ResultPage<DocContentMatch> findInContent(final FindInContentRequest request) {
        return LOGGER.logDurationIfInfoEnabled(() ->
                contentIndex.findInContent(request), "findInContent");
    }

    @Override
    public DocContentHighlights fetchHighlights(final FetchHighlightsRequest request) {
        return LOGGER.logDurationIfInfoEnabled(() ->
                contentIndex.fetchHighlights(request), "fetchHighlights");
    }

    @Override
    public void reindex() {
        contentIndex.reindex();
    }

    @Override
    public void onChange(final EntityEvent event) {
        handler.onChange(event);
    }
}
