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

package stroom.dashboard.client.embeddedquery;

import stroom.dashboard.client.main.SettingsPresenter;
import stroom.dashboard.client.query.BasicQuerySettingsPresenter;
import stroom.dashboard.client.query.SelectionHandlersPresenter;
import stroom.widget.tab.client.presenter.LinkTabsLayoutView;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class EmbeddedQuerySettingsPresenter extends SettingsPresenter {

    @Inject
    public EmbeddedQuerySettingsPresenter(final EventBus eventBus, final LinkTabsLayoutView view,
                                          final BasicEmbeddedQuerySettingsPresenter basicSettingsPresenter,
                                          final SelectionHandlersPresenter selectionHandlersPresenter) {
        super(eventBus, view);
        getView().asWidget().addStyleName("settingsPresenter");

//        selectionHandlersPresenter.setBasicQuerySettingsPresenter(basicSettingsPresenter);

        addTab("Basic", basicSettingsPresenter);
//        addTab("Selection Handlers", selectionHandlersPresenter);
    }

    @Override
    public boolean validate() {
        return super.validate();
    }
}
