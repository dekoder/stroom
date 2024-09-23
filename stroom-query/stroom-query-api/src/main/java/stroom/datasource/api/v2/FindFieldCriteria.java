package stroom.datasource.api.v2;

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

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
public class FindFieldCriteria extends BaseCriteria {

    public static final String SORT_BY_NAME = "name";

    @JsonProperty
    private final DocRef dataSourceRef;
    @JsonProperty
    private final StringMatch stringMatch;
    @JsonProperty
    private final Boolean queryable;

    public FindFieldCriteria(@JsonProperty("pageRequest") final PageRequest pageRequest,
                             @JsonProperty("sortList") final List<CriteriaFieldSort> sortList,
                             @JsonProperty("dataSourceRef") final DocRef dataSourceRef) {
        this(pageRequest, sortList, dataSourceRef, null, null);
    }

    @JsonCreator
    public FindFieldCriteria(@JsonProperty("pageRequest") final PageRequest pageRequest,
                             @JsonProperty("sortList") final List<CriteriaFieldSort> sortList,
                             @JsonProperty("dataSourceRef") final DocRef dataSourceRef,
                             @JsonProperty("stringMatch") final StringMatch stringMatch,
                             @JsonProperty("queryable") final Boolean queryable) {
        super(pageRequest, sortList);
        this.dataSourceRef = dataSourceRef;
        this.stringMatch = stringMatch;
        this.queryable = queryable;
    }

    public DocRef getDataSourceRef() {
        return dataSourceRef;
    }

    public StringMatch getStringMatch() {
        return stringMatch;
    }

    public Boolean getQueryable() {
        return queryable;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FindFieldCriteria)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final FindFieldCriteria that = (FindFieldCriteria) o;
        return Objects.equals(dataSourceRef, that.dataSourceRef) &&
                Objects.equals(stringMatch, that.stringMatch) &&
                Objects.equals(queryable, that.queryable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataSourceRef, stringMatch, queryable);
    }
}
