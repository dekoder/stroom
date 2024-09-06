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

package stroom.explorer.client.presenter;

import stroom.docref.DocRef;
import stroom.explorer.shared.ExplorerTreeFilter;
import stroom.explorer.shared.NodeFlag;
import stroom.util.shared.GwtNullSafe;

import java.util.List;
import java.util.Set;

public class ExplorerTreeFilterBuilder {

    private Set<String> includedTypes;
    private Set<String> includedRootTypes;
    private Set<String> tags;
    private Set<NodeFlag> nodeFlags;
    private Set<String> requiredPermissions;
    private String nameFilter;
    private boolean nameFilterChange;
    private List<DocRef> recentItems;

    /**
     * Set the document types to include. If null/empty return all root notes.
     */
    public void setIncludedTypeSet(final Set<String> types) {
        includedTypes = types;
    }

    /**
     * Set the document types to include. If null/empty return all root notes.
     */
    public void setIncludedTypes(final String... types) {
        this.includedTypes = SetUtil.toSet(types);
    }

    /**
     * Set the document types of root nodes to include. If null/empty return all root notes.
     */
    public void setIncludedRootTypeSet(final Set<String> types) {
        includedRootTypes = types;
    }

    /**
     * Set the document types of root nodes to include. If null/empty return all root notes.
     */
    public void setIncludedRootTypes(final String... types) {
        this.includedRootTypes = SetUtil.toSet(types);
    }

    public void setTags(final String... tags) {
        this.tags = SetUtil.toSet(tags);
    }

    public void setNodeFlags(final Set<NodeFlag> nodeFlags) {
        this.nodeFlags = nodeFlags;
    }

    public void setRequiredPermissions(final String... requiredPermissions) {
        this.requiredPermissions = SetUtil.toSet(requiredPermissions);
    }

    /**
     * This sets the name filter to be used when fetching items. This method
     * returns false is the filter is set to the same value that is already set.
     */
    public boolean setNameFilter(final String nameFilter) {
        return setNameFilter(nameFilter, false);
    }

    /**
     * This sets the name filter to be used when fetching items. This method
     * returns false is the filter is set to the same value that is already set.
     */
    public boolean setNameFilter(final String nameFilter, final boolean forceChange) {
        final String filter = GwtNullSafe.get(
                nameFilter,
                String::trim,
                str -> str.length() == 0
                        ? null
                        : str);

        if (!forceChange && ((GwtNullSafe.allNull(filter, this.nameFilter))
                || (filter != null && filter.equals(this.nameFilter)))) {
            return false;
        } else {
            this.nameFilter = filter;
            this.nameFilterChange = true;

            return true;
        }
    }

    public void setRecentItems(final List<DocRef> recentItems) {
        this.recentItems = recentItems;
    }

    public ExplorerTreeFilter build() {
        final boolean nameFilterChange = this.nameFilterChange;
        this.nameFilterChange = false;

        return new ExplorerTreeFilter(
                SetUtil.copySet(includedTypes),
                SetUtil.copySet(includedRootTypes),
                SetUtil.copySet(tags),
                nodeFlags,
                SetUtil.copySet(requiredPermissions),
                nameFilter,
                nameFilterChange,
                recentItems);
    }
}
