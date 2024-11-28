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

package stroom.security.client.presenter;

import stroom.content.client.presenter.ContentTabPresenter;
import stroom.dispatch.client.RestFactory;
import stroom.query.api.v2.ExpressionTerm;
import stroom.query.api.v2.ExpressionTerm.Condition;
import stroom.security.client.presenter.UserAndGroupsPresenter.UserAndGroupsView;
import stroom.security.client.presenter.UserListPresenter.Mode;
import stroom.security.shared.User;
import stroom.security.shared.UserFields;
import stroom.security.shared.UserResource;
import stroom.svg.client.SvgPresets;
import stroom.svg.shared.SvgImage;
import stroom.ui.config.client.UiConfigCache;
import stroom.util.shared.GwtNullSafe;
import stroom.util.shared.ResultPage;
import stroom.util.shared.UserRef;
import stroom.util.shared.string.CaseType;
import stroom.widget.button.client.ButtonView;
import stroom.widget.util.client.MouseUtil;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.View;

import java.util.List;
import java.util.Optional;

public class UserAndGroupsPresenter extends ContentTabPresenter<UserAndGroupsView> {

    private static final UserResource USER_RESOURCE = GWT.create(UserResource.class);

    private final UserListPresenter userList;
    private final UserListPresenter parentsList;
    private final UserListPresenter childrenList;

    private final CreateUserPresenter createUserPresenter;
    private final Provider<CreateNewGroupPresenter> createNewGroupPresenterProvider;
    private final Provider<UserRefPopupPresenter> userRefPopupPresenterProvider;
    private final RestFactory restFactory;

    private ButtonView createButton = null;
    private ButtonView editButton = null;
    private ButtonView deleteButton = null;

    private ButtonView addMemberOfButton = null;
    private ButtonView removeMemberOfButton = null;

    private ButtonView addMembersInButton = null;
    private ButtonView removeMembersInButton = null;

    @Inject
    public UserAndGroupsPresenter(final EventBus eventBus,
                                  final UserAndGroupsView view,
                                  final Provider<UserListPresenter> userListPresenterProvider,
                                  final CreateUserPresenter createUserPresenter,
                                  final Provider<CreateNewGroupPresenter> createNewGroupPresenterProvider,
                                  final Provider<UserRefPopupPresenter> userRefPopupPresenterProvider,
                                  final UiConfigCache uiConfigCache,
                                  final RestFactory restFactory) {
        super(eventBus, view);
        this.userList = userListPresenterProvider.get();
        this.userList.setMode(Mode.USERS_AND_GROUPS);
        this.userList.getView().setLabel("Users And Groups");
        this.userList.setName("userList");
        // Top pane doesn't need to link back to itself
        this.userList.setValidUserScreensForActionMenu(UserScreen.allExcept(UserScreen.USERS_AND_GROUPS));

        this.parentsList = userListPresenterProvider.get();
        // A parent can only be a group
        this.parentsList.setMode(Mode.GROUPS_ONLY);
        this.parentsList.setName("parentsList");
        this.parentsList.setValidUserScreensForActionMenu(UserScreen.all());

        this.childrenList = userListPresenterProvider.get();
        this.childrenList.setMode(Mode.USERS_AND_GROUPS);
        this.childrenList.setName("childrenList");
        this.childrenList.setValidUserScreensForActionMenu(UserScreen.all());

        this.createUserPresenter = createUserPresenter;
        this.createNewGroupPresenterProvider = createNewGroupPresenterProvider;
        this.userRefPopupPresenterProvider = userRefPopupPresenterProvider;
        this.restFactory = restFactory;

        view.setUserList(userList.getView());
        view.setParentsView(parentsList.getView());
        view.setChildrenView(childrenList.getView());

        createButton = userList.addButton(SvgPresets.ADD.title("Create Group"));
        editButton = userList.addButton(SvgPresets.EDIT.title("Edit Group"));

        // Only want to let user create a stroom user if they are using an external IDP.
        // If internal then all users are available to be picked from the account tbl.
        uiConfigCache.get(config -> {
            if (config != null) {
                if (config.isExternalIdentityProvider()) {
                    createButton.setTitle("Create User Or Group");
                }
            }
        }, this);

        // You can only delete groups on this screen, UsersPresenter for user deletion
        deleteButton = userList.addButton(SvgPresets.DELETE.title("Delete Group"));

        addMemberOfButton = parentsList.addButton(SvgPresets.ADD);
        removeMemberOfButton = parentsList.addButton(SvgPresets.REMOVE);

        addMembersInButton = childrenList.addButton(SvgPresets.ADD);
        removeMembersInButton = childrenList.addButton(SvgPresets.REMOVE);

        /// Initialise state.
        onSelection();
    }

    @Override
    protected void onBind() {
        super.onBind();
        registerHandler(createButton.addClickHandler(e -> {
            if (MouseUtil.isPrimary(e)) {
                onCreateUserOrGroup();
            }
        }));
        registerHandler(editButton.addClickHandler(e -> {
            if (MouseUtil.isPrimary(e)) {
                onEditUserOrGroup();
            }
        }));
        registerHandler(deleteButton.addClickHandler(e -> {
            if (MouseUtil.isPrimary(e)) {
                onDelete();
            }
        }));
        registerHandler(userList.getSelectionModel().addSelectionHandler(e -> {
            onSelection();
        }));

        // Deal with Member Of Groups pane.
        registerHandler(parentsList.getSelectionModel().addSelectionHandler(e -> {
            final User user = userList.getSelectionModel().getSelected();
            final User group = parentsList.getSelectionModel().getSelected();
            removeMemberOfButton.setEnabled(group != null && user != null);
        }));

        registerHandler(addMemberOfButton.addClickHandler(e -> {
            if (MouseUtil.isPrimary(e)) {
                final User user = userList.getSelectionModel().getSelected();
                if (user != null) {
                    final UserRefPopupPresenter userRefPopupPresenter = userRefPopupPresenterProvider.get();
                    userRefPopupPresenter.setAdditionalTerm(ExpressionTerm
                            .builder()
                            .field(UserFields.IS_GROUP.getFldName())
                            .condition(Condition.EQUALS)
                            .value("true")
                            .build());

                    userRefPopupPresenter.show("Select Group", groupRef -> {
                        if (groupRef != null) {
                            addUserToGroup(user.asRef(), groupRef, parentsList, groupRef);
                        }
                    });
                }
            }
        }));

        registerHandler(removeMemberOfButton.addClickHandler(e -> {
            if (MouseUtil.isPrimary(e)) {
                final User user = userList.getSelectionModel().getSelected();
                final User group = parentsList.getSelectionModel().getSelected();
                removeUserFromGroup(user, group, parentsList);
            }
        }));

        // Deal with Members in Group pane.
        registerHandler(childrenList.getSelectionModel().addSelectionHandler(e -> {
            final User group = userList.getSelectionModel().getSelected();
            final User user = childrenList.getSelectionModel().getSelected();
            removeMembersInButton.setEnabled(group != null && user != null);
        }));

        registerHandler(addMembersInButton.addClickHandler(e -> {
            if (MouseUtil.isPrimary(e)) {
                final User group = userList.getSelectionModel().getSelected();
                if (group != null) {
                    final UserRefPopupPresenter userRefPopupPresenter = userRefPopupPresenterProvider.get();
                    userRefPopupPresenter.show("Add User Or Group", userRef -> {
                        if (userRef != null) {
                            addUserToGroup(userRef, group.asRef(), childrenList, userRef);
                        }
                    });
                }
            }
        }));

        registerHandler(removeMembersInButton.addClickHandler(e -> {
            if (MouseUtil.isPrimary(e)) {
                final User group = userList.getSelectionModel().getSelected();
                final User user = childrenList.getSelectionModel().getSelected();
                removeUserFromGroup(user, group, childrenList);
            }
        }));
    }

    private void onSelection() {
        final User selected = userList.getSelectionModel().getSelected();
//        GWT.log("onSelection - selected: " + selected);
        if (selected != null) {
            parentsList.setAdditionalTerm(ExpressionTerm
                    .builder()
                    .field(UserFields.PARENTS_OF.getFldName())
                    .condition(Condition.EQUALS)
                    .value(selected.getUuid())
                    .build());
            parentsList.refresh();

            final StringBuilder parentLabel = new StringBuilder()
                    .append(selected.getType(CaseType.SENTENCE))
                    .append(" \"")
                    .append(selected.getDisplayName())
                    .append("\" is a member of:");
            parentsList.getView().setLabel(parentLabel.toString());
            getView().setParentsVisible(true);

            childrenList.setAdditionalTerm(ExpressionTerm
                    .builder()
                    .field(UserFields.CHILDREN_OF.getFldName())
                    .condition(Condition.EQUALS)
                    .value(selected.getUuid())
                    .build());
            childrenList.refresh();

            final StringBuilder childLabel = new StringBuilder();
            if (selected.isGroup()) {
                childLabel.append("Members of group \"");
                childLabel.append(selected.getDisplayName());
                childLabel.append("\":");
            } else {
                childLabel.append("No group selected");
            }

            childrenList.getView().setLabel(childLabel.toString());
            getView().setParentsVisible(true);
            getView().setChildrenVisible(selected.isGroup());

            if (selected.isGroup()) {
                editButton.setTitle("Edit " + getDescription(selected));
                editButton.setEnabled(true);
                deleteButton.setTitle("Delete " + getDescription(selected));
                deleteButton.setEnabled(true);
            } else {
                editButton.setTitle("User editing is not supported");
                editButton.setEnabled(false);
                deleteButton.setTitle("Users can only be deleted on the Users screen");
                deleteButton.setEnabled(false);
            }
            addMemberOfButton.setEnabled(true);
            addMembersInButton.setEnabled(selected.isGroup());

        } else {
            parentsList.getView().setLabel("No Selection");
            childrenList.getView().setLabel("No Selection");
            getView().setParentsVisible(false);
            getView().setChildrenVisible(false);
            editButton.setTitle("No Selection");
            editButton.setEnabled(false);
            deleteButton.setTitle("No Selection");
            deleteButton.setEnabled(false);
            addMemberOfButton.setEnabled(false);
            addMembersInButton.setEnabled(false);
        }
    }

    private void addUserToGroup(final UserRef user,
                                final UserRef group,
                                final UserListPresenter userListPresenter,
                                final UserRef selection) {
        if (group != null && user != null) {
            restFactory
                    .create(USER_RESOURCE)
                    .method(res -> res.addUserToGroup(user.getUuid(), group.getUuid()))
                    .onSuccess(r -> {

                        // Add a consumer to monitor the new result list when there is a refresh.
                        // This allows us to try to select the newly added item.
                        userListPresenter.setResultPageConsumer(resultPage -> {
                            userListPresenter.setResultPageConsumer(null);
                            if (selection == null) {
                                userListPresenter.getSelectionModel().clear();
                            } else {
                                if (resultPage != null && resultPage.getValues() != null) {
                                    final Optional<User> optional = resultPage
                                            .getValues()
                                            .stream()
                                            .filter(u -> u.getUuid().equals(selection.getUuid()))
                                            .findAny();
                                    if (optional.isPresent()) {
                                        userListPresenter.getSelectionModel().setSelected(optional.get());
                                    } else {
                                        userListPresenter.getSelectionModel().clear();
                                    }
                                }
                            }
                        });

                        userList.refresh();
                        parentsList.refresh();
                        childrenList.refresh();
                    })
                    .taskMonitorFactory(userListPresenter.getPagerView())
                    .exec();
        }
    }

    private void removeUserFromGroup(final User user,
                                     final User group,
                                     final UserListPresenter userListPresenter) {
        if (GwtNullSafe.allNonNull(user, group)) {
            final User nextSelection = getNextSelection(userListPresenter);

            restFactory
                    .create(USER_RESOURCE)
                    .method(res -> res.removeUserFromGroup(user.getUuid(), group.getUuid()))
                    .onSuccess(r -> {
                        userList.refresh();
                        parentsList.refresh();
                        childrenList.refresh();

                        if (nextSelection == null) {
                            userListPresenter.getSelectionModel().clear();
                        } else {
                            userListPresenter.getSelectionModel().setSelected(nextSelection);
                        }
                    })
                    .taskMonitorFactory(userListPresenter.getPagerView())
                    .exec();
        }
    }

    private User getNextSelection(final UserListPresenter userListPresenter) {
        final User currentSelection = userListPresenter.getSelectionModel().getSelected();
        User nextSelection = null;
        final ResultPage<User> resultPage = userListPresenter.getCurrentData();
        if (resultPage != null) {
            final List<User> list = resultPage.getValues();
            if (currentSelection != null && list != null) {
                final int index = list.indexOf(currentSelection);
                if (index == 0) {
                    if (list.size() > 1) {
                        nextSelection = list.get(index + 1);
                    }
                } else if (index > 0) {
                    nextSelection = list.get(index - 1);
                }
            }
        }
        return nextSelection;
    }

    private void onCreateUserOrGroup() {
        createUserPresenter.show(
                UserAndGroupHelper.createAfterChangeConsumer(userList),
                true);
    }

    private void onEditUserOrGroup() {
        UserAndGroupHelper.onEditUserOrGroup(userList, createNewGroupPresenterProvider, this);
    }

    private void onDelete() {
        UserAndGroupHelper.onDelete(userList, restFactory, this);
    }

    private static String getDescription(final User user) {
        return user.getType(CaseType.LOWER)
               + " '" + user.asRef().toDisplayString() + "'";
    }

    public void refresh() {
        userList.refresh();
        onSelection();
    }

//    public void show() {
//        userList.refresh();
//
//        final PopupSize popupSize = PopupSize.builder()
//                .width(Size
//                        .builder()
//                        .initial(800)
//                        .min(400)
//                        .resizable(true)
//                        .build())
//                .height(Size
//                        .builder()
//                        .initial(800)
//                        .min(400)
//                        .resizable(true)
//                        .build())
//                .build();
//        final String caption = "User Groups";
//        ShowPopupEvent.builder(UserAndGroupsPresenter.this)
//                .popupType(PopupType.CLOSE_DIALOG)
//                .popupSize(popupSize)
//                .caption(caption)
//                .onShow(e -> userList.getView().focus())
//                .fire();
//    }

    @Override
    public SvgImage getIcon() {
        return SvgImage.USERS;
    }

    @Override
    public String getLabel() {
        return "Users And Groups";
    }

    @Override
    public String getType() {
        return "UsersAndGroups";
    }

    public void showUser(final UserRef userRef) {
        userList.setResultPageConsumer(userResultPage -> {
            onSelection();
        });
        userList.showUser(userRef);
    }


    // --------------------------------------------------------------------------------


    public interface UserAndGroupsView extends View {

        void setUserList(View view);

        void setParentsView(View view);

        void setParentsVisible(boolean visible);

        void setChildrenView(View view);

        void setChildrenVisible(boolean visible);
    }
}
