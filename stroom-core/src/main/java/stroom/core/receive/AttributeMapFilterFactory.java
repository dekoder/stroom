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

package stroom.core.receive;

import stroom.docref.DocRef;
import stroom.receive.common.AttributeMapFilter;
import stroom.receive.common.DataReceiptPolicyAttributeMapFilterFactory;
import stroom.receive.common.FeedStatusAttributeMapFilter;
import stroom.receive.common.ReceiveDataConfig;
import stroom.receive.rules.shared.ReceiveDataRules;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class AttributeMapFilterFactory {

    private final Provider<ReceiveDataConfig> receiveDataConfigProvider;
    private final DataReceiptPolicyAttributeMapFilterFactory dataReceiptPolicyAttributeMapFilterFactory;
    private final FeedStatusAttributeMapFilter feedStatusAttributeMapFilter;

    private volatile AttributeMapFilter attributeMapFilter;
    private final AtomicReference<String> lastPolicyUuid = new AtomicReference<>();

    @Inject
    public AttributeMapFilterFactory(
            final Provider<ReceiveDataConfig> receiveDataConfigProvider,
            final DataReceiptPolicyAttributeMapFilterFactory dataReceiptPolicyAttributeMapFilterFactory,
            final FeedStatusAttributeMapFilter feedStatusAttributeMapFilter) {

        this.receiveDataConfigProvider = receiveDataConfigProvider;
        this.dataReceiptPolicyAttributeMapFilterFactory = dataReceiptPolicyAttributeMapFilterFactory;
        this.feedStatusAttributeMapFilter = feedStatusAttributeMapFilter;
    }

    public AttributeMapFilter create() {
        final String receiptPolicyUuid = receiveDataConfigProvider.get().getReceiptPolicyUuid();
        final String last = lastPolicyUuid.get();
        if (attributeMapFilter == null || !Objects.equals(last, receiptPolicyUuid)) {
            lastPolicyUuid.compareAndSet(last, receiptPolicyUuid);

            if (receiptPolicyUuid != null && receiptPolicyUuid.length() > 0) {
                attributeMapFilter = dataReceiptPolicyAttributeMapFilterFactory.create(
                        new DocRef(ReceiveDataRules.TYPE, receiptPolicyUuid));
            } else {
                attributeMapFilter = feedStatusAttributeMapFilter;
            }
        }

        return attributeMapFilter;
    }
}
