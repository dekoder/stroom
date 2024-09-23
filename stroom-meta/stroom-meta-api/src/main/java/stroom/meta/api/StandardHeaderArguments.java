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

package stroom.meta.api;

import java.util.Set;

public interface StandardHeaderArguments {

    String GUID = "GUID";
    String COMPRESSION = "Compression";
    String COMPRESSION_ZIP = "ZIP";
    String COMPRESSION_GZIP = "GZIP";
    String COMPRESSION_NONE = "NONE";

    Set<String> VALID_COMPRESSION_SET = Set.of(
            COMPRESSION_GZIP,
            COMPRESSION_ZIP,
            COMPRESSION_NONE);

    String CONTENT_LENGTH = "content-length";

    String CONTENT_ENCODING = "content-encoding";
    String CONTENT_ENCODING_GZIP = "gzip";
    String CONTENT_ENCODING_DEFLATE = "deflate";
    String CONTENT_ENCODING_BROTLI = "br";
    String CONTENT_ENCODING_ZSTD = "zstd";

    String USER_AGENT = "user-agent";

    String REMOTE_ADDRESS = "RemoteAddress";
    String REMOTE_HOST = "RemoteHost";
    String RECEIVED_TIME = "ReceivedTime";
    /**
     * A comma delimited list of ReceivedTime values, oldest first that includes the
     * ReceivedTime value as its last item.
     */
    String RECEIVED_TIME_HISTORY = "ReceivedTimeHistory";
    String RECEIVED_PATH = "ReceivedPath";
    String EFFECTIVE_TIME = "EffectiveTime";
    String REMOTE_DN = "RemoteDN";
    String REMOTE_CERT_EXPIRY = "RemoteCertExpiry";
    String REMOTE_FILE = "RemoteFile";

    // The unique identifier of the user on the IDP
    String UPLOAD_USER_ID = "UploadUserId";
    // Username of the user on the IDP, may not be unique
    String UPLOAD_USERNAME = "UploadUsername";

    String STREAM_SIZE = "StreamSize";

    String STROOM_STATUS = "Stroom-Status";
    String STROOM_ERROR = "Stroom-Error";

    String FEED = "Feed";
    String TYPE = "Type";

    // Typically added in by nginx
    String X_FORWARDED_FOR = "X-Forwarded-For";

    Set<String> HEADER_CLONE_EXCLUDE_SET = Set.of(
            "accept",
            "connection",
            "content-length",
            "transfer-encoding",
            "expect",
            COMPRESSION);

    /**
     * Header keys for values that are date/time strings
     */
    Set<String> DATE_HEADER_KEYS = Set.of(
            EFFECTIVE_TIME,
            RECEIVED_TIME);
}
