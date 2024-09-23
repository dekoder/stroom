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

import stroom.security.client.presenter.UserEditPresenter.UserEditView;
import stroom.security.client.presenter.UserEditUiHandlers;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public final class UserEditViewImpl extends ViewWithUiHandlers<UserEditUiHandlers> implements UserEditView {

    private final Widget widget;
    @UiField
    SimplePanel userGroups;
    @UiField
    SimplePanel appPermissions;

    @Inject
    public UserEditViewImpl(final EventBus eventBus, final Binder binder) {
        widget = binder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setUserGroupsView(View view) {
        userGroups.setWidget(view.asWidget());
    }

    @Override
    public void setAppPermissionsView(View view) {
        appPermissions.setWidget(view.asWidget());
    }

    public interface Binder extends UiBinder<Widget, UserEditViewImpl> {

    }
}
