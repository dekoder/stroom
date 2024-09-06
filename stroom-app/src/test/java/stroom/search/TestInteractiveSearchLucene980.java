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

package stroom.search;

import stroom.index.impl.IndexShardCreator;
import stroom.index.shared.LuceneVersion;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;

class TestInteractiveSearchLucene980 extends AbstractInteractiveSearchTest {

    private static boolean doneSetup;

    @Inject
    private IndexShardCreator indexShardCreator;

    @BeforeEach
    void setup() {
        indexShardCreator.setIndexVersion(LuceneVersion.LUCENE_9_8_0);
        if (!doneSetup) {
            super.setup();
            doneSetup = true;
        }
    }
}
