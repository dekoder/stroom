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

package stroom.search.extraction;

import stroom.datasource.api.v2.AnalyzerType;
import stroom.datasource.api.v2.FieldType;
import stroom.datasource.api.v2.IndexField;
import stroom.index.shared.IndexFieldImpl;
import stroom.pipeline.LocationFactoryProxy;
import stroom.pipeline.errorhandler.ErrorReceiverProxy;
import stroom.pipeline.filter.AbstractXMLFilter;
import stroom.query.language.functions.Val;
import stroom.util.shared.Severity;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFieldFilter extends AbstractXMLFilter {

    private static final String DOCUMENT = "document";
    private static final String FIELD = "field";
    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String ANALYSER = "analyser";
    private static final String INDEXED = "indexed";
    private static final String STORED = "stored";
    private static final String TERM_POSITIONS = "termPositions";
    private static final String CASE_SENSITIVE = "caseSensitive";
    private static final String VALUE = "value";

    private final LocationFactoryProxy locationFactory;
    private final ErrorReceiverProxy errorReceiverProxy;

    private Locator locator;


    private IndexFieldImpl.Builder currentFieldBuilder;
    private String currentElement;
    private String currentValue;

    private List<FieldValue> currentFieldValues;

    public AbstractFieldFilter(final LocationFactoryProxy locationFactory,
                               final ErrorReceiverProxy errorReceiverProxy) {
        this.locationFactory = locationFactory;
        this.errorReceiverProxy = errorReceiverProxy;
    }


    /**
     * Sets the locator to use when reporting errors.
     *
     * @param locator The locator to use.
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
        super.setDocumentLocator(locator);
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
            throws SAXException {
        currentElement = localName;
        if (DOCUMENT.equals(localName)) {
            currentFieldValues = new ArrayList<>();
        } else if (FIELD.equals(localName)) {
            currentFieldBuilder = IndexFieldImpl.builder();
            currentValue = null;
        }
        super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (DOCUMENT.equals(localName)) {
            if (!currentFieldValues.isEmpty()) {
                processFields(currentFieldValues);
            }
            currentFieldValues = null;
            currentFieldBuilder = null;
            currentValue = null;

        } else if (FIELD.equals(localName)) {
            if (currentFieldBuilder != null && currentValue != null) {
                final IndexFieldImpl indexField = currentFieldBuilder.build();
                final Val val = convertValue(indexField, currentValue);
                if (val != null) {
                    final FieldValue fieldValue = new FieldValue(indexField, val);
                    currentFieldValues.add(fieldValue);
                }
            }

            currentFieldBuilder = null;
            currentValue = null;
        }

        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        final String string = new String(ch, start, length);
        if (NAME.equals(currentElement)) {
            currentFieldBuilder.fldName(string);
        } else if (TYPE.equals(currentElement)) {
            final FieldType type = FieldType.fromDisplayValue(string);
            currentFieldBuilder.fldType(type);
        } else if (ANALYSER.equals(currentElement)) {
            final AnalyzerType analyzerType = AnalyzerType.fromDisplayValue(string);
            currentFieldBuilder.analyzerType(analyzerType);
        } else if (INDEXED.equals(currentElement)) {
            currentFieldBuilder.indexed(Boolean.parseBoolean(string));
        } else if (STORED.equals(currentElement)) {
            currentFieldBuilder.stored(Boolean.parseBoolean(string));
        } else if (TERM_POSITIONS.equals(currentElement)) {
            currentFieldBuilder.termPositions(Boolean.parseBoolean(string));
        } else if (CASE_SENSITIVE.equals(currentElement)) {
            currentFieldBuilder.caseSensitive(Boolean.parseBoolean(string));
        } else if (VALUE.equals(currentElement)) {
            currentValue = string;
        }

        super.characters(ch, start, length);
    }

    private Val convertValue(final IndexField indexField, final String value) {
        try {
            return IndexFieldUtil.convertValue(indexField, value);
        } catch (final RuntimeException e) {
            log(Severity.ERROR, e.getMessage(), e);
        }
        return null;
    }

    private void log(final Severity severity, final String message, final Exception e) {
        errorReceiverProxy.log(severity, locationFactory.create(locator), getElementId(), message, e);
    }

    protected abstract void processFields(final List<FieldValue> fieldValues);
}
