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

import stroom.security.client.presenter.FolderPermissionsTabPresenter;
import stroom.widget.form.client.FormGroup;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.ViewImpl;

public final class FolderPermissionsTabViewImpl extends ViewImpl
        implements FolderPermissionsTabPresenter.FolderPermissionsTabView {

    private final Widget widget;
    @UiField
    SimplePanel users;
    @UiField
    FormGroup folderPermissionsUsers;
    @UiField
    ScrollPanel permissions;
    @UiField
    ScrollPanel createPermissions;

    @Inject
    public FolderPermissionsTabViewImpl(final EventBus eventBus, final Binder binder) {
        widget = binder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setUserView(View view) {
        users.setWidget(view.asWidget());
    }

    @Override
    public void setPermissionsView(View view) {
        permissions.setWidget(view.asWidget());
    }

    @Override
    public void setUsersLabelText(String text) {
        folderPermissionsUsers.setLabel(text);
    }

    @Override
    public void setCreatePermissionsView(View view) {
        createPermissions.setWidget(view.asWidget());
    }


    // --------------------------------------------------------------------------------


    public interface Binder extends UiBinder<Widget, FolderPermissionsTabViewImpl> {

    }
}
