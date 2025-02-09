/*
 * Decompiled with CFR 0.152.
 */
package net.lenni0451.eventapi.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.lenni0451.eventapi.events.EventTarget;
import net.lenni0451.eventapi.events.IEvent;
import net.lenni0451.eventapi.events.types.IStoppable;
import net.lenni0451.eventapi.listener.IErrorListener;
import net.lenni0451.eventapi.listener.IEventListener;
import net.lenni0451.eventapi.reflection.ReflectedEventListener;

public class EventManager {
    private static final Map<Class<? extends IEvent>, List<EventExecutor>> EVENT_LISTENER = new ConcurrentHashMap<Class<? extends IEvent>, List<EventExecutor>>();
    private static final List<IErrorListener> ERROR_LISTENER = new CopyOnWriteArrayList<IErrorListener>();

    public static void call(IEvent event) {
        if (event == null) {
            return;
        }
        ArrayList eventListener = new ArrayList();
        if (EVENT_LISTENER.containsKey(event.getClass())) {
            eventListener.addAll(EVENT_LISTENER.get(event.getClass()));
        }
        if (EVENT_LISTENER.containsKey(IEvent.class)) {
            eventListener.addAll(EVENT_LISTENER.get(IEvent.class));
        }
        for (EventExecutor listener : eventListener) {
            try {
                listener.getEventListener().onEvent(event);
            }
            catch (Throwable e2) {
                if (ERROR_LISTENER.isEmpty()) {
                    throw new RuntimeException(e2);
                }
                ERROR_LISTENER.forEach(errorListener -> errorListener.catchException(e2));
            }
            if (event instanceof IStoppable && ((IStoppable)((Object)event)).isStopped()) break;
        }
    }

    public static <T extends IEventListener> void register(T listener) {
        EventManager.register(IEvent.class, (byte)2, listener);
    }

    public static void register(Object listener) {
        Method[] methodArray = listener.getClass().getMethods();
        int n2 = methodArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Method method = methodArray[n3];
            if (method.isAnnotationPresent(EventTarget.class)) {
                EventTarget anno = method.getAnnotation(EventTarget.class);
                Class<?>[] methodArguments = method.getParameterTypes();
                if (methodArguments.length == 1 && IEvent.class.isAssignableFrom(methodArguments[0])) {
                    ReflectedEventListener eventListener = new ReflectedEventListener(listener, methodArguments[0], method);
                    if (methodArguments[0].equals(IEvent.class)) {
                        EventManager.register(anno.priority(), eventListener);
                    } else {
                        EventManager.register(methodArguments[0], anno.priority(), eventListener);
                    }
                }
            }
            ++n3;
        }
    }

    public static <T extends IEventListener> void register(Class<? extends IEvent> eventType, T listener) {
        EventManager.register(eventType, (byte)2, listener);
    }

    public static <T extends IEventListener> void register(byte eventPriority, T listener) {
        EventManager.register(IEvent.class, eventPriority, listener);
    }

    public static <T extends IEventListener> void register(Class<? extends IEvent> eventType, byte eventPriority, T listener) {
        List eventListener = EVENT_LISTENER.computeIfAbsent(eventType, k2 -> new CopyOnWriteArrayList());
        eventListener.add(new EventExecutor(listener, eventPriority));
        for (Map.Entry<Class<? extends IEvent>, List<EventExecutor>> entry : EVENT_LISTENER.entrySet()) {
            List<EventExecutor> eventExecutor = entry.getValue();
            eventExecutor.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
        }
    }

    public static void unregister(Object listener) {
        for (Map.Entry<Class<? extends IEvent>, List<EventExecutor>> entry : EVENT_LISTENER.entrySet()) {
            entry.getValue().removeIf(eventExecutor -> eventExecutor.getEventListener().equals(listener) || eventExecutor.getEventListener() instanceof ReflectedEventListener && ((ReflectedEventListener)eventExecutor.getEventListener()).getCallInstance().equals(listener));
        }
    }

    public static void addErrorListener(IErrorListener errorListener) {
        if (!ERROR_LISTENER.contains(errorListener)) {
            ERROR_LISTENER.add(errorListener);
        }
    }

    public static boolean removeErrorListener(IErrorListener errorListener) {
        return ERROR_LISTENER.remove(errorListener);
    }

    private static class EventExecutor {
        private final IEventListener eventListener;
        private final byte priority;

        public EventExecutor(IEventListener eventListener, byte priority) {
            this.eventListener = eventListener;
            this.priority = priority;
        }

        public IEventListener getEventListener() {
            return this.eventListener;
        }

        public byte getPriority() {
            return this.priority;
        }
    }
}

