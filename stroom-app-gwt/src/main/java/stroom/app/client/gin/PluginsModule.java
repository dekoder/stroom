/*
 * Copyright 2017 Crown Copyright
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

package stroom.app.client.gin;

import stroom.core.client.gin.PluginModule;
import stroom.document.client.DocumentPluginEventManager;
import stroom.explorer.client.NavigationPlugin;
import stroom.help.client.HelpPlugin;
import stroom.trackers.client.TrackersPlugin;

public class PluginsModule extends PluginModule {

    @Override
    protected void configure() {
        bindPlugin(DocumentPluginEventManager.class);

        bindPlugin(HelpPlugin.class);
        bindPlugin(TrackersPlugin.class);
        bindPlugin(NavigationPlugin.class);
    }
}
