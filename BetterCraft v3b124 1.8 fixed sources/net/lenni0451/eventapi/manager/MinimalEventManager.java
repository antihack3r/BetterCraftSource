/*
 * Decompiled with CFR 0.152.
 */
package net.lenni0451.eventapi.manager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.lenni0451.eventapi.events.IEvent;
import net.lenni0451.eventapi.listener.IEventListener;

public class MinimalEventManager {
    private static final Map<Class<? extends IEvent>, List<IEventListener>> EVENT_LISTENER = new ConcurrentHashMap<Class<? extends IEvent>, List<IEventListener>>();

    public static void call(IEvent event) {
        if (EVENT_LISTENER.containsKey(event.getClass())) {
            EVENT_LISTENER.get(event.getClass()).forEach(l2 -> {
                try {
                    l2.onEvent(event);
                }
                catch (Throwable e2) {
                    e2.printStackTrace();
                }
            });
        }
    }

    public static <T extends IEventListener> void register(Class<? extends IEvent> eventType, T listener) {
        EVENT_LISTENER.computeIfAbsent(eventType, c2 -> new CopyOnWriteArrayList()).add(listener);
    }

    public static <T extends IEventListener> void unregister(T listener) {
        EVENT_LISTENER.forEach((key, value) -> {
            boolean bl2 = value.removeIf(l2 -> l2.equals(listener));
        });
    }
}

