# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/) 
and this project adheres to [Semantic Versioning](http://semver.org/).


## [Unreleased]

~~~
DO NOT ADD CHANGES HERE - ADD THEM USING log_change.sh
~~~


* Issue **#4437** : Fix proxy not handling input files larger than 4 GiB.

* Issue **#4069** : Reduce proxy memory usage.

* Change the hard-coded test credentials to match those in v7.2 so that a test stack with 7.0 proxy and 7.2 stroom can communicate with each other. This change has no bearing on production deployments.

* Issue **#3838** : Change ref data meta store to log a warning rather than error when meta entries are not present. This is consistent with behaviour in v7.2.

* Issue **#3595** : Fix `data()` links when the dashboard is directly linked. Only works for `displayType` of `dialog`, not `tab` as there are no tabs in a direct linked dashboard.

* Issue **#3579** : Fix the `Retention` column on the data viewer screen when the data retention rule contains disabled terms.

* Issue **#3588** : Fix query parameters not being de-referenced in audit logs for dashboard search and download results. Fix double logging of download results.

* Issue **#3504** : Fix XML schema cache deadlock.

* Fix failing build.

* Issue **#3203** : Allow unauthenticated servlets to have paths without `/noauth/` in. Add path specs `/stroom/datafeed` and `/stroom/datafeed/*` for the data receipt servlet in addition to the existing `/noauth/` ones.

* Issue **#3333** : Fix Xerces UTF-8 handling by always using a reader.

* Issue **#3270** : Change the auto logging of audit events to ignore any initiated by the processing user account as this is not human initiated.

* Issue **#3278** : Fix warning about the data retention rule summary query cancellation action not been correctly configured for auto logging.

* Remove thread sleep accidentally added for testing purposes in data retention summary query execution.

* Issue **#3292** : Change the reference data load to not load entries with null values. If `overrideExistingValues` is set and an entry with a null value overrides an existing one with a non-null value, the existing one will be removed.

* Issue **#3299** : Fix schema non-compliant events when importing config. Also fix error handling when user tries to import with no file selected. Improved the audit events for file upload.

* Remove audit logging for `MetaResourceImpl.getTypes` which just lists the stream types and is not an explicit user action.

* Issue **#3305** : Change the formatting of index/data volume limit to show 3 significant figures.

* Issue **#3280** : Make orphan file/meta finder jobs drop out if stroom is shutdown. Change orphan meta finder job to user more efficient SQL. Improve logging output from both finders to include start time, duration and any error/interruption. Fix missing feed names in orphan file finder log.

* Issue **#2759** : Normalise Windows line endings () to Unix-style () when pasting into the ACE text editor.

* Issue **#3272** : Change handling for reference loads in unexpected states. It now logs an app log error but carries on and loads over the top.

* Issue **#3260** : Change the select-all filter based delete/restore to delete by IDs with a temporary table to avoid locking other rows. This is configurable using the property `data.meta.metaStatusUpdateBatchSize`, with 0 meaning all in one batch.

* Issue **#3201** : Change the way the mapping of feed and stream types to IDs is cached. Now uses the existing `Meta Type Cache` and `Meta Feed Cache` caches rather than simple hash maps that duplicated the caching.

* Issue **#3136** : Improve the debug logging for reference data effective streams and add validation of the effective stream sets when debug is enabled for `stroom.pipeline.refdata.EffectiveStreamCache`.

* Remove the read/write locking used on the caches. Change cache rebuild to do a full rebuild if config has changed, else just clear the existing cache.

* Issue **#3286** : Fix error when changing property values for cache properties.

* Issue **#2759** : Change `in dictionary` for filters and searchables to ignore blank lines.

* Issue **#3271** : Fix warnings in logs about auto logging not being configured for reference data purge.

* Issue **#3259** : Make task creation and queueing multi threaded.

* Issue **#3276** : Remove ASSIGNED task status and guard for task creation deadlocks.

* Issue **#3274** : Improve progress monitoring.

* Issue **#3274** : Improve config descriptions.

* Issue **#3259** : Make task creation and queueing multi threaded.

* Issue **#3259** : Make task creation a separate managed job.

* Issue **#3255** : Fix error when creating a new processor filter.

* Issue **#3204** : More changes to improve task creation performance.

* Issue **#3204** : Split UNPROCESSED(1) task state into CREATED(0) and QUEUED(1) to improve task creation performance and management.

* Issue **#3204** : Change processor_filter_tracker to have a status enum and an optional message.

* Issue **#3225** : Improve performance of the database query used to find logically deleted streams for the `Data Delete` job. Add the index `meta_status_status_time_idx` on table `meta`. Add a summary log for the job.

* Issue **#3231** : Change severity from ERROR to WARN when a reference lookup is performed with no reference loaders configured.


## [v7.1-beta.23] - 2023-02-02

* Issue **#3136** : Improve ref data lookup trace logging. Refactor effective streams query.

* De-dup error message text produced by XSLT functions.

* Issue **#3218** : Fix XSLT and dashboard `hash()` functions stripping leading zeros from the hash result.

* Issue **#3221** : Queue all unowned tasks unless associated meta is locked. Tasks for deleted meta will be processed and complete as expected where meta is deleted.

* Issue **#3220** : Fix Info tooltip on Active Tasks sub-tab not showing when stream is not unlocked or is physically deleted.


## [v7.1-beta.22] - 2023-01-27

* Issue **#3204** : Improve task creation logging to list considered filters in order and provide queue information.

* Issue **#3195** : Fix editor highlight positions being incorrectly calculated.

* Issue **#3204** : Fix slow physical deletion of completed/deleted tasks.

* Issue **#3204** : Improve task creation performance. Add system info for inspecting task queues.

* Improve performance of power of ten method in ref data byte buffer pool.

* Issue **#3209** : Fix logging of slow batch SQL.

* Issue **#3192** : Fix duplicate user events for exported processor filters.

* Issue **#3197** : Fix NPE when modifying a property value, (back-porting #3143).


## [v7.1-beta.21] - 2023-01-19

* Add logging of queries that take >2s to execute. Enabled by setting `stroom.db.util.SlowQueryExecuteListener: DEBUG`.

* Add info logging to the export all api resource. Outputs the number of docs export along with counts by type.

* Issue **#3194** : Fix for JDK XML 1.1 parser bug.


## [v7.1-beta.20] - 2023-01-16

* Issue **#3189** : Change effective stream query to use inner joins.


## [v7.1-beta.19] - 2023-01-13

* Issue **#3163** : Add filter to expression field picker.

* Issue **#3171** : Fix stroom booting successfully when there have been failed migrations.

* Issue **#3177** : Stop logical deletes changing the update time if already logically deleted. Add more info logging. Change task name used in logs from `Processor Task Delete Executor` to match job name `Processor Task Retention`.

* Fix property name mentioned in _Processor Task Retention_ job description.

* Issue **#3177** : Fix bad SQL in _Processor Task Retention_ that results in tasks not being deleted. Fix logical deletion of processor filters and processors that result in processor tasks being incorrectly deleted.

* Issue **#3111** : Revert previous change that added double quote handling. Now we only trim leading/trailing whitespace from expression terms.

* Issue **#3180** : Enable "in dictionary" condition for "Id" fields.

* Issue **#1876** : Add `distinct` dashboard expression function for returning a list of unique values.


## [v7.1-beta.18] - 2023-01-06

* Issue **#2819** : Scale query expression panel height so all items can be viewed on scroll.

* Issue **#2860** : Retry (with backoff) when indexing to an Elasticsearch cluster and the cluster reports it is overloaded. This improves indexing success rate, reducing the chance of  streams being generated when a cluster is under heavy load.

* Issue **#3127** : Fix ClassCastException when clearing a cache on the Caches screen.

* Issue **#3143** : Fix NPE when modifying a property value.

* Issue **#3145** : Fix initial API Key expiry so it picks up the default value from config.

* Issue **#3136** : Simplify code that finds effective reference data streams. Add more debug logging.

* Issue **#3159** : Improve appearance of user Task Manager.

* Issue **#3141** : Increase default width of global property editor dialog.

* Issue **#3166** : Fix stream import when the zip contains a mix of single-part and multi-part streams.

* Issue **#3136** : Fix the reference data lookup logic that determines if a ref stream contains a given map or not. Fix NPE in `RefDataLookupRequest#toString()`. Change `ReferenceDataResult` to hold message templates to reduce memory use. Change `RefDataStoreHolder` to only add available maps once per ref stream. Improve in app logging of lookups.

* Issue **#3140** : Ignore processor filter updates on import.

* Issue **#3125** : Fix import rename and move so that imported items end up renamed in explorer and store.

* Issue **#3148** : Fix dependencies screen showing status of Missing for the Ref Data and Dual Searchables. Also fix the missing icon on that screen for Searchables.


## [v7.1-beta.17] - 2022-12-09

* Relax validation requiring proxy repo and failed retry directories to exist before proxy boots. Now checks they exist (creating if they don't) at time of use.


## [v7.1-beta.16] - 2022-12-08

* Issue **#3031** : Add connectors to poll from AWS SQS.

* Issue **#3066** : Make all responses set the HTTP header `Strict-Transport-Security` to force all HTTP traffic on the domain onto HTTPS. Also add property `stroom.security.webContent.strictTransportSecurity` to configure the header value.

* Issue **#3074** : Fix data retention summary and purge job when condition includes a pipeline. Fix data viewer screen to allow filtering by pipeline. Fix filtering using the `in folder` condition. Add `<`, `<=`, `>`, `>=`, `between` conditions to ID fields, e.g. stream ID. **WARNING**: the expression field `Pipeline` has had the following conditions removed; `in`, `in dictionary`, `=`, `contains` and the field `Pipeline Name` has been added with the following conditions; `in`, `in dictionary`, `=`. This may impact processor filters or retention rules that use the `Pipeline` field. See the SQL at https://github.com/gchq/stroom/issues/3074 to find any processor filters using this field with a now un-supported condition. Change the Reference Data searchable data source to support `in`, `in dictionary` and wild carding.

* Fix bug in quick filter when user enters two identical tokens into a quick filter, e.g. `bob bob`.

* Issue **#3111** : Trim leading/trailing white space from term values in the expression tree builder. Users can keep leading/trailing white space if they double quote the value, e.g. `" some text "`. If the value needs to include a double quote then it can be escaped with a `\` like this `I said \"hello\"`.

* Improve description for `useJvmSslConfig` property on `HttpAppender`.

* Issue **#3091** : Add feature to optionally maintain import names and paths.

* Issue **#3101** : Add sensible defaults to processor filter import.

* Issue **#2491** : Add feature to allow a specific folder to be used as an import root folder destination.

* Issue **#3038** : Fix number 2 for intermittent websocket alert.

* Issue **#3097** : Fix Kryo output buffer size = -1 when writing to streams.

* Issue **#3073** : Add string truncation and value length protection for search result fields.

* Issue **#3038** : Fix intermittent websocket alert.

* Issue **#3087** : Stop URI generation adding port 443 unnecessarily.

* Issue **#3090** : Fix import of documents from 5.5 that contain data.

* Issue **#3084** : Add the config property `stroom.data.meta.rawMetaTypes` to allow custom stream types (as defined in `stroom.data.meta.metaTypes`) to be categorised as _raw_ types. Also add `Data Encoding` to the Info tab on the Data screen to show the Data Encoding value set on the feed's settings.

* Issue **#3069** : Fix import of a Feed's Stream Type when the export was made in Stroom v5. Add validation to fail the import if the stream type in the export is not in `stroom.data.meta.metaTypes`. Fix word wrapping on the import error messages tooltip.

* Remove duplicate guice binds.

* Issue **#3078** : Uplift apache commons-text to v1.10.0 to address vulnerability CVE-2922-42889.

* Fix typo in description for property `stroom.ui.applicationInstanceKeepAliveIntervalMs`.

* Issue **#3038** : Improve error handling of web socket code to prevent alerts for expected error conditions.

* Issue **#3056** : Make `View As Hex` editor option available in the Source view (but not when stepping). Change hex dump to use uppercase hex values.

* Issue **#3063** : Fix the conversion of bytes to values like `1.9K` to not always round down.

* Add trace logging to volume selectors.

* Issue **#3014** : Add `Index Shards` searchable datasource so you can search shards on a dashboard.

* Issue **#3016** : Evict items from XSLT pool cache when an XSLT doc is changed. Also evict XSLTs that import/include the one that has changed.

* Improve warning message for ref streams with the same effective date.

* Issue **#3027** : Replace processor_task index on status with one on status and create_time_ms.

* Issue **#3032** : Improve performance of Orphan Meta Finder job. Also add new `fs_orphaned_meta_tracker` table to track progress.

* Remove `Eviction Weight` from the cache stats table as it is meaningless when we do not set custom cache item weights.

* Issue **#3034** : Change FS/Index volumes free/used to be based on limit if set. Implement volume selector for index volumes. Add a local volume map to cache index volumes/groups. Fix validation when creating/editing index volumes. Add creation/validation of index volume path to the index volume edit screen. Add validation of FS volumes. Make volumes with relative paths be relative to stroom.home. Change index volume list sort order to Node|Path and make Node the first column.

* Issue **#3043** : Fix SQL error when creating a batch search.

* Change cluster lock mechanism to keep trying to get the lock rather than timing out after 30s. Now times out after 30mins.

* Issue **#3048** : Prevent copying of feeds in the explorer tree. Feeds already cannot be renamed so there is no value in allowing copy if the new copy cannot be renamed.

* Issue **#3052** : Change `Reference Data - Effective Stream Cache` to use a default `expireAfterWrite` of `10m` rather than `expireAfterAccess`.

* Issue **#3051** : Fix `refData/(purgeByAge|purgeByStream|clearBufferPool)` API calls so they process all nodes concurrently.

* Issue **#2992** : Change the cache clear button to clear the cache and rebuild it from current config values.

* Add `Property Path` column to the Caches screen.

* Issue **#3055** : Change `Document Permission Cache` to be expireAfterWrite 30s. Add change handlers to invalidate entries in `Pipeline Structure Cache` when any pipeline in the inheritance chain is changed. Remove unused cache `Index Shard Searcher Cache` and associated job `Index Searcher Cache Refresh`. Add change handlers on `User` entity to `User App Permissions Cache` & `User Cache`.

* Issue **#3057** : Change InvalidXmlCharFilterReader to filter out restricted control characters.


## [v7.1-beta.15] - 2022-08-17

* Issue **#3002** : Fix bootstrap process for deployment of a new version.

* Issue **#3001** : Fix the Source display when stepping context data.

* Issue **#2988** : Remove mention of hex viewer in the error message when viewing raw data that can't be decoded in the stepper. Also ensure stepping up to the un-decodable data works ok.

* Issue **#3018** : Fix incorrect cast of a SQLException to an InterruptedException.

* Issue **#3011** : Fix issue of data being truncated in text pane before the pipeline is run.

* Issue **#3008** : Fix `current-user()` call not returning anything when used in a pipeline on a dash text pane.

* Issue **#2993** : Add sorting to columns on Nodes screen.

* Issue **#2993** : Add column sorting to Jobs screen. Set default sort to node name.

* Issue **#2867** : Stop the XML formatter used in the data preview from formatting non-XML data as XML when it finds angle brackets in the data.

* Change the hex dump display to render single bytes using US_ASCII instead of UTF8.

* Issue **#3028** : Catch entity change events so modified feed entities are removed from the cache. Also update the data display if a feed's encoding is changed.

* Issue **#3031** : Add connectors to poll from AWS SQS.

* Add system info for pool caches, e.g. XSLT pool cache.

* Add button on Caches screen to evict expired entries. Move cache stats from info icon to table columns. Add Hit ratio figure.

* Issue **#2995** : Remove old_index_id column from v_index_shard view. Remove unused old_index_id column from index_shard table (if it exists).

* Add system info for listing keys of a named cache.

* Issue **#2997** : Stop other nodes booting if migration fails on the first node.


## [v7.1-beta.14] - 2022-07-20

* Issue **#2998** : Add feature to receive individual accounting events.


## [v7.1-beta.13] - 2022-07-19

* Issue **#2969** : Fix search termination for Elastic searches.

* Issue **#2981** : On-heap searches now complete automatically once the table result limit is reached.

* Issue **#2030** : Add stats for reference data and search LMDB off heap store sizes on disk. Requires the import of content pack internal-statistics-sql-v2.2.

* Issue **#2942** : Add `v_fs_volume`, `v_doc`, `v_feed_doc`, `v_index_volume`, `v_job_node`, `v_processor_task` & `v_permission` DB views. Add `id` col to `v_meta` DB view.

* Issue **#2978** : Handle lock wait errors when waiting for bootstrap lock. It now keeps retrying until it gets the lock.

* Issue **#2985** : Add warning when caches evict items due to size constraint.

* Issue **#2987** : Fix search termination.

* Issue **#2984** : Change the purge of partial ref loads to happen as part of the purge job and not on boot.

* Issue **#2977** : Fix to destroy stale searches.


## [v7.1-beta.12] - 2022-07-05

* Issue **#2834** : Fix `countGroups` search table expression.

* Issue **#2961** : Fix sorting on selector and countGroups columns.

* Issue **#2897** : On boot, delete ref streams from the store that have a state of LOAD_IN_PROGRESS or PURGE_IN_PROGRESS.

* Issue **#2965** : Fix server tasks paging.

* Issue **#2964** : Fix server tasks paging.

* Issue **#2967** : Change application instance message.

* Issue **#2966** : Fix interrupts being ignored in stats aggregation and improve task info messages.

* Issue **#2834** : Fix `countGroups` search table expression.

* Issue **#2961** : Fix sorting on selector and countGroups columns.

* Issue **#2960** : Stop releasing owned tasks under lock.

* Issue **#2939** : Remove duplicate createTime and updateTime doc properties.

* Issue **#2959** : Turn off source line numbering in Saxon.

* Issue **#2939** : Add DB migration to remove duplicate keys from `doc.data` json column.

* Issue **#2966** : Add batching to stage 2 stats aggregation. Add new property `statisticAggregationStageTwoBatchSize`. Remove interruption checks in stats flush to avoid loss of data.

* Improve error messages for ref data value deserialisation.

* Issue **#2938** : Fix StreamId error when querying annotations data source.

* Issue **#2944** : Truncate error messages for processor_filter_tracker status to fit DB.

* Issue **#2935** : Remove query UUID in query download.

* Issue **#2945** : Fix deadlock updating processor task by updating individually.

* Issue **#2946** : Add property `statisticFlushBatchSize`.

* Issue **#2946** : Change SQL Statistics flush to use single large prepared statements for more efficient inserts to SQL_STAT_VAL_SRC.

* Issue **#2954** : Stop logging SQL exceptions as errors in JooqUtil.

* Issue **#2948** : Processing tasks will complete normally when data deleted.

* Issue **#2946** : Add statistics properties `inMemAggregatorPoolSize`, `inMemPooledAggregatorSizeThreshold`, `inMemPooledAggregatorAgeThreshold` and `inMemFinalAggregatorSizeThreshold`.

* Issue **#2931** : Set cluster state update frequency to 1m.

* Issue **#2933** : Change structure of config object for http-call function to allow setting various HTTP client configuration properties, including HTTP protocol version.

* Issue **#2902** : Release queued tasks if no longer master and from dead nodes.

* Issue **#2925** : Remove order by from Attribute Value Data Retention job to try to speed it up. Also improve logging for the job.

* Issue **#2924** : Fix feed name resolution from UUID in stream appenders.

* Issue **#2870** : Fix delete of old processor filters and trackers.

* Issue **#2912** : Prevent users from renaming feeds as this breaks the link with the files stored on the file system.

* Issue **#2903** : Fix pipeline structure inheritance to hide dead inherited links.

* Issue **#2906** : Fix rolling appenders failing due to "no logged in user".

* Issue **#2916** : Change LMDB thread interrupted messages from ERROR to DEBUG.

* Issue **#2914** : Propagate useAsRead to sub tasks.

* Issue **#2877** : Handle missing index when performing shard retention.

* Issue **#2896** : Improve application instance error handling.

* Issue **#2900** : Move stream type to file extension mappings into config (`stroom.data.filesystemVolume.metaTypeExtensions`) to allow use of legacy file extensions.

* Issue **#2906** : Fix RollingStreamAppender failing to roll on timed basis.

* Issue **#2904** : Fix NPE when setting the feed to null on the StreamAppender.

* Issue **#2901** : Change stream type drop downs on feed setting and stream upload to included all stream types.

* Change start.sh in stroom docker image to accept multiple arguments to support command utilities.

* Issue **#2872** : Fix permission exception thrown when removing search store from cache.

* Issue **#2889** : Change pipeline entity deletion to also logically delete the processor, processor filters and any unprocessed tasks.

* Change the shard system info provider to only query the shard if the node has ownership.

* Issue **#2879** : Prevent shards closing during read.

* Issue **#2879** : Prevent interrupts during index shard use.

* Issue **#2888** : Fix error when paging and then filtering on the dependencies screen.

* Add system info provider for index shards.

* Issue **#2883** : Fix issue of search errors not being shown in the UI.

* Issue **#2879** : Prevent interrupting threads that may be reading search index shards.

* Issue **#2874** : Add debug logging for shard writing/reading.

* Issue **#2881** : Fix broken CLI commands, e.g. `reset_password`.

* No changes, failed build.

* Issue **#2855** : Add property `stroom.index.writer.slowIndexWriteWarningThreshold` to configure the threshold for warning about slow index shard writes.

* Issue **#2856** : Strip `[` and `]` from IPv6 addresses in the logged events to ensure schema compliance.

* Issue **#2857** : Add `/stroomAdmin/filteredhealthcheck` and `/proxyAdmin/filteredhealthcheck` servlets to allow filtering of the health checks that are run.

* Issue **#2863** : Fix UI hanging when data viewer navigation buttons are clicked repeatedly and very quickly.

* Issue **#2839** : Fix invalid event XML generated when adding user perms to an entity with no perms (i.e. System entity).

* Issue **#2843** : Fix NPE when logging in to a user with no password.

* Increase column width for _Sign in Failures_ column on Manage Accounts screen so the sort icon is visible.

* Issue **#2840** : Fix various issues with sorting on Manage Accounts screen.

* Issue **#2841** : Fix issues with sorting/filtering on API Keys screen.

* Increase User Id column width on API Keys screen.

* Issue **#2847** : Fix download of dashboard query and results.

* Issue **#2838** : Change web socket code to avoid errors for expected cases.

* Issue **#2851** : Add configuration property `stroom.statistics.sql.slowQueryWarningThreshold` to configure slow statistics sql query warning threshold.

* Issue **#2827** : Fix Format feature in editor when comment contains unmatched double quote.

* Issue **#2830** : Change logging of document permission changes to log a single event containing the full before/after state of the doc's perms.

* Issue **#2830** : Ensure the creation of a stroom user record is only logged once.

* Issue **#2830** : When cascading document permissions, only log the change for the top level but mark it with the cascade setting.

* Issue **#2816** : Fix missing navigation controls when viewing a multi part stream where a middle part is binary and the rest are text.

* Add missing Singlton annotation to ProxyConfigProvider.


## [v7.1-beta.11] - 2022-04-01

* Issue **#2749** : Improve support for  and  fields when searching an Elasticsearch index.

* Issue **#2822** : Add application instance management to keep track of active queries.

* Issue **#2822** : Add application instance management to keep track of active queries.

* Issue **#2824** : Fix to stop SearchableStore interrupting threads that it is no longer using.

* Issue **#2822** : Create a managed search UUID in the web socket before starting search.

* Issue **#2817** : Change data retention impact summary to show the counts of records that would be deleted now rather than at some point in the future.


## [v7.1-beta.10] - 2022-03-18

* Issue **#2725** : Support multiple levels of nesting in Elasticsearch indexing.

* Issue **#2730** : Fix folder presenter Processors tab always being empty.

* Issue **#2759** : Normalise Windows line endings () to Unix-style () when pasting into the ACE text editor.

* Issue **#2815** : Ensure early termination of Lucene searches when required.

* Issue **#2801** : Add Guice support and authorisation to web sockets.

* Issue **#2804** : Ensure unowned tasks are added even when there are many associated locked meta records.

* Issue **#2807** : Show all nodes on Index Volume edit dialog, not just enabled.

* Change the common alert dialog to show the detail by default.

* Change the common alert dialog so the width is the same if detail is shown or not.

* Issue **#2806** : Fix NPEs when a visualisation pane is present but the visualisation is not defined. Show warning about the misconfiguration.

* Add _Dual_ searchable datasource to always return a single column/row.

* Issue **#2810** : Fix display of error streams with the data() link expression.

* Issue **#2801** : Add our own native web socket code to GWT.

* Fix failing test.

* Issue **#2801** : Add `connect-src 'self' wss:;` to default `contentSecurityPolicy` property, select web sockets ws/wss based on current scheme and improve logging.

* Issue **#2618** : Fix UI to show all caches even ones that only exist on remote nodes.

* Issue **#2801** : Add Web Socket mechanism to keep search results active.

* Issue **#2805** : Fix default annotation expression function.

* Issue **#2778** : Simplify search result store cache and query UUID creation.

* Ensure meta types retrieved from config are trimmed.

* Issue **#2802** : Ensure tasks are created but only queued if fill task queue enabled.

* Issue **#2805** : Fix default annotation expression function.

* Make application fail to start if there is no build version/date.

* Issue **#2799** : Add protection for null/empty meta types in `stroom.data.meta.metaTypes`.

* Issue **#2795** : Speculative fix for the bind errors on MetaTypeDaoImpl.
  

* Issue **#2782** : Fix validation of primitive properties, e.g. booleans.

* Issue **#2782** : Fix property edit screen `Sources` field showing `Multiple Sources` for a single node.

* Issue **#2787** : Add config_update_tracker table to improve the process of updating each node's effective config. Also fix the updating of the effective value/source columns in the properties list view.

* Issue **#2786** : Handle multiple IPs in X-FORWARDED-FOR header.

* Issue **#2789** : Add locking to ensure one node performs the DB migrations and all others wait for it to complete. DB migration checks will not be run on boot if the build version matches that in the bootstrap_lock table.

* Issue **#2789** : Make the creation of the admin and processing users resiliant to multiple nodes doing it at once.

* Issue **#2768** : Suppress unable to keep alive message.

* Issue **#2775** : Ensure all URL params are encoded.

* Issue **#2768** : Fix auto recurring searches.

* Issue **#2768** : Fix auto recurring searches.

* Issue **gchq/stroom#2578** : Improve logging of Pipeline operations.

* Issue **#2768** : Fix auto recurring searches.

* Issue **#2743** : Reset reactivation time when accounts are reactivated.

* Issue **#2742** : Ensure meta queries provide unique results.

* Issue **#2751** : Change task name for searchables to be the searchable name. Ensure task info is populated.

* Issue **#2757** : Make ref load throw exception (and thus result in an ERROR) when a stream previously failed.

* Issue **#2753** : Change default proxy config to set useDefaultOpenIdCredentials to false.

* Issue **#2761** : Fix descriptions of _Attribute Value Data Retention_ and _Processor Task Retention_ jobs.

* Issue **#2744** : Fix logging for expected interrupt exception.

* Issue **#2746** : Fix broken help links in UI.

* Change byte buffer pool clear method to unmap drained buffers.

* Move some LMDB related classes.

* Issue **#2736** : Fix missing or wrongly appearing meta/context tab.

* Issue **#2732** : Fix bug when opening active tasks tab for an empty folder.

* Issue **#2734** : Improve search task info.

* Issue **#2733** : Stop search buttons disappearing.

* Issue **#2735** : Fix batch search across remote nodes.

* Issue **#2739** : Add support for query parameters when performing batch searches.

* Issue **#2520** : Add token authentication to data feed API.


## [v7.1-beta.9] - 2022-01-20

* Make temp prop nullable and change default to TEMP/stroom(-proxy)?.

* Issue **#2709** : Reduce the number of UI refreshes when receiving data from multiple nodes.

* Issue **#2708** : Keep search results fresh by constantly pinging result caches for all active searches.


## [v7.1-beta.8] - 2022-01-17

* Issue **#2715** : Fix invalid rest return type.


## [v7.1-beta.7] - 2022-01-14

* Issue **#2640** : Support adding formatted dates to index name in ElasticIndexingFilter.

* Issue **#2675** : Improve Elasticsearch scroll query performance

* Issue **#2677** : Respect column formatting in Excel cell styles.

* Issue **#2703** : Improve search trace log to identify multi query search issues.

* Issue **#2705** : Attempt to fix SIDSEGV issue caused by too many LMDB readers.

* Issue **#2688** : Add `metaTypes` to Proxy's `proxyRequestConfig`. Change data receipt to validate types against this new config property.

* Issue **#2688** : Change defaults for Strooms `metaTypes` config property. Change type of `metaTypes` from String to a Set of Strings. Change data receipt to validate against this configured set.

* Issue **#2694** : Improve error handling when search result store has reached max capacity.

* Issue **#2689** : Add logging.

* Issue **#2679** : Add unique index on `meta_processor.pipeline_uuid`.

* Issue **#2596** : Disable the Ace editor settings menu (`ctrl-,`) as users should not have access to it and it does not work with modal dialogs.

* Issue **#2139** : Add DB migration scripts to the ZIP distribution.

* Issue **#2685** : Fix issue where searchable stores are limited to 1 million rows.

* Issue **#2696** : Change the byte buffer pool to not block by default and make that configurable.

* Issue **#2666** : Fix handling of missing reference stream file.

* Change error table to wrap error message text.

* Issue **#2644** : Fix error handling when volume is full during proxy aggregation.

* Issue **#2650** : Fix handling of negative numbers and negation in expression parser.

* Issue **#2664** : Fix issue with search where a complete coprocessor was causing other coprocessor payloads to be read incorrectly.

* Issue **#2648** : Add error message to data viewer when ID is invalid.

* Issue **#2657** : Change classes to use provided config to stop the system using stale config values.

* Issue **#2603** : Suppress expected buffer underflow exception.

* Make directly injected NotInjectable config throw an exception to prevent it happening.

* Issue **#2652** : Add delay to stream event map creation so extraction opens fewer streams.

* Issue **#2639** : Fix error message when invalid property values are used.

* Issue **#2578** : Fix how application config is (re)loaded so that it copes with null or sparse branches.

* Issue **#2361** : Fix create_account command so it allows creation of accounts with no password.

* Issue **#2627** : Add processor filter and processor task id to meta.

* Issue **#2628** : Add processor filter and processor task id to info log.

* Issue **#2647** : Fix `Unrecognised permission assigned` error in logs.

* Issue **#2629** : Change the way duplicate output is handled by deleting previous output from complete tasks at the end of processing.

* Issue **#2603** : Add debug to diagnose buffer underflow.

* Issue **#2637** : Fix hanging searches.

* Issue **#2631** : Add debug to diagnose UI issue.

* Issue **#2633** : Fix shutdown.

* Issue **#2632** : Suppress warnings.

* Issue **#2603** : Improve error handling.

* Issue **#2635** : Improve search completion code.

* Issue **#2625** : Disable test temporarily to fix build.

* Issue **#2630** : Fix NPE and improve logging.

* Issue **#2625** : Disable test temporarily to fix build.

* Issue **#2615** : Fix meta status change triggered by check superseded.

* Issue **#2614** : Fix NPE.

* Issue **#2621** : Improve search performance.

* Issue **#2617** : Fix pipeline data equality for pipeline structure cache to be useful.

* Issue **#2619** : Improve meta DAO performance.

* Issue **#2611** : Fix java lang error.

* Issue **#2608** : Fix segments for rolled streams created using the `StreamAppender`.

* Issue **#2604** : Add code to diagnose unexpected event counts in search extraction.

* Issue **#2605** : Create sub tasks for all search processes.

* Issue **#2606** : Improve error handling during search.

* Issue **#2472** : Fix the way the in memory config is updated by the file monitor. Improve the logging when config properties are changed on a node.

* Issue **#2595** : Improve search performance and fix issues.

* Issue **#2471** : Changes to ensure SQLite DB connection is only used by one process at a time.

* Issue **#2471** : Changes to help diagnose proxy aggregation issues.

* Issue **#2582** : Remove unnecessary legacy migration for processor filter.

* Issue **#2577** : Fix issue with expression terms changing conditions incorrectly.

* Issue **#2585** : Fix proxy aggregation task nesting.

* Issue **#2574** : Improve processor filter creation to allow min and max create times.

* Issue **#2579** : Fix processor filter creation from stream multi selection.

* Issue **#2580** : Fix completion of processor filters by allowing the user to specify an end time.

* Issue **#2576** : Fix scrolling of processor filter info pane.

* Uplift event-logging library to `5.0-beta.27_schema-v4.0-beta.3` to fix missing failure outcomes on logged events.

* Issue **#2557** : Optimise meta queries to ensure join order is as expected.

* Issue **#2538** : Change logging of quick filter searches to log the fully qualified filter input.

* Issue **#2565** : Stop orphan file finder reporting dirs that contain child dirs as empty.

* Issue **#2563** : Fix bad expression logic.

* Issue **#2562** : Fix NPE in the UI related to uninitialised processor filter trackers.

* Stop logging audit events for most NodeResource call as they are not direct user actions.

* Issue **#2553** : Prevent get and clear methods in LMDB data store from running at the same time. Add check for the LMDB env being closed to prevent JVM crash.

* Issue **#2555** : Remove checkbox from dead tasks in the server tasks screen to stop users trying to delete them.

* Issue **#2564** : Improve search performance.

* Issue **#2582** : Fix DB migration for `processor_filter`.

* Issue **#2542** : Improve autologged searches "raw" JSON.

* Issue **#2534** : Implement ProcessorResource.fetch to fix event log.

* Issue **#2533** : Fix corrupt event format for logout due to NPE.

* Issue **#2530** : Fix issue of explorer tree not refreshing on copy, move and delete when auth is disabled.

* Add wait time debug logging around LMDB locks.

* Increase maxReaders default to 150 for reference data.

* Issue **#2548** : Clear contents of `stroom.search.resultStore.lmdb.localDir` on boot.

* Issue **#2549** : Remove `maxDbs` from `lmdb` config. Remove `readerBlockedByWriter` from `resultStore.lmdb` config.

* Issue **#2544** : Add additional constraints to processor filter instead of using tracker state

* No changes. Previous build failed due to networking issues.

* Issue **#2540** : No longer error when interrupting shard flush process

* Issue **#2541** : Stop finished queries from deleting LMDB envs for all queries.

* Issue **#2535** : Added validation to data type names.

* Issue **#2530** : Fixed explorer refresh on copy and move

* Revert accidental whitespace change to V07_00_00_007__meta_retention_tracker.sql

* Issue **#2519** : Added validation for processor filter expressions and more info about filters to the UI.

* Issue **#2501** : Change reference data store to use consistent approach to last access time truncation.

* Issue **#2424** : Change security filter to 404 any unexpected URIs.

* Issue **#2493** : Fix missing part nav controls when data can't be decoded.

* Issue **#2497** : Added summary to orphan file finder. Fixed issue with `OrphanFileFinder` incorrectly identifying some dirs as being empty.

* Issue **#2500** : Add a primary key to the `meta_retention_tracker` table for MySQL Group Replication.

* Change meta retention tracking to track at the time period level so a killed job preserves the position of the periods already processed.

* Issue **#2513** : Fixed stepping to unique values.

* Issue **#2496** : Fixed issue where data browser was showing duplicate streams.

* Issue **#2511** : Fixed stepping error handling.

* Issue **#2512** : Fixed stepping error handling.

* Issue **#2478** : Create a single place in config for the LMDB library path and extraction dir. Delete old LMDB library binaries on boot.

* Change `/api/refData/v1/purgeByAge/{purgeAge}`, `/api/refData/v1/purgeByStream/{refStreamId}` and `/api/refData/v1/clearBufferPool` to act on all nodes unless the `nodeName` query param is provided.

* Issue **#2483** : Change reference data purge to delete entries in batchs to avoid large transactions that result in errors.

* Issue **#2502** : Fix `Cannot read properties` error when switching streams after deleting one.

* Issue **#2449** : Reduce the number of REST calls that property filtering in the UI makes.

* Issue **#2523** : Uplift standard-pipelines to v0.4

* Issue **#2527** : Fix stepping source hightlight for first record of fragment XML data.

* Issue **#2526** : Fix incorrect input pane in stepper for cooked xml

* Issue **#2531** : Fix incorrect value in Retention column in data browser and on Info tab when `Feed is` conditions are used.

* Issue **#2532** : Change logged event for Manage Accounts quick filter use.

* Fix performance issue with ref data range lookups.

* Change default value for `stroom.pipeline.referenceData.readerBlockedByWriter` to true.

* Fix locking for `stroom.pipeline.referenceData.readerBlockedByWriter`.

* Issue **#2494** : Changed logging to help diagnose problem.

* Issue **#2429** : The server tasks screen now handles errors that occur when trying to contact unreachable nodes.

* Issue **#2492** : Fixed issue getting filter priorities in processor task data store.

* Issue **#2489** : Change to wrap long error messages in stepping display.

* Issue **#2487** : Fixed issue flushing and deleting index shards.

* Issue **#2442** : Removed enabled/disabled states for permission users and user groups as these are now controlled by account authentication. 

* Issue **#2431** : Fixed issue with item selection in account and token list pages.

* Issue **#2484** : Fix failing ref lookups when one loader has range data.

* Issue **#2485** : Autologger: UNLOGGED calls always being logged.

* Issue **#2395** : Fixed timezone issue in API key display.

* Issue **#2452** : New pipeline references now default to `Reference` data type.

* Issue **#2414** : The processing tasks data source now exposes start, end and status times plus filter priorities.

* Issue **#2441** : Improve logging and prevent autologger warning for `MetaResourcImpl`

* Issue **#2440** : Prevent autologger warning for `ExplorerResourceImpl`

* Issue **#2465** : `SaveAs` is now working for non admin users.

* Issue **#2460** : Processing filters now reliably process feeds where wildcards are used to match feed names.

* Issue **#2475** : Orphan file and meta finder now show the correct task progress.

* Issue **#2474** : Fixed orphan file finder.

* Issue **#2467** : Fix viewing of streams that can't be decoded. Errors now displayed in large banner rather than as text in the editor.

* Add a view as hex option to the Data Preview panes.

* Add option to show/hide the editor indent guides.

* Remove white space on editor context menu.

* Change data viewing 'progress' bar to have a minimum width of 3px to make it more visible.

* Issue **#2469** : Made `SafeXmlFilter` available for use in the application.

* Issue **#2403** : Fix guice bind errors in stroom and proxy when the `path` config branch is empty or set to null.

* Issue **#2462** : Made changes to stop DB connections being used within the context of other open connections.

* Uplift LMDBJava to 0.8.2 to fix LMDBJava cursor comparator bug.

* Issue **#2464** : Add `leakDetectionThreshold` to the hikari pool config.

* Issue **#2463** : Integrate the hikari connection pool with drop wizard's health checks and metrics.

* Add debug logging to AuthenticationStateSessionUtil

* Issue **#2448** : Change data receipt ERROR log messages to WARN. Also improve log message content for success and failure.

* Issue **#2412** : You are now able to view locked streams or deleted data if it is still accessible.

* Issue **#2413** : Removed duplication of data retention fields in info pane.

* Issue **#2443** : Internal meta statistics are now added with processing user permissions.

* Issue **#2455** : Improved error handling when streams not found.

* Issue **#2399** : Now changing expression fields keeps the same condition if it is still applicable to the new field.

* Issue **#2426** : Fixed user selection problem for document permissions.

* Issue **#2436** : Fixed small UI issue whereby a user or group was not immediately removed from document permissions when the remove button was clicked.

* Issue **#2416** : Added logging to identify cause of slow meta listing.

* Improve termination handling in reference data purge.

* Improve the data in the `/api/refData/v1/refStreamInfo` api method.

* Add API method to clear the byte buffer pool.

* Improve the output of the system info api call for the byte buffer pool.

* Issue **#2439** : Change log level for an error in the byte buffer pool.

* Issue **#2444** : Fix release of buffers to the pool that was causing ref data searches to hang.

* Issue **#2433, #2434** : Fixed shutdown task order.

* Issue **#2430** : Removed file system clean task and replaced with orphan finding jobs for files and meta.

* Issue **#2432** : Volume state is now updated without optimistic locking.

* Issue **#2382** : Improved charset resolution.

* Issue **#2447** : Correctly offset displayed date/time values by the configured user timezone.

* Issue **#2457** : Display a tooltip when hovering the mouse over a grid column cell.

* Issue **#2382** : Improve error message for invalid feed encodings.

* Issue **#2387** : Fix reference data load/lookup failure handling.

* Issue **#2422** : Change ref lookups to only do a lookup against a ref feeds that are known to contian the map being looked up against.

* Issue **#2411** : Remove 10,000 limit on dashboard search of ref store.

* Issue **#2389** : Add an API method for purging a single reference data stream.

* Issue **#2379** : Change ref data lookups to truncate last access time to hourly.

* Uplift Dropwizard from 1.3.14 to 1.3.29.

* Issue **#2424** : Stop session creation for rest calls. Remove unused SessionMap class.

* Change debug summary logging in Data Delete job to info level.

* Issue **#2402** : Improved logging for error caused by invalid meta filter values.

* Issue **#2404** : Added debug to help diagnose issue.

* Issue **#2423** : Added error logging and configuration to handle buffer overflows when dealing with large search result values.

* Issue **#2410** : Fixes for dashboard child selectors like `first()` and `last()`.

* Issue **#2417** : Bad regex filter value no longer logged unnecessarily.

* Issue **#2418** : LMDB environment is now only closed when the search results are no longer needed.

* Issue **#2419** : Conditional formatting errors are now returned to the UI.

* Issue **#2405, #2407** : Errors caused by a thread interrupt when viewing a stream are no longer logged.

* Issue **#2406** : Volume status update is now performed synchronously.

* Issue **#2401, #2408, #2409** : Processing tasks no longer terminate by interrupting threads so error streams can now be written correctly.

* Issue **#2398** : Fix to clear interrupt state for threads used by terminated tasks.

* Issue **#2396** : Add `stroom:pointIsInsideXYPolygon` XSLT function.

* Allow HTTP request headers to be customized in HTTPAppender.

* Issue **#2392** : Fix for token type to only allow `api`.


## [v7.1-beta.6] - 2021-09-01

* Issue **#2277** : Add processing filter clone function.

* Issue **#2390** : Fix NPE thrown during proxy aggregation.


## [v7.1-beta.5] - 2021-08-31

* Issue **#2380** : Fix _Attribute Value Data Retention_ job blocking shutdown.

* Issue **#2379** : Change reference data store LMDB to use MDB_NOTLS flag to not tie readers to threads as we typically use thread pools.

* Fix problem with ref data prefix mapping code increasing loop iterations on each element/record.

* Add better logging of ref streams waiting for a lock to load.

* Stop ref streams that are already loaded from calling start/stop processing.

* Add method to ref data store API to list ref stream processing info.

* Improve the ref data store API to allow filtering of the ref entries.

* Change default number of ref loader lock stripes from 100 to 2048 and add it to config.

* Issue **#2371** : Change search LMDB to use MDB_NOTLS flag to not tie readers to threads.

* Issue **#2371** : Fix max readers error not being shown on dashboard.

* Issue **#2370** : Added processor node info to output meta data.

* Issue **#2349** : New dashboard tables will now link to the most recently added query by default.

* Issue **#2351** : Improved error popup text for server responses.

* Issue **#2368** : Fixed server task nesting.

* Issue **#2369** : Fix missing SQL join when reprocessing all streams matching a filter.

* Issue **#2369** : Fix error when searching meta store from a dashboard with a meta key in the query.

* Change explorer root node creation to happen under cluster lock.


## [v7.1-beta.4] - 2021-08-23

* Add `enableJobsOnBootstrap` to the docker distribution config.yml to allow it to be overridden in test stacks.

* Fix broken help links on jobs screen.

* Issue **#2367** : Fix for job node creation.

* Issue **#2365** : Fix to reduce memory used by `BlockGZIPInput`.

* Issue **#2366** : Fix NPE caused by visualisations that do not define maxValues.

* Issue **#2220** : OpenID connect web tokens can now be refreshed to maintain validity.

* Included Leaflet.draw Javascript plugin within UI bundle.

* Issue **#2357** : Remove dropwizard logger configuration entries that have default values.

* Issue **#2350** : Fix distribution start script so it works with a different stroom home dir.

* Issue **#1469** : Add hot loading of config to proxy.

* Change proxy and stroom config validation to cope with relative paths and `~`.

* Issue **#2353** : Swallow NoSuchFileException in config monitor.

* Issue **#2355** : Jobs are no longer enabled by default on bootstrap.

* Issue **#2358** : Changed default stroom home and stroom temp config paths to be null by default so they are resolved relative to the jar or use java tmp respectively.

* Issue **#2354** : Old job node records associated with old jobs are now removed for all nodes regardless of what node is performing the job bootstrap activity.

* Issue **#2343** : The OIDC back channel `redirect_uri` now uses the same URI stored when making the front channel request. 

* Issue **gchq/stroom-resources#104** : Expose `stroom.ui.helpUrl` in the config.yml so the docs served by nginx can be accessed.

* Issue **#2331** : Remove unused config properties `stroom.ui.url.(apiKeys|changepassword|users)`.

Improve error handling during reference data initialisation.

* Improve exception handling when node name is not configured.

* Fixed issue where annotation menu button did not show when existing annotation was selected.

* Fix problem with merging DB connection configs when values not supplied.

* Make relative proxy file paths be relative to configured proxy home directory.

* Make proxy logger file paths support `~` and relative paths be relative to proxy home.

* Remove redundant items from stroom and proxy distribution config yaml files.

* Rename `jerseyClient` key in proxy config.yml to `restClient`.

* Add `remotecertexpiry` to the default config value for `proxyConfig.logStream.metaKeys`.

* Issue **#2335** : Added CLI command `create_api_key` to create an API key for a specified user.

* Added layout density user preference.

* Issue **#2288** : Added export content menu item.

* Added name filtering on export items.

* Moved stepping filter button to make the feature more obvious.

* Fixed issue where annotation menu button did not show when existing annotation was selected.

* Issue **#2317** : The user id can now be resolved from the `username` JWT claim if `email` is not present.

* Issue **#2316** : Fixed React dialog styling by increasing CSS specificity.

* Issue **#2313** : Integrate LeafletJS for geographical mapping.

* Issue **#2293** : Fix location of banner.txt in zip distribution.

* Issue **#2278** : Close a dialog box on keypress. `Escape` = Close, `Ctrl+Enter` = OK.

* Issue **#220** : Users can now change font and font size.

* Issue **#215** : User date/time format preferences are now usable in dashboard tables.

* Fix problem with DynamicAssetsBundle throwing an exception when run from the fat jar.

* Issue **#2295** : Improved appearance, readability and usability of UI elements, especially in dark mode. 

* Issue **#2292** : Fixed issue with `SolrIndex`, `ElasticCluster` and `ElasticIndex` entities not importing correctly
  from a `v6` to `v7` instance.

* Issue **#2113** : Added user preferences for date and time display.

* Issue **#2291** : Fixed issue where the configured Stroom instance title did not change the browser tab title.

* Issue **#2219** : Added migration for feed retention settings to retention rules.

* Issue **#2250** : Improved token authentication.

* Issue **#2250** : Using arrow keys no longer moves popup dialogs.

* Issue **#2264** : Users for user permissions are now retrieved from the account service plus authorisation.

* Add entity relationship diagram and database DDL SQL to release artefacts.

* Issue **#2241** : Changing field or operator in the query expression editor no longer deselects the selected row. 

* Issue **#2241** : Made Firefox and Chrome drop downs look the same.

* Issue **#2260** : The UI no longer caches node status.

* Issue **#2260** : Removed default node config for default index volume group creation.

* Issue **#1828** : Added glass element to ensure mouse capture is maintained when dragging or resizing dialogs over dashboard visualisations.

* Issue **#2285** : SaveAs now provides the current name as the initial value for the new name.

* Issue **#2275** : Stepping from data popup now takes you to the correct record.

* Uplift send_to_stroom.sh script to v3.1.0

* Issue **#2263** : Removed unnecessary JWS algorithm constraints.

* Issue **#2240** : Indexes now show empty selection for volume group until one is selected.

* Issue **#2248** : Migrated dashboard tables now maintain hidden column status.

* Issue **#2280** : Remove trailing comma in some log events.

* Issue **#2267** : Change prefixes used for quick filter for consistency.

* Issue **#2265** : Fix exception when filtering with qualifier but no term, e.g. `name:`.

* Issue **#2266** : Improve quick filter tooltip text.

* Issue **#2261** : Fix missing node name in index and fs volume stats.

* Issue **#2224** : Support indexing properties as JSON objects with Elasticsearch.

* Issue **#2256** : Support searching against Elasticsearch index name patterns.

* Issue **#2257** : Allow Elasticsearch indexing to proceed in the absence of an existing index.

* Issue **#2113** : Added user preferences including theme support.

* Issue **#2241** : Change add icon on data retention screen to add above selected. Add action icon and menu to retention rule table. Restyle rule edit screen.

* Issue **#2254** : Change `data` expression function to accept a first param for what to text to show.

* Issue **#2249** : Fix bug in data retention impact summary tree expansion.

* Issue **#2246** : Fix incorrect handling of parameters to `data` Stroom expression function.

* Add default sorting by user id in the acounts (users) and tokens screens.

* Issue **#2155** : Change default quick filter mode to use contains matching by default with chars anywhere matching now available via `~` prefix. Change quick filter to always treat space as a term delimiter unless in dbl quotes. Add sorting of results by match quality for chars anywhere and regex matching.

* Issue **#2242** : Fix help link in quick filter tool tips. Now comes from config.

* Provide more informative error than NPE when failing to fetch streams that are associated with missing meta

* Issue **#2247** : Correct configuration of Autologger for NodeResourceImpl.

* Update banner to advertise `noauth/datafeed` URL instead of older version.

* Issue **#2243** : Remove unwanted charset commands from migration script.

* Issue **#2231** : Added visualisation selection behaviour to dashboards.

* Issue **#2232** : Fixed issue where search was getting stuck due to LMDB locking transactions.

* Issue **#2238** : Renamed table `docstore_history` to `docstore_schema_history`.

* Issue **#2226** : Ensure that `<Process>` audit events are schema compliant.

* Uplift version of `stroom-logs` content pack selected for download to `3.0-beta.1`

* Issue **#2228** : Stroom Dropwizard and Stroom Proxy Send/Receive log default formats improved.

* Issue **#2235** : Add CHANGELOG to the release artefacts.

* Issue **#2233** : Fix typo in SQL.

* Issue **#2233** : Fix null volume ID in index shard migration.

* Issue **#2229** : Fix migration issue.

* Issue **#2214** Allow file permission override for `FileAppender` and `RollingFileAppender`.

* Issue **#1820** : Stroom now records `UploadedBy` meta attribute when manually uploading data via the UI.

* Issue **#190** : Statistics are now recorded when manually uploading data.

* Issue **#2223** : Fixed migration issue.

* Change stroom dependencies to use maven central instead of bintray.

* Issue **#2207** : Fixed dashboard column rename issue where column name was not updated visually after rename.

* Issue **#2209** : Fixed more property migration issues.

* Issue **#2205** : Improved migration to prevent null DB values being lost.


## [v7.1-beta.3] - 2021-04-30

* Rules from dashboards early capability demonstrator / prototype capability.

* Issue **#2172** : Further improvements to search payload transfer and search completion.

* Issue **#2193** : Enable autologger to work for delete with criteria operations.


## [v7.1-beta.2] - 2021-04-28

* Issue **#2201** : Improved Stroom Proxy and aggregation process.

* Issue **#2203** : Fix NPE in index doc partition by migration.

* Issue **#2184** : Improved logging for OpenId flow.

* Issue **#2205** : Improved migration to prevent null DB values being lost.

* Issue **#2098** : Properties that contain passwords no longer transfer any part of the password to the UI.

* Issue **#1841** : Fixed migration of some config props.

* Issue **#2151** : The UI now shows REST service error messages properly.

* Issue **#1930** : Dashboards opened from links now stop querying when closed.

* Issue **#2166** : You can now change index volume group names.

* Issue **#2090** : Changed to log the authenticated user (if there is one) during noauth calls.

* Issue **#2186** : Fix autologger handling of update operations on entities referenced by id alone.

* Issue **#2183** : Improve error message when property values cannot be de-serialised. Change property DB migration to add conversion of legacy property values that are now a collection type, e.g. List<String>.

* Issue **#2187** : Fixed issue editing a processing filter that has been changed.

* Issue **#2079** : Removed unused session resource code from React UI and backend.

* Issue **#2185** : Stroom now supports the use of an externally provided logout endpoint with the `logoutEndpoint` configuration property.

* Issue **#2188** : Changed all autologged REST methods to return a value (void is not compatible with autologger)

* Issue **#2184** : It should now be possible to use the Cognito OpenId configuration endpoint with Stroom. You should no longer need to set the `jwtClaimsResolver` in the Stroom config as the standard resolver should work. However, you will need to set the new `tokenExpectedInRequest` property to `true` as Cognito delivers fresh tokens with every request.

* Issue **#2177** : Stroom should no longer crash when it is unable to retrieve OpenId configuration.

* Issue **#2176** : Now avoids NPE and produces a proper error when a pipeline cannot be located when loading reference data.

* Issue **#2179** : Extend cron expression syntax. Both `/` (interval) and `-` (range) are now supported.

* Issue **#2172** : To improve search performance local search results are no longer transferred with payloads to a secondary local store.

* Issue **#2172** : To improve search performance only primary search result stores using LMDB will serialise data, i.e. stores used for visualisations now just use search objects and not binary data.

* Issue **#2180** : Fix NPE when Stream Appender has no stream type defined.

* Issue **#2167** : Prevent autologger warning for `RestResourceAutoLoggerImpl`.

* Remove merge artifacts from `scripts.env`.

* Issue **#2172** : Changed the way keys and values are packed into LMDB.

* Issue **#2180** : Fix NPE when Stream Appender has no stream type defined.

* Issue **#2167** : Prevent autologger warning for `RestResourceAutoLoggerImpl`.


## [v7.1-beta.1] - 2021-04-13

* Ported Elasticsearch integration from v6, for compatibility with v7.

* Issue **#2034**: Fixed Solr column ordering in dashboard tables.

* Issue **#759** : Add GZIP support for `FileAppender` and `RollingFileAppender`.


## [v7.0-beta.104] - 2021-04-13

* Switched from `node-sass` to `sass`.

* Fix `node` and `swagger-typescript-api` versions.


## [v7.0-beta.103] - 2021-04-13

* Rebuild.


## [v7.0-beta.102] - 2021-04-09

* Issue **#2174** : The expression to DB condition converter is now more tolerant of missing value mappings.


## [v7.0-beta.101] - 2021-04-08

* Issue **#2172** : Limited the maximum size of LMDB keys and values.

* Issue **#2171** : Fixed dashboard table expression editor field insertion. 

* Issue **#2168** : Removed special columns from dashboard tables.

* Issue **#2154** : Fixed error adding text widget to a dashboard.

* Issue **#2025** : Added caching for DNS name resolution.

* Issue **#2025** : Event logging now attempts to use the `X-FORWARDED-FOR` request header to identify the originating client IP.


## [v7.0-beta.100] - 2021-04-02

* Issue **#1598** : Audit logging uplifted throughout codebase.

* Issue **#1613** : Added event logging to UserResourceImpl.


## [v7.0-beta.99] - 2021-04-01

* Issue **#1928**: Stroom will now redirect users to the root URL if the GWT UI is not hosted within the React wrapper. To develop GWT code it is still necessary to use the GWT UI directly outside of the wrapper so to enable this you can set the newly added `requireReactWrapper` property to false.

* Issue **#2156**: The properties screen now shows a warning triangle when there are unreachable nodes rather than showing an error for all property values.

* Issue **#2157**: Fixed issue where pager was causing an exception paging to last on API keys and Accounts list pages.

* Issue **#2153**: Fixed option to log all REST calls.

* Issue **#2085**: User now gets notification that a password has been changed.

* Issue **#2142**: Changed certificate authentication to ensure that if a certificate is presented then the DN from the cert will be used and no other header attribute.


## [v7.0-beta.98] - 2021-03-30

* Issue **#2138** : Fixed error thrown when updating a property due to the property being updated twice as a result of new event logging code. 

* Issue **#2150** : Added `topMenuTextColour` property to allow the top menu text colour to be changed. Renamed the `backgroundColor` property to `backgroundColour` for consistency. 

* Issue **#2152** : Session list now only shows user authenticated sessions.

* Issue **#2149** : Fixed index volume and index shard migration.


## [v7.0-beta.97] - 2021-03-26

* Issue **#2136** : Fixed sorting problems in users and API keys pages.

* Issue **#2146** : Fixed use of dashboard expression parameters.

* Issue **#2141** : Pre v7 index shards can now be used after upgrade.

* Issue **#2142** : Fixed certificate authentication issues.

* Issue **#2140** : Fixed migration issue that was causing the creation of unnecessary index volume groups. 

* Issue **#2137** : Data retention rules are now migrated from previous versions.

* Issue **#2107** : Removed `Feed Name` field and fixed UUID to field name resolution.

* Issue **#2142** : Added debug to help diagnose client cert auth issues.

* Issue **#2107** : Fixed issue where the processor filter UI was saying that no filter had been applied to feeds because the UI wasn't checking feed filtering by docref.


## [v7.0-beta.96] - 2021-03-23

* Issue **#2099** : Fix stepping source pane for segmented (cooked) data.

* Issue **#479** : Include folder names in audit events when exporting configuration.

* Provide audit log record for permission changes to explorer items (documents)


## [v7.0-beta.95] - 2021-03-18

* Issue **#2105** : Fixed migration of annotations DB.


## [v7.0-beta.94] - 2021-03-17

* Issue **#2104** : Fixed issue where the index creation stored procedure was trying to delete a procedure with the wrong name before creating a new one. 

* Issue **#2103** : Fixed statistics migration script to correctly check for empty tables.

* Issue **#2102** : Fixed query migration script.

* Removed unused properties `resilientReplicationCount` and `preferLocalVolumes`.

* Add null protection to `login_count` and `login_failures` in `users` to `account` table migration.


## [v7.0-beta.93] - 2021-03-16

* Issue **#2088** : Fixed retrieval of stored search results when not using extraction.

* Issue **#2088** : Fixed NullPointerException caused when stepping.

* Issue **#2084** : Fix Bad Request message and lockup after cancelling content import.


## [v7.0-beta.92] - 2021-03-15

* Issue **#2096** : Remove deprecated int display lengths when creating tables

* Issue **#2095** : Tidy upo statistics migration.

* Issue **#2094** : Corrected DB table creation to state the charset as `utf8mb4` and not `utf8` which is ambiguous in MySQL.


## [v7.0-beta.91] - 2021-03-14

* Refactor auth/identity DB migration scripts.

* Add pre migration SQL scripts.


## [v7.0-beta.90] - 2021-03-12

* Issue **#2087** : Fixed NPE caused during legacy migration.

* Uplift guice to v5.0.1.

* Issue **#1871** : Invalidate the users and user groups cache when the _manage_users_ command is run.

* Issue **#2064** : Delete empty directories left by running unit test.

* Add index to cluster_lock table to fix whole table locking for single lock key.

* Issue **#2059** : Add cluster lock protection to task creation. Stops duplicate task creation when master node changes.

* Change task creation by master node to try to wait for search tasks to complete and to try to only create the configured number of tasks.

* Refactor logic to determine master node into one place. Fixes discrepancies between UI and back-end.

* Change node monitoring screen to return nodes in name order.

* Issue **#2066** : Add data bars to node monitoring screen.

* Issue **#2059** : Fix `Duplicate key` error in task assignment.

* Issue **#2056** : Fix error sending permission change events to other cluster nodes.

* Add JVM OOM args to zip distribution scripts.

* Issue **#1866** : Change zip distribution shell scripts to execute from anywhere.


## [v7.0-beta.89] - 2021-02-26

* Change stroom/proxy docker image base to `adoptopenjdk/openjdk15:jdk-15.0.2_7-alpine`

* Add authentication config to swagger spec.


## [v7.0-beta.88] - 2021-02-26

* Fix travis release artefacts.


## [v7.0-beta.87] - 2021-02-25

* No changes


## [v7.0-beta.86] - 2021-02-25

* Fix travis release artefacts.


## [v7.0-beta.85] - 2021-02-24

* Change dockerfile to use Open JDK 15

* Change build to use Open JDK 15

* Fix travis build failure.

* Issue **#2028** : Don't autolog standard object fields by default


## [v7.0-beta.84] - 2021-02-24

* No changes, adding more release artefacts in Travis build.


## [v7.0-beta.83] - 2021-02-23

* Add -q flag to start/stop/migrate.sh to stop log tailing.

* Change migrate.sh to run the migration in the background.

* Add JVM OOM args to zip distribution scripts.

* Issue **#1866** : Change zip distribution shell scripts to execute from anywhere.

* Issue **#1742** : Ensure that an <Object> is always logged to guarantee schema compliance.


## [v7.0-beta.82] - 2021-02-18

* Issue **#2049** : Updated Swagger and moved to the OpenAPI 3.0 Specification.

* Issue **#2049** : Fixed some issues with the resource API that were preventing visualisations from loading. 


## [v7.0-beta.81] - 2021-02-16

* Issue **#2042** : Fixed an issue sorting search results that was making sorting very slow causing searches with large numbers of results to hang. 

* Issue **#2043** : Removed an artificial limit on the number of data points that will be returned to a dashboard visualisation. The UI code had been written to only request a maximum of 1000 data points which meant that some visualisations were missing expected data. It may be necessary to add some limitation to avoid the UI being overloaded but the limitation has been removed for now as it was not configurable and did not warn the user when the limit had been reached.

* Migrated new UI to use Swagger generated endpoints and types.

* Issue **#1414** : A User Id can no longer be changed once a user is created.

* Issue **#1862** : Email and name fields are no longer required when creating users.

* Issue **#1765** : Added confirmation dialog when deleting users and API keys.

* Issue **#2036** : Autologger now delegates exception handling.

* Issue **#2039** : Limit the amount of text data output by autologger.

* Issue **#2037** : Add config prop to ensure every REST call is logged

* Issue **#2038** : Allow autologger action to be modified (search and process)

* Issue **#2027** : Fix autologger update operation

* Issue **#1764** : The create API key page now loads users to select on open.

* Issue **#1766** : Removed comment from token.

* Issue **#1763** : Improved column sizes on API keys dialog.

* Issue **#1767** : Improved column sizes on account management dialog.

* Improve exception alerts in the UI.

* Issue **#2023** : Enable autologger to output multiple path or query parameters

* Issue **#2022** : Simplify consistent event logging with POJOs

* Issue **#2021** : Fix typo when autologger encounters class names ending in 'y'

* Issue **#2020** : Prevent autologger redacting boolean properties


## [v7.0-beta.80] - 2021-01-28

* Issue **#2018** : Fixed intermittent search issue that was sometimes causing search to complete too early without results.

* Fix dashboards not handling NUMERIC index fields.

* Fix bug in Negate expression function.

* Issue **#1995** : Add help info to the expression functions drop down menu.

* Issue **#1911** : Add a drop down menu for picking index fields in the expression editor.

* Issue **#2004** : Fix import of legacy v6 index so default volume group is assigned.

* Issue **#2017** : Fixed dashboard table filtering.

* Issue **#1946** : Removed unnecessary index shard state change error.


## [v7.0-beta.79] - 2021-01-26

* Issue **#2006** : Use UTC timezone when comparing date in repository folder name.

* Issue **#2006** : Use `ArrayList.size()` as a method, instead of a property in Gradle build.

* Issue **#2016** : Fixed StackOverflowException in document event log. 

* Issue **#2003** : Fixed some issues with LMDB search results.

* Issue **#2011** : Redacting obviously sensitive data in automatically generated logs.

* Introduce functionality to provide configurable, automatic logging for RESTful API calls.

* Add `search_results` dir to dockerfile.

* Fix NPE in StroomEventLoggingUtil.


## [v7.0-beta.78] - 2021-01-14

* Issue **#2000** : `RemoteSearchResultFactory.destroy()` is now performed as the processing user.

* Issue **#2000** : Fixed NPE affecting adding/removing columns on a dashboard table and changing column options like grouping and sorting.

* Issue **#2000** : Fixed dashboard table child result expansion.

* Issue **#2001** : Fixed intermittent test failure associated with byte buffers being used incorrectly with LMDB.

* Issue **#1997** : Fix missing _Format_ option on XSLT and TextConverter editors.

* Improved security for handling entity events.


## [v7.0-beta.77] - 2021-01-12

* Issue **#1867** : Cluster entity events are now sent to each node asynchronously to prevent delays caused by one or more slow/bad nodes.

* Issue **#1923** : Fixed an issue affecting sorting dashboard table values that have mixed data types. In addition you can now sort columns alphanumerically if the column format is set to text. 

* Issue **#1811** : Fixed issue where deleting or cutting/pasting text in a dashboard query editor was not marking the dashboard as dirty.

* Search results are now stored off-heap to reduce the chance of out of memory errors.

* Issue **#1911** : Add a drop down menu for picking index fields in the expression editor.

* Issue **#1990** : Change order of items in quick filter popup help.

* Change quick filter word boundary matching to handle a mix of delimited and canelCase, e.g. `stroom.prop.maxFileSize`.

* Issue **#1986** : Fix missing gutter warning/error icons in the stepper code editor.


## [v7.0-beta.76] - 2021-01-07

* No changes.


## [v7.0-beta.75] - 2021-01-06

* Issue **#1989** : Fix for dashboard tables that were only showing a total of 100 rows.

* Change event logging to use new fluent API.


## [v7.0-beta.74] - 2020-12-15

* No changes.


## [v7.0-beta.73] - 2020-12-15

* Change github tokens in travis build.


## [v7.0-beta.72] - 2020-12-15

* Issue **#1983** : Fix line number inconsistency in View Source when last char is a line break.

* Issue **#1971** : Fix 'no appender' errors when editing a Data volume.

* Issue **#1965** : Ignore gzipped data that has no uncompressed content.

* Issue **#1976** : Add an enabled check box and insert above button to retention rules list.

* Fix bug with retention rules impact summary when rows are identical.

* Replace two buttons with toggle button on retetion impact summary.

* Fix path for user event logs.

* Uplift send_to_stroom script to v3.0.

* Issue **#1978** : Fix Meta tab losing syntax highlighting when switching streams.

* Remove byte count in brackets on Info tab when size is below 1024 bytes.

* Fix help links on Jobs screen.

* Fix inability to select text on Info tab in Data viewer.

* Issue **#1963** : Fix data/source view progress bar showing blue when all data is visible.

* Issue **#1974** : Fix job screen only showing one job.

* Issue **#1970** : Fixed issue related to accidental execution of SearchDebugUtil outside of tests.

* Change reference data lookup request object to support string or epoch millis date.

* Add byte count to Info tab, make date values consistent.

* Fix problem of wrong charset being used.

* Fix syntax highlighting for Meta streams in Source view.

* Fix bug in PreviewInputStream read() method.

* Improve the way the YAML logger paths are modified on boot.

* Issue **#1964** : BGZIP files are now closed on exception.

* Changed default dashboard time zone to use UTC.

* Fixed SQL statistics upsert statements for MySQL 5.7.

* Issue **#1954** : Change code that sets ReceivedPath to try getting a value from DOCKER_HOST_(HOSTNAME|IP) env vars first.


## [v7.0-beta.71] - 2020-12-02

* Issue **#1957** : Fix invalidation of the stat datasource caches on content import and other changes.

* Issue **#1960** : Fix the data preview display of empty streams.

* Moved content download to Java.

* All paths in the config YAML including logging config can now be made relative to the home dir.

* Issue **#1912** : Moved dashboard table conditional formatting logic to server.

* Fix missing favicon.

* Issue **#1808** : Fix bug with permission handling for Retention Policy feature.

* Issue **#1948** : Made UI report errors that occur during download.

* Issue **#1944** : You can now define a stroom home config property and all relative paths will become subpaths of this location. 

* Fix byte conversion bug with `read()` method in RASegmentInputStream.

* Add `viewType` and `displayType` args to `data(...)` dashboard expression.

* Fix task spinner appearing briefly on every poll and consuming a lot of CPU.

* Add _progress_ bar to Source Data view and Data Preview to show location in the file.

* Issue **#1678** : Fix data display in dashboard text pane.

* Issue **#1679** : Fix data display in dashboard text pane.

* Issue **#1777** : Fix sub stream tab selection when switching streams in data screen.

* Issue **#1647** : Right align numeric columns in data screen.

* Issue **#1872** : Fix display of source data when data has no line breaks.

* Add completion and snippets to dashboard expression builder.

* Issue **#1895** : Change dashboard field expression editor use the Ace editor like other edit screens.

* Replace stream Info icon on data screen with a sub-stream type tab.

* Add Source View tab available from Data Preview screen to show the unformatted source data.

* Fix highlighting while stepping single line data.

* Add completion and snippets to edit screens using the ACE editor.

* Add editor options to use Vim bindings, show invisble characters, highlight current line, word wrap.

* Issue **#1949** : Fixed bug in download for streams from multiple feeds.


## [v7.0-beta.70] - 2020-11-16

* Issue **#1947** : Fixed NPE thrown when trying to unassign processing tasks by setting the assigned node to null.

* Issue **#1940** : Old searches are now terminated by the processing user.

* Issue **#1932** : Physical stream delete will no longer fail if a file or directory it wants to delete cannot be found, i.e. has been deleted by another external process.

* Fix log output counts for reference data.

* Add REST endpoint for purging reference data.

* Issue **#1938** : Fix missing ref loading errors/warnings, improve warning messages.


## [v7.0-beta.69] - 2020-11-10

* Improve handling of duplicates in reference data loads.

* Improve error messages for reference loading failures.

* Issue **#1936** : Fix reference data loaded not loading string values > 1000btyes.

* Improve PooledByteBufferOutputStream.

* Issue **#1807** : Remove need for Manage Nodes permission in order to list nodes (needed to manage volumes).

* Issue **#1806** : Remove need for Manage Nodes permission in order to list nodes (needed to manage tasks).

* Issue **#1925** : Fixed logging error that was happening on search.

* Issue **#1921** : Fixed problem with the dashboard text pane not migrating properly to the new special stream id and event id fields. 

* Issue **#1910** : Fixed issue preventing display of table data where a table had duplicate column names.

* Issue **#1919** : Fixed issue that was preventing dashboard tabs from being closed.

* Removed rxjava.

* Issue **#1919** : Dashboards now prevent tabs being closed from the close button if some nested tabs on the same pane are hidden.

* Issue **#1915** : Multiple statistic searches on a dashboard are now executed in parallel.

* Issue **#1915** : Fixed task context user identity for statistics searches.

* Issue **#1915** : Fixed task context for statistics searches.

* Merged external expression and query libraries into the source code and added Kryo serialisation to search results.

* Issue **#1910** : Duplicate fields in dashboard tables are now avoided by adding a numeric suffix to the field name when adding a duplicate.

* Issue **#1918** : Text presenter was losing track of stream and event id fields when settings were changed.

* Issue **#1906** : Added info about queue sizes to extraction task.

* Issue **#1906** : Made changes to allow early termination of searches if we have enough data.

* Issue **#1906** : Fixed node task nesting.

* Issue **#1906** : The maximum size of the stream event map is now configurable with the `stroom.search.extraction.maxStreamEventMapSize` property.

* Issue **#1906** : Improved the way search extractions events are grouped so we can extract more events per stream and therefore improve performance.

* Issue **#1907** : Fixed NPE.


## [v7.0-beta.68] - 2020-10-22

* Issue **#1733** : Support xsl:output options for XML output from pipeline (XMLWriter)

* Issue **#1893** : Change delimited string volume properties to lists of strings

* Issue **#1848** : Fix NPE when importing certain processor filters.

* Issue **#1894** : Improvements to search performance and fix for hanging searches.


## [v7.0-beta.67] - 2020-10-15

* Issue **#1901** : Create default (index) volume group if it is used prior to UI.

* Issue **#1900** : Fix inter-node task assignment, change how processing user equality is checked.

* Change dashboard field expression editor to be a bit wider and use a monospace font.

* Issue **#1887** : Fix searches hanging generally and specifically when streams have been deleted.

* Issue **#1877** : Change conditional formatting to support decimals.

* Change conditional formatting to set the available rule operators according to the format type of the column.

* Change conditional formatting to support date terms and date comparisons.

* Issue **#1885** : Fix annotations icon not being enabled on dashboard tables.

* Issue **#1883** : Code now deals with missing streams when performing search extraction.

* Issue **#1882** : Added capacity restriction to the stream event map used in search result extraction. The previous version was causing out of memory exceptions.

* Change names of hidden special table columns on dashboards to avoid name clashes.

* Make dashboard table settings popup bigger to accommodate the conditional formatting.

* Added logic to rename conditional formatting term fields on a column rename.

* Added logic to prevent renaming a column to an existing name.

* Issue **#1872** : Partially fixed to show the 1st 1k chars of the single line raw source. Full fix will come in v7.

* Issue **#1874** : Fixed dashboard tables not showing data if the stream id column is present.

* Issue **#1868** : Stop `Stream not found with id=nnn` errors during searching.

* Issue **#1864** : Added `*` wildcard to conditional formatting matches. 

* Issue **#1865** : Fixed NoSuchMethodError.

* Issue **#1854** : Changed search mechanism to poll for remote results to reduce the chances of hung searches.

* Uplift event-logging-schema content pack to v3.4.2.

* Uplift standard-pipelines content pack to v0.2.

* Uplift template-pipelines content pack to v0.3.

* Change the off-heap ref store to use xxHash for hashing its values.

* Change key widths used in ref data store. Existing stores will need to be deleted and re-generated.


## [v7.0-beta.66] - 2020-09-24

* Added code to authenticate against AWS ALB.


## [v7.0-beta.65] - 2020-09-24

* Added code to authenticate against AWS ALB.


## [v7.0-beta.64] - 2020-09-24

* Added code to authenticate against AWS ALB.


## [v7.0-beta.63] - 2020-09-22

* Added code to authenticate against AWS ALB.


## [v7.0-beta.62] - 2020-09-22

* Added code to authenticate against AWS ALB.


## [v7.0-beta.61] - 2020-09-22

* Added code to authenticate against AWS ALB.


## [v7.0-beta.60] - 2020-09-22

* Added code to authenticate against AWS ALB.


## [v7.0-beta.59] - 2020-09-22

* Added code to authenticate against AWS ALB.

* Changed default behaviour of `useDefaultOpenIdCredentials`.


## [v7.0-beta.58] - 2020-09-22

* Added code to authenticate against AWS ALB.


## [v7.0-beta.57] - 2020-09-18

* Failed build.


## [v7.0-beta.56] - 2020-09-18

* Added code to authenticate against AWS ALB.

* Remove requirement for Reference stream type in ref data API lookup requests.


## [v7.0-beta.55] - 2020-09-15

* Fix names in travis release plugin.


## [v7.0-beta.54] - 2020-09-15

* Rename release artefact `stroom-proxy-config.X.yml` to `stroom-proxy-app-config.X.yml`.


## [v7.0-beta.53] - 2020-09-15

* Change `prod.yml` and `proxy-prod.yml` to be templated so as to generate custom config for the zip and docker distributions.

* Add the docker config files to the release artefacts.

* Issue **#580** : Added conditional formatting options to dashboard tables.

* Add comments to `prod.yml`/`config.yml`.

* Change `reset_password` CLI command to also reset various locked/inactive type flags.

* Change stroom admin path from `admin` to `stroomAdmin` in the distribution.

* Fix `command not found` bug in distribution `start.sh`.

* Change `start.sh` to log pre-logback output to start.sh.log.

* Change logFormat to include time in `prod.yml` and `config.yml`.


## [v7.0-beta.52] - 2020-09-10

* Issue **#1850** : Add new command line commands `create_account`, `reset_password` and `manage_users` to enable the creation of accounts to bootstrap the application.

* Change `admin` to `stroomAdmin` in distribution shell scripts.


## [v7.0-beta.51] - 2020-09-09

* Added `formTokenRequest` property to OpenId config for use with AWS authentication. This forces the use of a form request when fetching tokens.

* Issue **#1824** : Fix for search hang when extraction is requested but no search pipeline is provided.

* Issue **#1083** : Added `any()`, `first()`, `last()`, `nth()`, `top()` and `bottom()` selection functions to select child values of grouped items.

* Issue **#1837** : Added `joining()` function to concatenate supplied fields in child rows.

* Issue **#1784** : Several functions were previously prevented from working on results from aggregate functions but are now applied regardless.

* Fix config file validation not working when hot loading config file changes.

* Change config file validation to be the first thing that happens on boot.

* Fix error when empty branches are in the config file.

* Add `pipeline.referenceData.getLmdbSystemLibraryPath` prop to support provided LMDB binary.

* Change extraction location of bundled LMDB binary to be the same as the store files.

* Change default value for `pipeline.referenceData.maxPutsBeforeCommit` to 0 (i.e. don't commit mid-load).


## [v7.0-beta.50] - 2020-09-07

* Add /api/refData/v1/lookup REST endpoint for doing ref data lookups.

* Issue **#1755** : Stepping now runs in a separate thread to prevent interruption of DW threads when trying to terminate stepping early.

* Issue **#1798** : Fixed REST serialisation issue that was preventing stepping XPath filters from being passed to the server.

* Issue **#1666** : Stepping now loads element documents that use name patterns.

* Issue **#1666** : Parsers now support name patterns for loading config documents. 

* Issue **#1835** : Fix error when viewing data as lowly user.

* Issue **#1836** : Fix Forbidden error when importing data.

* Issue **#1809** : Fix handling of import with no permissions except Import Configuration.

* Issue **#1657** : Remove INTERNAL_PROCESSING_USER from Users list in App Permissions screen.

* Issue **#1782** : Fix handling of empty NOT/AND/OR in stats queries and immprove the error handling for the remote data sources.

* Issue **#1781** : Fix SQL stats handling of NOT() with more than one term in the NOT.

* Issue **#1830** : Change quick filters on Annotations screen to use fuzzy filtering consistent with the rest of stroom. Disable the comment quick filter drop down if there are no standard comments configured. Remove the qualified fields from the quick filter tooltips.

* Issue **#1829** : Fix Annotations screen recording change history when clicking an empty title/subject.

* Issue **#1737** : Fix quick filter in users/groups popup.

* Issue **#1832** : Fix inability to add users/groups in the Document Permissions screen.


## [v7.0-beta.49] - 2020-09-02

* Fix accidental commit of broken code.


## [v7.0-beta.48] - 2020-09-02

* Fix duplicate call to bintray upload in travis script.


## [v7.0-beta.47] - 2020-09-02

* Issue **#1821** : Fix SQL Stat queries whose table doesn't use any datasource fields.

* Issue **#1694** : Fix UUID filtering in quick filters, now using `uuid:` field qualifier. Removed support for `#` prefix in Quick Filter and suggesters.

* Issue **#1699** : Add a docker managed volume for the ref data store.

* Add `pooledByteBufferCounts` to ref data config.

* Issue **#1700** : Stopped stepping happening on open.

* Uplift LMDB to v0.8.1.

* Changed implementation of the byte buffer pool used in the ref data store to improve performance.

* Increase default value for ref data `maxPutsBeforeCommit` to improve load times.

* Fix instances of trace logging that are not using lambdas for complex args. This is particularly a problem in the ref data store code.

* Made stroom compatible with AWS authentication.

* Issue **#1707** : Fix reference data lookups picking the wrong effective stream.

* Issue **#1797** : Altered how search completion is recorded to try and prevent hanging. 

* Issue **#1762** : Fix for search jobs that do not terminate correctly.

* The build should now ensure GWT compilation only occurs after test has completed.

* Issue **#1790** : You can now provide `TYPE` as an optional HTTP header when sending data to Stroom. If provided this attribute is used to determine what data type to assign to the data being received. Data forwarding and aggregation also maintains this attribute and behaviour. 

* Issue **#1665** : Recognised meta types can now be specified in config and drop downs now allow selection in the pipeline editor.


## [v7.0-beta.46] - 2020-08-23

* Issue **#1702** : Fix namespace handling in XML reference data values.

* Issue **#1789** : Prevent dashboards without an extraction pipeline showing as "Missing" on dependency screen.

* Issue **#1803** : Fix `/api/export/v1` failing with NoSuchFileException.

* Issue **#1719** : Create rest endpoint to get reference data store entries. Experimental feature at the moment.

* Issue **#1649** : Make the local reference data store searchable from the dashboard. Experimental feature at the moment.

* Issue **#1805** : Fix missing alert popup when document is saved but has been updated by another user/tab.

* Fix _No appender for stroom.docref.DocRef_ ERRORs in the log.


## [v7.0-beta.45] - 2020-08-14

* Issue **#1793** : Fixed Solr search query creation.

* Issue **#1791** : Fixed Solr connection test response.

* Update Gradle to v6.6

* Revert back to full build.


## [v7.0-beta.44] - 2020-08-14

* Reverted deploy changes until travis dpl v2 is stable.


## [v7.0-beta.43] - 2020-08-14

* Fixing release artefacts.


## [v7.0-beta.42] - 2020-08-13

* Issue **#1783** : Made change to prevent nodes called by the cluster from using localhost, 127.0.0.1 or the same URL as other nodes.

* Issue **#1706** : Terminating processing jobs early now writes appropriate termination errors to the processing info (error) stream and deletes other outputs.

* Issue **#1749** : Removed old benchmark job.


## [v7.0-beta.41] - 2020-08-12

* Issue **#1785** : Fix proxy not forwarding any data.

* Issue **#1675** : All dashboard table fields are now present in text pane settings even if they are hidden or special, e.g. internally added mandatory fields like StreamId and EventId. This change prevents the field settings from being altered incorrectly when these fields were not found.

* Issue **#1758** : Added file locations to meta details and improved tooltip layout.

* Issue **#1778** : Remove error streams following reprocessing when no new streams are created.

* Added support for time based expressions when searching for streams from UI. 

* Issue **#1760** : Support time based expressions for ProcessorTask data source

* Issue **#1761** : Allow processor id to be displayed when searching processor tasks data source.

* Issue **#1693** : Fix dependencies screen listing links to internal searchables as "missing".

* Issue **#1751** : Display correct UUID for "to" dependency in UI dependency screen.

* Issue **#1664** : Fix crash when all streams for pipeline are deleted.

* Issue **#1701** : Fix crash when alternative pipeline is selected/used for processing.

* Issue **#1756** : Fix for IdEnrichmentFilter where is attempts to change attribute values that already exist.

* Issue **#1741** : Fix for search hanging issue.

* Issue **#1740** : `CombinedParser` now removes invalid XML 1.0 characters when `fixInvalidChars` is set and not XML 1.1.

* Add `readTimeout` property to `HTTPAppender` 

* Issue **#1747** : Nodes are now notified about changes to document permissions so that caches are cleared etc.

* Issue **#1752** : Meta info tooltips now show appropriate units for values.

* The `admin` account is now auto created if it doesn't exist.

* Issue **#1310** : Improved file cleanup between tests.

* Issue **#1533** : Improved meta data attribute value flushing to DB.

* Issue **#1634** : `FileSystemClean` will now only examine active data volumes.

* Issue **#1672** : Index shards are now only updated in the DB on flush when the document count or shard size changes.

* Issue **#1713** : Fixed issue where processor task start times were being displayed incorrectly.

* Issue **#1748** : Removed border from explorer quick filter.

* Issue **#1656** : Only managed jobs will now appear on the jobs page.

* Issue **#1669** : Changed the way next scheduled time is calculated based on current time.

* Issue **#1662** : Processor tasks and meta data sources now correctly show pipeline names in dashboard results.

* Issue **#1677** : Active tasks are now correctly filtered.

* Issue **#1718** : Added server task info for some tasks.

* Issue **#1731** : Fixed calendar date picker style that was broken by tooltip CSS changes.

* Issue **#1657** : `INTERNAL_PROCESSING_USER` is no longer visible in the UI.

* Issue **#1449** : You can now create users to associate permissions by clicking the create button in the `User Permissions` page.

* Issue **#1727** : Typo.

* Issue **#1501** : Multiple fixes for new UI.

* Issue **#1506** : Multiple fixes for new UI.

* Issue **#1561** : Multiple fixes for new UI.

* Issue **#1483** : Multiple fixes for new UI.

* Issue **#1525** : Multiple fixes for new UI.

* Issue **#1587** : Multiple fixes for new UI.

* Issue **#1526** : Multiple fixes for new UI.

* Issue **#1499** : Multiple fixes for new UI.

* Issue **#1481** : Multiple fixes for new UI.

* Issue **#1498** : Multiple fixes for new UI.

* Issue **#1660** : Multiple fixes for new UI.

* Issue **#1659** : Multiple fixes for new UI.

* Issue **#1725** : Fix Data Splitter onlyMatch using zero based instead of one based numbers.


## [v7.0-beta.39] - 2020-07-06

* Issue **#1716** : Prevent export of processor filters that are reprocess or deleted.

* Issue **#1638** : Suppress error when searching deleted streams.

* Issue **#1696** : Fix reprocessing from unfiltered meta data view.

* Issue **#1648** : Fix streams not being deleted following reprocessing.

* Issue **#1695** : Fix `Records` stream types not being identified correctly.

* Issue **#1668** : Fixed incorrect parameter count for XSLT `meta` function.

* Issue **#1619** : Fix delete stream summary.


## [v7.0-beta.38] - 2020-06-25

* Issue **#1670** : Stop _parse-uri_ XSLT function returning -1 for missing port numbers.

* Issue **#1673** : Increase limit for age spinner in retention rules to 9999.

* Issue **#1683** : Add `!` NOT operator to fuzzy match filtering.

* Add field searching to Activity quick filter.

* Add field searching to entity selection popups.

* Change entity selection popups to clear quick filter on show.

* Add column sorting and field searching to Properties screen.

* Add field searching to Explorer Tree quick filter.

* Add field searching to Properties quick filter.

* Add field searching to Server Tasks quick filter.

* Add field searching to dependencies quick filter.

* Improve info tooltip layouts.

* Issue **#1248** : Add quick filter to dependencies screen.

* Issue **#1650** : Use consistent blue colour.

* Issue **#1671** :Fix XSLT function `hex-to-oct`.

* Add `readTimeout` property to `HTTPAppender`.

* Issue **#1632** : SQL stats now compatible with MySQL 8 Group Replication

* Issue **#1650** : Use consistent blue colour.

* Issue **#1627** : Fix Up/Down buttons on Rule Set screen. Now keeps selection after use.

* Issue **#1277** : Fix Enable/Disable toggle button on Rule Set screen.


## [v7.0-beta.37] - 2020-06-15

* Add _Impact Summary_ tab to _Data Retention_ to show breakdown of counts of streams to be deleted.

* Add support for the `.` separator in the word boundary fuzzy matching.

* Change the fuzzy match filter to switch to a case sensitive wild-carded exact match when the input contains a `*`.

* Issue **#1640** : Fix server error when clicking disabled delete/info icon for deleted streams.

* Issue **#1639** : Default index volume group property changes.

* Issue **#1636** : Fix data retention deletion using wrong action for rules.

* Issue **#1280** : Fix creation of default index volumes.


## [v7.0-beta.36] - 2020-06-02

* Issue **#1621** : Fix NPE in proxy content syncing.

* Issue **#1462** : Stroom not working with MySQL 8.0 due to SQLException

* Issue **#1564** : Fix error in data retention section of stream info popup.

* Change data retention delete batching approach to use time ranges.

* Issue **#1611** : Change explorer tree filtering to also filter on an exact match of the entity's UUID.

* Add regex filtering with `/` prefix to fuzzy matching.

* Change word boundary matching to require a `?` prefix.


## [v7.0-beta.35] - 2020-05-28

* Issue **#1608** : Fixed NPE in UI data presenter.

* Issue **#1595** : Fixed names for imported items that already exist but are updated by import.

* Issue **#1603** : XSLT imports now error if more than one matching XSLT is found.

* Issue **#1604** : XSLT import resolution now accepts the use of UUIDs and DocRef strings.

* Issue **#1403** : Dashboard query download now retains expression parameters.

* Issue **#1514** : Fixed properties edit presenter issue.

* Issue **#1569** : Additional changes to improve the new `Data Delete` task that replaces the `File System Clean` task.

* Issue **#1565** : Stop data retention rules deleting all data.

* Add default data retention rule to the UI screen to make it clear what happens by default.

* Add fuzzy match filter to explorer tree.


## [v7.0-beta.34] - 2020-05-26

* Issue **#1569** : Removed recursive multi threading from file system clean as thread limit was being reached. 

* Issue **#1478** : Fixed data volume creation and other resource methods.

* Issue **#1594** : Now auto creates root explorer node on startup if it is missing.

* Issue **#1544** : Fixes for imported dashboards.

* Issue **#1586** : Fixed migration and initial population of standard meta type names.

* Issue **#1592** : Changed DB bit(1) columns to be tinyint(1) so that they show values correctly in the CLI.

* Issue **#1510** : Added logical delete for processor and processor filter to allow a user to force deletion without encountering a DB constraint. 

* Issue **#1557** : Process, reprocess, delete and download data functions now provide an impact summary before a user can proceed with the action.

* Issue **#1557** : The process data function in the data browser now provides the option to process or reprocess data. When selected a user can also choose: the priority of the process filters that will be created; to set the priority automatically based on previous filters; set the enabled state.

* Issue **#1557** : Reprocessing data no longer has a limitation on how many items can be reprocessed as it is now implemented by reprocess specific filters.

* Issue **#1585** : Fixed issue that was preventing viewing folders processors.

* Issue **#1557** : Added an impact summary to meta data actions such as delete, restore, process and download.

* Issue **#1593** : NPE copying empty expressions


## [v7.0-beta.33] - 2020-05-22

* Issue **#1588** : Fix processor filter import.

* Issue **#1566** : Fixed UI data restore behaviour.

* Make public port configurable


## [v7.0-beta.32] - 2020-05-19

* Issue **#1573** : Active tasks tab now only shows tasks related to the open feed.

* Issue **#1584** : Add @ApiParam to POST/PUT/DELETE endpoints so the request type appears in swagger-ui.

* Issue **#1581** : Change streamId to a path param in GET /api/data/v1.

* Issue **#1567** : Added error handling so the confirmation dialog continues to work even when there is a failure in a previous use.

* Issue **#1568** : Pipeline names should now be shown where needed in the UI.

* Issue **#1457** : Change field value suggester to use fuzzy matching.

* Issue **#1574** : Make feed suggestions return all feeds, not just ones with meta.

* Issue **#1544** : Imported dashboards from 6.1 now work.

* Issue **#1577** : Cluster node status is now updated when node settings are changed.

* Issue **#1396** : Completely changed DB migration and import/export compatibility code.

* Fix index creation stored procedure.

* Issue **#1508** : Tidy up property descriptions, change connection pool props to use Stroom Duration type.

* Issue **#473** : Fix value stats being ignored during in memory stat aggregation.

* Issue **#1141** : Make SQL stats aggregation delete unused stat keys at the end.


## [v7.0-beta.31] - 2020-05-12

* Issue **#1546** : Fixed opening and editing of data retention rules.

* Issue **#1494** : Scrollbars now have a white background unless used in a readonly text area.

* Issue **#1547** : Added pipeline names to processor task screens.

* Issue **#1543** : Prevent import/export of processor filters with id fields

* Issue **#1112** : You can now copy feeds along with other items and copies are named appropriately.

* Issue **#1112** : When copying a selection of several items, the dependencies between the items are altered in the resulting copies so that the copied items work together as a new set of content.

* Issue **#1112** : As part of fixing dependencies when copying items, the dependencies screen now works correctly and now also shows processor filters. 

* Issue **#1545** : Add property `enableDistributedJobsOnBootstrap` to enable/disable processing on first boot.


## [v7.0-beta.30] - 2020-05-06

* Issue **#1503** : Further fix for enabled/disabled expression items and dashboard tab visibility.

* Issue **#1511** : Data pages now show pipeline names rather than pipeline UUIDs.

* Issue **#1529** : Fix error when selecting datasource in new dashboard.

* Fix NPE in SystemInfoResource.get().

* Issue **#1527** : Fixed missing aud in API eky tokens.

* Add missing guice binding for SystemInfoResource.

* Make export add new line to the end of all files to adhere to POSIX standard.

* Issue **#1532** : Fixed index shard criteria in UI.

* Change SecurityFilter to return a 401 on authentication exceptions.

* Move some health checks into SystemInfoResource.

* Remove healthchecks from rest resources and servlets that never give an unhealthy result.

* Add error info to AppConfigMonitor health check.


## [v7.0-beta.29] - 2020-05-04

* Issue **#1496** : Fixed paging of processed data.

* Add stroom.statistics.internal.enabledStoreTypes and make internal stat processing respect it.

* Improve SQL stats shutdown processing so all in memory stats are flushed.

* Issue **#1521** : Dashboards with missing datasources break entirely.

* Issue **#1477** : Disable edit button on stream processor.

* Issue **#1497** : Fixed data list result paging.

* Issue **#1492** : Fixed data list result paging.

* Issue **#1513** : You can now view data in folders.

* Issue **#1500** : Fixed data delete/restore behaviour.

* Issue **#1515** : Fix proxyDir default when running in a stack.

* Issue **#1509** : Unable to update processor filter.

* Issue **#1495** : Speculative fix for missing swagger.json file in the fat jar.

* Issue **#1503** : Fixed Dashboard serialisation and JSON template.

* Issue **#1479** : Unable to set index volume limits.


## [v7.0-beta.28] - 2020-04-29

* Issue **#1489** : Reprocess streams feature failing.

* Issue **#1465** : Add default Open ID credentials to allow proxy to be able to authenticate out of the box.

* Issue **#1455** : Fix interactive search.

* Issue **#1471** : Pipeline name not shown on processors/filters in UI.

* Issue **#1491** : Download stream feature failing. 

* Issue **#1433** : StandardKafkaProducer failed when writing XML kafka payloads. 


## [v7.0-beta.27] - 2020-04-27

* Issue **#1417** : Allow processor filters to be exported with Pipelines. 

* Issue **#1480** : Index settings now shows index volume groups and allows selection. 

* Issue **#1450** : Further attempt to improve criteria filtering on data tab.

* Issue **#1467** : The cluster node state node uses NodeResource to determine active nodes.

* Issue **#1448** : The internal processing user now has a JWT and passes it when making calls to other nodes.


## [v7.0-beta.26] - 2020-04-22

* Fix gradle build for versioned builds


## [v7.0-beta.25] - 2020-04-22

* Assorted fixes to the new React UI pages.


## [v7.0-beta.24] - 2020-04-21

* Issue **#1450** : Stop data tabs showing all feeds.

* Issue **#1454** : Fix NPE in feed name suggestion box.

* Remove internal statistics from setup sample data.

* Fix issue of pipeline structure not showing when it contains a StatisticsFilter.

* Update auth flow for auth-into-stroom integration

* Issue **#1426** : Change /logout endpoint to /noauth/logout.

* Fix `Expecting a real user identity` errors on auto import of content packs.

* Increase wait timeout to 240s in `start.sh`.

* Issue **#1404** : Fixed issue with invalid XML character filter.

* Issue **#1413** : Attempt to fix search hanging issue.

* Issue **#1393** : The annotations data popup now formats content on load.

* Issue **#1399** : Removed error logging for expected exceptions in TaskExecutor.

* Issue **#1385** : File output param `streamId` now aliased to `sourceId` and `streamNo` is now aliased to `partNo` for consistency with new source tracking XSLT functions.

* Issue **#1392** : Downloading dashboard queries now provides the current query without the need to save the dashboard.

* Issue **#1427** : Change remote call to auth service to a local call.


## [v7.0-beta.23] - 2020-03-24

* Rename all legacy DB tables to `OLD_`.

* Issue **#1394** : Fix duplicate tables appearing in Monitoring -> Database Tables.

* Add NodeEndpointConfiguration. Change `node` table to hold the base endpoint.


## [v7.0-beta.22] - 2020-03-10

* Brought stroom-auth-service into stroom

* Issue **#563** : Kafka producer improvements - StandardKafkaProducer

* Issue **#1399** : Removed error logging for expected exceptions in TaskExecutor. 

* Fix missing $ in start.sh

* Issue **#1387** : Changed the way tasks are executed to reduce changes of unhandled execution errors.

* Issue **#1378** : Improved logging detail when processor filters fail.

* Issue **#1379** : Fixed issue where you couldn't open a processor filter if parts of the filter referenced deleted items.

* Issue **#1378** : Improved logging detail when processor filters fail.

* Issue **#1382** : Added `decode-url` and `encode-url` XSLT functions.

* Issue **#655** : Fixed SQL Stats queries ignoring the enabled state of the dashboard query terms.

* Issue **#1362** : Fixed issue where hiding dashboard annotation fields removed them.

* Issue **#1357** : Fixed dragging tabs in dashboard with hidden panes to create a new split.

* Issue **#1357** : Fixed dragging tabs in dashboard with hidden panes.

* Issue **#1368** : Fixed FindReplaceFilter as it wasn't working when used in conjunction with Data Splitter.

* Issue **#1361** : Changed the way headers are parsed for the HttpCall XSLT function.


## [v7.0-beta.21] - 2020-02-24

* Add null checks to DB migration.

* Add deletion of constraint `IDX_SHARD_FK_IDX_ID` to migration script.


## [v7.0-beta.20] - 2020-02-13

* Fix bug in `processor_task` migration script.


## [v7.0-beta.19] - 2020-02-10

* Fix bugs in DB migration scripts.


## [v7.0-beta.18] - 2020-02-05

* Re-locate index database migrations.

* Fix issues with migrating null audit columns.

* Improve output of TestYamlUtil.


## [v7.0-beta.17] - 2020-01-29

* Issue **#1355** : Fixed stepping from dashboard text pane.

* Issue **#1354** : Fixed double click to edit list items, e.g. properties.

* Issue **#1340** : Fixed issue with FindReplaceFilter where it failed in some cases when more than one filter was chained together.

* Issue **#1338** : You can now configure the max size of the map store cache.

* Issue **#1350** : Fixed scope of dictionaries when loaded in multiple XSLT pipeline steps.

* Issue **#1347** : Added SSL options to `http-call` XSLT method.

* Issue **#1352** : Fixed Hessian serialisation of user identities on tasks.

* Change docker image to allow us to pass in the dropwizard command to run, e.g. server|migrate.

* Stop MySQL outputing Note level warnings during migration about things that don't exist when we expect them not to.


## [v7.0-beta.13] - 2019-12-24

* Add `migrate` command line argument to run just the DB migrations.

* Updated API key to include audience and added client id and secret.

* Change `stroom.conf.sh` to also look for ip in `/sbin`

* Issue **#260** : You can now hide dashboard tabs.

* Issue **#1332** : The text pane can now be configured to show source data.

* Issue **#1311** : Improved source location tracking.


## [v7.0-beta.12] - 2019-12-04

* Change local.yml.sh to also look for ip in /sbin


## [v7.0-beta.11] - 2019-12-04

* Fix invalid SQL syntax in V07_00_00_012__Dictionary


## [v7.0-beta.10] - 2019-12-04

* Update auth api version

* Add clientId and clientSecret to config

* Update API keys (needed aud)

* Issue **#1338** : Added new config options to control the maximum size of some caches: `stroom.pipeline.parser.maxPoolSize`, `stroom.pipeline.schema.maxPoolSize`, `stroom.pipeline.schema.maxPoolSize`, `stroom.pipeline.xslt.maxPoolSize`, `stroom.entity.maxCacheSize`, `stroom.referenceData.mapStore.maxCacheSize`.

* Issue **#642** : Downloading query details now ignores hidden fields.

* Issue **#1337** : Fixed issue where downloading large numbers of search results in Excel format was exceeding maximum style count of 64000. 

* Issue **#1341** : Added XSRF protection to GWT RPC requests.

* Issue **#1335** : Made session cookie `Secure` and `HttpOnly`.

* Issue **#1334** : Fix 404 when accessing `/stroom/resourcestore/........`, i.e. fix Tools->Export.

* Issue **#1333** : Improved resilience against XSS attacks.

* Issue **#1330** : Allow configuration of `Content-Type` in HTTPAppender.

* Issue **#1327** : Improvements to annotations.

* Issue **#1328** : Increased size of data window and removed max size restrictions.

* Issue **#1324** : Improved logging and added SSL options for HTTPAppender.


## [v7.0-beta.9] - 2019-11-20

* Fix SSL connection failure on remote feed staus check.

* Remove ConfigServlet as the functionality is covered by ProxyConfigHealthCheck.

* Fix password masking in ProxyConfigHealthCheck.

* Change servlet path of ProxyStatusServlet from `/config` to `/status`.


## [v7.0-beta.8] - 2019-11-20

* Change precedence order for config properties. YAML > database > default. Change UI to show effective value. Add hot loading of YAML file changes.

* Issue **#1322** : Stroom now asks if you really want to leave site when stepping items are dirty. Also fixed `Save` and `Save All` menu items and dashboard param changes now correctly make a dashboard dirty.

* Issue **#1320** : Fixed formatting of XML where trailing spaces were being removed from content surrounded by start and end tags (data content) which should not happen. 

* Issue **#1321** : Make path relative in stroom distribution .zip.sha256 hash file.

* The auth service now supports the use of HTTPS without certificate verification and adds additional logging.

* Issue **gchq/stroom-auth#157** : Automatically refresh user's API key when it expires.

* Issue **#1243** : Dashboard visualisations now link with similar functions available to dashboard tables, e.g. `link()`, `dashboard()`, `annotation()`, `stepping()`, `data()`.

* Issue **#1316** : JSONParser now includes various parse options including handling comments.

* Issue **#48** : Added option to hide/show dashboard table columns.

* Issue **#1315** : Improved health check for missing API key.

* Updated stroom expression to v1.5.4 and added new field types.

* Issue **#1315** : Improved health check for missing API key.

* Issue **#1314** : Fixed NPE thrown when logging caused when viewing docs that can't be found.

* Issue **#1313** : Suggestion boxes now make suggestions immediately before the user even starts typing.

* Issue **#1043** : Added feature to allow floating point numbers to be indexed.

* Issue **#1312** : Dictionaries now change the entity name in the DB when renamed.

* Issue **#1312** : Fixed read only behaviour of dictionary settings UI.

* Issue **#1300** : Multiple changes to annotations.

* Issue **#1265** : Added `modulus()` function along with alias `mod()` and modulus operator `%`.

* Issue **#1300** : Added `annotation()` link creation function, `currentUser()` alias for `param('currentUser()')` and additional link creation functions for `data()` and `stepping()`.

* Issue **#67** : Table columns now display menu items on left click.

* Uplift stroom-query to v2.2.4 to add better diagnostic logging.

* Uplift Kafka client to v2.2.1.

* Issue **#1293** : Add more static file types to allow nginx/browser caching on.

* Issue **#1295** : Add authentication bypass for servlets such as /remoting, /status, /echo, etc.

* Issue **#1297** : The UI now supplies API tokens to the backend for resource calls.

* Issue **#1296** : Fixed NPE in StreamMapCreator caused when a stream can not be found.


## [v7.0-beta.7] - 2019-10-23

* Issue **#1288** : Streams now show the name of the pipeline used to create them even if the user doesn't have permission to see the pipeline.

* Issue **#1282** : Fixed issue where items were imported into the explorer even if not selected for import.

* Issue **#1291** : Fixed issue where empty dashboard table cells did not select table rows when clicked. 

* Issue **#1290** : Fixed issue where executor provider was not executing supplied runnable if parent task had terminated.

* Fix problem of missing fallback config in docker image.


## [v7.0-beta.6] - 2019-10-15

* Add default for stroom.security.authentication.durationToWarnBeforeExpiry

* Fix missing icons for Kafka Config and Rule Set.

* Fix Kafka Config entity serialisation.

* Issue **#1264** : Dashboards running in embedded mode will not always ask for the user to choose an activity if the users session has one set already.

* Issue **#1275** : Fixed permission filtering when showing related streams.

* Issue **#1274** : Fixed issue with batch search caused by Hibernate not returning pipeline details in stream processor filters.

* Issue **#1272** : Fixed saving query favourites.

* Issue **#1266** : Stroom will now lock the cluster before releasing owned tasks so it doesn't clash with other task related processes that lock the DB for long periods.

* Issue **#1264** : Added `embedded` mode for dashboards to hide dashboard chrome and save options.

* Issue **#1264** : Stroom no longer asks if you want to leave the web page if no content needs saving.

* Issue **#1263** : Fixed issues related to URL encoding/decoding with the `dashboard()` function.

* Issue **#1263** : Fixed issue where date expressions were being allowed without '+' or '-' signs to add or subtract durations.

* Add fallback config.yml file into the docker images for running outside of a stack.

* Issue **#1263** : Fixed issues related to URL encoding/decoding in dashboard expressions.

* Issue **#1262** : Improved behaviour of `+` when used for concatenation in dashboard expressions.

* Issue **#1259** : Fixed schema compliance when logging failed document update events.

* Issue **#1245** : Fixed various issues with session management and authentication.

* Issue **#1258** : Fixed issue affecting search expressions against keyword fields using dictionaries containing carriage returns.


## [v7.0-beta.5] - 2019-09-23

* Fixes to proxy


## [v7.0-beta.4] - 2019-09-16

* Fix stroom-proxy Dockerfile


## [v7.0-beta.3] - 2019-09-16

* Minor fixes, including an essential fix to config


## [v7.0-beta.2] - 2019-09-13

* Fix docker build


## [v7.0-beta.1] - 2019-09-11

* Issue **#1253** : Data retention policies containing just `AND` will now match everything.

* Issue **#1252** : Stream type suggestions no longer list internal types.

* Issue **#1218** : All stepping panes will now show line numbers automatically if there are indicators (errors, warnings etc) that need to be displayed.  

* Issue **#1254** : Added option to allow non Java escaped find and replacement text to be used in `FindReplaceFilter`. 

* Issue **#1250** : Fixed logging description for reading and writing documents.

* Issue **#1251** : Copy permissions from a parent now shows changes prior to the user clicking ok.

* Issue **#758** : You no longer need the `Manage Processors` privilege to call `stroom:meta('Pipeline')` in XSLT.

* Issue **#1256** : Fix error caused when logging data source name when downloading search results.

* Issue **#399** : Fix for error message when stepping that said user needed `read` permission on parent pipeline and not just `use`.

* Issue **#1242** : Fix for pipeline corruption caused when moving elements back to inherited parents.

* Issue **#1244** : Updated Dropwizard to version 1.3.14 to fix session based memory leak.

* Issue **#1246** : Removed elastic search document type, menu items and filter.

* Issue **#1247** : Added XSLT functions (`source`, `sourceId`, `partNo`, `recordNo`, `lineFrom`, `colFrom`, `lineTo`, `colTo`) to determine the current source location so it can be embedded in a cooked event. Events containing raw source location info can be made into links in dashboard tables or the text pane so that a user can see raw source data or jump directly to stepping that raw record.

* Add data retention feature and index optimisation to Solr indexes.

* Initial support for Solr indexing and search.

* Issue **#1244** : Updated Dropwizard to version 1.3.14 to fix session based memory leak.

* Issue **#1246** : Removed elastic search document type, menu items and filter.

* Issue **#1214** : Fixed issue where the max results setting in dashboard tables was not always being obeyed. Also fixed some dashboard table result page size issues.

* Issue **#1238** : During proxy clean task we no longer show a failed attempt to delete an empty directory as an error as this condition is expected.

* Issue **#1237** : Fixed issue where explorer model requests were failing outside of user sessions, e.g. when we want to find folder descendants for processing.

* Issue **#1230** : Fix test.

* Issue **#1230** : Search expressions no longer have the `contains` condition. 

* Issue **#1220** : Fixed attempt to open newly created index shards as if they were old existing shards.

* Issue **#1232** : Fixed handling of enter key on pipeline element editor dialog.

* Issue **#1229** : Fixed issue where users needed `Read` permission on an index instead of just `Use` permission to search it.

* Issue **#1207** : Removed task id from meta to reduce DB size and complexity especially given the fact tasks are transient. Superseded output is now found by querying the processor task service when new output is written rather than using task ids on meta.

* Uplift HBase to 2.1.5 and refactor code accordingly

* Uplift Kafka to 2.1.1 and refactor code accordingly

* Uplift Curator to 4.2.0

* Issue **#1143** : Added mechanism to inject dashboard parameters into expressions using the `param` and `params` functions so that dashboard parameters can be echoed by expressions to create dashboard links.

* Issue **#1205** : Change proxy repo clean to not delete configured rootRepoDir.

* Issue **#1204** : Fix ProxySecurityFilter to use correct API key on feedStatus requests.

* Issue **#1211** : Added a quick filter to the server tasks page.

* Issue **#1206** : Fixed sorting active tasks when clicking column header.

* Issue **#1201** : Fixed dependencies.

* Issue **#1201** : Fixed tests.

* Issue **#1201** : Document permission changes now mutate the user document permissions cache rather than clearing it.

* Issue **#1153** : Changed security context to be a Spring singleton to improve explorer performance.

* Issue **#1202** : Fixed NumberFormatException in StreamAttributeMapUtil.

* Issue **#1203** : Fixed event logging detail for dictionaries.

* Issue **#1197** : Restored Save As functionality.

* Issue **#1199** : The index fields page now copes with more than 100 index fields.

* Issue **#1200** : Removed blocking queue that was causing search to hang when full.

* Issue **#1198** : Filtering by empty folders now works correctly.

* Comment out rollCron in proxy-prod.yml

* Change swagger UI at gchq.github.io/stroom to work off 6.0 branch

* Issue **#1195** : Fixed issue where combination of quick filter and type filter were not displaying explorer items correctly.

* Issue **#1153** : Changed the way document permissions are retrieved and cached to improve explorer performance.

* Issue **#1196** : Added code to resolve data source names from doc refs if the name is missing when logging.

* Issue **#1165** : Fixed corruption of pipeline structure when adding items to Source.

* Issue **#1193** : Added optional validation to activities.

* Change default config for proxy repositoryFormat to "${executionUuid}/${year}-${month}-${day}/${feed}/${pathId}/${id}"

* Issue **#1194** : Fixed NPE in FindTaskProgressCriteria.

* Issue **#1191** : SQL statistics search tasks now show appropriate information in the server tasks pane.

* Issue **#1192** : Executor provider tasks now run as the current user.

* Issue **#1190** : Copied indexes now retain associated index volumes.

* Issue **#1177** : Data retention now works with is doc refs.

* Issue **#1160** : Proxy repositories now only roll if all output streams for a repository are closed. Proxy repositories also only calculate the current max id if the `executionUuid` repo format param is not used.

* Issue **#1186** : Volume status is now refreshed every 5 minutes.

* Fix incorrect default keystore in proxy config yaml.

* Rename environment variables in proxy config yaml.

* Issue **#1170** : The UI should now treat the `None` tree node as a null selection.

* Issue **#1184** : Remove dropwizard yaml files from docker images.

* Issue **#1181** : Remove dropwizard config yaml from the docker images.

* Issue **#1152** : You can now control the maximum number of files that are fragmented prior to proxy aggregation with `stroom.maxFileScan`.

* Issue **#1182** : Fixed use of `in folder` for data retention and receipt policies.

* Updated to allow stacks to be built at this version.

* Issue **#1154** : Search now terminates during result creation if it is asked to do so.

* Issue **#1167** : Fix for proxy to deal with lack of explorer folder based collections.

* Issue **#1172** : Fixed logging detail for viewing docs.

* Issue **#1166** : Fixed issue where users with only read permission could not copy items.

* Issue **#1174** : Reduced hits on the document permission cache.

* Issue **#1168** : Statistics searches now work when user only has `Use` permission.

* Issue **#1170** : Extra validation to check valid feed provided for stream appender.

* Issue **#1174** : The size of the document permissions cache is now configurable via the `stroom.security.documentPermissions.maxCacheSize` property.

* Issue **#1176** : Created index on document permissions to improve performance.

* Issue **#1175** : Dropping unnecessary index `explorerTreePath_descendant_idx`.

* Issue **#747** : XSLT can now reference dictionaries by UUID.

* Issue **#1167** : Use of folders to include child feeds and pipelines is now supported.

* Issue **#1153** : The explorer tree is now built with fewer DB queries.

* Issue **#1163** : Added indexes to the DB to improve explorer performance.

* Issue **#1153** : The explorer tree now only rebuilds synchronously for users who alter the tree, if has never been built or is very old. All other rebuilds of the explorer tree required to keep it fresh will happen asynchronously.

* Issue **#1162** : Proxy aggregation will no longer recurse parts directories when creating parts.

* Issue **#1157** : Migration now adds dummy feeds etc to processor filters if the original doc can't be found. This will prevent filters from matching more items than they should if migration fails to map feeds etc because they can't be found.

* Issue **#1162** : Remove invalid CopyOption in move() call.

* Issue **#1159** : Fix NPE in rolling appenders with no frequency value.

* Issue **#1160** : Proxy repositories will no longer scan contents on open if they are set to be read only.

* Issue **#1162** : Added buffering etc to improve the performance of proxy aggregation.

* Issue **#1156** : Added code to reduce unlikely chance of NPE or uncontrolled processing in the event of a null or empty processing filter.

* Issue **#1149** : Changed the way EntryIdSet is unmarshalled so jaxb can now use the getter to add items to a collection.

* Ignore broken junit test that cannot work as it stands

* Fix NPE in DictionaryStoreImpl.findByName().

* Issue **#1146** : Added `encodeUrl()`, `decodeUrl()` and `dashboard()` functions to dashboard tables to make dashboard linking easier. The `link()` function now automatically encodes/decodes each param so that parameters do not break the link format, e.g. `[Click Here](http://www.somehost.com/somepath){dialog|Dialog Title}`.

* Issue **#1144** : Changed StreamRange to account for inclusive stream id ranges in v6.0 that was causing an issue with file system maintenance.

* Mask passwords on the proxy admin page.

* Add exception to wrapped exception in the feedStatus service.

* Issue **#1140** : Add health check for proxy feed status url.

* Issue **#1138** : Stroom proxy now deletes empty repository directories based on creation time and depth first so that pruning empty directories is quicker and generally more successful.

* Issue **#1137** : Change proxy remote url health check to accept a 406 code as the feed will not be specified.

* Issue **#1135** : Data retention policies are now migrated to use `Type` and not `Stream Type`.

* Issue **#1136** : Remove recursive chown from stroom and proxy docker entrypoint scripts.


## [v7.0-alpha.5] - 2019-06-12

* Fix YAML substitution.


## [v7.0-alpha.4] - 2019-06-11

* Update API paths


## [v7.0-alpha.3] - 2019-05-10

* Fix config


## [v7.0-alpha.2] - 2019-05-10

* Fix config

* Issue **#1134** : Proxy now requires feed name to always be supplied.

* Expose proxy api key in yaml config via SYNC_API_KEY

* Issue **#1130** : Change `start.sh` so it works when realpath is not installed.

* Issue **#1129** : Fixed stream download from the UI.

* Issue **#1119** : StreamDumpTool will now dump data to zip files containing all data and associated meta and context data. This now behaves the same way as downloading data from the UI and can be used as an input to proxy aggregation or uploaded manually.


## [v7.0-alpha.1] - 2019-04-23

* Fix config issue

* Fixed NPE created when using empty config sections.

* Issue **#1122** : Fixed hessian communication between stroom and stroom proxy used to establish feed receive status. Added restful endpoints for feed status to stroom and stroom proxy. Proxy will now be able to request feed status from upstream stroom or stroom proxy instances.

* Fixed incompatibility issues with MySQL 5.7 and 8.0.

* Added debug to help diagnose search failures

* Issue **#382** : Large zip files are now broken apart prior to proxy aggregation.

* Change start script to use absolute paths for jar, config and logs to distinguish stroom and proxy instances.

* Issue **#1116** : Better implementation of proxy aggregation.

* Issue **#1116** : Changed the way tasks are executed to ensure thread pools expand to the maximum number of threads specified rather than just queueing all tasks and only providing core threads.

* Remove full path from file in sha256 hash file release artifact.

* Issue **#1115** : Add missing super.startProcessing to AbstractKafkaProducerFilter.

* Improve exception handling and logging in RemoteDataSourceProvider. Now the full url is included in dashboard connection errors.

* Change Travis build to generate sha256 hashes for release zip/jars.

* Uplift the visualisations content pack to v3.2.1

* Issue **#1100** : Fix incorrect sort direction being sent to visualisations.

* Add guard against race condition

* Add migration script to remove property `stroom.node.status.heapHistogram.jMapExecutable`.

* Uplift base docker image to openjdk:8u191-jdk-alpine3.9, reverting back to JDK for access to diagnostic tools.

* Issue **#1084** : Change heap histogram statistics to java MBean approach rather than jmap binary. Remove stroom.node.status.heapHistogram.jMapExecutable property.

* Improve resource for setting user's status

* Issue **#1079** : Improved the logging of permission errors encountered during stream processing

* Issue **#1058** : Added property `stroom.pipeline.parser.secureProcessing` to enable/disable the XML secure processing feature.

* Issue **#1062** : Add env var for UI path

* Uplift distribution visualisation content pack to v3.1.0

* Add transform_user_extract.py, for pre-6.0 to 6.0 user migration

* Issue **#1059** : Fix guice errors on stroom-proxy startup.

* Issue **#1010** : Improve distribution start/stop/etc scripts by adding monochrome switch and background log tailing.

* Issue **#1053** : Add API to disabled authorisation users

* Issue **#1042** : Improve error message for an ApiException when requesting a user's token.

* Issue **#1050** : Prevent creation of permission entries if key already exists.

* Issue **#1015** : Add sortDirections[] and keySortDirection to visualisation data object to fix sorting in the visualisations.

* Issue **#1019** : Fix visualisations settings dialog so you can un-set text and list controls.

* Issue **#1041** : Add a healthcheck to Stroom to alert for API key expiry

* Issue **#1040** : Fix for visualisations that do not require nested data.

* Issue **#1036** : Fix for scrollbar position on explorer popup windows.

* Issue **#1037** : Updated `moment.js` for parsing/formatting dates and times.

* Issue **#1021** : Dashboard links now allow `{}` characters to be used without URL encoding.

* Issue **#1018** : Added Health Checks for the external connectors that are registered via plugins

* Issue **#1025** : Fixed ACE editor resize issue where horizontal scroll bar was not always correctly shown.

* Issue **#1025** : Updated ACE editor to v1.4.2.

* Issue **#1022** : Added `Contains` condition to all search expression fields so that regex terms can be used.

* Issue **#1024** : Superseded output helper no longer expects initialisation in all cases.

* Issue **#1021** : Multiple changes to improve vis, dashboard and external linking in Stroom.

* Issue **#1019** : Fix visualisations settings dialog so you can un-set text and list controls.

* Issue **#986** : Fix direct dashboard links.

* Issue **#1006** : Added Exception Mapper for PermissionExceptions to return HTTP FORBIDDEN.

* Issue **#1012** : Fix for NPE caused when checking if an output is superseded.

* Issue **#1011** : Old UI versions running in browsers often cause Stroom to throw an NPE as it can't find the appropriate GWT serialisation policy. Stroom will no longer throw an NPE but will report an `IncompatibleRemoteServiceException` instead. This is the default GWT behaviour.

* Issue **#1007** : Max visualisation results are now limited by default to the maximum number of results defined for the first level of the parent table. This can be further limited by settings in the visualisation.

* Issue **#1004** : Table cells now support multiple links.

* Issue **#1001** : Changed link types to `tab`, `dialog`, `dashboard`, `browser`.

* Issue **#1001** : Added dashboard link option to link to a dashboard from within a vis, e.g. `stroomLink(d.name, 'type=Dashboard&uuid=<TARGET_DASHBOARD_UUID>&params=userId%3D' + d.name, 'DASHBOARD')`.

* Issue **#1001** : Added dashboard link option to link to a dashboard using the `DASHBOARD` target name, e.g. `link(${UserId}, concat('type=Dashboard&uuid=<TARGET_DASHBOARD_UUID>', ${UserId}), '', 'DASHBOARD')`.

* Issue **#1002** : Popup dialogs shown when clicking dashboard hyperlinks are now resizable.

* Issue **#993** : Moving documents in the explorer no longer affects items that are being edited as they are not updated in the process.

* Issue **#996** : Updated functions in dashboard function picker.

* Issue **#981** : Fixed dashboard deletion

* Issue **#989** : Upgraded stroom-expression to v1.4.13 to add new dashboard `link` function.

* Issue **#988** : Changed `generate-url` XSLT function to `link` so it matches the dashboard expression. Changed the parameters to create 4 variants of the function to make creation of simple links easier.

* Issue **#980** : Fix for NPE when fetching dependencies for scripts.

* Issue **#978** : Re-ordering the fields in stream data source

* Issue **gchq/stroom-content#31** : Uplift stroom-logs content pack to v2.0-alpha.5.

* Issue **#982** : Stop proxy trying to health check the content syncing if it isn't enabled.

* Change error logging in ContentSyncService to log stack trace

* Uplift send_to_stroom.sh in the distribution to v2.0

* Issue **#973** : Export servlet changed to a Resource API, added permission check, improved error responses.

* Issue **#969** : The code now suppresses errors for index shards being locked for writing as it is expected. We now lock shards using maps rather than the file system as it is more reliable between restarts.

* Issue **#941** : Internal Meta Stats are now being written

* Issue **#970** : Add stream type of `Records` for translated stroom app events.

* Issue **#966** : Proxy was always reporting zero bytes for the request content in the receive log.

* Issue **#938** : Fixed an NPE in authentication session state.

* Change the proxy yaml configuration for the stack to add `remotedn` and `remotecertexpiry` headers to the receive log

* Change logback archived logs to be gzip compressed for stroom and proxy

* Uplift stroom-logs content pack to v2.0-alpha.3

* Uplift send_to_stroom script to v1.8.1

* Issue **#324** : Changed XML serialisation so that forbidden XML characters U+FFFE and U+FFFF are not written. Note that these characters are not even allowed as character references so they are ignored entirely.

* Issue **#945** : More changes to fix some visualisations only showing 10 data points.

* Issue **#945** : Visualisations now show an unlimited number of data points unless constrained by their parent table or their own maximum value setting.

* Issue **#948** : Catching Spring initialisation runtime errors and ensuring they are logged.

* Add `set_log_levels.sh` script to the distribution

* Uplift visualisations content pack to v3.0.6 in the gradle build

* Issue **#952** : Remote data sources now execute calls within the context of the user for the active query. As a result all running search `destroy()` calls will now be made as the same user that initiated the search.

* Issue **#566** : Info and warning icons are now displayed in stepping screen when needed.

* Issue **#923** : Dashboard queries will now terminate if there are no index shards to search.

* Issue **#959** : Remove Material UI from Login and from password management pages

* Issue **#933** : Add health check for password resets

* Issue **#929** : Add more comprehensive password validation

* Issue **#876** : Fix password reset issues

* Issue **#768** : Preventing deletion of /store in empty volumes

* Issue **#939** : Including Subject DN in receive.log

* Issue **#940** : Capturing User DN and cert expiry on DW terminated SSL

* Issue **#744** : Improved reporting of error when running query with no search extraction pipeline

* Issue **#134** : Copy permissions from parent button

* Issue **#688** : Cascading permissions when moving/copying folder into a destination

* Issue **#788** : Adding DocRef and IsDocRef to stroom query to allow doc ref related filtering. Migration of stream filters uses this.

* Issue **#936** : Add conversion of header `X-SSL-Client-V-End` into `RemoteCertExpiry`, translating date format in the process.

* Issue **#953** : Fixed NPE.

* Issue **#947** : Fixed issue where data retention policy contains incorrect field names.

* Remove Material UI from the Users and API Keys pages

* Add content packs to stroom distribution

* Change distribution to use send_to_stroom.sh v1.7

* Updated stroom expression to v1.4.12 to improve handling or errors values and add new type checking functions `isBoolean()`, `isDouble()`, `isError()`, `isInteger()`, `isLong()`, `isNull()`, `isNumber()`, `isString()`, `isValue()`. Testing equality of null with `x=null()` is no longer valid and must be replaced with `isNull(x)`.

* Issue **#920** : Fix error handling for sql stats queries

* Remove log sending cron process from docker images (now handled by stroom-log-sender).

* Issue **#924** : The `FindReplaceFilter` now records the location of errors.

* Issue **#939** : Added `remotedn` to default list of keys to include in `receive.log`.

* Add git_tag and git_commit labels to docker images

* Uplift stroom-logs content pack in docker image to` v2.0-alpha.2`

* Stop truncation of `logger` in logback console logs

* Issue **#921** : Renaming open documents now correctly changes their tab name. Documents that are being edited now prevent the rename operation until they are saved.

* Issue **#922** : The explorer now changes the selection on a right click if the item clicked is not already selected (could be part of a multi select).

* Issue **#903** : Feed names can now contain wildcard characters when filtering in the data browser.

* Add API to allow creation of an internal Stroom user.

* Fix logger configuration for SqlExceptionHelper

* Add template-pipelines and standard-pipelines content packs to docker image

* Issue **#904** : The UI now shows dictionary names in expressions without the need to enter edit mode.

* Updated ACE editor to v1.4.1.

* Add colours to console logs in docker.

* Issue **#869** : Delete will now properly delete all descendant nodes and documents when deleting folders but will not delete items from the tree if they cannot be deleted, e.g. feeds that have associated data.

* Issue **#916** : You can no longer export empty folders or import nothing.

* Issue **#911** : Changes to feeds and pipelines no longer clear data browsing filters.

* Issue **#907** : Default volumes are now created as soon as they are needed.

* Issue **#910** : Changes to index settings in the UI now register as changes and enable save.

* Issue **#913** : Improve FindReplaceFilter to cope with more complex conditions.

* Change log level for SqlExceptionHelper to OFF, to stop expected exceptions from polluting the logs

* Fix invalid requestLog logFormat in proxy configuration

* Stop service discovery health checks being registered if stroom.serviceDiscovery.enabled=false

* Add fixed version of send_to_stroom.sh to release distribution

* Uplift docker base image for stroom & proxy to openjdk:8u181-jdk-alpine3.8

* Add a health check for getting a public key from the authentication service.

* Issue **#897** : Import no longer attempts to rename or move existing items but will still update content.

* Issue **#902** : Improved the XSLT `format-date` function to better cope with week based dates and to default values to the stream time where year etc are omitted.

* Issue **#905** : Popup resize and move operations are now constrained to ensure that a popup cannot be dragged off screen or resized to be bigger than the current browser window size.

* Issue **#898** : Improved the way many read only aspects of the UI behave.

* Issue **#894** : The system now generates and displays errors to the user when you attempt to copy a feed.

* Issue **#896** : Extended folder `create` permissions are now correctly cached.

* Issue **#893** : You can now manage volumes without the `Manage Nodes` permission.

* Issue **#892** : The volume editor now waits for the node list to be loaded before opening.

* Issue **#889** : Index field editing in the UI now works correctly.

* Issue **#891** : `StreamAppender` now keeps track of it's own record write count and no longer makes use of any other write counting pipeline element.

* Issue **#885** : Improved the way import works to ensure updates to entities are at least attempted when creating an import confirmation.

* Issue **#892** : Changed `Ok` to `OK`.

* Issue **#883** : Output streams are now immediately unlocked as soon as they are closed.

* Removed unnecessary OR operator that was being inserted into expressions where only a single child term was being used. This happened when reprocessing single streams.

* Issue **#882** : Splitting aggregated streams now works when using `FindReplaceFilter`. This functionality was previously broken because various reader elements were not passing the `endStream` event on.

* Issue **#881** : The find and replace strings specified for the `FindReplaceFilter` are now treated as unescaped Java strings and now support new line characters etc.

* Issue **#880** : Increased the maximum value a numeric pipeline property can be set to via the UI to 10000000.

* Issue **#888** : The dependencies listing now copes with external dependencies failing to provide data due to authentication issues.

* Issue **#890** : Dictionaries now show the words tab by default.

* Add admin healthchecks to stroom-proxy

* Add stroom-proxy docker image

* Refactor stroom docker images to reduce image size

* Add enabled flag to storing, forwarding and synching in stroom-proxy configuration

* Issue **#884** : Added extra fonts to stroom docker image to fix bug downloading xls search results.

* Issue **#879** : Fixed bug where reprocess and delete did not work if no stream status was set in the filter.

* Issue **#878** : Changed the appearance of stream filter fields to be more user friendly, e.g. `feedName` is now `Feed` etc.

* Issue **#809** : Changed default job frequency for `Stream Attributes Retention` and `Stream Task Retention` to `1d` (one day).

* Issue **#813** : Turned on secure processing feature for XML parsers and XML transformers so that external entities are not resolved. This prevents DoS attacks and gaining unauthorised access to the local machine.

* Issue **#871** : Fix for OptimisticLockException when processing streams.

* Issue **#872** : The parser cache is now automatically cleared when a schema changes as this can affect the way a data splitter parser is created.

* Add a health check for getting a public key from the authentication service.

* Issue **#897** : Import no longer attempts to rename or move existing items but will still update content.

* Issue **#902** : Improved the XSLT `format-date` function to better cope with week based dates and to default values to the stream time where year etc are omitted.

* Issue **#905** : Popup resize and move operations are now constrained to ensure that a popup cannot be dragged off screen or resized to be bigger than the current browser window size.

* Issue **#898** : Improved the way many read only aspects of the UI behave.

* Issue **#894** : The system now generates and displays errors to the user when you attempt to copy a feed.

* Issue **#896** : Extended folder `create` permissions are now correctly cached.

* Issue **#893** : You can now manage volumes without the `Manage Nodes` permission.

* Issue **#892** : The volume editor now waits for the node list to be loaded before opening.

* Issue **#889** : Index field editing in the UI now works correctly.

* Issue **#891** : `StreamAppender` now keeps track of it's own record write count and no longer makes use of any other write counting pipeline element.

* Issue **#885** : Improved the way import works to ensure updates to entities are at least attempted when creating an import confirmation.

* Issue **#892** : Changed `Ok` to `OK`.

* Issue **#883** : Output streams are now immediately unlocked as soon as they are closed.

* Removed unnecessary OR operator that was being inserted into expressions where only a single child term was being used. This happened when reprocessing single streams.

* Issue **#882** : Splitting aggregated streams now works when using `FindReplaceFilter`. This functionality was previously broken because various reader elements were not passing the `endStream` event on.

* Issue **#881** : The find and replace strings specified for the `FindReplaceFilter` are now treated as unescaped Java strings and now support new line characters etc.

* Issue **#880** : Increased the maximum value a numeric pipeline property can be set to via the UI to 10000000.

* Issue **#888** : The dependencies listing now copes with external dependencies failing to provide data due to authentication issues.

* Issue **#890** : Dictionaries now show the words tab by default.

* Add admin healthchecks to stroom-proxy

* Add stroom-proxy docker image

* Refactor stroom docker images to reduce image size

* Add enabled flag to storing, forwarding and synching in stroom-proxy configuration

* Issue **#884** : Added extra fonts to stroom docker image to fix bug downloading xls search results.

* Issue **#879** : Fixed bug where reprocess and delete did not work if no stream status was set in the filter.

* Issue **#878** : Changed the appearance of stream filter fields to be more user friendly, e.g. `feedName` is now `Feed` etc.

* Issue **#809** : Changed default job frequency for `Stream Attributes Retention` and `Stream Task Retention` to `1d` (one day).

* Issue **#813** : Turned on secure processing feature for XML parsers and XML transformers so that external entities are not resolved. This prevents DoS attacks and gaining unauthorised access to the local machine.

* Issue **#871** : Fix for OptimisticLockException when processing streams.

* Issue **#872** : The parser cache is now automatically cleared when a schema changes as this can affect the way a data splitter parser is created.

* Issue **#865** : Made `stroom.conf` location relative to YAML file when `externalConfig` YAML property is set.

* Issue **#867** : Added an option `showReplacementCount` to the find replace filter to choose whether to report total replacements on process completion.

* Issue **#867** : Find replace filter now creates an error if an invalid regex is used.

* Issue **#855** : Further fixes for stepping data that contains a BOM.

* Changed selected default tab for pipelines to be `Data`.

* Issue **#860** : Fixed issue where stepping failed when using any sort of input filter or reader before the parser.

* Issue **#867** : Added an option `showReplacementCount` to the find replace filter to choose whether to report total replacements on process completion.

* Improved Stroom instance management scripts

* Add contentPack import

* Fix typo in Dockerfile

* Issue **#859** : Change application startup to keep retrying when establishing a DB connection except for certain connection errors like access denied.

* Issue **#730** : The `System` folder now displays data and processors. This is a bug fix related to changing the default initial page for some document types.

* Issue **#854** : The activity screen no longer shows a permission error when shown to non admin users.

* Issue **#853** : The activity chooser will no longer display on startup if activity tracking is not enabled.

* Issue **#855** : Fixed stepping data that contains a BOM.

* Change base docker image to openjdk:8u171-jdk-alpine

* Improved loading of activity list prior to showing the chooser dialog.

* Issue **#852** : Fix for more required permissions when logging other 'find' events.

* Issue **#730** : Changed the default initial page for some document types.

* Issue **#852** : Fix for required permission when logging 'find' events.

* Changed the way the root pane loads so that error popups that appear when the main page is loading are not hidden.

* Issue **#851** : Added additional type info to type id when logging events.

* Issue **#848** : Fixed various issues related to stream processor filter editor.

* Issue **#815** : `stroom.pageTitle` property changed to `stroom.htmlTitle`.

* Issue **#732** : Added `host-address` and `host-name` XSLT functions.

* Issue **#338** : Added `splitAggregatedStreams` property to `StreamAppender`, `FileAppender` and `HDFSFileAppender` so that aggregated streams can be split into separate streams on output.

* Issue **#338** : Added `streamNo` path replacement variable for files to record the stream number within an aggregate.

* Added tests and fixed sorting of server tasks.

* Improved the way text input and output is buffered and recorded when stepping.

* The find and replace filter now resets the match count in between nested streams so that each stream is treated the same way, i.e. it can have the same number of text replacements.

* Added multiple fixes and improvements to the find and replace filter including limited support of input/output recording when stepping.

* Issue **#827** : Added `TextReplacementFilterReader` pipeline element.

* Issue **#736** : Added sorting to server tasks table.

* Inverted the behaviour of `disableQueryInfo` to now be `requireQueryInfo`.

* Issue **#596** : Rolling stream and file appenders can now roll on a cron schedule in addition to a frequency.

* The accept button now enabled on splash screen.

* Added additional event logging to stepping.

* An activity property with an id of `disableQueryInfo` can now be used to disable the query info popup on a per activity basis.

* Activity properties can now include the attributes `id`, `name`, `showInSelection` and `showInList` to determine their appearance and behaviour;

* Nested elements are now usable in the activity editor HTML.

* Record counts are now recorded on a per output stream basis even when splitting output streams.

* Splash presenter buttons are now always enabled.

* Fix background colour to white on activity pane.

* Changed `splitWhenBiggerThan` property to `rollSize` and added the property to the rolling appenders for consistency.

* Issue **#838** : Fix bug where calculation of written and read bytes was being accounted for twice due to the use of Java internal `FilterInputStream` and `FilterOutputStream` behaviour. This was leading to files being split at half od the expected size. Replaced Java internal classes with our own `WrappedInputStream` and `WrappedOutputStream` code.

* Issue **#837** : Fix bug to no longer try and record set activity events for null activities.

* Issue **#595** : Added stream appender and file appender property `splitWhenBiggerThan` to limit the size of output streams.

* Now logs activity change correctly.

* Add support for checkbox and selection control types to activity descriptions.

* Issue **#833** : The global property edit dialog can now be made larger.

* Fixed some issues in the activity manager.

* Issue **#722** : Change pipeline reference data loader to store its reference data in an off-heap disk backed LMDB store to reduce Java heap usage. See the `stroom.refloader.*` properties for configuration of the off-heap store.

* Issue **#794** : Automatically suggest a pipeline element name when creating it

* Issue **#792** : Preferred order of properties for Pipeline Elements

* Issue **824** : Fix for replace method in PathCreator also found in stroom proxy.

* Issue **#828** : Changed statistics store caches to 10 minute time to live so that they will definitely pick up new statistics store definitions after 10 minutes.

* Issue **#774** : Event logging now logs find stream criteria correctly so that feeds ids are included.

* Issue **#829** : Stroom now logs event id when viewing individual events.

* Added functionality to record actions against user defined activities.

* Added functionality to show a splash screen on login.

* Issue **#791** : Fixed broken equals method so query total row count gets updated correctly.

* Issue **#830** : Fix for API queries not returning before timing out.

* Issue **#824** : Fix for replace method in PathCreator also found in stroom proxy.

* Issue **#820** : Fix updating index shards so that they are loaded, updated and saved under lock.

* Issue **#819** : Updated `stroom-expression` to v1.4.3 to fix violation of contract exception when sorting search results.

* Issue **#817** : Increased maximum number of concurrent stream processor tasks to 1000 per node.

* Moved Index entities over to the new multi part document store.

* Moved Pipeline entities over to the new multi part document store.

* Moved both Statistic Store entity types over to the new multi part document store.

* Moved XSLT entities over to the new multi part document store.

* Moved Visualisation entities over to the new multi part document store.

* Moved Script entities over to the new multi part document store.

* Moved Dashboard entities over to the new multi part document store.

* Moved XmlSchema entities over to the new multi part document store.

* Moved TextConverter entities over to the new multi part document store.

* Modified the storage of dictionaries to use the new multi part document store.

* Changed the document store to hold multiple entries for a document so that various parts of a document can be written separately, e.g. the meta data about a dictionary and the dictionary text are now written as separate DB entries. Entries are combined during the serialisation/deserialisation process.

* Changed the import export API to use byte arrays to hold values rather than strings. *POSSIBLE BREAKING CHANGE*
Issue **gchq/stroom-expression#22** : Add `typeOf(...)` function to dashboard.

* Issue **#697** : Fix for reference data sometimes failing to find the appropriate effective stream due to the incorrect use of the effective stream cache. It was incorrectly configured to use a time to idle (TTI) expiry rather than a time to live (TTL) expiry meaning that heavy use of the cache would prevent the cached effective streams being refreshed.

* Issue **#806** : Fix for clearing previous dashboard table results if search results deliver no data.

* Issue **#805** : Fix for dashboard date time formatting to use local time zone.

* Issue **#803** : Fix for group key conversion to an appropriate value for visualisations.

* Issue **#802** : Restore lucene-backward-codecs to the build

* Issue **#800** : Add DB migration script 33 to replace references to the `Stream Type` type in the STRM_PROC_FILT table with `streamTypeName`.

* Issue **#798** : Add DB migration script 32 to replace references to the `NStatFilter` type in the PIPE table with `StatisticsFilter`.

* Fix data receipt policy defect

* Issue **#791** : Search completion signal is now only sent to the UI once all pending search result merges are completed.

* Issue **#795** : Import and export now works with appropriate application permissions. Read permission is required to export items and Create/Update permissions are required to import items depending on whether the update will create a new item or update an existing one.

* Improve configurabilty of stroom-proxy.

* Issue **#783** : Reverted code that ignored duplicate selection to fix double click in tables.

* Issue **#782** : Fix for NPE thrown when using CountGroups when GroupKey string was null due to non grouped child rows.

* Issue **#778** : Fix for text selection on tooltips etc in the latest version of Chrome.

* Uplift stroom-expression to v1.4.1

* Issue **#776** : Removal of index shard searcher caching to hopefully fix Lucene directory closing issue.

* Issue **#779** : Fix permissions defect.

* Issue **gchq/stroom-expression#22** : Add `typeOf(...)` function to dashboard.

* Issue **#766** : Fix NullPointerExceptions when downloading table results to Excel format.

* Issue **#770** : Speculative fix for memory leak in SQL Stats queries.

* Issue **#761** : New fix for premature truncation of SQL stats queries due to thread interruption.

* Issue **#748** : Fix build issue resulting from a change to SafeXMLFilter.

* Issue **#748** : Added a command line interface (CLI) in addition to headless execution so that full pipelines can be run against input files.

* Issue **#748** : Fixes for error output for headless mode.

* Issue **#761** : Fixed statistic searches failing to search more than once.

* Issue **#756** : Fix for state being held by `InheritableThreadLocal` causing objects to be held in memory longer than necessary.

* Issue **#761** : Fixed premature truncation of SQL stats queries due to thread interruption.

* Added `pipeline-name` and `put` XSLT functions back into the code as they were lost in a merge.

* Issue **#749** : Fix inability to query with only `use` privileges on the index.

* Issue **#613** : Fixed visualisation display in latest Firefox and Chrome.

* Added permission caching to reference data lookup.

* Updated to stroom-expression 1.3.1

    Added cast functions `toBoolean`, `toDouble`, `toInteger`, `toLong` and `toString`.
    Added `include` and `exclude` functions.
    Added `if` and `not` functions.
    Added value functions `true()`, `false()`, `null()` and `err()`.
    Added `match` boolean function.
    Added `variance` and `stDev` functions.
    Added `hash` function.
    Added `formatDate` function.
    Added `parseDate` function.
    Made `substring` and `decode` functions capable of accepting functional parameters.
    Added `substringBefore`, `substringAfter`, `indexOf` and `lastIndexOf` functions.
    Added `countUnique` function.

* Issue **#613** : Fixed visualisation display in latest Firefox and Chrome.

* Issue **#753** : Fixed script editing in UI.

* Issue **#751** : Fix inability to query on a dashboard with only use+read rights.

* Issue **#719** : Fix creation of headless Jar to ensure logback is now included.

* Issue **#735** : Change the format-date xslt function to parse dates in a case insensitive way.

* Issue **#719** : Fix creation of headless Jar. Exclude gwt-unitCache folder from build JARs.

* Issue **#720** : Fix for Hessian serialisation of table coprocessor settings.

* Issue **#217** : Add an 'all/none' checkbox to the Explorer Tree's quick filter.

* Issue **#400** : Shows a warning when cascading folder permissions.

* Issue **#405** : Fixed quick filter on permissions dialog, for users and for groups. It will now match anywhere in the user or group name, not just at the start.

* Issue **#708** : Removed parent folder UUID from ExplorerActionHandler.

* Application security code is now implemented using lambda expressions rather than AOP. This simplifies debugging and makes the code easier to understand.

* Changed the task system to allow task threads to be interrupted from the task UI.

* Made changes to improve search performance by making various parts of search wait for interruptible conditions.

* Migrated code from Spring to Guice for managing dependency injection.

* Issue **#229** : When a user 'OKs' a folder permission change it can take a while to return. This disables the ok/cancel buttons while Stroom is processing the permission change.

* Issue **#405** : Fixed quick filter on permissions dialog, for users and for groups. It will now match anywhere in the user or group name, not just at the start.

* Issue **#588** : Fixed display of horizontal scrollbar on explorer tree in export, create, copy and move dialogs.

* Issue **#691** : Volumes now reload on edit so that the entities are no longer stale the second time they are edited.

* Issue **#692** : Properties now reload on edit so that the entities are no longer stale the second time they are edited.

* Issue **#703** : Removed logging of InterruptedException stack trace on SQL stat queries, improved concurrency code.

* Issue **#697** : Improved XSLT `Lookup` trace messages.

* Issue **#697** : Added a feature to trace XSLT `Lookup` attempts so that reference data lookups can be debugged.

* Issue **#702** : Fix for hanging search extraction tasks

* Issue **#701** : The search `maxDocIdQueueSize` is now 1000 by default.

* Issue **#700** : The format-date XSLT function now defaults years, months and days to the stream receipt time regardless of whether the input date pattern specifies them.

* Issue **#657** : Change SQL Stats query code to process/transform the data as it comes back from the database rather than holding the full resultset before processing. This will reduce memory overhead and improve performance.

* Issue **#634** : Remove excessive thread sleeping in index shard searching. Sleeps were causing a significant percentage of inactivity and increasing memory use as data backed up. Add more logging and logging of durations of chunks of code. Add an integration test for testing index searching for large data volumes.

* Issue **#698** : Migration of Processing Filters now protects against folders that have since been deleted

* Issue **#634** : Remove excessive thread sleeping in index shard searching. Sleeps were causing a significant percentage of inactivity and increasing memory use as data backed up. Add more logging and logging of durations of chunks of code. Add an integration test for testing index searching for large data volumes.

* Issue **#659** : Made format-date XSLT function default year if none specified to the year the data was received unless this would make the date later then the received time in which case a year is subtracted.

* Issue **#658** : Added a hashing function for XSLT translations.

* Issue **#680** : Fixed the order of streams in the data viewer to descending by date

* Issue **#679** : Fixed the editing of Stroom properties that are 'persistent'.

* Issue **#681** : Added dry run to check processor filters will convert to find stream criteria. Throws error to UI if fails.

* Issue **#676** : Fixed use of custom stream type values in expression based processing filters.

* Issue **#673** : Fixed issue with Stream processing filters that specify Create Time

* Issue **#675** : Fixed issue with datafeed requests authenticating incorrectly

* Issue **#666** : Fixed the duplicate dictionary issue in processing filter migrations, made querying more efficient too

* Database migration fixes and tools

* Issue **#668** : Fixed the issue that prevented editing of stroom volumes

* Issue **#669** : Elastic Index Filter now uses stroomServiceUser to retrieve the index config from the Query Elastic service.

* Minor fix to migrations

* Add logging to migrations

* Add logging to migrations

* Issue **#651** : Removed the redundant concept of Pipeline Types, it's half implementation prevented certain picker dialogs from working.

* Issue **#481** : Fix handling of non-incremental index queries on the query API. Adds timeout option in request and blocking code to wait for the query to complete. Exit early from wait loops in index/event search.

* Issue **#626** : Fixed issue with document settings not being persisted

* Issue **#621** : Changed the document info to prevent requests for multi selections

* Issue **#620** : Copying a directory now recursively copies it's contents, plus renaming copies is done more intelligently.

* Issue **#546** : Fixed race conditions with the Explorer Tree, it was causing odd delays to population of the explorer in various places.

* Issue **#495** : Fixed the temporary expansion of the Explorer Tree caused by filtering

* Issue **#376** : Welcome tab details fixed since move to gradle

* Issue **#523** : Changed permission behaviours for copy and move to support `None`, `Source`, `Destination` and `Combined` behaviours. Creating new items now allows for `None` and `Destination` permission behaviours. Also imported items now receive permissions from the destination folder. Event logging now indicates the permission behaviour used during copy, move and create operations.

* Issue **#480** : Change the downloaded search request API JSON to have a fetch type of ALL.

* Issue **#623** : Fixed issue where items were being added to sublist causing a stack overflow exception during data retention processing.

* Issue **#617** : Introduced a concept of `system` document types that prevents the root `System` folder type from being created, copied, deleted, moved, renamed etc.

* Issue **#622** : Fix incorrect service discovery based api paths, remove authentication and authorisation from service discovery

* Issue **#568** : Fixed filtering streams by pipeline in the pipeline screen.

* Issue **#565** : Fixed authorisation issue on dashboards.

* Issue **#592** : Mount stroom at /stroom.

* Issue **#608** : Fixed stream grep and stream dump tools and added tests to ensure continued operation.

* Issue **#603** : Changed property description from `tags` to `XML elements` in `BadTextXMLFilterReader`.

* Issue **#600** : Added debug to help diagnose cause of missing index shards in shard list.

* Issue **#611** : Changed properties to be defined in code rather than Spring XML.

* Issue **#605** : Added a cache for retrieving user by name to reduce DB use when pushing users for each task.

* Issue **#610** : Added `USE INDEX (PRIMARY)` hint to data retention select SQL to improve performance.

* Issue **#607** : Multiple improvements to the code to ensure DB connections, prepared statements, result sets etc use try-with-resources constructs wherever possible to ensure no DB resources are leaked. Also all connections obtained from a data source are now returned appropriately so that connections from pools are reused.

* Issue **#602** : Changed the data retention rule table column order.

* Issue **#606** : Added more stroom properties to tune the c3P0 connection pool. The properties are prefixed by `stroom.db.connectionPool` and `stroom.statistics.sql.db.connectionPool`.

* Issue **#601** : Fixed NPE generated during index shard retention process that was caused by a shard being deleted from the DB at the same time as the index shard retention job running.

* Issue **#609** : Add configurable regex to replace IDs in heap histogram class names, e.g. `....$Proxy54` becomes `....$Proxy--ID-REMOVED--`

* Issue **#570** : Refactor the heap histogram internal statistics for the new InternalStatisticsReceiver

* Issue **#599** : DocumentServiceWriteAction was being used in the wrong places where EntityServiceSaveAction should have been used instead to save entities that aren't document entities.

* Issue **#593** : Fixed node save RPC call.

* Issue **#591** : Made the query info popup more configurable with a title, validation regex etc. The popup will now only be displayed when enabled and when a manual user action takes place, e.g. clicking a search button or running a parameterised execution with one or more queries.

* Added 'prompt' option to force the identity provider to ask for a login.

* Issue **#549** : Change to not try to connect to kafka when kafka is not configured and improve failure handling

* Issue **#573** : Fixed viewing folders with no permitted underlying feeds. It now correctly shows blank data screen, rather than System/Data.

* Issue **#150** : Added a feature to optionally require specification of search purpose.

* Issue **#572** : Added a feature to allow easy download of dictionary contents as a text file.

* Generate additional major and minor floating docker tags in travis build, e.g. v6-LATEST and v6.0-LATEST

* Change docker image to be based on openjdk:8u151-jre-alpine

* Added a feature to list dependencies for all document entities and indicate where dependencies are missing.

* Issue **#540** : Improve description text for stroom.statistics.sql.maxProcessingAge property

* Issue **#538** : Lists of items such as users or user groups were sometimes not being converted into result pages correctly, this is now fixed.

* Issue **#537** : Users without `Manage Policies` permission can now view streams.

* Issue **#522** : Selection of data retention rules now remains when moving rules up or down.

* Issue **#411** : When data retention rules are disabled they are now shown greyed out to indicate this.

* Issue **#536** : Fix for missing visualisation icons.

* Issue **#368** : Fixed hidden job type button on job node list screen when a long cron pattern is used.

* Issue **#507** : Added dictionary inheritance via import references.

* Issue **#554** : Added a `parseUri` XSLT function.

* Issue **#557** : Added dashboard functions to parse and output URI parts.

* Issue **#552** : Fix for NPE caused by bad XSLT during search data extraction.

* Issue **#560** : Replaced instances of `Files.walk()` with `Files.walkFileTree()`. `Files.walk()` throws errors if any files are deleted or are not accessible during the walk operation. This is a major issue with the Java design for walking files using Java 8 streams. To avoid this issue `Files.walkFileTree()` has now been used in place of `Files.walk()`.

* Issue **#567** : Changed `parseUri` to be `parse-uri` to keep it consistently named with respect to other XSLT functions. The old name `parseUri` still works but is deprecated and will be removed in a later version.

* Issue **#567** : The XSLT function `parse-uri` now correctly returns a `schemeSpecificPart` element rather than the incorrectly named `schemeSpecificPort`.

* Issue **#567** : The dashboard expression function `extractSchemeSpecificPortFromUri` has now been corrected to be called `extractSchemeSpecificPartFromUri`.

* Issue **#567** : The missing dashboard expression function `extractQueryFromUri` has been added.

* Issue **#571** : Streams are now updated to have a status of deleted in batches using native SQL and prepared statements rather than using the stream store.

* Issue **#559** : Changed CSS to allow table text selection in newer browsers.

* Issue **#574** : Fixed SQL debug trace output.

* Issue **#574** : Fixed SQL UNION code that was resulting in missing streams in the data browser when paging.

* Issue **#590** : Improved data browser performance by using a local cache to remember feeds, stream types, processors, pipelines etc while decorating streams.

* Issue **#150** : Added a property to optionally require specification of search purpose.

* New authentication flow based around OpenId

* New user management screens

* The ability to issue API keys

* Issue **#501** : Improve the database teardown process in integration tests to speed up builds

* Relax regex in build script to allow tags like v6.0-alpha.3 to be published to Bintray

* Add Bintray publish plugin to Gradle build

* Issue **#75** : Upgraded to Lucene 5.

* Issue **#135** : [BREAKING CHANGE] Removed JODA Time library and replaced with Java 7 Time API. This change breaks time zone output previously formatted with `ZZ` or `ZZZ`.

* Added XSLT functions generate-url and fetch-json

* Added ability to put clickable hyperlinks in Dashboard tables

* Added an HTTP appender.

* Added an appender for the proxy store.

* Issue **#412** : Fixed no-column table breakage

* Issue **#380** : Fixed build details on welcome/about

* Issue **#348** : Fixed new menu icons.

* Issue **98** : Fix premature trimming of results in the store

* Issue **360** : Fix inability to sort sql stats results in the dashboard table

* Issue **#550** : Fix for info message output for data retention.

* Issue **#551** : Improved server task detail for data retention job.

* Issue **#541** : Changed stream retention job descriptions.

* Issue **#553** : The data retention job now terminates if requested to do so and also tracks progress in a local temp file so a nodes progress will survive application restarts.

* Change docker image to use openjdk:8u151-jre-alpine as a base

* Issue **#539** : Fix issue of statistic search failing after it is imported

* Issue **#547** : Data retention processing is now performed in batches (size determined by `stroom.stream.deleteBatchSize`). This change should reduce the memory required to process the data retention job.

* Issue **#541** : Marked old stream retention job as deprecated in description.

* Issue **#542** : Fix for lazy hibernate object initialisation when stepping cooked data.

* Issue **#524** : Remove dependency on stroom-proxy:stroom-proxy-repo and replaced with duplicated code from stroom-proxy-repo (commit b981e1e)

* Issue **#203** : Initial release of the new data receipt policy functionality.

* Issue **#202** : Initial release of the new data retention policy functionality.

* Issue **#521** : Fix for the job list screen to correct the help URL.

* Issue **#526** : Fix for XSLT functions that should return optional results but were being forced to return a single value.

* Issue **#527** : Fix for XSLT error reporting. All downstream errors were being reported as XSLT module errors and were
 hiding the underlying exception.

* Issue **#501** : Improve the database teardown process in integration tests to speed up builds.

* Issue **#511** : Fix NPE thrown during pipeline stepping by downstream XSLT.

* Issue **#521** : Fix for the job list screen to use the help URL system property for displaying context sensitive help.

* Issue **#511** : Fix for XSLT functions to allow null return values where a value cannot be returned due to an error etc.

* Issue **#515** : Fix handling of errors that occur before search starts sending.

* Issue **#506** : In v5 dashboard table filters were enhanced to allow parameters to be used in include/exclude filters. The implementation included the use of ` \ ` to escape `$` characters that were not to be considered part of a parameter reference. This change resulted in regular expressions requiring ` \ ` being escaped with additional ` \ ` characters. This escaping has now been removed and instead only `$` chars before `{` chars need escaping when necessary with double `$$` chars, e.g. use `$${something` if you actually want `${something` not to be replaced with a parameter.

* Issue **#505** : Fix the property UI so all edited value whitespace is trimmed

* Issue **#513** : Now only actively executing tasks are visible as server tasks

* Issue **#483** : When running stream retention jobs the transactions are now set to REQUIRE_NEW to hopefully ensure that the job is done in small batches rather than a larger transaction spanning multiple changes.

* Issue **#508** : Fix directory creation for index shards.

* Issue **#492** : Task producers were still not being marked as complete on termination which meant that the parent cluster task was not completing. This has now been fixed.

* Issue **#497** : DB connections obtained from the data source are now released back to the pool after use.

* Issue **#492** : Task producers were not being marked as complete on termination which meant that the parent cluster task was not completing. This has now been fixed.

* Issue **#497** : Change stream task creation to use straight JDBC rather than hibernate for inserts and use a configurable batch size (stroom.databaseMultiInsertMaxBatchSize) for the inserts.

* Issue **#502** : The task executor was not responding to shutdown and was therefore preventing the app from stopping gracefully.

* Issue **#476** : Stepping with dynamic XSLT or text converter properties now correctly falls back to the specified entity if a match cannot be found by name.

* Issue **#498** : The UI was adding more than one link between 'Source' and 'Parser' elements, this is now fixed.

* Issue **#492** : Search tasks were waiting for part of the data extraction task to run which was not checking for termination. The code for this has been changed and should now terminate when required.

* Issue **#494** : Fix problem of proxy aggregation never stopping if more files exist

* Issue **#490** : Fix errors in proxy aggregation due to a bounded thread pool size

* Issue **#484** : Remove custom finalize() methods to reduce memory overhead

* Issue **#475** : Fix memory leak of java.io.File references when proxy aggregation runs

* Issue **#470** : You can now correctly add destinations directly to the pipeline 'Source' element to enable raw streaming.

* Issue **#487** : Search result list trimming was throwing an illegal argument exception `Comparison method violates its general contract`, this should now be fixed.

* Issue **#488** : Permissions are now elevated to 'Use' for the purposes of reporting the data source being queried.

* Migrated to ehcache 3.4.0 to add options for off-heap and disk based caching to reduce memory overhead.

* Caches of pooled items no longer use Apache Commons Pool.

* Issue **#401** : Reference data was being cached per user to ensure a user centric view of reference data was being used. This required more memory so now reference data is built in the context of the internal processing user and then filtered during processing by user access to streams.

* The effective stream cache now holds 1000 items.

* Reduced the amount of cached reference data to 100 streams.

* Reduced the number of active queries to 100.

* Removed Ehcache and switched to Guava cache.

* Issue **#477** : Additional changes to ensure search sub tasks use threads fairly between multiple searches.

* Issue **#477** : Search sub tasks are now correctly linked to their parent task and can therefore be terminated by terminating parent tasks.

* Issue **#425** : Changed string replacement in pipeline migration code to use a literal match

* Issue **#469** : Add Heap Histogram internal statistics for memory use monitoring

* Issue **#463** : Made further improvements to the index shard writer cache to improve performance.

* Issue **#448** : Some search related tasks never seem to complete, presumably because an error is thrown at some point and so their callbacks do not get called normally. This fix changes the way task completion is recorded so that it isn't dependant on the callbacks being called correctly.

* Issue **#464** : When a user resets a password, the password now has an expiry date set in the future determined by the password expiry policy. Password that are reset by email still expire immediately as expected.

* Issue **#462** : Permission exceptions now carry details of the user that the exception applies to. This change allows error logging to record the user id in the message where appropriate.

* Issue **#463** : Many index shards are being corrupted which may be caused by insufficient locking of the shard writers and readers. This fix changes the locking mechanism to use the file system.

* Issue **#451** : Data paging was allowing the user to jump beyond the end of a stream whereby just the XML root elements were displayed. This is now fixed by adding a constraint to the page offset so that the user cannot jump beyond the last record. Because data paging assumes that segmented streams have a header and footer, text streams now include segments after a header and before a footer, even if neither are added, so that paging always works correctly regardless of the presence of a header or footer.

* Issue **#461** : The stream attributes on the filter dialog were not sorted alphabetically, they now are.

* Issue **#460** : In some instances error streams did not always have stream attributes added to them for fatal errors. This mainly occurred in instances where processing failed early on during pipeline creation. An error was recorded but stream attributes were not added to the meta data for the error stream. Processing now ensures that stream attributes are recorded for all error cases.

* Issue **#442** : Remove 'Old Internal Statistics' folder, improve import exception handling

* Issue **#457** : Add check to import to prevent duplicate root level entities

* Issue **#444** : Fix for segment markers when writing text to StreamAppender.

* Issue **#447** : Fix for AsyncSearchTask not being displayed as a child of EventSearchTask in the server tasks view.

* Issue **#421** : FileAppender now causes fatal error where no output path set.

* Issue **#427** : Pipelines with no source element will now only treat a single parser element as being a root element for backwards compatibility.

* Issue **#420** : Pipelines were producing errors in the UI when elements were deleted but still had properties set on them. The pipeline validator was attempting to set and validate properties for unknown elements. The validator now ignores properties and links to elements that are undeclared.

* Issue **#420** : The pipeline model now removes all properties and links for deleted elements on save.

* Issue **#458** : Only event searches should populate the `searchId`. Now `searchId` is only populated when a stream processor task is created by an event search as only event searches extract specific records from the source stream.

* Issue **#437** : The event log now includes source in move events.

* Issue **#419** : Fix multiple xml processing instructions appearing in output.

* Issue **#446** : Fix for deadlock on rolling appenders.

* Issue **#444** : Fix segment markers on RollingStreamAppender.

* Issue **#426** : Fix for incorrect processor filters. Old processor filters reference `systemGroupIdSet` rather than `folderIdSet`. The new migration updates them accordingly.

* Issue **#429** : Fix to remove `usePool` parser parameter.

* Issue **#439** : Fix for caches where elements were not eagerly evicted.

* Issue **#424** : Fix for cluster ping error display.

* Issue **#441** : Fix to ensure correct names are shown in pipeline properties.

* Issue **#433** : Fixed slow stream queries caused by feed permission restrictions.

* Issue **#385** : Individual index shards can now be deleted without deleting all shards.

* Issue **#391** : Users needed `Manage Processors` permission to initiate pipeline stepping. This is no longer required as the 'best fit' pipeline is now discovered as the internal processing user.

* Issue **#392** : Inherited pipelines now only require 'Use' permission to be used instead of requiring 'Read' permission.

* Issue **#394** : Pipeline stepping will now show errors with an alert popup.

* Issue **#396** : All queries associated with a dashboard should now be correctly deleted when a dashboard is deleted.

* Issue **#393** : All caches now cache items within the context of the current user so that different users do not have the possibility of having problems caused by others users not having read permissions on items.

* Issue **#358** : Schemas are now selected from a subset matching the criteria set on SchemaFilter by the user.

* Issue **#369** : Translation stepping wasn't showing any errors during stepping if a schema had an error in it.

* Issue **#364** : Switched index writer lock factory to a SingleInstanceLockFactory as index shards are accessed by a single process.

* Issue **#363** : IndexShardWriterCacheImpl now closes and flushes writers using an executor provided by the TaskManager. Writers are now also closed in LRU order when sweeping up writers that exceed TTL and TTI constraints.

* Issue **#361** : Information has been added to threads executing index writer and index searcher maintenance tasks.

* Issue **#356** : Changed the way index shard writers are cached to improve indexing performance and reduce blocking.

* Issue **#353** : Reduced expected error logging to debug.

* Issue **#354** : Changed the way search index shard readers get references to open writers so that any attempt to get an open writer will not cause, or have to wait for, a writer to close.

* Issue **#351** : Fixed ehcache item eviction issue caused by ehcache internally using a deprecated API.

* Issue **#347** : Added a 'Source' node to pipelines to establish a proper root for a pipeline rather than an assumed one based on elements with no parent.

* Issue **#350** : Removed 'Advanced Mode' from pipeline structure editor as it is no longer very useful.

* Issue **#349** : Improved index searcher cache to ensure searchers are not affected by writers closing.

* Issue **#342** : Changed the way indexing is performed to ensure index readers reference open writers correctly.

* Issue **#346** : Improved multi depth config content import.

* Issue **#328** : You can now delete corrupt shards from the UI.

* Issue **#343** : Fixed login expiry issue.

* Issue **#345** : Allowed for multi depth config content import.

* Issue **#341** : Fixed arg in SQL.

* Issue **#340** : Fixed headless and corresponding test.

* Issue **#333** : Fixed event-logging version in build.

* Issue **#334** : Improved entity sorting SQL and separated generation of SQL and HQL to help avoid future issues.

* Issue **#335** : Improved user management

* Issue **#337** : Added certificate auth option to export servlet and disabled the export config feature by default.

* Issue **#337** : Added basic auth option to export servlet to complement cert based auth.

* Issue **#332** : The index shard searcher cache now makes sure to get the current writer needed for the current searcher on open.

* Issue **#322** : The index cache and other caching beans should now throw exceptions on `get` that were generated during the creation of cached items.

* Issue **#325** : Query history is now cleaned with a separate job. Also query history is only recorded for manual querying, i.e. not when query is automated (on open or auto refresh). Queries are now recorded on a dashboard + query component basis and do not apply across multiple query components in a dashboard.

* Issue **#323** : Fixed an issue where parser elements were not being returned as 'processors' correctly when downstream of a reader.

* Issue **#322** : Index should now provide a more helpful message when an attempt is made to index data and no volumes have been assigned to an index.

* Issue **#316** : Search history is now only stored on initial query when using automated queries or when a user runs a query manually. Search history is also automatically purged to keep either a specified number of items defined by `stroom.query.history.itemsRetention` (default 100) or for a number of days specified by `stroom.query.history.daysRetention` (default 365).

* Issue **#317** : Users now need update permission on an index plus 'Manage Index Shards' permission to flush or close index shards. In addition to this a user needs delete permission to delete index shards.

* Issue **#319** : SaveAs now fetches the parent folder correctly so that users can copy items if they have permission to do so.

* Issue **#311** : Fixed request for `Pipeline` in `meta` XSLT function. Errors are now dealt with correctly so that the XSLT will not fail due to missing meta data.

* Issue **#313** : Fixed case of `xmlVersion` property on `InvalidXMLCharFilterReader`.

* Issue **#314** : Improved description of `tags` property in `BadTextXMLFilterReader`.

* Issue **#307** : Made some changes to avoid potential NPE caused by session serialisation.

* Issue **#306** : Added a stroom `meta` XSLT function. The XSLT function now exposes `Feed`, `StreamType`, `CreatedTime`, `EffectiveTime` and `Pipeline` meta attributes from the currently processing stream in addition to any other meta data that might apply. To access these meta data attributes of the current stream use `stroom:meta('StreamType')` etc. The `feed-attribute` function is now an alias for the `meta` function and should be considered to be deprecated.

* Issue **#303** : The stream delete job now uses cron in preference to a frequency.

* Issue **#152** : Changed the way indexing is performed so that a single indexer object is now responsible for indexing documents and adding them to the appropriate shard.

* Issue **#179** : Updated Saxon-HE to version 9.7.0-18 and added XSLTFilter option to `usePool` to see if caching might be responsible for issue.

* Issue **#288** : Made further changes to ensure that the IndexShardWriterCache doesn't try to reuse an index shard that has failed when adding any documents.

* Issue **#295** : Made the help URL absolute and not relative.

* Issue **#293** : Attempt to fix mismatch document count error being reported when index shards are opened.

* Issue **#292** : Fixed locking for rolling stream appender.

* Issue **#292** : Rolling stream output is no longer associated with a task, processor or pipeline to avoid future processing tasks from deleting rolling streams by thinking they are superseded.

* Issue **#292** : Data that we expect to be unavailable, e.g. locked and deleted streams, will no longer log exceptions when a user tries to view it and will instead return an appropriate message to the user in place of the data.

* Issue **#288** : The error condition 'Expected a new writer but got the same one back!!!' should no longer be encountered as the root cause should now be fixed. The original check has been reinstated so that processing will terminate if we do encounter this problem.

* Issue **#295** : Fixed the help property so that it can now be configured.

* Issue **#296** : Removed 'New' and 'Delete' buttons from the global property dialog.

* Issue **#279** : Fixed NPE thrown during proxy aggregation.

* Issue **#294** : Changing stream task status now tries multiple times to attempt to avoid a hibernate LockAcquisitionException.

* Issue **#287** : XSLT not found warnings property description now defaults to false.

* Issue **#261** : The save button is now only enabled when a dashboard or other item is made dirty and it is not read only.

* Issue **#286** : Dashboards now correctly save the selected tab when a tab is selected via the popup tab selector (visible when tabs are collapsed).

* Issue **#289** : Changed Log4J configuration to suppress logging from Hibernate SqlExceptionHandler for expected exceptions like constraint violations.

* Issue **#288** : Changed 'Expected a new writer...' fatal error to warning as the condition in question might be acceptable.

* Issue **#285** : Attempted fix for GWT RPC serialisation issue.

* Issue **#283** : Statistics for the stream task queue are now captured even if the size is zero.

* Issue **#226** : Fixed issue where querying an index failed with "User does not have the required permission (Manage Users)" message.

* Issue **#281** : Made further changes to cope with Files.list() and Files.walk() returning streams that should be closed with 'try with resources' construct.

* Issue **#224** : Removing an element from the pipeline structure now removes all child elements too.

* Issue **#282** : Users can now upload data with just 'Data - View' and 'Data - Import' application permissions, plus read permission on the appropriate feed.

* Issue **#199** : The explorer now scrolls selected items into view.

* Issue **#280** : Fixed 'No user is currently authenticated' issue when viewing jobs and nodes.

* Issue **#278** : The date picker now hides once you select a date.

* Issue **#281** : Directory streams etc are now auto closed to prevent systems running out of file handles.

* Issue **#263** : The explorer tree now allows you to collapse the root 'System' node after it is first displayed.

* Issue **#266** : The explorer tree now resets (clears and collapses all previously open nodes) and shows the currently selected item every time an explorer drop down in opened.

* Issue **#233** : Users now only see streams if they are administrators or have 'Data - View' permission. Non administrators will only see data that they have 'read' permission on for the associated feed and 'use' permission on for the associated pipeline if there is one.

* Issue **#265** : The stream filter now orders stream attributes alphabetically.

* Issue **#270** : Fixed security issue where null users were being treated as INTERNAL users.

* Issue **#270** : Improved security by pushing user tokens rather than just user names so that internal system (processing) users are clearly identifiable by the security system and cannot be spoofed by regular user accounts.

* Issue **#269** : When users are prevented from logging in with 'preventLogin' their failed login count is no longer incremented.

* Issue **#267** : The login page now shows the maintenance message.

* Issue **#276** : Session list now shows session user ids correctly.

* Issue **#201** : The permissions menu item is no longer available on the root 'System' folder.

* Issue **#176** : Improved performance of the explorer tree by increasing the size of the document permissions cache to 1M items and changing the eviction policy from LRU to LFU.

* Issue **#176** : Added an optimisation to the explorer tree that prevents the need for a server call when collapsing tree nodes.

* Issue **#273** : Removed an unnecessary script from the build.

* Issue **#277** : Fixed a layout issue that was causing the feed section of the processor filter popup to take up too much room.

* Issue **#274** : The editor pane was only returning the current user edited text when attached to the DOM which meant changes to text were ignored if an editor pane was not visible when save was pressed. This has now been fixed so that the current content of an editor pane is always returned even when it is in a detached state.

* Issue **#264** : Added created by/on and updated by/on info to pipeline stream processor info tooltips.

* Issue **#222** : Explorer items now auto expand when a quick filter is used.

* Issue **#205** : File permissions in distribution have now been changed to `0750` for directories and shell scripts and `0640` for all other files.

* Issue **#240** : Separate application permissions are now required to manage DB tables and tasks.

* Issue **#210** : The statistics tables are now listed in the database tables monitoring pane.

* Issue **#249** : Removed spaces between values and units.

* Issue **#237** : Users without 'Download Search Results' permission will no longer see the download button on the table component in a dashboard.

* Issue **#232** : Users can now inherit from pipelines that they have 'use' permissions on.

* Issue **#191** : Max stream size was not being treated as IEC value, e.g. Mebibytes etc.

* Issue **#235** : Users can now only view the processor filters that they have created if they have 'Manage Processors' permission unless they are an administrator in which case they will see all filters. Users without the 'Manage Processors' permission who are also not administrators will see no processor filters in the UI. Users with 'Manage Processors' permission who are not administrators will be able to update their own processor filters if they have 'update' permission on the associated pipeline. Administrators are able to update all processor filters.

* Issue **#212** : Changes made to text in any editor including those made with cut and paste are now correctly handled so that altered content is now saved.

* Issue **#247** : The editor pane now attempts to maintain the scroll position when formatting content.

* Issue **#251** : Volume and memory statistics are now recorded in bytes and not MiB.

* Issue **#243** : The error marker pane should now discover and display all error types even if they are preceded by over 1000 warnings.

* Issue **#254** : Fixed search result download.

* Issue **#209** : Statistics are now queryable in a dashboard if a user has 'use' permissions on a statistic.

* Issue **#255** : Fixed issue where error indicators were not being shown in the schema validator pane because the text needed to be formatted so that it spanned multiple lines before attempting to add annotations.

* Issue **#257** : The dashboard text pane now provides padding at the top to allow for tabs and controls.

* Issue **#174** : Index shard checking is now done asynchronously during startup to reduce startup time.

* Issue **#225** : Fixed NPE that was caused by processing instruction SAX events unexpectedly being fired by Xerces before start document events. This looks like it might be a bug in Xerces but the code now copes with the unexpected processing instruction event anyway.

* Issue **#230** : The maintenance message can now be set with the property 'stroom.maintenance.message' and the message now appears as a banner at the top of the screen rather than an annoying popup. Non admin users can also be prevented from logging on to the system by setting the 'stroom.maintenance.preventLogin' property to 'true'.

* Issue **#155** : Changed password values to be obfuscated in the UI as 20 asterisks regardless of length.

* Issue **#188** : All of the writers in a pipeline now display IO in the UI when stepping.

* Issue **#208** : Schema filter validation errors are now shown on the output pane during stepping.

* Issue **#211** : Turned off print margins in all editors.

* Issue **#200** : The stepping presenter now resizes the top pane to fit the tree structure even if it is several elements high.

* Issue **#168** : Code and IO is now loaded lazily into the element presenter panes during stepping which prevents the scrollbar in the editors being in the wrong position.

* Issue **#219** : Changed async dispatch code to work with new lambda classes rather than callbacks.

* Issue **#221** : Fixed issue where `*.zip.bad` files were being picked up for proxy aggregation.

* Issue **#242** : Improved the way properties are injected into some areas of the code to fix an issue where 'stroom.maxStreamSize' and other properties were not being set.

* Issue **#241** : XMLFilter now ignores the XSLT name pattern if an empty string is supplied.

* Issue **#236** : 'Manage Cache Permission' has been changed to 'Manage Cache'.

* Issue **#219** : Made further changes to use lambda expressions where possible to simplify code.

* Issue **#231** : Changed the way internal statistics are created so that multiple facets of a statistic, e.g. Free & Used Memory, are combined into a single statistic to allow combined visualisation.

* Issue **#172** : Further improvement to dashboard L&F.

* Issue **#194** : Fixed missing Roboto fonts.

* Issue **#195** : Improved font weights and removed underlines from link tabs.

* Issue **#196** : Reordered fields on stream, relative stream, volume and server task tables.

* Issue **#182** : Changed the way dates and times are parsed and formatted and improved the datebox control L&F.

* Issue **#198** : Renamed 'INTERNAL_PROCESSING_USER' to 'INTERNAL'.

* Issue **#154** : Active tasks are now sortable by processor filter priority.

* Issue **#204** : Pipeline processor statistics now include 'Node' as a tag.

* Issue **#170** : Changed import/export to delegate import/export responsibility to individual services. Import/export now only works with items that have valid UUIDs specified.

* Issue **#164** : Reduced caching to ensure tree items appear as soon as they are added.

* Issue **#177** : Removed 'Meta Data-Bytes Received' statistic as it was a duplicate.

* Issue **#152** : Changed the way index shard creation is locked so that only a single shard should be fetched from the cache with a given shard key at any one time.

* Issue **#189** : You now have to click within a checkbox to select it within a table rather than just clicking the cell the checkbox is in.

* Issue **#186** : Data is no longer artificially wrapped with the insertion of new lines server side. Instead the client now receives the data and an option to soft wrap lines has been added to the UI.

* Issue **#167** : Fixed formatting of JavaScript and JSON.

* Issue **#175** : Fixed visibility of items by inferred permissions.

* Issue **#178** : Added new properties and corresponding configuration to connect and create a separate SQL statistics DB.

* Issue **#172** : Improved dashboard L&F.

* Issue **#169** : Improved L&F of tables to make better use of screen real estate.

* Issue **#191** : Mebibytes (multiples of 1024) etc are now used as standard throughout the application for both memory and disk sizes and have single letter suffixes (B, K, M, G, T).

* Issue **#173** : Fixed the way XML formatter deals with spaces in attribute values.

* Issue **#151** : Fixed meta data statistics. 'metaDataStatistics' bean was declared as an interface and not a class.

* Issue **#158** : Added a new global property 'stroom.proxy.zipFilenameDelimiter' to enable Stroom proxy repositories to be processed that have a custom file name pattern.

* Issue **#153** : Clicking tick boxes and other cell components in tables no longer requires the row to be selected first.

* Issue **#148** : The stream browsing UI no longer throws an error when attempting to clear markers from the error markers pane.

* Issue **#160** : Stream processing tasks are now created within the security context of the user that created the associated stream processor filter.

* Issue **#157** : Data is now formatted by the editor automatically on display.

* Issue **#144** : Old processing output will now be deleted when content is reprocessed even if the new processing task does not produce output.

* Issue **#159** : Fixed NPE thrown during import.

* Issue **#166** : Fixed NPE thrown when searching statistics.

* Issue **#165** : Dashboards now add a query and result table from a template by default on creation. This was broken when adding permission inheritance to documents.

* Issue **#162** : The editor annotation popup now matches the style of other popups.

* Issue **#163** : Imported the Roboto Mono font to ensure consistency of the editor across platforms.

* Issue **#143** : Stroom now logs progress information about closing index shard writers during shutdown.

* Issue **#140** : Replaced code editor to improve UI performance and add additional code formatting & styling options.

* Issue **#146** : Object pool should no longer throw an error when abandoned objects are returned to the pool.

* Issue **#142** : Changed the way permissions are cached so that changes to permissions provide immediate access to documents.

* Issue **#123** : Changed the way entity service result caching works so that the underlying entity manager is cached instead of individual services. This allows entity result caching to be performed while still applying user permissions to cached results.

* Issue **#156** : Attempts to open items that that user does not have permission to open no longer show an error and spin the progress indicator forever, instead the item will just not open.

* Issue **#141** : Improved log output during entity reference migration and fixed statistic data source reference migration.

* Issue **#127** : Entity reference replacement should now work with references to 'StatisticsDataSource'.

* Issue **#125** : Fixed display of active tasks which was broken by changes to the task summary table selection model.

* Issue **#121** : Fixed cache clearing.

* Issue **#122** : Improved the look of the cache screen.

* Issue **#106** : Disabled users and groups are now displayed with greyed out icon in the UI.

* Issue **#132** : The explorer tree is now cleared on login so that users with different permissions do not see the previous users items.

* Issue **#128** : Improved error handling during login.

* Issue **#130** : Users with no permissions are no longer able to open folders including the root System folder to attempt data browsing.

* Issue **#120** : Entity chooser now treats 'None' as a special root level explorer node so that it can be selected in the same way as other nodes, e.g. visibly selected and responsive to double click.

* Issue **#129** : Fixed NPE.

* Issue **#119** : User permissions dialog now clears permissions when a user or group is deleted.

* Issue **#115** : User permissions on documents can now be inherited from parent folders on create, copy and move.

* Issue **#109** : Added packetSize="65536" property to AJP connector in server.xml template.

* Issue **#100** : Various list of items in stroom now allow multi selection for add/remove purposes.

* Issue **#112** : Removed 'pool' monitoring screen as all pools are now caches of one form or another.

* Issue **#105** : Users were not seeing 'New' menu for folders that they had some create child doc permissions for. This was due to DocumentType not implementing equals() and is now fixed.

* Issue **#111** : Fixed query favourites and history.

* Issue **#91** : Only CombinedParser was allowing code to be injected during stepping. Now DSParser and XMLFragmentParser support code injection during stepping.

* Issue **#107** : The UI now only shows new pipeline element items on the 'Add' menu that are allowed children of the selected element.

* Issue **#113** : User names are now validated against a regex specified by the 'stroom.security.userNamePattern' property.

* Issue **#116** : Rename is now only possible when a single explorer item is selected.

* Issue **#114** : Fixed selection manager so that the explorer tree does not select items when a node expander is clicked.

* Issue **#65** : Selection lists are now limited to 300px tall and show scrollbars if needed.

* Issue **#50** : Defaults table result fields to use local time without outputting the timezone.

* Issue **#15** : You can now express time zones in dashboard query expressions or just omit a time zone to use the locale of the browser.

* Issue **#49** : Dynamic XSLT selection now works with pipeline stepping.

* Issue **#63** : Entity selection control now shows current entity name even if it has changed since referencing entity was last saved.

* Issue **#70** : You can now select multiple explorer rows with ctrl and shift key modifiers and perform bulk actions such as copy, move, rename and delete.

* Issue **#85** : findDelete() no longer tries to add ORDER BY condition on UPDATE SQL when deleting streams.

* Issue **#89** : Warnings should now be present in processing logs for reference data lookups that don't specify feed or stream type. This was previously throwing a NullPointerException.

* Issue **#90** : Fixed entity selection dialog used outside of drop down selection control.

* Issue **#88** : Pipeline reference edit dialog now correctly selects the current stream type.

* Issue **#77** : Default index volume creation now sets stream status to INACTIVE rather than CLOSED and stream volume creation sets index status to INACTIVE rather than CLOSED.

* Issue **#93** : Fixed code so that the 'Item' menu is now visible.

* Issue **#97** : Index shard partition date range creation has been improved.

* Issue **#94** : Statistics searches now ignore expression terms with null or empty values so that the use of substitution parameters can be optional.

* Issue **#87** : Fixed explorer scrolling to the top by disabling keyboard selection.

* Issue **#104** : 'Query' no longer appears as an item that a user can allow 'create' on for permissions within a folder.

* Issue **#103** : Added 10 years as a supported data retention age.

* Issue **#86** : The stream delete button is now re-enabled when new items are selected for deletion.

* Issue **#81** : No exception will now be thrown if a client rejects a response for an EntityEvent.

* Issue **#79** : The client node no longer tries to create directories on the file system for a volume that may be owned by another node.

* Issue **#92** : Error summaries of multiple types no longer overlap each other at the top of the error markers list.

* Issue **#64** : Fixed Hessian serialisation of 'now' which was specified as a ZonedDateTime which cannot be serialised. This field is now a long representing millseconds since epoch.

* Issue **#62** : Task termination button is now enabled.

* Issue **#60** : Fixed validation of stream attributes prior to data upload to prevent null pointer exception.

* Issue **#9** : Created a new implementation of the expression parser that improved expression tokenisation and deals with BODMAS rules properly.

* Issue **#36** : Fixed and vastly improved the configuration of email so that more options can be set allowing for the use of other email services requiring more complex configuration such as gmail.

* Issue **#24** : Header and footer strings are now unescaped so that character sequences such as '\n' are translated into single characters as with standard Java strings, e.g. '\n' will become a new line and '\t' a tab.

* Issue **#40** : Changed Stroom docker container to be based on Alpine linux to save space

* Issue **#40** : Auto import of content packs on Stroom startup and added default content packs into the docker build for Stroom.

* Issue **#30** : Entering stepping mode was prompting for the pipeline to step with but also auto selecting a pipeline at the same time and entering stepping immediately.

* Dashboard auto refresh is now limited to a minimum interval of 10 seconds.

* Issue **#31** : Pipeline stepping was not including user changes immediately as parsers and XSLT filters were using cached content when they should have been ignoring the cache in stepping mode.

* Issue **#27** : Stroom now listens to window closing events and asks the user if they really want to leave the page. This replaces the previous crude attempts to block keys that affected the history or forced a browser refresh.

* Issue **#2** : The order of fields in the query editor is now alphabetical.

* Issue **#3** : When a filter is active on a dashboard table column, a filter icon now appears to indicate this.

* Issue **#5** : Replace() and Decode() dashboard table expression functions no longer ignore cells with null values.

* Issue **#7** : Dashboards are now able to query on open.

* Issue **#8** : Dashboards are now able to re-query automatically at fixed intervals.

* Updated GWT to v2.8.0 and Gin to v2.1.2.

* Issue **#12** : Dashboard queries can now evaluate relative date/time expressions such as now(), hour() etc. In addition to this the expressions also allow the addition or subtraction of durations, e.g. now - 5d.

* Issue **#14** : Dashboard query expressions can now be parameterised with any term able to accept a user defined parameter, e.g. ${user}. Once added parameters can be changed for the entire dashboard via a text box at the top of the dashboard screen which will then execute all queries when enter is pressed or it loses focus.

* Issue **#16** : Dashboard table filters can also accept user defined parameters, e.g. ${user}, to perform filtering when a query is executed.

* Fixed missing text presenter in dashboards.

* Issue **#18** : The data dashboard component will now show data relative to the last selected table row (even if there is more than one table component on the dashboard) if the data component has not been configured to listen to row selections for a specific table component.

* Changed table styling to colour alternate rows, add borders between rows and increase vertical padding

* Issue **#22** : Dashboard table columns can now be configured to wrap text via the format options.

* Issue **#28** : Dashboard component dependencies are now listed with the component name plus the component id in brackets rather than just the component id.

* Issue **#202** : Initial release of the new data retention policy functionality.

[Unreleased]: https://github.com/gchq/stroom/compare/v7.1-beta.23...HEAD
[v7.1-beta.23]: https://github.com/gchq/stroom/compare/v7.1-beta.22...v7.1-beta.23
[v7.1-beta.22]: https://github.com/gchq/stroom/compare/v7.1-beta.21...v7.1-beta.22
[v7.1-beta.21]: https://github.com/gchq/stroom/compare/v7.1-beta.20...v7.1-beta.21
[v7.1-beta.20]: https://github.com/gchq/stroom/compare/v7.1-beta.19...v7.1-beta.20
[v7.1-beta.19]: https://github.com/gchq/stroom/compare/v7.1-beta.18...v7.1-beta.19
[v7.1-beta.18]: https://github.com/gchq/stroom/compare/v7.1-beta.17...v7.1-beta.18
[v7.1-beta.17]: https://github.com/gchq/stroom/compare/v7.1-beta.16...v7.1-beta.17
[v7.1-beta.16]: https://github.com/gchq/stroom/compare/v7.1-beta.15...v7.1-beta.16
[v7.1-beta.15]: https://github.com/gchq/stroom/compare/v7.1-beta.14...v7.1-beta.15
[v7.1-beta.14]: https://github.com/gchq/stroom/compare/v7.1-beta.13...v7.1-beta.14
[v7.1-beta.13]: https://github.com/gchq/stroom/compare/v7.1-beta.12...v7.1-beta.13
[v7.1-beta.12]: https://github.com/gchq/stroom/compare/v7.1-beta.11...v7.1-beta.12
[v7.1-beta.11]: https://github.com/gchq/stroom/compare/v7.1-beta.10...v7.1-beta.11
[v7.1-beta.10]: https://github.com/gchq/stroom/compare/v7.1-beta.9...v7.1-beta.10
[v7.1-beta.9]: https://github.com/gchq/stroom/compare/v7.1-beta.8...v7.1-beta.9
[v7.1-beta.8]: https://github.com/gchq/stroom/compare/v7.1-beta.7...v7.1-beta.8
[v7.1-beta.7]: https://github.com/gchq/stroom/compare/v7.1-beta.6...v7.1-beta.7
[v7.1-beta.6]: https://github.com/gchq/stroom/compare/v7.1-beta.5...v7.1-beta.6
[v7.1-beta.5]: https://github.com/gchq/stroom/compare/v7.1-beta.4...v7.1-beta.5
[v7.1-beta.4]: https://github.com/gchq/stroom/compare/v7.1-beta.3...v7.1-beta.4
[v7.1-beta.3]: https://github.com/gchq/stroom/compare/v7.1-beta.2..v7.1-beta.3
[v7.1-beta.2]: https://github.com/gchq/stroom/compare/v7.1-beta.1...v7.1-beta.2
[v7.1-beta.1]: https://github.com/gchq/stroom/compare/v7.0-beta.104...v7.1-beta.1
[v7.0-beta.104]: https://github.com/gchq/stroom/compare/v7.0-beta.103...v7.0-beta.104
[v7.0-beta.103]: https://github.com/gchq/stroom/compare/v7.0-beta.102...v7.0-beta.103
[v7.0-beta.102]: https://github.com/gchq/stroom/compare/v7.0-beta.101...v7.0-beta.102
[v7.0-beta.101]: https://github.com/gchq/stroom/compare/v7.0-beta.100...v7.0-beta.101
[v7.0-beta.100]: https://github.com/gchq/stroom/compare/v7.0-beta.99...v7.0-beta.100
[v7.0-beta.99]: https://github.com/gchq/stroom/compare/v7.0-beta.98...v7.0-beta.99
[v7.0-beta.98]: https://github.com/gchq/stroom/compare/v7.0-beta.97...v7.0-beta.98
[v7.0-beta.97]: https://github.com/gchq/stroom/compare/v7.0-beta.96...v7.0-beta.97
[v7.0-beta.96]: https://github.com/gchq/stroom/compare/v7.0-beta.95...v7.0-beta.96
[v7.0-beta.95]: https://github.com/gchq/stroom/compare/v7.0-beta.94...v7.0-beta.95
[v7.0-beta.94]: https://github.com/gchq/stroom/compare/v7.0-beta.93...v7.0-beta.94
[v7.0-beta.93]: https://github.com/gchq/stroom/compare/v7.0-beta.92...v7.0-beta.93
[v7.0-beta.92]: https://github.com/gchq/stroom/compare/v7.0-beta.91...v7.0-beta.92
[v7.0-beta.91]: https://github.com/gchq/stroom/compare/v7.0-beta.90...v7.0-beta.91
[v7.0-beta.90]: https://github.com/gchq/stroom/compare/v7.0-beta.89...v7.0-beta.90
[v7.0-beta.89]: https://github.com/gchq/stroom/compare/v7.0-beta.88...v7.0-beta.89
[v7.0-beta.88]: https://github.com/gchq/stroom/compare/v7.0-beta.87...v7.0-beta.88
[v7.0-beta.87]: https://github.com/gchq/stroom/compare/v7.0-beta.86...v7.0-beta.87
[v7.0-beta.86]: https://github.com/gchq/stroom/compare/v7.0-beta.85...v7.0-beta.86
[v7.0-beta.85]: https://github.com/gchq/stroom/compare/v7.0-beta.84...v7.0-beta.85
[v7.0-beta.84]: https://github.com/gchq/stroom/compare/v7.0-beta.83...v7.0-beta.84
[v7.0-beta.83]: https://github.com/gchq/stroom/compare/v7.0-beta.82...v7.0-beta.83
[v7.0-beta.82]: https://github.com/gchq/stroom/compare/v7.0-beta.81...v7.0-beta.82
[v7.0-beta.81]: https://github.com/gchq/stroom/compare/v7.0-beta.80...v7.0-beta.81
[v7.0-beta.80]: https://github.com/gchq/stroom/compare/v7.0-beta.79...v7.0-beta.80
[v7.0-beta.79]: https://github.com/gchq/stroom/compare/v7.0-beta.78...v7.0-beta.79
[v7.0-beta.78]: https://github.com/gchq/stroom/compare/v7.0-beta.77...v7.0-beta.78
[v7.0-beta.77]: https://github.com/gchq/stroom/compare/v7.0-beta.76...v7.0-beta.77
[v7.0-beta.76]: https://github.com/gchq/stroom/compare/v7.0-beta.75...v7.0-beta.76
[v7.0-beta.75]: https://github.com/gchq/stroom/compare/v7.0-beta.74...v7.0-beta.75
[v7.0-beta.74]: https://github.com/gchq/stroom/compare/v7.0-beta.73...v7.0-beta.74
[v7.0-beta.73]: https://github.com/gchq/stroom/compare/v7.0-beta.72...v7.0-beta.73
[v7.0-beta.72]: https://github.com/gchq/stroom/compare/v7.0-beta.71...v7.0-beta.72
[v7.0-beta.71]: https://github.com/gchq/stroom/compare/v7.0-beta.70...v7.0-beta.71
[v7.0-beta.70]: https://github.com/gchq/stroom/compare/v7.0-beta.69...v7.0-beta.70
[v7.0-beta.69]: https://github.com/gchq/stroom/compare/v7.0-beta.68...v7.0-beta.69
[v7.0-beta.68]: https://github.com/gchq/stroom/compare/v7.0-beta.67...v7.0-beta.68
[v7.0-beta.67]: https://github.com/gchq/stroom/compare/v7.0-beta.66...v7.0-beta.67
[v7.0-beta.66]: https://github.com/gchq/stroom/compare/v7.0-beta.65...v7.0-beta.66
[v7.0-beta.65]: https://github.com/gchq/stroom/compare/v7.0-beta.64...v7.0-beta.65
[v7.0-beta.64]: https://github.com/gchq/stroom/compare/v7.0-beta.63...v7.0-beta.64
[v7.0-beta.63]: https://github.com/gchq/stroom/compare/v7.0-beta.62...v7.0-beta.63
[v7.0-beta.62]: https://github.com/gchq/stroom/compare/v7.0-beta.61...v7.0-beta.62
[v7.0-beta.61]: https://github.com/gchq/stroom/compare/v7.0-beta.60...v7.0-beta.61
[v7.0-beta.60]: https://github.com/gchq/stroom/compare/v7.0-beta.59...v7.0-beta.60
[v7.0-beta.59]: https://github.com/gchq/stroom/compare/v7.0-beta.58...v7.0-beta.59
[v7.0-beta.58]: https://github.com/gchq/stroom/compare/v7.0-beta.57...v7.0-beta.58
[v7.0-beta.57]: https://github.com/gchq/stroom/compare/v7.0-beta.56...v7.0-beta.57
[v7.0-beta.56]: https://github.com/gchq/stroom/compare/v7.0-beta.55...v7.0-beta.56
[v7.0-beta.55]: https://github.com/gchq/stroom/compare/v7.0-beta.54...v7.0-beta.55
[v7.0-beta.54]: https://github.com/gchq/stroom/compare/v7.0-beta.53...v7.0-beta.54
[v7.0-beta.53]: https://github.com/gchq/stroom/compare/v7.0-beta.52...v7.0-beta.53
[v7.0-beta.52]: https://github.com/gchq/stroom/compare/v7.0-beta.51...v7.0-beta.52
[v7.0-beta.51]: https://github.com/gchq/stroom/compare/v7.0-beta.50...v7.0-beta.51
[v7.0-beta.50]: https://github.com/gchq/stroom/compare/v7.0-beta.49...v7.0-beta.50
[v7.0-beta.49]: https://github.com/gchq/stroom/compare/v7.0-beta.48...v7.0-beta.49
[v7.0-beta.48]: https://github.com/gchq/stroom/compare/v7.0-beta.47...v7.0-beta.48
[v7.0-beta.47]: https://github.com/gchq/stroom/compare/v7.0-beta.46...v7.0-beta.47
[v7.0-beta.46]: https://github.com/gchq/stroom/compare/v7.0-beta.45...v7.0-beta.46
[v7.0-beta.45]: https://github.com/gchq/stroom/compare/v7.0-beta.44...v7.0-beta.45
[v7.0-beta.44]: https://github.com/gchq/stroom/compare/v7.0-beta.43...v7.0-beta.44
[v7.0-beta.43]: https://github.com/gchq/stroom/compare/v7.0-beta.42...v7.0-beta.43
[v7.0-beta.42]: https://github.com/gchq/stroom/compare/v7.0-beta.41...v7.0-beta.42
[v7.0-beta.41]: https://github.com/gchq/stroom/compare/v7.0-beta.40...v7.0-beta.41
[v7.0-beta.40]: https://github.com/gchq/stroom/compare/v7.0-beta.39...v7.0-beta.40
[v7.0-beta.39]: https://github.com/gchq/stroom/compare/v7.0-beta.38...v7.0-beta.39
[v7.0-beta.38]: https://github.com/gchq/stroom/compare/v7.0-beta.37...v7.0-beta.38
[v7.0-beta.37]: https://github.com/gchq/stroom/compare/v7.0-beta.36...v7.0-beta.37
[v7.0-beta.36]: https://github.com/gchq/stroom/compare/v7.0-beta.35...v7.0-beta.36
[v7.0-beta.35]: https://github.com/gchq/stroom/compare/v7.0-beta.34...v7.0-beta.35
[v7.0-beta.34]: https://github.com/gchq/stroom/compare/v7.0-beta.33...v7.0-beta.34
[v7.0-beta.33]: https://github.com/gchq/stroom/compare/v7.0-beta.32...v7.0-beta.33
[v7.0-beta.32]: https://github.com/gchq/stroom/compare/v7.0-beta.31...v7.0-beta.32
[v7.0-beta.31]: https://github.com/gchq/stroom/compare/v7.0-beta.30...v7.0-beta.31
[v7.0-beta.30]: https://github.com/gchq/stroom/compare/v7.0-beta.29...v7.0-beta.30
[v7.0-beta.29]: https://github.com/gchq/stroom/compare/v7.0-beta.28...v7.0-beta.29
[v7.0-beta.28]: https://github.com/gchq/stroom/compare/v7.0-beta.27...v7.0-beta.28
[v7.0-beta.27]: https://github.com/gchq/stroom/compare/v7.0-beta.26...v7.0-beta.27
[v7.0-beta.26]: https://github.com/gchq/stroom/compare/v7.0-beta.25...v7.0-beta.26
[v7.0-beta.25]: https://github.com/gchq/stroom/compare/v7.0-beta.24...v7.0-beta.25
[v7.0-beta.24]: https://github.com/gchq/stroom/compare/v7.0-beta.23...v7.0-beta.24
[v7.0-beta.23]: https://github.com/gchq/stroom/compare/v7.0-beta.22...v7.0-beta.23
[v7.0-beta.22]: https://github.com/gchq/stroom/compare/v7.0-beta.21...v7.0-beta.22
[v7.0-beta.21]: https://github.com/gchq/stroom/compare/v7.0-beta.20...v7.0-beta.21
[v7.0-beta.20]: https://github.com/gchq/stroom/compare/v7.0-beta.19...v7.0-beta.20
[v7.0-beta.19]: https://github.com/gchq/stroom/compare/v7.0-beta.18...v7.0-beta.19
[v7.0-beta.18]: https://github.com/gchq/stroom/compare/v7.0-beta.17...v7.0-beta.18
[v7.0-beta.17]: https://github.com/gchq/stroom/compare/v7.0-beta.16...v7.0-beta.17
[v7.0-beta.16]: https://github.com/gchq/stroom/compare/v7.0-beta.15...v7.0-beta.16
[v7.0-beta.15]: https://github.com/gchq/stroom/compare/v7.0-beta.14...v7.0-beta.15
[v7.0-beta.14]: https://github.com/gchq/stroom/compare/v7.0-beta.13...v7.0-beta.14
[v7.0-beta.13]: https://github.com/gchq/stroom/compare/v7.0-beta.12...v7.0-beta.13
[v7.0-beta.12]: https://github.com/gchq/stroom/compare/v7.0-beta.11...v7.0-beta.12
[v7.0-beta.11]: https://github.com/gchq/stroom/compare/v7.0-beta.10...v7.0-beta.11
[v7.0-beta.10]: https://github.com/gchq/stroom/compare/v7.0-beta.9...v7.0-beta.10
[v7.0-beta.9]: https://github.com/gchq/stroom/compare/v7.0-beta.8...v7.0-beta.9
[v7.0-beta.8]: https://github.com/gchq/stroom/compare/v7.0-beta.7...v7.0-beta.8
[v7.0-beta.7]: https://github.com/gchq/stroom/compare/v7.0-beta.6...v7.0-beta.7
[v7.0-beta.6]: https://github.com/gchq/stroom/compare/v7.0-beta.5...v7.0-beta.6
[v7.0-beta.5]: https://github.com/gchq/stroom/compare/v7.0-beta.4...v7.0-beta.5
[v7.0-beta.4]: https://github.com/gchq/stroom/compare/v7.0-beta.3...v7.0-beta.4
[v7.0-beta.3]: https://github.com/gchq/stroom/compare/v7.0-beta.2...v7.0-beta.3
[v7.0-beta.2]: https://github.com/gchq/stroom/compare/v7.0-beta.1...v7.0-beta.2
[v7.0-beta.1]: https://github.com/gchq/stroom/compare/v7.0-alpha.5...v7.0-beta.1
[v7.0-alpha.5]: https://github.com/gchq/stroom/compare/v7.0-alpha.4...v7.0-alpha.5
[v7.0-alpha.4]: https://github.com/gchq/stroom/compare/v7.0-alpha.3...v7.0-alpha.4
[v7.0-alpha.3]: https://github.com/gchq/stroom/compare/v7.0-alpha.2...v7.0-alpha.3
[v7.0-alpha.2]: https://github.com/gchq/stroom/compare/v7.0-alpha.1...v7.0-alpha.2
[v7.0-alpha.1]: https://github.com/gchq/stroom/compare/v6.0.0...v7.0-alpha.1
[v6.0.0]: https://github.com/gchq/stroom/compare/v5.4.0...v6.0.0
