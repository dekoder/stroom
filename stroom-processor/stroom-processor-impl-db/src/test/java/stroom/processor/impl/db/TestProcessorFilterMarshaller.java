package stroom.processor.impl.db;

import stroom.index.shared.LuceneIndexDoc;
import stroom.processor.shared.ProcessorFilter;
import stroom.processor.shared.QueryData;
import stroom.query.api.v2.ExpressionOperator;
import stroom.query.api.v2.ExpressionTerm;
import stroom.query.api.v2.ExpressionTerm.Condition;
import stroom.query.api.v2.Param;
import stroom.query.api.v2.TimeRange;

import jakarta.xml.bind.JAXBException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class TestProcessorFilterMarshaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestProcessorFilterMarshaller.class);

    @Test
    void testMarshall() throws JAXBException {
        final QueryData queryData = new QueryData();
        queryData.setDataSource(LuceneIndexDoc.buildDocRef()
                .randomUuid()
                .name("Some idx")
                .build());
        queryData.setParams(List.of(new Param("key1", "val1")));
        queryData.setTimeRange(new TimeRange("MyName", Condition.BETWEEN, "week() -1w", "week()"));
        queryData.setExpression(
                ExpressionOperator.builder()
                        .addTerm(ExpressionTerm.builder()
                                .field("SomeField")
                                .condition(Condition.EQUALS)
                                .value("xxxx")
                                .build())
                        .build()
        );

        final ProcessorFilter processorFilter = new ProcessorFilter();
        // Blank tracker
        processorFilter.setReprocess(true);
        processorFilter.setEnabled(true);
        processorFilter.setPriority(1);
        processorFilter.setProcessor(null);
        processorFilter.setQueryData(queryData);
        processorFilter.setMinMetaCreateTimeMs(System.currentTimeMillis());
        processorFilter.setMaxMetaCreateTimeMs(System.currentTimeMillis());

        final ProcessorFilterMarshaller processorFilterMarshaller = new ProcessorFilterMarshaller();
        final ProcessorFilter marshalled1 = processorFilterMarshaller.marshal(processorFilter);

        final String xml1 = marshalled1.getData();
        Assertions.assertThat(xml1)
                .isNotBlank();
        LOGGER.debug("marshalled:\n{}", xml1);

        // Now un-marshall

        final ProcessorFilter processorFilter2 = new ProcessorFilter();
        processorFilter2.setData(marshalled1.getData());
        processorFilterMarshaller.unmarshal(processorFilter2);

        Assertions.assertThat(processorFilter2)
                .isEqualTo(processorFilter);

        // Now re-marshall and compare

        final ProcessorFilter marshalled2 = processorFilterMarshaller.marshal(processorFilter2);
        final String xml2 = marshalled2.getData();
        Assertions.assertThat(xml2)
                .isEqualTo(xml1);
    }
}
