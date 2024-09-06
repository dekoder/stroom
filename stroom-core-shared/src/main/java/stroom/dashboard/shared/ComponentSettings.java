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

package stroom.dashboard.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = QueryComponentSettings.class, name = "query"),
        @JsonSubTypes.Type(value = TableComponentSettings.class, name = "table"),
        @JsonSubTypes.Type(value = VisComponentSettings.class, name = "vis"),
        @JsonSubTypes.Type(value = TextComponentSettings.class, name = "text"),
        @JsonSubTypes.Type(value = KeyValueInputComponentSettings.class, name = "key-value-input"),
        @JsonSubTypes.Type(value = ListInputComponentSettings.class, name = "list-input"),
        @JsonSubTypes.Type(value = TextInputComponentSettings.class, name = "text-input")
})
@JsonInclude(Include.NON_NULL)
public interface ComponentSettings {

    Builder copy();

    interface Builder {

        ComponentSettings build();
    }
}
