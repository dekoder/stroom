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

package stroom.importexport.impl;

import stroom.importexport.api.ImportExportActionHandlers;
import stroom.importexport.api.ImportExportDocumentEventLog;
import stroom.importexport.api.ImportExportSerializer;
import stroom.lifecycle.api.LifecycleBinder;
import stroom.util.RunnableWrapper;

import com.google.inject.AbstractModule;
import jakarta.inject.Inject;

public class ImportExportModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ImportExportService.class).to(ImportExportServiceImpl.class);
        bind(ImportExportSerializer.class).to(ImportExportSerializerImpl.class);
        bind(ImportExportDocumentEventLog.class).to(ImportExportDocumentEventLogImpl.class);
        bind(ImportExportActionHandlers.class).to(ImportExportActionHandlersImpl.class);

        //Startup with very low priority to ensure it starts after everything else
        //in particular
        LifecycleBinder.create(binder())
                .bindStartupTaskTo(ContentPackImportStartup.class, 6);
    }

    private static class ContentPackImportStartup extends RunnableWrapper {

        @Inject
        ContentPackImportStartup(final ContentPackImport contentPackImport) {
            super(contentPackImport::startup);
        }
    }
}
