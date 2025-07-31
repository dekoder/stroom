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

package stroom.annotation.client;

import stroom.annotation.shared.Annotation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

import java.util.function.Consumer;

public class ShowFindAnnotationEvent extends GwtEvent<ShowFindAnnotationEvent.Handler> {

    private static Type<Handler> TYPE;

    private final Consumer<Annotation> annotationConsumer;

    private ShowFindAnnotationEvent(final Consumer<Annotation> annotationConsumer) {
        this.annotationConsumer = annotationConsumer;
    }

    public static void fire(final HasHandlers handlers,
                            final Consumer<Annotation> annotationConsumer) {
        handlers.fireEvent(new ShowFindAnnotationEvent(annotationConsumer));
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }
        return TYPE;
    }

    public Consumer<Annotation> getAnnotationConsumer() {
        return annotationConsumer;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(final Handler handler) {
        handler.onShow(this);
    }

    public interface Handler extends EventHandler {

        void onShow(ShowFindAnnotationEvent event);
    }
}
