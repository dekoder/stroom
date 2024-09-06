package stroom.analytics.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(Include.NON_NULL)
public class NotificationEmailDestination extends NotificationDestination {

    @JsonProperty
    private final String to;
    @JsonProperty
    private final String cc;
    @JsonProperty
    private final String bcc;
    @JsonProperty
    private final String subjectTemplate;
    @JsonProperty
    private final String bodyTemplate;

    @JsonCreator
    public NotificationEmailDestination(
            @JsonProperty("to") final String to,
            @JsonProperty("cc") final String cc,
            @JsonProperty("bcc") final String bcc,
            @JsonProperty("subjectTemplate") final String subjectTemplate,
            @JsonProperty("bodyTemplate") final String bodyTemplate) {

        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
    }

    public String getTo() {
        return to;
    }

    public String getCc() {
        return cc;
    }

    public String getBcc() {
        return bcc;
    }

    public String getSubjectTemplate() {
        return subjectTemplate;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    @Override
    public String toString() {
        return "AnalyticNotificationEmailDestination{" +
               "to='" + to + '\'' +
               ", cc='" + cc + '\'' +
               ", bcc='" + bcc + '\'' +
               ", subjectTemplate='" + subjectTemplate + '\'' +
               ", bodyTemplate='" + bodyTemplate + '\'' +
               '}';
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final NotificationEmailDestination that = (NotificationEmailDestination) object;
        return Objects.equals(to, that.to) && Objects.equals(cc, that.cc) && Objects.equals(
                bcc, that.bcc)
               && Objects.equals(subjectTemplate,
                that.subjectTemplate) && Objects.equals(bodyTemplate, that.bodyTemplate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, cc, bcc, subjectTemplate, bodyTemplate);
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }


    // --------------------------------------------------------------------------------


    public static class Builder {

        private String to;
        private String cc;
        private String bcc;
        private String subjectTemplate;
        private String bodyTemplate;

        private Builder() {
        }

        private Builder(final NotificationEmailDestination config) {
            this.to = config.to;
            this.cc = config.cc;
            this.bcc = config.bcc;
            this.subjectTemplate = config.subjectTemplate;
            this.bodyTemplate = config.bodyTemplate;
        }

        public Builder to(final String to) {
            this.to = to;
            return this;
        }

        public Builder cc(final String cc) {
            this.cc = cc;
            return this;
        }

        public Builder bcc(final String bcc) {
            this.bcc = bcc;
            return this;
        }

        public Builder subjectTemplate(final String subjectTemplate) {
            this.subjectTemplate = subjectTemplate;
            return this;
        }

        public Builder bodyTemplate(final String bodyTemplate) {
            this.bodyTemplate = bodyTemplate;
            return this;
        }

        public NotificationEmailDestination build() {
            return new NotificationEmailDestination(
                    to,
                    cc,
                    bcc,
                    subjectTemplate,
                    bodyTemplate);
        }
    }
}
