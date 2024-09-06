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

package stroom.security.client.view;

import stroom.item.client.SelectionBox;
import stroom.security.client.presenter.DocumentPermissionsPresenter;
import stroom.security.shared.ChangeDocumentPermissionsRequest.Cascade;
import stroom.svg.shared.SvgImage;
import stroom.widget.button.client.Button;
import stroom.widget.form.client.FormGroup;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.ViewImpl;

public final class DocumentPermissionsViewImpl extends ViewImpl
        implements DocumentPermissionsPresenter.DocumentPermissionsView {

    private final Widget widget;
    @UiField
    SimplePanel tabs;
    @UiField
    Button copyPermissionsFromParentButton;
    @UiField
    FormGroup cascadeGrid;
    @UiField
    SelectionBox<Cascade> cascade;

    @Inject
    public DocumentPermissionsViewImpl(final Binder binder) {
        widget = binder.createAndBindUi(this);
        copyPermissionsFromParentButton.setIcon(SvgImage.COPY);
        cascade.addItem(Cascade.NO);
        cascade.addItem(Cascade.CHANGES_ONLY);
        cascade.addItem(Cascade.ALL);
        cascade.setValue(Cascade.NO);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setTabsView(View view) {
        tabs.setWidget(view.asWidget());
    }

    @Override
    public SelectionBox<Cascade> getCascade() {
        return cascade;
    }

    @Override
    public Button getCopyPermissionsFromParentButton() {
        return copyPermissionsFromParentButton;
    }

    @Override
    public void setCascadeVisible(boolean visible) {
        cascadeGrid.setVisible(visible);
    }

    public interface Binder extends UiBinder<Widget, DocumentPermissionsViewImpl> {

    }
}
