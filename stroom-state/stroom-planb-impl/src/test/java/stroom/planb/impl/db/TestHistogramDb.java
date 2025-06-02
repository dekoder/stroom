/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package stroom.planb.impl.db;

import stroom.bytebuffer.impl6.ByteBufferFactoryImpl;
import stroom.bytebuffer.impl6.ByteBuffers;
import stroom.entity.shared.ExpressionCriteria;
import stroom.planb.impl.db.histogram.HistogramDb;
import stroom.planb.impl.db.histogram.HistogramFields;
import stroom.planb.impl.db.histogram.HistogramKey;
import stroom.planb.impl.db.histogram.HistogramValue;
import stroom.planb.impl.db.histogram.Tag;
import stroom.planb.impl.db.histogram.Tags;
import stroom.planb.shared.HistogramKeySchema;
import stroom.planb.shared.HistogramKeyType;
import stroom.planb.shared.HistogramPeriod;
import stroom.planb.shared.HistogramSettings;
import stroom.planb.shared.HistogramValueMax;
import stroom.planb.shared.HistogramValueSchema;
import stroom.query.api.ExpressionOperator;
import stroom.query.common.v2.ExpressionPredicateFactory;
import stroom.query.language.functions.FieldIndex;
import stroom.query.language.functions.Val;
import stroom.query.language.functions.ValString;
import stroom.util.io.ByteSize;
import stroom.util.io.FileUtil;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class TestHistogramDb {

    private static final int ITERATIONS = 100;
    private static final ByteBuffers BYTE_BUFFERS = new ByteBuffers(new ByteBufferFactoryImpl());
    private static final HistogramSettings BASIC_SETTINGS = new HistogramSettings
            .Builder()
            .maxStoreSize(ByteSize.ofGibibytes(100).getBytes())
            .build();

    private final Instant refTime = Instant.parse("2000-01-01T00:00:00.000Z");
    private final List<KeyFunction> keyFunctions = List.of(
            new KeyFunction(HistogramKeyType.TAGS.name(), HistogramKeyType.TAGS,
                    i -> getKey(refTime)));

    private Tags getTags() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("captain", ValString.create("kirk")));
        tags.add(new Tag("mr", ValString.create("spock")));
        tags.add(new Tag("dr", ValString.create("mccoy")));
        tags.add(new Tag("lieutenant", ValString.create("uhura")));
        return new Tags(tags);
    }

    private HistogramKey getKey(final Instant time) {
        return new HistogramKey(getTags(), time);
    }

    @Test
    void test(@TempDir Path tempDir) {
        testWrite(tempDir);

        final Instant refTime = Instant.parse("2000-01-01T00:00:00.000Z");
        try (final HistogramDb db = HistogramDb.create(
                tempDir,
                BYTE_BUFFERS,
                BASIC_SETTINGS,
                true)) {
            assertThat(db.count()).isEqualTo(1);

            final Tags tags = getTags();
            // Check exact time states.
            checkHistogram(db, tags, refTime, 1L);

            final HistogramKey key = getKey(refTime);
            final Long value = db.get(key);
            assertThat(value).isNotNull();
            assertThat(value).isEqualTo(1L);

            final FieldIndex fieldIndex = new FieldIndex();
            fieldIndex.create(HistogramFields.KEY);
            fieldIndex.create(HistogramFields.TIME);
            fieldIndex.create(HistogramFields.DURATION);
            fieldIndex.create(HistogramFields.VALUE);
            final List<Val[]> results = new ArrayList<>();
            final ExpressionPredicateFactory expressionPredicateFactory = new ExpressionPredicateFactory();
            db.search(
                    new ExpressionCriteria(ExpressionOperator.builder().build()),
                    fieldIndex,
                    null,
                    expressionPredicateFactory,
                    results::add);
            assertThat(results.size()).isEqualTo(3600L);
            assertThat(results.getFirst()[0].toString()).isEqualTo("captain=kirk, dr=mccoy, lieutenant=uhura, mr=spock");
            assertThat(results.getFirst()[1].toString()).isEqualTo("2000-01-01T00:00:00.000Z");
            assertThat(results.getFirst()[2].toString()).isEqualTo("1s");
            assertThat(results.getFirst()[3].toString()).isEqualTo("1");
        }
    }

    @Test
    void testGetHistogram(@TempDir Path tempDir) {
        final Tags tags = getTags();
        final Instant time = Instant.parse("2000-01-01T00:00:00.000Z");
        try (final HistogramDb db = HistogramDb.create(tempDir, BYTE_BUFFERS, BASIC_SETTINGS, false)) {
            db.write(writer -> {
                final HistogramKey k = getKey(time);
                db.insert(writer, new HistogramValue(k, 1L));
            });
        }

        try (final HistogramDb db = HistogramDb.create(
                tempDir,
                BYTE_BUFFERS,
                BASIC_SETTINGS,
                true)) {
            assertThat(db.count()).isEqualTo(1);
            checkHistogram(db, tags, time, 1L);
        }
    }

    @TestFactory
    Collection<DynamicTest> testMultiWrite() {
        return createMultiKeyTest(1, false, 1);
    }

    @TestFactory
    Collection<DynamicTest> testMultiWritePerformance() {
        return createMultiKeyTest(ITERATIONS, false, ITERATIONS);
    }

    @TestFactory
    Collection<DynamicTest> testMultiWriteRead() {
        return createMultiKeyTest(1, true, 1);
    }

    @TestFactory
    Collection<DynamicTest> testMultiWriteReadPerformance() {
        return createMultiKeyTest(ITERATIONS, true, ITERATIONS);
    }

    Collection<DynamicTest> createMultiKeyTest(final int iterations,
                                               final boolean read,
                                               final long expectedValue) {
        final List<DynamicTest> tests = new ArrayList<>();
        for (final KeyFunction keyFunction : keyFunctions) {
//            for (final ValueFunction valueFunction : HistogramValueTestUtil.getValueFunctions()) {
//            for (final HistogramPeriod period : HistogramPeriod.values()) {

            HistogramPeriod period = HistogramPeriod.HOUR;
            tests.add(DynamicTest.dynamicTest("key type = " + keyFunction +
//                                                      ", Value type = " + valueFunction +
                                              ", Period = " + period,
                    () -> {
                        final HistogramSettings settings = new HistogramSettings
                                .Builder()
                                .keySchema(new HistogramKeySchema.Builder()
                                        .keyType(keyFunction.keyType)
                                        .period(period)
                                        .build())
                                .valueSchema(new HistogramValueSchema.Builder()
                                        .valueType(HistogramValueMax.TWO)
                                        .build())
                                .build();

                        Path path = null;
                        try {
                            path = Files.createTempDirectory("stroom");

                            testWrite(path, settings, iterations,
                                    keyFunction.function,
                                    i -> 1L);
                            if (read) {
                                testSimpleRead(path, settings, iterations,
                                        keyFunction.function,
                                        i -> expectedValue);
                            }

                        } catch (final IOException e) {
                            throw new UncheckedIOException(e);
                        } finally {
                            if (path != null) {
                                FileUtil.deleteDir(path);
                            }
                        }
                    }));
//            }
//            }
        }
        return tests;
    }

    @Test
    void testMerge(@TempDir final Path rootDir) throws IOException {
        final Path dbPath1 = rootDir.resolve("db1");
        final Path dbPath2 = rootDir.resolve("db2");
        Files.createDirectory(dbPath1);
        Files.createDirectory(dbPath2);

        testWrite(dbPath1);
        testWrite(dbPath2);

        try (final HistogramDb db = HistogramDb.create(dbPath1, BYTE_BUFFERS, BASIC_SETTINGS, false)) {
            db.merge(dbPath2);
        }
    }

    @Test
    void testDelete(@TempDir final Path rootDir) throws IOException {
        final Path dbPath = rootDir.resolve("db");
        Files.createDirectory(dbPath);

        testWrite(dbPath);

        try (final HistogramDb db = HistogramDb.create(dbPath, BYTE_BUFFERS, BASIC_SETTINGS, false)) {
            assertThat(db.count()).isEqualTo(1);
            db.deleteOldData(Instant.MIN, true);
            assertThat(db.count()).isEqualTo(1);
            db.deleteOldData(Instant.now(), true);
            assertThat(db.count()).isEqualTo(0);
        }
    }

    @Test
    void testDelete2(@TempDir final Path rootDir) throws IOException {
        final Path dbPath = rootDir.resolve("db");
        Files.createDirectory(dbPath);

        final Instant refTime = Instant.parse("2000-01-01T00:00:00.000Z");
        try (final HistogramDb db = HistogramDb.create(dbPath, BYTE_BUFFERS, BASIC_SETTINGS, false)) {
            insertData(db, refTime, 1L, 100, 60 * 60 * 24);
            insertData(db, refTime, 1L, 100, 60 * 60 * 24);
            insertData(db, refTime, 1L, 10, -60 * 60 * 24);
            insertData(db, refTime, 1L, 10, -60 * 60 * 24);

            assertThat(db.count()).isEqualTo(109L);

            db.deleteOldData(Instant.MIN, true);
            assertThat(db.count()).isEqualTo(109L);

            db.deleteOldData(Instant.MIN, true);
            assertThat(db.count()).isEqualTo(109L);
        }
    }

    private void testWrite(final Path dbDir) {
        final Instant refTime = Instant.parse("2000-01-01T00:00:00.000Z");
        try (final HistogramDb db = HistogramDb.create(dbDir, BYTE_BUFFERS, BASIC_SETTINGS, false)) {
            insertData(db, refTime, 1, 100, 10);
        }
    }

    private void testWrite(final Path dbDir,
                           final HistogramSettings settings,
                           final int insertRows,
                           final Function<Integer, HistogramKey> keyFunction,
                           final Function<Integer, Long> valueFunction) {
        try (final HistogramDb db = HistogramDb.create(dbDir, BYTE_BUFFERS, settings, false)) {
            insertData(db, insertRows, keyFunction, valueFunction);
        }
    }

    private void insertData(final HistogramDb db,
                            final int rows,
                            final Function<Integer, HistogramKey> keyFunction,
                            final Function<Integer, Long> valueFunction) {
        db.write(writer -> {
            for (int i = 0; i < rows; i++) {
                final HistogramKey k = keyFunction.apply(i);
                final Long v = valueFunction.apply(i);
                db.insert(writer, new HistogramValue(k, v));
            }
        });
    }

    private void testSimpleRead(final Path dbDir,
                                final HistogramSettings settings,
                                final int rows,
                                final Function<Integer, HistogramKey> keyFunction,
                                final Function<Integer, Long> valueFunction) {
        try (final HistogramDb db = HistogramDb.create(dbDir, BYTE_BUFFERS, settings, true)) {
            for (int i = 0; i < rows; i++) {
                final HistogramKey key = keyFunction.apply(i);
                final Long value = db.get(key);
                assertThat(value).isNotNull();
                assertThat(value).isEqualTo(valueFunction.apply(i));
            }
        }
    }

    private void insertData(final HistogramDb db,
                            final Instant refTime,
                            final long delta,
                            final int rows,
                            final long deltaSeconds) {
        db.write(writer -> {
            for (int i = 0; i < rows; i++) {
                final Instant time = refTime.plusSeconds(i * deltaSeconds);
                final HistogramKey k = getKey(time);
                db.insert(writer, new HistogramValue(k, delta));
            }
        });
    }

    private void checkHistogram(final HistogramDb db,
                                final Tags tags,
                                final Instant time,
                                final Long expected) {
        final HistogramKey key = new HistogramKey(tags, time);
        final Long state = db.get(key);
        assertThat(state).isEqualTo(expected);
    }

    private record KeyFunction(String description,
                               HistogramKeyType keyType,
                               Function<Integer, HistogramKey> function) {

        @Override
        public String toString() {
            return description;
        }
    }
}
