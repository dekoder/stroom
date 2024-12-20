package stroom.pipeline.refdata;

import stroom.data.shared.StreamTypeNames;
import stroom.docref.DocRef;
import stroom.event.logging.api.StroomEventLoggingService;
import stroom.event.logging.mock.MockStroomEventLoggingService;
import stroom.pipeline.refdata.RefDataLookupRequest.ReferenceLoader;
import stroom.pipeline.shared.PipelineDoc;
import stroom.test.common.util.test.AbstractResourceTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

class TestReferenceDataResourceImpl extends AbstractResourceTest<ReferenceDataResource> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestReferenceDataResourceImpl.class);

    @Mock
    private ReferenceDataService referenceDataService;
    private final StroomEventLoggingService mockStroomEventLoggingService = new MockStroomEventLoggingService();

    @Override
    public ReferenceDataResource getRestResource() {
        return new ReferenceDataResourceImpl(
                () -> referenceDataService,
                () -> mockStroomEventLoggingService);
    }

    @Override
    public String getResourceBasePath() {
        return ReferenceDataResource.BASE_PATH;
    }


    @Test
    void lookup() {
        LOGGER.info("Running test");

        final RefDataLookupRequest request = new RefDataLookupRequest(
                "MyMap",
                "jblogss",
                null,
                List.of(
                        new ReferenceLoader(
                                DocRef.builder()
                                        .name("LoaderPipeline")
                                        .type(PipelineDoc.TYPE)
                                        .uuid(UUID.randomUUID().toString())
                                        .build(),
                                DocRef.builder()
                                        .name("RefFeed")
                                        .type(StreamTypeNames.REFERENCE)
                                        .uuid(UUID.randomUUID().toString())
                                        .build(),
                                StreamTypeNames.REFERENCE)
                )
        );

        Mockito.when(referenceDataService.lookup(Mockito.any(RefDataLookupRequest.class)))
                .thenReturn("GBR");

        doPostTest(
                ReferenceDataResource.LOOKUP_SUB_PATH,
                request,
                String.class,
                "GBR");
    }


    @Test
    void lookup_nullMap() {

        LOGGER.info("Running test");

        final RefDataLookupRequest request = new RefDataLookupRequest(
                null,
                "jblogss",
                null,
                List.of(
                        new ReferenceLoader(
                                DocRef.builder()
                                        .name("LoaderPipeline")
                                        .type(PipelineDoc.TYPE)
                                        .uuid(UUID.randomUUID().toString())
                                        .build(),
                                DocRef.builder()
                                        .name("RefFeed")
                                        .type(StreamTypeNames.REFERENCE)
                                        .uuid(UUID.randomUUID().toString())
                                        .build(),
                                StreamTypeNames.REFERENCE)
                )
        );

        Assertions.assertThatThrownBy(
                        () -> {
                            doPostTest(
                                    ReferenceDataResource.LOOKUP_SUB_PATH,
                                    request,
                                    String.class,
                                    "GBR");
                        })
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("mapName must not be null");
    }

    @Test
    void lookup_nullKey() {

        LOGGER.info("Running test");

        final RefDataLookupRequest request = new RefDataLookupRequest(
                "MyMap",
                null,
                null,
                List.of(
                        new ReferenceLoader(
                                DocRef.builder()
                                        .name("LoaderPipeline")
                                        .type(PipelineDoc.TYPE)
                                        .uuid(UUID.randomUUID().toString())
                                        .build(),
                                DocRef.builder()
                                        .name("RefFeed")
                                        .type(StreamTypeNames.REFERENCE)
                                        .uuid(UUID.randomUUID().toString())
                                        .build(),
                                StreamTypeNames.REFERENCE)
                )
        );

        Assertions.assertThatThrownBy(
                        () -> {
                            doPostTest(
                                    ReferenceDataResource.LOOKUP_SUB_PATH,
                                    request,
                                    String.class,
                                    "GBR");
                        })
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("key must not be null");
    }

    @Test
    void lookup_noLoaders() {

        LOGGER.info("Running test");

        final RefDataLookupRequest request = new RefDataLookupRequest(
                "MyMap",
                "jbloggs",
                null,
                Collections.emptyList());

        Assertions.assertThatThrownBy(
                        () -> {
                            doPostTest(
                                    ReferenceDataResource.LOOKUP_SUB_PATH,
                                    request,
                                    String.class,
                                    "GBR");
                        })
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("referenceLoaders must not be empty");
    }
}
