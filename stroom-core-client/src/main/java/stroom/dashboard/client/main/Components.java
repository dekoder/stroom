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

package stroom.dashboard.client.main;

import stroom.dashboard.client.main.ComponentRegistry.ComponentType;
import stroom.dashboard.client.unknown.UnknownComponentPresenter;
import stroom.dashboard.shared.DashboardDoc;
import stroom.pipeline.client.event.ChangeDataEvent;
import stroom.pipeline.client.event.ChangeDataEvent.ChangeDataHandler;
import stroom.pipeline.client.event.HasChangeDataHandlers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.json.client.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Components implements HasHandlers, HasChangeDataHandlers<Components>, Iterable<Component> {

    private final EventBus eventBus;
    private final ComponentRegistry componentRegistry;

    private final Map<String, Component> idMap = new HashMap<>();
    private final Map<String, List<Component>> typeMap = new HashMap<>();

    private final Provider<UnknownComponentPresenter> unknownComponentProvider;

    private final JavaScriptObject context;
    private DashboardDoc dashboard;

    @Inject
    public Components(final EventBus eventBus, final ComponentRegistry componentRegistry,
                      final Provider<UnknownComponentPresenter> unknownComponentProvider) {
        this.eventBus = eventBus;
        this.componentRegistry = componentRegistry;
        this.unknownComponentProvider = unknownComponentProvider;
        this.context = new JSONObject().getJavaScriptObject();
    }

    public Component add(final String type, final String id) {
        Component component = componentRegistry.getComponent(type);
        if (component == null) {
            component = unknownComponentProvider.get();
            component.setComponents(this);
            idMap.put(id, component);

        } else {
            component.setComponents(this);
            idMap.put(id, component);

            typeMap.computeIfAbsent(type, k -> new ArrayList<>()).add(component);
            ChangeDataEvent.fire(this, this);
        }

        // component.onOpen();

        return component;
    }

    public void onClose(final String id) {
        final Component component = idMap.remove(id);
        if (component != null) {
            component.onClose();
        }
    }

    public void remove(final String id, final boolean fireEvents) {
        final Component component = idMap.remove(id);
        if (component != null) {
            final String type = component.getComponentType().getId();
            final List<Component> list = typeMap.get(type);
            if (list != null) {
                list.remove(component);
                if (list.size() == 0) {
                    typeMap.remove(type);
                }
            }

            component.onRemove();

            if (fireEvents) {
                ChangeDataEvent.fire(this, this);
            }
        }
    }

    public void onClose() {
        final List<String> componentIdList = new ArrayList<>(idMap.keySet());
        for (final String id : componentIdList) {
            onClose(id);
        }
    }

    public void removeAll() {
        final List<String> componentIdList = new ArrayList<>(idMap.keySet());
        for (final String id : componentIdList) {
            remove(id, false);
        }
    }

    public Collection<Component> getComponents() {
        return idMap.values();
    }

    public Component get(final String id) {
        return idMap.get(id);
    }

    public List<Component> getSortedComponentsByType(final String type) {
        List<Component> list = typeMap.get(type);
        if (list != null) {
            list = new ArrayList<>(list);
            list.sort((o1, o2) -> o1.getDisplayValue().compareTo(o2.getDisplayValue()));
            return list;
        }
        return new ArrayList<>(0);
    }

    public String validateOrGetLastComponentId(final String id, final String typeId) {
        String newId = null;

        if (id != null) {
            // See if we can find this component.
            final Component component = get(id);
            // If we have found the component check that it is the right type.
            if (component != null && typeId.equals(component.getComponentType().getId())) {
                // Set the id as this is a valid component.
                newId = id;
            }
        }

        if (newId == null) {
            // If we didn't find the component or it has an invalid type then just choose the last component of the
            // required type if we can find one.
            final List<Component> list = typeMap.get(typeId);
            if (list != null && list.size() > 0) {
                newId = list.get(list.size() - 1).getId();
            }
        }

        return newId;
    }

    public List<ComponentType> getComponentTypes() {
        return componentRegistry.getTypes();
    }

    public void clear() {
        idMap.clear();
        typeMap.clear();
    }

    @Override
    public void fireEvent(final GwtEvent<?> event) {
        eventBus.fireEventFromSource(event, this);
    }

    @Override
    public HandlerRegistration addChangeDataHandler(final ChangeDataHandler<Components> handler) {
        return eventBus.addHandlerToSource(ChangeDataEvent.getType(), this, handler);
    }

    public HandlerRegistration addComponentChangeHandler(final ComponentChangeEvent.Handler handler) {
        return eventBus.addHandlerToSource(ComponentChangeEvent.getType(), this, handler);
    }

    public void fireComponentChangeEvent(final Component component) {
        ComponentChangeEvent.fire(this, component);
    }

    @Override
    public Iterator<Component> iterator() {
        return idMap.values().iterator();
    }

    public int size() {
        return idMap.size();
    }

    public DashboardDoc getDashboard() {
        return dashboard;
    }

    public void setDashboard(final DashboardDoc dashboard) {
        this.dashboard = dashboard;
    }

    public JavaScriptObject getContext() {
        return context;
    }
}
