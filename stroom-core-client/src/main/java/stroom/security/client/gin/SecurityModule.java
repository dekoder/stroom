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

package stroom.security.client.gin;

import stroom.core.client.gin.PluginModule;
import stroom.security.client.ApiKeysPlugin;
import stroom.security.client.CurrentUser;
import stroom.security.client.LoginManager;
import stroom.security.client.LogoutPlugin;
import stroom.security.client.ManageUserPlugin;
import stroom.security.client.api.ClientSecurityContext;
import stroom.security.client.presenter.ApiKeysPresenter;
import stroom.security.client.presenter.CreateMultipleUsersPresenter;
import stroom.security.client.presenter.CreateNewUserPresenter;
import stroom.security.client.presenter.DocumentPermissionsPresenter;
import stroom.security.client.presenter.DocumentPermissionsTabPresenter;
import stroom.security.client.presenter.EditApiKeyPresenter;
import stroom.security.client.presenter.FolderPermissionsTabPresenter;
import stroom.security.client.presenter.GroupEditPresenter;
import stroom.security.client.presenter.PermissionsListPresenter;
import stroom.security.client.presenter.PermissionsListPresenter.PermissionsListView;
import stroom.security.client.presenter.UserEditPresenter;
import stroom.security.client.presenter.UserListView;
import stroom.security.client.view.ApiKeysViewImpl;
import stroom.security.client.view.CreateMultipleUsersViewImpl;
import stroom.security.client.view.CreateNewUserViewImpl;
import stroom.security.client.view.DocumentPermissionsTabViewImpl;
import stroom.security.client.view.DocumentPermissionsViewImpl;
import stroom.security.client.view.EditApiKeyViewImpl;
import stroom.security.client.view.FolderPermissionsTabViewImpl;
import stroom.security.client.view.PermissionsListViewImpl;
import stroom.security.client.view.UserEditViewImpl;
import stroom.security.client.view.UserGroupEditViewImpl;
import stroom.security.client.view.UserListViewImpl;

import com.google.inject.Singleton;

public class SecurityModule extends PluginModule {

    @Override
    protected void configure() {
        bind(ClientSecurityContext.class).to(CurrentUser.class).in(Singleton.class);

        bind(LoginManager.class).in(Singleton.class);

        bindPlugin(LogoutPlugin.class);

        // Users
        bindPlugin(ManageUserPlugin.class);
        bindPlugin(ApiKeysPlugin.class);
        bindSharedView(UserListView.class, UserListViewImpl.class);
        bindSharedView(UserEditPresenter.UserEditView.class, UserEditViewImpl.class);
        bindSharedView(GroupEditPresenter.UserGroupEditView.class, UserGroupEditViewImpl.class);
        bindSharedView(CreateNewUserPresenter.CreateNewUserView.class, CreateNewUserViewImpl.class);
        bindSharedView(CreateMultipleUsersPresenter.CreateMultipleUsersView.class, CreateMultipleUsersViewImpl.class);

        bindPresenterWidget(PermissionsListPresenter.class,
                PermissionsListView.class,
                PermissionsListViewImpl.class);
        bindPresenterWidget(DocumentPermissionsPresenter.class,
                DocumentPermissionsPresenter.DocumentPermissionsView.class,
                DocumentPermissionsViewImpl.class);
        bindPresenterWidget(DocumentPermissionsTabPresenter.class,
                DocumentPermissionsTabPresenter.DocumentPermissionsTabView.class,
                DocumentPermissionsTabViewImpl.class);
        bindPresenterWidget(FolderPermissionsTabPresenter.class,
                FolderPermissionsTabPresenter.FolderPermissionsTabView.class,
                FolderPermissionsTabViewImpl.class);

        bindPresenterWidget(ApiKeysPresenter.class,
                ApiKeysPresenter.ApiKeysView.class,
                ApiKeysViewImpl.class);
        bindPresenterWidget(EditApiKeyPresenter.class,
                EditApiKeyPresenter.EditApiKeyView.class,
                EditApiKeyViewImpl.class);
    }
}
