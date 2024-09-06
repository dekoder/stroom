/*
 * Copyright 2018 Crown Copyright
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

package stroom.resource.impl;

import stroom.job.api.ScheduledJobsBinder;
import stroom.lifecycle.api.LifecycleBinder;
import stroom.resource.api.ResourceStore;
import stroom.util.RunnableWrapper;

import com.google.inject.AbstractModule;
import jakarta.inject.Inject;

public class ResourceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ResourceStore.class).to(ResourceStoreImpl.class);

        ScheduledJobsBinder.create(binder())
                .bindJobTo(DeleteTempFile.class, builder -> builder
                        .name("Delete temp file")
                        .description("Deletes the resource store temporary file.")
                        .managed(false)
                        .frequencySchedule("1h"));

        LifecycleBinder.create(binder())
                .bindStartupTaskTo(ResourceStoreStartup.class)
                .bindShutdownTaskTo(ResourceStoreShutdown.class);
    }

    private static class DeleteTempFile extends RunnableWrapper {

        @Inject
        DeleteTempFile(final ResourceStoreImpl resourceStore) {
            super(resourceStore::execute);
        }
    }

    private static class ResourceStoreStartup extends RunnableWrapper {

        @Inject
        ResourceStoreStartup(final ResourceStoreImpl resourceStore) {
            super(resourceStore::startup);
        }
    }

    private static class ResourceStoreShutdown extends RunnableWrapper {

        @Inject
        ResourceStoreShutdown(final ResourceStoreImpl resourceStore) {
            super(resourceStore::shutdown);
        }
    }
}
