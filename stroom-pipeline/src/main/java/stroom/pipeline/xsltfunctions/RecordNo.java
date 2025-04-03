/*
 * Copyright 2016 Crown Copyright
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

package stroom.pipeline.xsltfunctions;

import stroom.pipeline.shared.SourceLocation;
import stroom.pipeline.state.LocationHolder;
import stroom.pipeline.state.LocationHolder.FunctionType;
import stroom.util.shared.NullSafe;

import jakarta.inject.Inject;

class RecordNo extends AbstractLocationFunction {

    @Inject
    RecordNo(final LocationHolder locationHolder) {
        super(locationHolder);
    }

    @Override
    String getValue(final SourceLocation location) {
        return NullSafe.get(location, SourceLocation::getRecordIndex, i -> String.valueOf(i + 1));
    }

    @Override
    FunctionType getType() {
        return FunctionType.RECORD_NO;
    }
}
