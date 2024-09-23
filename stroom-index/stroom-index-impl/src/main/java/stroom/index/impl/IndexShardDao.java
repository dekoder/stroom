package stroom.index.impl;

import stroom.entity.shared.ExpressionCriteria;
import stroom.index.shared.FindIndexShardCriteria;
import stroom.index.shared.IndexShard;
import stroom.index.shared.IndexShardKey;
import stroom.index.shared.IndexVolume;
import stroom.query.language.functions.FieldIndex;
import stroom.query.language.functions.ValuesConsumer;
import stroom.util.shared.ResultPage;

import java.util.Optional;

public interface IndexShardDao {

    /**
     * Retrieve a specific Shard by it's ID
     *
     * @param id The Database ID of the shard
     * @return The Shard from the database
     */
    Optional<IndexShard> fetch(long id);

    /**
     * Locate shards based on various criteria
     *
     * @param criteria The details of the query
     * @return Index Shards matching the criteria
     */
    ResultPage<IndexShard> find(FindIndexShardCriteria criteria);

    void search(ExpressionCriteria criteria,
                FieldIndex fieldIndex,
                ValuesConsumer consumer);

    /**
     * Create a new Index Shard
     *
     * @param key           The Index Shard details
     * @param indexVolume   The {@link IndexVolume} to use
     * @param ownerNodeName The node that will own the shard
     * @param indexVersion  The version of the index in use
     * @return The newly created Index Shard
     */
    IndexShard create(IndexShardKey key,
                      IndexVolume indexVolume,
                      String ownerNodeName,
                      String indexVersion);

    /**
     * Delete a specific shard, by it's ID
     *
     * @param id The database ID of the shard to delete
     */
    boolean delete(Long id);

    /**
     * Update the status of a shard
     *
     * @param id     The database ID of the shard to update
     * @param status The new status value
     */
    boolean setStatus(Long id, IndexShard.IndexShardStatus status);

    /**
     * Force the status of a shard to be deleted
     */
    void logicalDelete(Long id);

    /**
     * Reset the status of node to `closed` on startup
     */
    void reset(Long id);

    /**
     * Update the details of the contents of a shard
     *
     * @param id               The database ID
     * @param documentCount    The number of documents
     * @param commitDurationMs commitDurationMs
     * @param commitMs         commitMs
     * @param fileSize         fileSize
     */
    void update(Long id, Integer documentCount, Long commitDurationMs, Long commitMs, Long fileSize);
}
