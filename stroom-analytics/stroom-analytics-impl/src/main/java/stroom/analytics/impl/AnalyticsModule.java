/*
 * Copyright 2020 Crown Copyright
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

package stroom.analytics.impl;

import stroom.analytics.api.AnalyticsService;
import stroom.datasource.api.v2.DataSourceProvider;
import stroom.explorer.api.HasDataSourceDocRefs;
import stroom.job.api.ScheduledJobsBinder;
import stroom.processor.api.ProcessorTaskExecutorBinder;
import stroom.processor.shared.ProcessorType;
import stroom.query.common.v2.HasResultStoreInfo;
import stroom.query.common.v2.SearchProvider;
import stroom.search.impl.NodeSearchTaskHandlerProvider;
import stroom.util.RunnableWrapper;
import stroom.util.entityevent.EntityEvent;
import stroom.util.guice.GuiceUtil;
import stroom.util.guice.RestResourcesBinder;
import stroom.util.shared.Clearable;
import stroom.util.shared.scheduler.CronExpressions;

import com.google.inject.AbstractModule;
import jakarta.inject.Inject;

public class AnalyticsModule extends AbstractModule {

    @Override
    protected void configure() {
        ScheduledJobsBinder.create(binder())
                .bindJobTo(TableBuilderAnalyticExecutorRunnable.class, builder -> builder
                        .name("Analytic Executor: Table Builder")
                        .description("Run table building analytics periodically")
                        .frequencySchedule("10m")
                        .enabled(false)
                        .advanced(true))
//                .bindJobTo(StreamingAnalyticExecutorRunnable.class, builder -> builder
//                        .name("Analytic Executor: Streaming")
//                        .description("Run streaming analytics periodically")
//                        .periodicSchedule("1m")
//                        .enabled(false)
//                        .advanced(true))
                .bindJobTo(ScheduledAnalyticExecutorRunnable.class, builder -> builder
                        .name("Analytic Executor: Scheduled Query")
                        .description("Run scheduled index query analytics periodically")
                        .frequencySchedule("10m")
                        .enabled(false)
                        .advanced(true))
                .bindJobTo(ExecutionHistoryRetentionRunnable.class, builder -> builder
                        .name("Analytic Execution History Retention")
                        .description("Delete analytic execution history older than configured retention period")
                        .cronSchedule(CronExpressions.EVERY_DAY_AT_3AM.getExpression())
                        .enabled(false)
                        .advanced(true));
        GuiceUtil.buildMultiBinder(binder(), HasResultStoreInfo.class).addBinding(AnalyticDataStores.class);

        RestResourcesBinder.create(binder())
                .bind(AnalyticProcessResourceImpl.class)
                .bind(AnalyticDataShardResourceImpl.class)
                .bind(DuplicateCheckResourceImpl.class)
                .bind(ExecutionScheduleResourceImpl.class);

        bind(AnalyticsService.class).to(AnalyticsServiceImpl.class);
        bind(DuplicateCheckFactory.class).to(DuplicateCheckFactoryImpl.class);

        GuiceUtil.buildMultiBinder(binder(), Clearable.class)
                .addBinding(StreamingAnalyticCache.class);
        GuiceUtil.buildMultiBinder(binder(), EntityEvent.Handler.class)
                .addBinding(StreamingAnalyticCache.class);

        // Live federated search provision.
        GuiceUtil.buildMultiBinder(binder(), DataSourceProvider.class)
                .addBinding(AnalyticsSearchProvider.class);
        GuiceUtil.buildMultiBinder(binder(), SearchProvider.class)
                .addBinding(AnalyticsSearchProvider.class);
        GuiceUtil.buildMultiBinder(binder(), HasDataSourceDocRefs.class)
                .addBinding(AnalyticsSearchProvider.class);
        GuiceUtil.buildMultiBinder(binder(), NodeSearchTaskHandlerProvider.class)
                .addBinding(AnalyticsNodeSearchTaskHandlerProvider.class);

        ProcessorTaskExecutorBinder.create(binder())
                .bind(ProcessorType.STREAMING_ANALYTIC, StreamingAnalyticProcessorExecutor.class);
    }

    private static class TableBuilderAnalyticExecutorRunnable extends RunnableWrapper {

        @Inject
        TableBuilderAnalyticExecutorRunnable(final TableBuilderAnalyticExecutor executor) {
            super(executor::exec);
        }
    }

    private static class ScheduledAnalyticExecutorRunnable extends RunnableWrapper {

        @Inject
        ScheduledAnalyticExecutorRunnable(final ScheduledQueryAnalyticExecutor executor) {
            super(executor::exec);
        }
    }

    private static class ExecutionHistoryRetentionRunnable extends RunnableWrapper {

        @Inject
        ExecutionHistoryRetentionRunnable(final ExecutionHistoryRetention executor) {
            super(executor::exec);
        }
    }
}

