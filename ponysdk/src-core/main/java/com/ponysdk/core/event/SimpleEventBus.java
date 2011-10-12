/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *	Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *	Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *  
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ponysdk.core.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.ponysdk.core.event.Event.Type;

public class SimpleEventBus implements EventBus {

    /**
     * Map of event type to map of event source to list of their handlers.
     */
    private final Map<Type<?>, Map<Object, List<?>>> map = new HashMap<Type<?>, Map<Object, List<?>>>();

    private final List<BroadcastEventHandler> broadcastHandlerManager = new ArrayList<BroadcastEventHandler>();

    @Override
    public <H extends EventHandler> HandlerRegistration addHandler(Type<H> type, H handler) {
        if (type == null) {
            throw new NullPointerException("Cannot add a handler with a null type");
        }
        if (handler == null) {
            throw new NullPointerException("Cannot add a null handler");
        }

        return doAdd(type, null, handler);
    }

    @Override
    public <H extends EventHandler> HandlerRegistration addHandlerToSource(final Type<H> type, final Object source, final H handler) {
        if (type == null) {
            throw new NullPointerException("Cannot add a handler with a null type");
        }
        if (source == null) {
            throw new NullPointerException("Cannot add a handler with a null source");
        }
        if (handler == null) {
            throw new NullPointerException("Cannot add a null handler");
        }

        return doAdd(type, source, handler);
    }

    @Override
    public void fireEvent(Event<?> event) {
        if (event == null) {
            throw new NullPointerException("Cannot fire null event");
        }
        doFire(event, null);
        fireBroadcastEvent(event);
    }

    @Override
    public void addHandler(BroadcastEventHandler handler) {
        broadcastHandlerManager.add(handler);
    }

    private void fireBroadcastEvent(Event<?> event) {
        for (final BroadcastEventHandler handler : broadcastHandlerManager) {
            handler.onEvent(event);
        }
    }

    @Override
    public void fireEventFromSource(Event<?> event, Object source) {
        if (event == null) {
            throw new NullPointerException("Cannot fire null event");
        }
        if (source == null) {
            throw new NullPointerException("Cannot fire from a null source");
        }
        doFire(event, source);
    }

    protected <H extends EventHandler> void doRemove(Event.Type<H> type, Object source, H handler) {
        doRemoveNow(type, source, handler);
    }

    private <H extends EventHandler> HandlerRegistration doAdd(final Event.Type<H> type, final Object source, final H handler) {
        return doAddNow(type, source, handler);
    }

    private <H extends EventHandler> HandlerRegistration doAddNow(final Event.Type<H> type, final Object source, final H handler) {
        final List<H> l = ensureHandlerList(type, source);
        l.add(handler);

        return new HandlerRegistration() {

            @Override
            public void removeHandler() {
                doRemove(type, source, handler);
            }
        };
    }

    private <H extends EventHandler> void doFire(Event<H> event, Object source) {
        if (source != null) {
            event.setSource(source);
        }

        final List<H> handlers = getDispatchList(event.getAssociatedType(), source);

        final ListIterator<H> it = handlers.listIterator();
        while (it.hasNext()) {
            final H handler = it.next();

            try {
                event.dispatch(handler);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <H> void doRemoveNow(Event.Type<H> type, Object source, H handler) {
        final List<H> l = getHandlerList(type, source);

        final boolean removed = l.remove(handler);
        assert removed : "redundant remove call";
        if (removed && l.isEmpty()) {
            prune(type, source);
        }
    }

    private <H> List<H> ensureHandlerList(Event.Type<H> type, Object source) {
        Map<Object, List<?>> sourceMap = map.get(type);
        if (sourceMap == null) {
            sourceMap = new HashMap<Object, List<?>>();
            map.put(type, sourceMap);
        }

        // safe, we control the puts.
        @SuppressWarnings("unchecked")
        List<H> handlers = (List<H>) sourceMap.get(source);
        if (handlers == null) {
            handlers = new ArrayList<H>();
            sourceMap.put(source, handlers);
        }

        return handlers;
    }

    private <H> List<H> getDispatchList(Event.Type<H> type, Object source) {
        final List<H> directHandlers = getHandlerList(type, source);
        if (source == null) {
            return directHandlers;
        }

        final List<H> globalHandlers = getHandlerList(type, null);

        final List<H> rtn = new ArrayList<H>(directHandlers);
        rtn.addAll(globalHandlers);
        return rtn;
    }

    public <H> List<H> getHandlerList(Event.Type<H> type, Object source) {
        final Map<Object, List<?>> sourceMap = map.get(type);
        if (sourceMap == null) {
            return Collections.emptyList();
        }

        // safe, we control the puts.
        @SuppressWarnings("unchecked")
        final List<H> handlers = (List<H>) sourceMap.get(source);
        if (handlers == null) {
            return Collections.emptyList();
        }

        return handlers;
    }

    private void prune(Event.Type<?> type, Object source) {
        final Map<Object, List<?>> sourceMap = map.get(type);

        final List<?> pruned = sourceMap.remove(source);

        assert pruned != null : "Can't prune what wasn't there";
        assert pruned.isEmpty() : "Pruned unempty list!";

        if (sourceMap.isEmpty()) {
            map.remove(type);
        }
    }
}