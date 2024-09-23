package stroom.searchable.impl;

import stroom.docref.DocRef;
import stroom.explorer.api.HasDataSourceDocRefs;
import stroom.searchable.api.Searchable;
import stroom.searchable.api.SearchableProvider;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Singleton
class SearchableProviderImpl implements SearchableProvider, HasDataSourceDocRefs {

    private final Set<Searchable> searchables;

    @Inject
    SearchableProviderImpl(final Set<Searchable> searchables) {
        this.searchables = searchables;
    }

    @Override
    public List<DocRef> getDataSourceDocRefs() {
        return list();
    }

    @Override
    public Searchable get(final DocRef docRef) {
        return searchables.stream()
                .filter(searchable -> Objects.equals(searchable.getDocRef(), docRef))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<DocRef> list() {
        return searchables.stream()
                .map(Searchable::getDocRef)
                .filter(Objects::nonNull)
                .toList();
    }
}
