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

package stroom.searchable.impl;

import stroom.explorer.api.IsSpecialExplorerDataSource;
import stroom.query.common.v2.SearchProviderRegistry;
import stroom.util.guice.GuiceUtil;
import stroom.util.guice.RestResourcesBinder;

import com.google.inject.AbstractModule;

public class SearchableModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SearchProviderRegistry.class).to(SearchProviderRegistryImpl.class);
        GuiceUtil.buildMultiBinder(binder(), IsSpecialExplorerDataSource.class)
                .addBinding(SpecialExplorerDataSourceImpl.class);

        RestResourcesBinder.create(binder())
                .bind(SearchableResourceImpl.class);
    }
}
