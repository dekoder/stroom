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

package stroom.receive.common;

import stroom.event.logging.rs.api.AutoLogged;
import stroom.event.logging.rs.api.AutoLogged.OperationType;
import stroom.proxy.feed.remote.GetFeedStatusRequestV2;
import stroom.proxy.feed.remote.GetFeedStatusResponse;
import stroom.util.HasHealthCheck;

import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.health.HealthCheck.Result;

import javax.inject.Inject;
import javax.inject.Provider;

@AutoLogged(OperationType.UNLOGGED)
public class FeedStatusResourceV2Impl implements FeedStatusResourceV2, HasHealthCheck {

    private final Provider<FeedStatusService> feedStatusServiceProvider;

    @Inject
    public FeedStatusResourceV2Impl(final Provider<FeedStatusService> feedStatusServiceProvider) {
        this.feedStatusServiceProvider = feedStatusServiceProvider;
    }

    @Timed
    @Override
    // TODO This should really be a GET with the feedName and senderDn as params
    public GetFeedStatusResponse getFeedStatus(final GetFeedStatusRequestV2 request) {
        return feedStatusServiceProvider.get().getFeedStatus(request);
    }

    @Override
    public Result getHealth() {
        return Result.healthy();
    }
}
