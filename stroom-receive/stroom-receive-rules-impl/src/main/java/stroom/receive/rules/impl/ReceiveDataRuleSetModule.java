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

package stroom.receive.rules.impl;

import stroom.docstore.api.DocumentActionHandlerBinder;
import stroom.importexport.api.ImportExportActionHandler;
import stroom.receive.common.DataReceiptPolicyAttributeMapFilterFactory;
import stroom.receive.rules.shared.ReceiveDataRules;
import stroom.util.guice.GuiceUtil;
import stroom.util.guice.RestResourcesBinder;

import com.google.inject.AbstractModule;

public class ReceiveDataRuleSetModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ReceiveDataRuleSetService.class).to(ReceiveDataRuleSetServiceImpl.class);
        bind(DataReceiptPolicyAttributeMapFilterFactory.class).to(DataReceiptPolicyAttributeMapFilterFactoryImpl.class);

        // Commented out pending re-visiting receipt rules as the current implementation
        // was not fully thought through
        // See https://github.com/gchq/stroom/issues/1125
//        GuiceUtil.buildMultiBinder(binder(), ExplorerActionHandler.class)
//                .addBinding(ReceiveDataRuleSetServiceImpl.class);

        GuiceUtil.buildMultiBinder(binder(), ImportExportActionHandler.class)
                .addBinding(ReceiveDataRuleSetServiceImpl.class);

        RestResourcesBinder.create(binder())
                .bind(ReceiveDataRuleSetResourceImpl.class);

        DocumentActionHandlerBinder.create(binder())
                .bind(ReceiveDataRules.TYPE, ReceiveDataRuleSetServiceImpl.class);

    }
}
