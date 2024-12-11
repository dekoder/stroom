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

package stroom.storedquery.api;

import stroom.dashboard.shared.FindStoredQueryCriteria;
import stroom.dashboard.shared.StoredQuery;
import stroom.util.shared.ResultPage;
import stroom.util.shared.UserRef;

public interface StoredQueryService {

    StoredQuery fetch(int id);

    StoredQuery create(StoredQuery storedQuery);

    StoredQuery update(final StoredQuery storedQuery);

    boolean delete(int id);

    /**
     * Delete all stored queries for a given owner.
     */
    int deleteByOwner(UserRef ownerUuid);

    ResultPage<StoredQuery> find(FindStoredQueryCriteria criteria);

}
