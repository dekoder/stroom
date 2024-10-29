package stroom.processor.shared;

import stroom.analytics.shared.AnalyticRuleDoc;
import stroom.datasource.api.v2.ConditionSet;
import stroom.datasource.api.v2.QueryField;
import stroom.docref.DocRef;
import stroom.pipeline.shared.PipelineDoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProcessorFields {

    public static final String PROCESSORS_TYPE = "Processors";
    public static final DocRef PROCESSORS_DOC_REF = DocRef.builder()
            .type(PROCESSORS_TYPE)
            .uuid(PROCESSORS_TYPE)
            .name(PROCESSORS_TYPE)
            .build();

    private static final List<QueryField> FIELDS = new ArrayList<>();
    private static final Map<String, QueryField> ALL_FIELD_MAP;


    public static final QueryField ID = QueryField.createId("Processor Id");
    public static final QueryField PROCESSOR_TYPE = QueryField.createText("Processor Type");
    public static final QueryField PIPELINE = QueryField.createDocRefByUuid(
            PipelineDoc.DOCUMENT_TYPE, "Processor Pipeline");
    public static final QueryField ANALYTIC_RULE = QueryField.createDocRefByUuid(
            AnalyticRuleDoc.DOCUMENT_TYPE, "Analytic Rule");
    public static final QueryField ENABLED = QueryField.createBoolean("Processor Enabled");
    public static final QueryField DELETED = QueryField.createBoolean("Processor Deleted");
    public static final QueryField UUID = QueryField.createText("Processor UUID");

    static {
        FIELDS.add(ID);
        FIELDS.add(PROCESSOR_TYPE);
        FIELDS.add(PIPELINE);
        FIELDS.add(ANALYTIC_RULE);
        FIELDS.add(ENABLED);
        FIELDS.add(DELETED);
        FIELDS.add(UUID);

        ALL_FIELD_MAP = FIELDS.stream().collect(Collectors.toMap(QueryField::getFldName, Function.identity()));
    }

    public static List<QueryField> getFields() {
        return new ArrayList<>(FIELDS);
    }

    public static Map<String, QueryField> getAllFieldMap() {
        return ALL_FIELD_MAP;
    }
}
