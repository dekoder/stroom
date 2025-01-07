/*
 * Copyright 2024 Crown Copyright
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

package stroom.analytics.client.presenter;

import stroom.analytics.shared.AnalyticProcessType;
import stroom.analytics.shared.QueryLanguageVersion;
import stroom.analytics.shared.ReportDoc;
import stroom.explorer.client.presenter.DocSelectionBoxPresenter;
import stroom.ui.config.client.UiConfigCache;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class ReportProcessingPresenter
        extends AbstractProcessingPresenter<ReportDoc> {


    @Inject
    public ReportProcessingPresenter(final EventBus eventBus,
                                     final AnalyticProcessingView view,
                                     final DocSelectionBoxPresenter errorFeedPresenter,
                                     final ScheduledProcessingPresenter scheduledProcessingPresenter,
                                     final TableBuilderProcessingPresenter tableBuilderProcessingPresenter,
                                     final StreamingProcessingPresenter streamingProcessingPresenter,
                                     final UiConfigCache uiConfigCache) {
        super(eventBus,
                view,
                errorFeedPresenter,
                scheduledProcessingPresenter,
                tableBuilderProcessingPresenter,
                streamingProcessingPresenter,
                uiConfigCache);
        getView().addProcessingType(AnalyticProcessType.SCHEDULED_QUERY);
    }

    @Override
    protected ReportDoc onWrite(final ReportDoc reportDoc) {
        return reportDoc
                .copy()
                .languageVersion(QueryLanguageVersion.STROOM_QL_VERSION_0_1)
                .errorFeed(errorFeedPresenter.getSelectedEntityReference())
                .analyticProcessType(getView().getProcessingType())
                .analyticProcessConfig(writeProcessConfig())
                .build();
    }
}
