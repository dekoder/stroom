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

package stroom.dashboard.client.table;

import stroom.dashboard.client.table.ColumnFunctionEditorPresenter.ColumnFunctionEditorView;
import stroom.editor.client.presenter.EditorView;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * This is the view for editing the function expression in a dashboard column
 */
public class ColumnFunctionEditorViewImpl extends ViewImpl implements ColumnFunctionEditorView {

    private final Widget widget;

    @UiField
    SimplePanel editorContainer;
    @UiField
    SimplePanel queryHelpContainer;

    @Inject
    public ColumnFunctionEditorViewImpl(final Binder binder) {

        widget = binder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setEditor(final EditorView editor) {
        this.editorContainer.setWidget(editor.asWidget());
    }

    @Override
    public void setQueryHelp(final View view) {
        queryHelpContainer.setWidget(view.asWidget());
    }

    public interface Binder extends UiBinder<Widget, ColumnFunctionEditorViewImpl> {

    }
}
