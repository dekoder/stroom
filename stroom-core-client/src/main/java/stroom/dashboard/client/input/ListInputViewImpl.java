/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.dashboard.client.input;

import stroom.dashboard.client.input.ListInputPresenter.ListInputView;
import stroom.item.client.SelectionBox;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import java.util.List;

public class ListInputViewImpl extends ViewWithUiHandlers<ListInputUiHandlers> implements ListInputView {

    private final Widget widget;

    @UiField
    SelectionBox<String> value;

    @Inject
    public ListInputViewImpl(final Binder binder) {
        widget = binder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setValues(final List<String> values) {
        final String selected = value.getValue();
        value.clear();
        if (values != null) {
            value.addItem("");
            value.addItems(values);
        }
        value.setValue(selected);
    }

    @Override
    public void setSelectedValue(final String selected) {
        this.value.setValue(selected);
    }

    @Override
    public String getSelectedValue() {
        return value.getText();
    }

    @Override
    public void setAllowTextEntry(final boolean allowTextEntry) {
        value.setAllowTextEntry(allowTextEntry);
    }

    @UiHandler("value")
    public void onSelectionChange(final ValueChangeEvent<String> event) {
        if (getUiHandlers() != null) {
            getUiHandlers().onValueChanged(value.getText());
        }
    }

    public interface Binder extends UiBinder<Widget, ListInputViewImpl> {

    }
}
