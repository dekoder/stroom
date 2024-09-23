package stroom.analytics.shared;

import stroom.docref.DocRef;
import stroom.docref.StringMatch;
import stroom.util.shared.BaseCriteria;
import stroom.util.shared.CriteriaFieldSort;
import stroom.util.shared.PageRequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({
        "pageRequest",
        "sortList",
        "ownerDocRef",
        "nodeName",
        "enabled"
})
@JsonInclude(Include.NON_NULL)
public class ExecutionScheduleRequest extends BaseCriteria {

    @JsonProperty
    private final DocRef ownerDocRef;
    @JsonProperty
    private final StringMatch nodeName;
    @JsonProperty
    private final Boolean enabled;

    @JsonCreator
    public ExecutionScheduleRequest(@JsonProperty("pageRequest") final PageRequest pageRequest,
                                    @JsonProperty("sortList") final List<CriteriaFieldSort> sortList,
                                    @JsonProperty("ownerDocRef") final DocRef ownerDocRef,
                                    @JsonProperty("nodeName") final StringMatch nodeName,
                                    @JsonProperty("enabled") final Boolean enabled) {
        super(pageRequest, sortList);
        this.ownerDocRef = ownerDocRef;
        this.nodeName = nodeName;
        this.enabled = enabled;
    }

    public DocRef getOwnerDocRef() {
        return ownerDocRef;
    }

    public StringMatch getNodeName() {
        return nodeName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final ExecutionScheduleRequest that = (ExecutionScheduleRequest) o;
        return Objects.equals(ownerDocRef, that.ownerDocRef) && Objects.equals(nodeName,
                that.nodeName) && Objects.equals(enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ownerDocRef, nodeName, enabled);
    }

    @Override
    public String toString() {
        return "ExecutionScheduleRequest{" +
                "ownerDocRef=" + ownerDocRef +
                ", nodeName=" + nodeName +
                ", enabled=" + enabled +
                '}';
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private PageRequest pageRequest;
        private List<CriteriaFieldSort> sortList;
        private DocRef ownerDocRef;
        private StringMatch nodeName;
        private Boolean enabled;

        private Builder() {
        }

        private Builder(final ExecutionScheduleRequest request) {
            this.pageRequest = request.getPageRequest();
            this.sortList = request.getSortList();
            this.ownerDocRef = request.ownerDocRef;
            this.nodeName = request.nodeName;
            this.enabled = request.enabled;
        }


        public Builder pageRequest(final PageRequest pageRequest) {
            this.pageRequest = pageRequest;
            return this;
        }

        public Builder sortList(final List<CriteriaFieldSort> sortList) {
            this.sortList = sortList;
            return this;
        }

        public Builder ownerDocRef(final DocRef ownerDocRef) {
            this.ownerDocRef = ownerDocRef;
            return this;
        }

        public Builder nodeName(final StringMatch nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public Builder enabled(final Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public ExecutionScheduleRequest build() {
            return new ExecutionScheduleRequest(
                    pageRequest,
                    sortList,
                    ownerDocRef,
                    nodeName,
                    enabled);
        }
    }
}
