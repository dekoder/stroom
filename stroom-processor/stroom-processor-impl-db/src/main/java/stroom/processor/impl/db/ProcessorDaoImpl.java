package stroom.processor.impl.db;

import stroom.db.util.ExpressionMapper;
import stroom.db.util.ExpressionMapperFactory;
import stroom.db.util.GenericDao;
import stroom.db.util.JooqUtil;
import stroom.entity.shared.ExpressionCriteria;
import stroom.processor.impl.ProcessorDao;
import stroom.processor.impl.db.jooq.tables.records.ProcessorRecord;
import stroom.processor.shared.Processor;
import stroom.processor.shared.ProcessorFields;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;
import stroom.util.logging.LogUtil;
import stroom.util.shared.ResultPage;

import jakarta.inject.Inject;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import static stroom.processor.impl.db.jooq.tables.Processor.PROCESSOR;

class ProcessorDaoImpl implements ProcessorDao {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(ProcessorDaoImpl.class);

    private static final Function<Record, Processor> RECORD_TO_PROCESSOR_MAPPER = new RecordToProcessorMapper();
    private static final BiFunction<Processor, ProcessorRecord, ProcessorRecord> PROCESSOR_TO_RECORD_MAPPER =
            new ProcessorToRecordMapper();

    private final ProcessorDbConnProvider processorDbConnProvider;
    private final GenericDao<ProcessorRecord, Processor, Integer> genericDao;
    private final ExpressionMapper expressionMapper;
    private final ProcessorFilterDaoImpl processorFilterDao;

    @Inject
    public ProcessorDaoImpl(final ProcessorDbConnProvider processorDbConnProvider,
                            final ExpressionMapperFactory expressionMapperFactory,
                            final ProcessorFilterDaoImpl processorFilterDao) {
        this.processorDbConnProvider = processorDbConnProvider;
        this.genericDao = new GenericDao<>(
                processorDbConnProvider,
                PROCESSOR,
                PROCESSOR.ID,
                PROCESSOR_TO_RECORD_MAPPER,
                RECORD_TO_PROCESSOR_MAPPER);
        this.processorFilterDao = processorFilterDao;

        expressionMapper = expressionMapperFactory.create();
        expressionMapper.map(ProcessorFields.ID, PROCESSOR.ID, Integer::valueOf);
        expressionMapper.map(ProcessorFields.PROCESSOR_TYPE, PROCESSOR.TASK_TYPE, String::valueOf);
        expressionMapper.map(ProcessorFields.ANALYTIC_RULE, PROCESSOR.PIPELINE_UUID, value -> value, false);
        expressionMapper.map(ProcessorFields.PIPELINE, PROCESSOR.PIPELINE_UUID, value -> value, false);
        expressionMapper.map(ProcessorFields.ENABLED, PROCESSOR.ENABLED, Boolean::valueOf);
        expressionMapper.map(ProcessorFields.DELETED, PROCESSOR.DELETED, Boolean::valueOf);
        expressionMapper.map(ProcessorFields.UUID, PROCESSOR.UUID, value -> value);
    }

    @Override
    public Processor create(final Processor processor) {
        // We don't use the delegate DAO here as we want to handle potential duplicates carefully so this
        // behaves as a getOrCreate method.
        // TODO This should be replaced with JooqUtil.tryCreate as ANY error in the sql will be
        //  ignored, not just key duplicates.
        return JooqUtil.contextResult(
                        processorDbConnProvider,
                        context -> {
                            final Optional<ProcessorRecord> optional = context
                                    .insertInto(PROCESSOR,
                                            PROCESSOR.CREATE_TIME_MS,
                                            PROCESSOR.CREATE_USER,
                                            PROCESSOR.UPDATE_TIME_MS,
                                            PROCESSOR.UPDATE_USER,
                                            PROCESSOR.UUID,
                                            PROCESSOR.TASK_TYPE,
                                            PROCESSOR.PIPELINE_UUID,
                                            PROCESSOR.ENABLED,
                                            PROCESSOR.DELETED)
                                    .values(processor.getCreateTimeMs(),
                                            processor.getCreateUser(),
                                            processor.getUpdateTimeMs(),
                                            processor.getUpdateUser(),
                                            processor.getUuid(),
                                            processor.getProcessorType().getDisplayValue(),
                                            processor.getPipelineUuid(),
                                            processor.isEnabled(),
                                            processor.isDeleted())
                                    .onDuplicateKeyIgnore()
                                    .returning(PROCESSOR.ID)
                                    .fetchOptional();

                            if (optional.isPresent()) {
                                final Integer id = optional.get().getId();
                                return context
                                        .select()
                                        .from(PROCESSOR)
                                        .where(PROCESSOR.ID.eq(id))
                                        .fetchOptional();
                            }

                            return context
                                    .select()
                                    .from(PROCESSOR)
                                    .where(PROCESSOR.PIPELINE_UUID.eq(processor.getPipelineUuid()))
                                    .and(PROCESSOR.TASK_TYPE.eq(processor.getProcessorType().getDisplayValue()))
                                    .fetchOptional();
                        })
                .map(RECORD_TO_PROCESSOR_MAPPER)
                .orElse(null);
    }

    @Override
    public Processor update(final Processor processor) {
        return genericDao.update(processor);
    }

    @Override
    public boolean delete(final int id) {
        // We don't want to allow direct physical delete, only logical delete.
        return logicalDeleteByProcessorId(id) > 0;
        //genericDao.delete(id);
    }

    @Override
    public int logicalDeleteByProcessorId(final int processorId) {
        try {
            return JooqUtil.transactionResult(processorDbConnProvider, context -> {
                // Logically delete all the child filters first
                processorFilterDao.logicalDeleteByProcessorId(processorId, context);
                final int count = logicalDeleteByProcessorId(processorId, context);
                return count;
            });
        } catch (final Exception e) {
            throw new RuntimeException("Error deleting filters and processor for processor id " + processorId, e);
        }
    }

    public int logicalDeleteByProcessorId(final int processorId, final DSLContext context) {
        final int count = context
                .update(PROCESSOR)
                .set(PROCESSOR.DELETED, true)
                .set(PROCESSOR.VERSION, PROCESSOR.VERSION.plus(1))
                .set(PROCESSOR.UPDATE_TIME_MS, Instant.now().toEpochMilli())
                .where(PROCESSOR.ID.eq(processorId))
                .and(PROCESSOR.DELETED.eq(false))
                .execute();
        LOGGER.debug("Logically deleted {} processors for processor Id {}",
                count,
                processorId);
        return count;
    }

    @Override
    public int physicalDeleteOldProcessors(final Instant deleteThreshold) {
        final List<Integer> result =
                JooqUtil.contextResult(processorDbConnProvider, context -> context
                        .select(PROCESSOR.ID)
                        .from(PROCESSOR)
                        .where(PROCESSOR.DELETED.eq(true))
                        .and(PROCESSOR.UPDATE_TIME_MS.lessThan(deleteThreshold.toEpochMilli()))
                        .fetch(PROCESSOR.ID));
        LOGGER.debug(() -> LogUtil.message("Found {} logically deleted processors with an update time older than {}",
                result.size(), deleteThreshold));

        final AtomicInteger totalCount = new AtomicInteger();
        // Delete one by one as we expect some constraint errors.
        result.forEach(processorId -> {
            try {
                LOGGER.debug("Deleting processor with id {}", processorId);
                final Integer count = JooqUtil.contextResult(processorDbConnProvider, context -> context
                        .deleteFrom(PROCESSOR)
                        .where(PROCESSOR.ID.eq(processorId))
                        .execute());
                totalCount.addAndGet(count);
            } catch (final DataAccessException e) {
                if (e.getCause() instanceof final SQLIntegrityConstraintViolationException sqlEx) {
                    LOGGER.debug("Expected constraint violation exception: " + sqlEx.getMessage(), e);
                } else {
                    throw e;
                }
            }
        });
        LOGGER.debug(() -> "physicalDeleteOldProcessors returning: " + totalCount.get());
        return totalCount.get();
    }

    @Override
    public Optional<Processor> fetch(final int id) {
        return genericDao.fetch(id);
    }

    @Override
    public Optional<Processor> fetchByPipelineUuid(final String pipelineUuid) {
        Objects.requireNonNull(pipelineUuid);
        return JooqUtil.contextResult(processorDbConnProvider, context -> context
                        .select()
                        .from(PROCESSOR)
                        .where(PROCESSOR.PIPELINE_UUID.eq(pipelineUuid))
                        .fetchOptional())
                .map(RECORD_TO_PROCESSOR_MAPPER);
    }

    @Override
    public Optional<Processor> fetchByUuid(final String uuid) {
        return JooqUtil.contextResult(processorDbConnProvider, context -> context
                        .select()
                        .from(PROCESSOR)
                        .where(PROCESSOR.UUID.eq(uuid))
                        .fetchOptional())
                .map(RECORD_TO_PROCESSOR_MAPPER);
    }

    @Override
    public ResultPage<Processor> find(final ExpressionCriteria criteria) {
        final Condition condition = expressionMapper.apply(criteria.getExpression());
        final int offset = JooqUtil.getOffset(criteria.getPageRequest());
        final int limit = JooqUtil.getLimit(criteria.getPageRequest(), true);
        final List<Processor> list = JooqUtil.contextResult(processorDbConnProvider, context -> context
                        .select()
                        .from(PROCESSOR)
                        .where(condition)
                        .limit(offset, limit)
                        .fetch())
                .map(RECORD_TO_PROCESSOR_MAPPER::apply);
        return ResultPage.createCriterialBasedList(list, criteria);
    }
}
