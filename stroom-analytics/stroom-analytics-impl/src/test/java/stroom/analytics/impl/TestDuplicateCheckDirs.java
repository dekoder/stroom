package stroom.analytics.impl;

import stroom.analytics.shared.AnalyticRuleDoc;
import stroom.lmdb2.LmdbEnvDir;
import stroom.lmdb2.LmdbEnvDirFactory;
import stroom.query.common.v2.DuplicateCheckStoreConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TestDuplicateCheckDirs {

    @Mock
    private LmdbEnvDirFactory mockLmdbEnvDirFactory;
    @Mock
    private LmdbEnvDirFactory.Builder mockLmdbEnvDirFactoryBuilder;
    @Mock
    private LmdbEnvDir mockLmdbEnvDir;
    @Mock
    private DuplicateCheckStoreConfig mockDuplicateCheckStoreConfig;

    @Test
    void testDeleteUnused_some() {

        Mockito.when(mockLmdbEnvDirFactory.builder())
                .thenReturn(mockLmdbEnvDirFactoryBuilder);
        Mockito.when(mockLmdbEnvDirFactoryBuilder.config(Mockito.any()))
                .thenReturn(mockLmdbEnvDirFactoryBuilder);
        Mockito.when(mockLmdbEnvDirFactoryBuilder.subDir(Mockito.any()))
                .thenReturn(mockLmdbEnvDirFactoryBuilder);
        Mockito.when(mockLmdbEnvDirFactoryBuilder.build())
                .thenReturn(mockLmdbEnvDir);

        final DuplicateCheckDirs duplicateCheckDirs = new DuplicateCheckDirs(
                mockLmdbEnvDirFactory, mockDuplicateCheckStoreConfig);

        final List<String> dupStoreUuids = List.of(
                "uuid1",
                "uuid2",
                "uuid3",
                "uuid4");
        final List<AnalyticRuleDoc> analyticRuleDocs = List.of(
                makeDoc("uuid2"),
                makeDoc("uuid4"),
                makeDoc("uuid5"));

        final List<String> deletedUuids = duplicateCheckDirs.deleteUnused(dupStoreUuids, analyticRuleDocs);
        assertThat(deletedUuids)
                .containsExactly(
                        "uuid1",
                        "uuid3");
    }

    @Test
    void testDeleteUnused_all() {

        Mockito.when(mockLmdbEnvDirFactory.builder())
                .thenReturn(mockLmdbEnvDirFactoryBuilder);
        Mockito.when(mockLmdbEnvDirFactoryBuilder.config(Mockito.any()))
                .thenReturn(mockLmdbEnvDirFactoryBuilder);
        Mockito.when(mockLmdbEnvDirFactoryBuilder.subDir(Mockito.any()))
                .thenReturn(mockLmdbEnvDirFactoryBuilder);
        Mockito.when(mockLmdbEnvDirFactoryBuilder.build())
                .thenReturn(mockLmdbEnvDir);

        final DuplicateCheckDirs duplicateCheckDirs = new DuplicateCheckDirs(
                mockLmdbEnvDirFactory, mockDuplicateCheckStoreConfig);

        final List<String> dupStoreUuids = List.of(
                "uuid1",
                "uuid2",
                "uuid3",
                "uuid4");
        final List<AnalyticRuleDoc> analyticRuleDocs = null;

        final List<String> deletedUuids = duplicateCheckDirs.deleteUnused(dupStoreUuids, analyticRuleDocs);
        assertThat(deletedUuids)
                .containsExactly(
                        "uuid1",
                        "uuid2",
                        "uuid3",
                        "uuid4");
    }

    @Test
    void testDeleteUnused_none() {
        final DuplicateCheckDirs duplicateCheckDirs = new DuplicateCheckDirs(
                mockLmdbEnvDirFactory, mockDuplicateCheckStoreConfig);

        final List<String> dupStoreUuids = List.of(
                "uuid1",
                "uuid2",
                "uuid3",
                "uuid4");
        final List<AnalyticRuleDoc> analyticRuleDocs = List.of(
                makeDoc("uuid1"),
                makeDoc("uuid2"),
                makeDoc("uuid3"),
                makeDoc("uuid4"),
                makeDoc("uuid5"));

        final List<String> deletedUuids = duplicateCheckDirs.deleteUnused(dupStoreUuids, analyticRuleDocs);
        assertThat(deletedUuids)
                .isEmpty();
    }

    private AnalyticRuleDoc makeDoc(final String uuid) {
        final AnalyticRuleDoc mockDoc = Mockito.mock(AnalyticRuleDoc.class);
        Mockito.when(mockDoc.getUuid())
                .thenReturn(uuid);
        return mockDoc;
    }
}
