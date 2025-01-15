package stroom.security.shared;

import stroom.util.shared.PageResponse;
import stroom.util.shared.ResultPage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionListResponse extends ResultPage<SessionDetails> {

    @JsonCreator
    public SessionListResponse(@JsonProperty("values") final List<SessionDetails> values,
                               @JsonProperty("pageResponse") final PageResponse pageResponse) {
        super(values, pageResponse);
    }

    public SessionListResponse(final List<SessionDetails> values) {
        super(values, SessionListResponse.createPageResponse(values));
    }

    public static SessionListResponse empty() {
        return new SessionListResponse(
                Collections.emptyList(),
                SessionListResponse.createPageResponse(Collections.emptyList()));
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
