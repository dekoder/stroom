package stroom.analytics.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(Include.NON_NULL)
public class ScheduledQueryAnalyticTrackerData extends AnalyticTrackerData {

    @JsonProperty
    private Long actualExecutionTimeMs;
    @JsonProperty
    private Long lastEffectiveExecutionTimeMs;
    @JsonProperty
    private Long nextEffectiveExecutionTimeMs;

    public ScheduledQueryAnalyticTrackerData() {
    }

    @JsonCreator
    public ScheduledQueryAnalyticTrackerData(
            @JsonProperty("actualExecutionTimeMs") final Long actualExecutionTimeMs,
            @JsonProperty("lastEffectiveExecutionTimeMs") final Long lastEffectiveExecutionTimeMs,
            @JsonProperty("nextEffectiveExecutionTimeMs") final Long nextEffectiveExecutionTimeMs,
            @JsonProperty("message") final String message) {
        super(message);
        this.actualExecutionTimeMs = actualExecutionTimeMs;
        this.lastEffectiveExecutionTimeMs = lastEffectiveExecutionTimeMs;
        this.nextEffectiveExecutionTimeMs = nextEffectiveExecutionTimeMs;
    }

    public Long getActualExecutionTimeMs() {
        return actualExecutionTimeMs;
    }

    public void setActualExecutionTimeMs(final Long actualExecutionTimeMs) {
        this.actualExecutionTimeMs = actualExecutionTimeMs;
    }

    public Long getLastEffectiveExecutionTimeMs() {
        return lastEffectiveExecutionTimeMs;
    }

    public void setLastEffectiveExecutionTimeMs(final Long lastEffectiveExecutionTimeMs) {
        this.lastEffectiveExecutionTimeMs = lastEffectiveExecutionTimeMs;
    }

    public Long getNextEffectiveExecutionTimeMs() {
        return nextEffectiveExecutionTimeMs;
    }

    public void setNextEffectiveExecutionTimeMs(final Long nextEffectiveExecutionTimeMs) {
        this.nextEffectiveExecutionTimeMs = nextEffectiveExecutionTimeMs;
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
        final ScheduledQueryAnalyticTrackerData that = (ScheduledQueryAnalyticTrackerData) o;
        return Objects.equals(actualExecutionTimeMs, that.actualExecutionTimeMs) &&
                Objects.equals(lastEffectiveExecutionTimeMs, that.lastEffectiveExecutionTimeMs) &&
                Objects.equals(nextEffectiveExecutionTimeMs, that.nextEffectiveExecutionTimeMs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), actualExecutionTimeMs, lastEffectiveExecutionTimeMs,
                nextEffectiveExecutionTimeMs);
    }

    @Override
    public String toString() {
        return "ScheduledQueryAnalyticTrackerData{" +
                "actualExecutionTimeMs=" + actualExecutionTimeMs +
                ", lastEffectiveExecutionTimeMs=" + lastEffectiveExecutionTimeMs +
                ", nextEffectiveExecutionTimeMs=" + nextEffectiveExecutionTimeMs +
                '}';
    }
}
