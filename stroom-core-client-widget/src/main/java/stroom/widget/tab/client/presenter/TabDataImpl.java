/*
 * Copyright 2016 Crown Copyright
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

package stroom.widget.tab.client.presenter;

import stroom.svg.shared.SvgImage;

public class TabDataImpl implements TabData {

    private final String text;
    private final boolean closeable;
    private final String type;

    public TabDataImpl(final String text) {
        this(text, false, null);
    }

    public TabDataImpl(final String text, final boolean closeable) {
        this(text, closeable, null);
    }

    public TabDataImpl(final String text, final String type) {
        this(text, false, type);
    }

    public TabDataImpl(final String text, final boolean closeable, final String type) {
        this.text = text;
        this.closeable = closeable;
        this.type = type;
    }

    @Override
    public SvgImage getIcon() {
        return null;
    }

    @Override
    public String getLabel() {
        return text;
    }

    @Override
    public boolean isCloseable() {
        return closeable;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TabDataImpl{" +
                "text='" + text + '\'' +
                ", closeable=" + closeable +
                ", type='" + type + '\'' +
                '}';
    }
}
