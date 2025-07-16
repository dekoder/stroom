package stroom.annotation.impl;

import stroom.annotation.shared.Annotation;
import stroom.annotation.shared.AnnotationDecorationFields;
import stroom.annotation.shared.AnnotationTag;
import stroom.annotation.shared.EventId;
import stroom.index.shared.IndexConstants;
import stroom.query.api.SpecialColumns;
import stroom.query.api.datasource.QueryField;
import stroom.query.common.v2.AnnotationMapperFactory;
import stroom.query.common.v2.StoredValueMapper;
import stroom.query.language.functions.FieldIndex;
import stroom.query.language.functions.Val;
import stroom.query.language.functions.ValDate;
import stroom.query.language.functions.ValLong;
import stroom.query.language.functions.ValNull;
import stroom.query.language.functions.ValString;
import stroom.query.language.functions.ref.StoredValues;
import stroom.util.shared.NullSafe;
import stroom.util.shared.UserRef;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationMapperFactoryImpl implements AnnotationMapperFactory {

    private static final Map<String, Function<Annotation, Val>> EXTRACTION_FUNCTIONS = Map.ofEntries(
            createLongFunction(AnnotationDecorationFields.ANNOTATION_ID_FIELD, Annotation::getId),
            createStringFunction(AnnotationDecorationFields.ANNOTATION_UUID_FIELD, Annotation::getUuid),
            createDateFunction(AnnotationDecorationFields.ANNOTATION_CREATED_ON_FIELD, Annotation::getCreateTimeMs),
            createStringFunction(AnnotationDecorationFields.ANNOTATION_CREATED_BY_FIELD, Annotation::getCreateUser),
            createDateFunction(AnnotationDecorationFields.ANNOTATION_UPDATED_ON_FIELD, Annotation::getUpdateTimeMs),
            createStringFunction(AnnotationDecorationFields.ANNOTATION_UPDATED_BY_FIELD, Annotation::getUpdateUser),
            createStringFunction(AnnotationDecorationFields.ANNOTATION_TITLE_FIELD, Annotation::getName),
            createStringFunction(AnnotationDecorationFields.ANNOTATION_SUBJECT_FIELD, Annotation::getSubject),
            createStringFunction(AnnotationDecorationFields.ANNOTATION_STATUS_FIELD, annotation ->
                    NullSafe.get(annotation, Annotation::getStatus, AnnotationTag::getName)),
            createStringFunction(AnnotationDecorationFields.ANNOTATION_ASSIGNED_TO_FIELD, annotation ->
                    NullSafe.get(annotation, Annotation::getAssignedTo, UserRef::getDisplayName)),
            createStringFunction(AnnotationDecorationFields.ANNOTATION_LABEL_FIELD, annotation ->
                    NullSafe.get(annotation, Annotation::getLabels, labels -> labels
                            .stream()
                            .map(AnnotationTag::getName)
                            .collect(Collectors.joining(", ")))),
            createStringFunction(AnnotationDecorationFields.ANNOTATION_COLLECTION_FIELD, annotation ->
                    NullSafe.get(annotation, Annotation::getCollections, collections -> collections
                            .stream()
                            .map(AnnotationTag::getName)
                            .collect(Collectors.joining(", ")))));

    private final Provider<AnnotationService> annotationServiceProvider;

    @Inject
    public AnnotationMapperFactoryImpl(final Provider<AnnotationService> annotationServiceProvider) {
        this.annotationServiceProvider = annotationServiceProvider;
    }

    private static Entry<String, Function<Annotation, Val>> createLongFunction(final QueryField field,
                                                                               final Function<Annotation, Long>
                                                                                       mapper) {
        return Map.entry(field.getFldName(), annotation -> {
            final Long l = mapper.apply(annotation);
            return NullSafe.getOrElse(l, ValLong::create, ValNull.INSTANCE);
        });
    }

    private static Entry<String, Function<Annotation, Val>> createStringFunction(final QueryField field,
                                                                                 final Function<Annotation, String>
                                                                                         mapper) {
        return Map.entry(field.getFldName(), annotation -> {
            final String s = mapper.apply(annotation);
            return NullSafe.getOrElse(s, ValString::create, ValNull.INSTANCE);
        });
    }

    private static Entry<String, Function<Annotation, Val>> createDateFunction(final QueryField field,
                                                                               final Function<Annotation, Long>
                                                                                       mapper) {
        return Map.entry(field.getFldName(), annotation -> {
            final Long l = mapper.apply(annotation);
            return NullSafe.getOrElse(l, ValDate::create, ValNull.INSTANCE);
        });
    }

    @Override
    public StoredValueMapper createMapper(final FieldIndex fieldIndex) {
        final int streamIdIndex = Objects.requireNonNullElse(fieldIndex.getPos(SpecialColumns.RESERVED_STREAM_ID),
                Objects.requireNonNullElse(fieldIndex.getPos(IndexConstants.STREAM_ID), -1));
        final int eventIdIndex = Objects.requireNonNullElse(fieldIndex.getPos(SpecialColumns.RESERVED_EVENT_ID),
                Objects.requireNonNullElse(fieldIndex.getPos(IndexConstants.EVENT_ID), -1));

        if (streamIdIndex == -1 || eventIdIndex == -1) {
            return AnnotationMapperFactory.NO_OP.createMapper(fieldIndex);
        }

        final List<Mutator> mutators = EXTRACTION_FUNCTIONS
                .entrySet()
                .stream()
                .map(entry -> {
                    final String name = entry.getKey();
                    final Function<Annotation, Val> function = entry.getValue();
                    final Integer pos = fieldIndex.getPos(name);
                    if (pos == null) {
                        return null;
                    }

                    return (Mutator) (storedValues, annotation) -> storedValues.set(pos, function.apply(annotation));
                })
                .filter(Objects::nonNull)
                .toList();

        // Don't do any annotation decoration if we were not asked for any annotation fields.
        if (mutators.isEmpty()) {
            return AnnotationMapperFactory.NO_OP.createMapper(fieldIndex);
        }

        final List<Mutator> allMutators;

        // Add annotation id if needed.
        final int idIndex = Objects.requireNonNullElse(fieldIndex.getPos(SpecialColumns.RESERVED_ID),
                Objects.requireNonNullElse(fieldIndex.getPos("Id"), -1));
        if (idIndex != -1) {
            final Function<Annotation, Val> function = annotation -> ValLong.create(annotation.getId());
            allMutators = new ArrayList<>(mutators);
            allMutators.add((storedValues, annotation) -> storedValues.set(idIndex, function.apply(annotation)));
        } else {
            allMutators = mutators;
        }

        final AnnotationService annotationService = annotationServiceProvider.get();
        return new StoredValueMapperImpl(annotationService, streamIdIndex, eventIdIndex, allMutators);
    }

    private record StoredValueMapperImpl(AnnotationService annotationService,
                                         int streamIdIndex,
                                         int eventIdIndex,
                                         List<Mutator> mutators) implements StoredValueMapper {

        @Override
        public Stream<StoredValues> create(final StoredValues storedValues) {
            final Val streamId = (Val) storedValues.get(streamIdIndex);
            final Val eventId = (Val) storedValues.get(eventIdIndex);
            if (streamId == null || !streamId.type().isNumber() || eventId == null || !eventId.type().isNumber()) {
                return Stream.of(storedValues);
            }

            final List<Annotation> list = annotationService
                    .getAnnotationsForEvents(new EventId(streamId.toLong(), eventId.toLong()));
            if (list == null || list.isEmpty()) {
                return Stream.of(storedValues);
            }

            if (list.size() == 1) {
                for (final Mutator mutator : mutators) {
                    mutator.mutate(storedValues, list.getFirst());
                }
                return Stream.of(storedValues);
            }

            return list.stream().map(annotation -> {
                final StoredValues copy = storedValues.copy();
                copy.setPeriod(storedValues.getPeriod());
                for (final Mutator mutator : mutators) {
                    mutator.mutate(copy, annotation);
                }

                return copy;
            });
        }
    }

    private interface Mutator {

        void mutate(StoredValues storedValues, Annotation annotation);
    }
}
