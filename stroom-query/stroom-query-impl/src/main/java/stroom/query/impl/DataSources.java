package stroom.query.impl;

import stroom.docref.DocRef;
import stroom.docref.StringMatch.MatchType;
import stroom.explorer.api.ExplorerActionHandler;
import stroom.query.common.v2.DataSourceProviderRegistry;
import stroom.query.shared.CompletionItem;
import stroom.query.shared.CompletionValue;
import stroom.query.shared.CompletionsRequest;
import stroom.query.shared.InsertType;
import stroom.query.shared.QueryHelpDataSource;
import stroom.query.shared.QueryHelpDetail;
import stroom.query.shared.QueryHelpRow;
import stroom.query.shared.QueryHelpType;
import stroom.svg.shared.SvgImage;
import stroom.util.shared.PageRequest;
import stroom.util.shared.ResultPage.ResultConsumer;
import stroom.util.string.StringMatcher;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class DataSources {

    private static final String DATA_SOURCE_ID = "data_source";
    private static final QueryHelpRow ROOT = QueryHelpRow
            .builder()
            .type(QueryHelpType.TITLE)
            .id(DATA_SOURCE_ID)
            .hasChildren(true)
            .title("Data Sources")
            .build();

    private final Provider<DataSourceProviderRegistry> dataSourceProviderRegistryProvider;
    private final Map<String, SvgImage> icons;

    @Inject
    DataSources(final Set<ExplorerActionHandler> explorerActionHandlers,
                final Provider<DataSourceProviderRegistry> dataSourceProviderRegistryProvider) {
        this.dataSourceProviderRegistryProvider = dataSourceProviderRegistryProvider;
        icons = explorerActionHandlers
                .stream()
                .collect(Collectors
                        .toMap(c -> c.getDocumentType().getType(),
                                c -> c.getDocumentType().getIcon()));
    }

    public void addRows(final PageRequest pageRequest,
                        final String parentPath,
                        final StringMatcher stringMatcher,
                        final ResultConsumer<QueryHelpRow> resultConsumer) {
        if (parentPath.isBlank()) {
            final boolean hasChildren = hasChildren(stringMatcher);
            if (hasChildren ||
                    MatchType.ANY.equals(stringMatcher.getMatchType()) ||
                    stringMatcher.match(ROOT.getTitle()).isPresent()) {
                resultConsumer.add(ROOT.copy().hasChildren(hasChildren).build());
            }
        } else if (parentPath.startsWith(DATA_SOURCE_ID + ".")) {
            final DataSourceProviderRegistry dataSourceProviderRegistry =
                    dataSourceProviderRegistryProvider.get();
            final ResultPageBuilder<QueryHelpRow> builder =
                    new ResultPageBuilder<>(pageRequest, Comparator.comparing(QueryHelpRow::getTitle));
            for (final DocRef docRef : dataSourceProviderRegistry.list()) {
                if (stringMatcher.match(docRef.getDisplayValue()).isPresent()) {
                    final QueryHelpRow row = QueryHelpRow
                            .builder()
                            .type(QueryHelpType.DATA_SOURCE)
                            .id(DATA_SOURCE_ID + "." + docRef.getUuid())
                            .icon(getIcon(docRef))
                            .title(docRef.getDisplayValue())
                            .data(new QueryHelpDataSource(docRef))
                            .build();
                    builder.add(row);
                }
            }
            for (final QueryHelpRow row : builder.build().getValues()) {
                if (!resultConsumer.add(row)) {
                    break;
                }
            }
        }
    }

    public void addCompletions(final CompletionsRequest request,
                               final PageRequest pageRequest,
                               final List<CompletionItem> resultList) {
        final DataSourceProviderRegistry dataSourceProviderRegistry =
                dataSourceProviderRegistryProvider.get();
        final StringMatcher stringMatcher = new StringMatcher(request.getStringMatch());
        List<DocRef> sortedList = new ArrayList<>();
        for (final DocRef docRef : dataSourceProviderRegistry.list()) {
            if (stringMatcher.match(docRef.getName()).isPresent()) {
                sortedList.add(docRef);
            }
        }
        sortedList.sort(Comparator.comparing(DocRef::getName));
        for (int i = pageRequest.getOffset();
             i < pageRequest.getOffset() + pageRequest.getLength() && i < sortedList.size();
             i++) {
            resultList.add(createCompletionValue(sortedList.get(i)));
        }
    }

    public Optional<QueryHelpDetail> fetchDetail(final QueryHelpRow row) {
        if (DATA_SOURCE_ID.equals(row.getId())) {
            final InsertType insertType = InsertType.NOT_INSERTABLE;
            final String insertText = null;
            final String documentation =
                    "A list of data sources that can be queried by specifying them in the 'from' clause.";
            return Optional.of(new QueryHelpDetail(insertType, insertText, documentation));

        } else if (QueryHelpType.DATA_SOURCE.equals(row.getType()) &&
                row.getId().startsWith(DATA_SOURCE_ID + ".")) {
            final QueryHelpDataSource dataSource = (QueryHelpDataSource) row.getData();
            final DocRef docRef = dataSource.getDocRef();
            final InsertType insertType = InsertType.plainText(docRef.getName());
            final String insertText = getInsertText(docRef);
            final String documentation = getDetail(docRef);
            return Optional.of(new QueryHelpDetail(insertType, insertText, documentation));
        }

        return Optional.empty();
    }

    private CompletionValue createCompletionValue(final DocRef docRef) {
        final String caption = docRef.getName();
        final String insertText = getInsertText(docRef);
        final String tooltip = getDetail(docRef);
        return new CompletionValue(
                caption,
                insertText,
                500,
                "Data Source",
                tooltip);
    }

    private String getInsertText(final DocRef docRef) {
        return docRef.getName().contains(" ")
                ? "\"" + docRef.getName() + "\""
                : docRef.getName();
    }

    private String getDetail(final DocRef docRef) {
        final DetailBuilder detail = new DetailBuilder();
        detail.title(docRef.getName());
        detail.description(description -> description
                .table(table -> table
                        .appendKVRow("Name:", docRef.getName())
                        .appendKVRow("Type:", docRef.getType())
                        .appendKVRow("UUID:", docRef.getUuid())));

        final DataSourceProviderRegistry dataSourceProviderRegistry =
                dataSourceProviderRegistryProvider.get();
        final Optional<String> documentation = dataSourceProviderRegistry.fetchDocumentation(docRef);
        documentation.ifPresent(detail::append);
        return detail.build();
    }

    private boolean hasChildren(final StringMatcher stringMatcher) {
        final DataSourceProviderRegistry dataSourceProviderRegistry =
                dataSourceProviderRegistryProvider.get();
        for (final DocRef docRef : dataSourceProviderRegistry.list()) {
            if (stringMatcher.match(docRef.getDisplayValue()).isPresent()) {
                return true;
            }
        }
        return false;
    }

    private SvgImage getIcon(final DocRef docRef) {
        SvgImage svgImage = icons.get(docRef.getType());
        if (svgImage == null) {
            svgImage = SvgImage.DOCUMENT_SEARCHABLE;
        }
        return svgImage;
    }
}
