/*
 * Copyright 2022 Crown Copyright
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
 *
 */

package stroom.analytics.client.presenter;

import stroom.analytics.client.presenter.DuplicateManagementPresenter.DuplicateManagementView;
import stroom.analytics.shared.AnalyticRuleDoc;
import stroom.analytics.shared.DuplicateNotificationConfig;
import stroom.docref.DocRef;
import stroom.document.client.event.DirtyUiHandlers;
import stroom.entity.client.presenter.DocumentEditPresenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;

import java.util.ArrayList;
import java.util.List;

public class DuplicateManagementPresenter
        extends DocumentEditPresenter<DuplicateManagementView, AnalyticRuleDoc>
        implements DirtyUiHandlers {

    private final DuplicateManagementListPresenter duplicateManagementListPresenter;

    @Inject
    public DuplicateManagementPresenter(final EventBus eventBus,
                                        final DuplicateManagementView view,
                                        final DuplicateManagementListPresenter duplicateManagementListPresenter) {
        super(eventBus, view);
        view.setUiHandlers(this);
        this.duplicateManagementListPresenter = duplicateManagementListPresenter;
        view.setListView(duplicateManagementListPresenter.getView());
    }

    @Override
    public void onDirty() {
        setDirty(true);
    }

    @Override
    protected void onRead(final DocRef docRef, final AnalyticRuleDoc document, final boolean readOnly) {
        final DuplicateNotificationConfig duplicateNotificationConfig = document.getDuplicateNotificationConfig();
        getView().setRememberNotifications(duplicateNotificationConfig.isRememberNotifications());
        getView().setSuppressDuplicateNotifications(duplicateNotificationConfig.isSuppressDuplicateNotifications());
        getView().setChooseColumns(duplicateNotificationConfig.isChooseColumns());
        getView().setColumns(String.join(", ", duplicateNotificationConfig.getColumnNames()));
        duplicateManagementListPresenter.read(docRef);
    }

    @Override
    protected AnalyticRuleDoc onWrite(final AnalyticRuleDoc document) {
        final String[] arr = getView().getColumns().split(",");
        final List<String> columns = new ArrayList<>(arr.length);
        for (final String col : arr) {
            final String trimmed = col.trim();
            if (trimmed.length() > 0) {
                columns.add(trimmed);
            }
        }
        final DuplicateNotificationConfig duplicateNotificationConfig =
                new DuplicateNotificationConfig(getView().isRememberNotifications(),
                        getView().isSuppressDuplicateNotifications(),
                        getView().isChooseColumns(),
                        columns);
        return document
                .copy()
                .duplicateNotificationConfig(duplicateNotificationConfig)
                .build();
    }

    public interface DuplicateManagementView extends View, HasUiHandlers<DirtyUiHandlers> {

        void setRememberNotifications(boolean rememberNotifications);

        boolean isRememberNotifications();

        void setSuppressDuplicateNotifications(boolean suppressDuplicateNotifications);

        boolean isSuppressDuplicateNotifications();

        void setChooseColumns(boolean chooseColumns);

        boolean isChooseColumns();

        void setColumns(String columns);

        String getColumns();

        void setListView(View view);
    }
}
