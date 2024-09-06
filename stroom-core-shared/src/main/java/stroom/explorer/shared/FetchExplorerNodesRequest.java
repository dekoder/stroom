package stroom.explorer.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Set;

@JsonInclude(Include.NON_NULL)
public class FetchExplorerNodesRequest {

    @JsonProperty
    private final Set<ExplorerNodeKey> openItems;
    @JsonProperty
    private final Set<ExplorerNodeKey> temporaryOpenedItems;
    @JsonProperty
    private final ExplorerTreeFilter filter;
    @JsonProperty
    private final Integer minDepth;
    @JsonProperty
    private final Set<ExplorerNodeKey> ensureVisible;
    @JsonProperty
    private final boolean showAlerts;

    @JsonCreator
    public FetchExplorerNodesRequest(
            @JsonProperty("openItems") final Set<ExplorerNodeKey> openItems,
            @JsonProperty("temporaryOpenedItems") final Set<ExplorerNodeKey> temporaryOpenedItems,
            @JsonProperty("filter") final ExplorerTreeFilter filter,
            @JsonProperty("minDepth") final Integer minDepth,
            @JsonProperty("ensureVisible") final Set<ExplorerNodeKey> ensureVisible,
            @JsonProperty("showAlerts") final boolean showAlerts) {
        this.openItems = openItems;
        this.temporaryOpenedItems = temporaryOpenedItems;
        this.filter = filter;
        this.minDepth = minDepth;
        this.ensureVisible = ensureVisible;
        this.showAlerts = showAlerts;
    }

    public Set<ExplorerNodeKey> getOpenItems() {
        return openItems;
    }

    public Set<ExplorerNodeKey> getTemporaryOpenedItems() {
        return temporaryOpenedItems;
    }

    public ExplorerTreeFilter getFilter() {
        return filter;
    }

    public Integer getMinDepth() {
        return minDepth;
    }

    public Set<ExplorerNodeKey> getEnsureVisible() {
        return ensureVisible;
    }

    public boolean getShowAlerts() {
        return showAlerts;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FetchExplorerNodesRequest criteria = (FetchExplorerNodesRequest) o;
        return Objects.equals(openItems, criteria.openItems) &&
                Objects.equals(temporaryOpenedItems, criteria.temporaryOpenedItems) &&
                Objects.equals(filter, criteria.filter) &&
                Objects.equals(minDepth, criteria.minDepth) &&
                Objects.equals(ensureVisible, criteria.ensureVisible);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openItems, temporaryOpenedItems, filter, minDepth, ensureVisible);
    }
}
