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
 */

package stroom.security.impl;

import stroom.cache.api.CacheManager;
import stroom.cache.api.LoadingStroomCache;
import stroom.docref.DocRef;
import stroom.security.api.UserService;
import stroom.util.entityevent.EntityAction;
import stroom.util.entityevent.EntityEvent;
import stroom.util.entityevent.EntityEventBus;
import stroom.util.entityevent.EntityEventHandler;
import stroom.util.shared.Clearable;

import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
@EntityEventHandler(type = UserDocRefUtil.USER, action = {EntityAction.CLEAR_CACHE})
class UserGroupsCache implements EntityEvent.Handler, Clearable {

    private static final String CACHE_NAME = "User Groups Cache";

    private final Provider<EntityEventBus> eventBusProvider;
    private final LoadingStroomCache<String, Set<String>> cache;

    @Inject
    UserGroupsCache(final CacheManager cacheManager,
                    final UserService userService,
                    final Provider<EntityEventBus> eventBusProvider,
                    final Provider<AuthorisationConfig> authorisationConfigProvider) {
        this.eventBusProvider = eventBusProvider;
        cache = cacheManager.createLoadingCache(
                CACHE_NAME,
                () -> authorisationConfigProvider.get().getUserGroupsCache(),
                userService::findGroupUuidsForUser);
    }

    Set<String> get(final String userUuid) {
        return cache.get(userUuid);
    }

    void remove(final String userUuid) {
        cache.invalidate(userUuid);
        final EntityEventBus entityEventBus = eventBusProvider.get();
        EntityEvent.fire(
                entityEventBus,
                UserDocRefUtil.createDocRef(userUuid),
                EntityAction.CLEAR_CACHE);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void onChange(final EntityEvent event) {
        final DocRef docRef = event.getDocRef();
        if (docRef != null && UserDocRefUtil.USER.equals(docRef.getType())) {
            cache.invalidate(docRef.getUuid());
        }
    }
}
