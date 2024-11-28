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
 */

package stroom.proxy.repo;

import stroom.data.zip.StreamProgressMonitor;
import stroom.meta.api.AttributeMap;
import stroom.meta.api.StandardHeaderArguments;
import stroom.util.io.BufferFactory;
import stroom.util.io.FileUtil;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;
import stroom.util.net.HostNameUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Class that reads a nested directory tree of stroom zip files.
 * <p>
 * <p>
 * TODO - This class is extended in ProxyAggregationExecutor in Stroom
 * so changes to the way files are stored in the zip repository
 * may have an impact on Stroom while it is using stroom.util.zip as opposed
 * to stroom-proxy-zip.  Need to pull all the zip repository stuff out
 * into its own repo with its own lifecycle and a clearly defined API,
 * then both stroom-proxy and stroom can use it.
 */
public final class ProxyForwardingFileSetProcessor implements FileSetProcessor {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(ProxyForwardingFileSetProcessor.class);

    private static final String PROXY_FORWARD_ID = "ProxyForwardId";

    private final AtomicLong proxyForwardId = new AtomicLong(0);

    private final StreamHandlerFactory handlerFactory;
    private final ProxyFileHandler proxyFileHandler;

    private volatile String hostName = null;

    ProxyForwardingFileSetProcessor(final StreamHandlerFactory handlerFactory, final BufferFactory bufferFactory) {
        this.handlerFactory = handlerFactory;
        proxyFileHandler = new ProxyFileHandler(bufferFactory);
    }

    @Override
    public void process(final FileSet fileSet) {
        final List<Path> files = fileSet.getFiles();
        if (files.size() > 0) {
            final long thisPostId = proxyForwardId.incrementAndGet();
            LOGGER.debug(() -> "processFeedFiles() - proxyForwardId " + thisPostId + " " + fileSet);

            // Sort the files in the file set so there is some consistency to processing.
            files.sort(Comparator.comparing(p -> p.getFileName().toString()));
            LOGGER.debug(() -> "process() - " + fileSet);
            LOGGER.trace(() -> "process() - " + fileSet + "{" + files + "}");

            final FileSetKey key = fileSet.getKey();
            final String feedName = key.getFeedName();
            final String typeName = key.getTypeName();

            final AttributeMap attributeMap = new AttributeMap();
            attributeMap.put(StandardHeaderArguments.FEED, feedName);
            if (typeName != null && !typeName.isBlank()) {
                attributeMap.put(StandardHeaderArguments.TYPE, typeName.trim());
            }
            attributeMap.put(StandardHeaderArguments.COMPRESSION, StandardHeaderArguments.COMPRESSION_ZIP);
            attributeMap.put(StandardHeaderArguments.RECEIVED_PATH, getHostName());
            if (LOGGER.isDebugEnabled()) {
                attributeMap.put(PROXY_FORWARD_ID, String.valueOf(thisPostId));
            }

            final List<StreamHandler> handlers = handlerFactory.addSendHandlers(new ArrayList<>());

            try {
                // Start the post
                for (final StreamHandler streamHandler : handlers) {
                    streamHandler.setAttributeMap(attributeMap);
                    streamHandler.handleHeader();
                }

                long sequenceId = 1;
                final StreamProgressMonitor streamProgress =
                        new StreamProgressMonitor("ProxyRepositoryReader " + key);

                for (final Path file : files) {
                    // Send no more if told to finish
                    if (Thread.currentThread().isInterrupted()) {
                        final String message = "Quitting early as we have been told to stop.\n" +
                                "Was processing " +
                                fileSet +
                                " file " +
                                FileUtil.getCanonicalPath(file) +
                                " sequence " +
                                sequenceId + ".\n" +
                                "Will attempt to send again after restart.";
                        LOGGER.info(message);
                        throw new RuntimeException(message);
                    }

                    sequenceId = proxyFileHandler.processFeedFile(handlers, file, streamProgress, sequenceId);
                }

                for (final StreamHandler streamHandler : handlers) {
                    streamHandler.handleFooter();
                }

                // Delete all of the files we have processed and their parent directories if possible.
                cleanup(files);

            } catch (final Throwable ex) {
                LOGGER.warn(() -> "processFeedFiles() - Failed to send to feed " + feedName + " ( " + ex + ")");
                LOGGER.debug(() -> "processFeedFiles() - Debug trace " + key, ex);
                for (final StreamHandler streamHandler : handlers) {
                    try {
                        streamHandler.handleError();
                    } catch (final IOException ioEx) {
                        LOGGER.error("fileSend()", ioEx);
                    }
                }
            }
        }
    }

    private void cleanup(final List<Path> deleteList) {
        LOGGER.debug(() -> "cleanup() " + deleteList);
        for (final Path file : deleteList) {
            LOGGER.debug(() -> "Deleting file: " + FileUtil.getCanonicalPath(file));
            ErrorFileUtil.deleteFileAndErrors(file);
        }

        // Delete any parent directories if we can.
        final Set<Path> parentDirs = deleteList.stream().map(Path::getParent).collect(Collectors.toSet());
        parentDirs.forEach(p -> {
            try {
                LOGGER.debug(() -> "Deleting dir: " + FileUtil.getCanonicalPath(p));
                Files.deleteIfExists(p);
            } catch (final IOException e) {
                LOGGER.debug(e::getMessage, e);
            }
        });
    }

    private String getHostName() {
        if (hostName == null) {
            hostName = HostNameUtil.determineHostName();
        }
        return hostName;
    }
}
