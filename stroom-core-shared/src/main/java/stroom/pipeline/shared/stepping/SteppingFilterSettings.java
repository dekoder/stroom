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

package stroom.pipeline.shared.stepping;

import stroom.pipeline.shared.XPathFilter;
import stroom.util.shared.OutputState;
import stroom.util.shared.Severity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(Include.NON_NULL)
public class SteppingFilterSettings {

    @JsonProperty
    private Severity skipToSeverity;
    @JsonProperty
    private OutputState skipToOutput;
    @JsonProperty
    private List<XPathFilter> filters;

    public SteppingFilterSettings() {
    }

    @JsonCreator
    public SteppingFilterSettings(@JsonProperty("skipToSeverity") final Severity skipToSeverity,
                                  @JsonProperty("skipToOutput") final OutputState skipToOutput,
                                  @JsonProperty("filters") final List<XPathFilter> filters) {
        this.skipToSeverity = skipToSeverity;
        this.skipToOutput = skipToOutput;
        this.filters = filters;
    }

    public Severity getSkipToSeverity() {
        return skipToSeverity;
    }

    public void setSkipToSeverity(final Severity skipToSeverity) {
        this.skipToSeverity = skipToSeverity;
    }

    public OutputState getSkipToOutput() {
        return skipToOutput;
    }

    public void setSkipToOutput(final OutputState skipToOutput) {
        this.skipToOutput = skipToOutput;
    }

    public List<XPathFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<XPathFilter> filters) {
        this.filters = filters;
    }

    public void clearUniqueValues() {
        if (filters != null) {
            for (final XPathFilter xPathFilter : filters) {
                xPathFilter.clearUniqueValues();
            }
        }
    }

    public void clearAllFilters() {
        skipToSeverity = null;
        skipToOutput = null;
        filters.clear();
    }

    public boolean hasActiveFilters() {
        return skipToSeverity != null
                || skipToOutput != null
                || (filters != null && !filters.isEmpty());
    }

    @Override
    public String toString() {
        return "SteppingFilterSettings{" +
                "skipToSeverity=" + skipToSeverity +
                ", skipToOutput=" + skipToOutput +
                ", filters=" + filters +
                '}';
    }
}
