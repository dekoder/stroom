package stroom.query.client.presenter;

import stroom.datasource.api.v2.QueryField;
import stroom.item.client.AbstractSelectionListModel;

import java.util.function.Consumer;

public class SimpleFieldSelectionListModel
        extends AbstractSelectionListModel<QueryField, FieldInfoSelectionItem>
        implements FieldSelectionListModel {

    @Override
    public void findFieldByName(final String fieldName, final Consumer<QueryField> consumer) {
        if (fieldName != null) {
            items.stream()
                    .filter(fieldInfo -> fieldInfo.getLabel().equals(fieldName))
                    .findFirst()
                    .ifPresent(item -> consumer.accept(item.getField()));
        } else {
            items.stream()
                    .findFirst()
                    .ifPresent(item -> consumer.accept(item.getField()));
        }
    }

    @Override
    public FieldInfoSelectionItem wrap(final QueryField item) {
        return new FieldInfoSelectionItem(item);
    }

    @Override
    public QueryField unwrap(final FieldInfoSelectionItem selectionItem) {
        if (selectionItem == null) {
            return null;
        }
        return selectionItem.getField();
    }
}
