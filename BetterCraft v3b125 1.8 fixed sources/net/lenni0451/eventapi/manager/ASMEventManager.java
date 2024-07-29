/*
 * Decompiled with CFR 0.152.
 */
package net.lenni0451.eventapi.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.lenni0451.eventapi.events.EventTarget;
import net.lenni0451.eventapi.events.IEvent;
import net.lenni0451.eventapi.events.types.IStoppable;
import net.lenni0451.eventapi.injection.asm.IInjectionEventHandler;
import net.lenni0451.eventapi.listener.IErrorListener;
import net.lenni0451.eventapi.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ASMEventManager {
    public static ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final Map<Class<? extends IEvent>, Map<Object, List<Method>>> TRACKED_LISTENER = new ConcurrentHashMap<Class<? extends IEvent>, Map<Object, List<Method>>>();
    private static final Map<Class<? extends IEvent>, IInjectionEventHandler> EVENT_HANDLER = new ConcurrentHashMap<Class<? extends IEvent>, IInjectionEventHandler>();
    private static final List<IErrorListener> ERROR_LISTENER = new CopyOnWriteArrayList<IErrorListener>();

    public static void call(IEvent event) {
        if (event != null && EVENT_HANDLER.containsKey(event.getClass())) {
            try {
                EVENT_HANDLER.get(event.getClass()).call(event);
                if (EVENT_HANDLER.containsKey(IEvent.class)) {
                    EVENT_HANDLER.get(IEvent.class).call(event);
                }
            }
            catch (Throwable e2) {
                if (ERROR_LISTENER.isEmpty()) {
                    throw new RuntimeException(e2);
                }
                ERROR_LISTENER.forEach(errorListener -> errorListener.catchException(e2));
            }
        }
    }

    public static void register(Object eventListener) {
        ArrayList<Class<? extends IEvent>> updatedEvents = new ArrayList<Class<? extends IEvent>>();
        boolean isStaticCall = eventListener instanceof Class;
        Method[] methodArray = (isStaticCall ? (Class<?>)eventListener : eventListener.getClass()).getDeclaredMethods();
        int n2 = methodArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Method method = methodArray[n3];
            EventTarget eventTarget = method.getDeclaredAnnotation(EventTarget.class);
            if (!(eventTarget == null || method.getParameterTypes().length != 1 || !IEvent.class.isAssignableFrom(method.getParameterTypes()[0]) || isStaticCall && !Modifier.isStatic(method.getModifiers()) || !isStaticCall && Modifier.isStatic(method.getModifiers()))) {
                Class<?> eventClass = method.getParameterTypes()[0];
                TRACKED_LISTENER.computeIfAbsent(eventClass, eventClazz -> new ConcurrentHashMap()).computeIfAbsent(eventListener, listener -> new CopyOnWriteArrayList()).add(method);
                updatedEvents.add(eventClass);
            }
            ++n3;
        }
        ASMEventManager.rebuildEventHandler(updatedEvents);
    }

    public static void unregister(Object eventListener) {
        ArrayList<Class<? extends IEvent>> updatedEvents = new ArrayList<Class<? extends IEvent>>();
        boolean isStaticCall = eventListener instanceof Class;
        Method[] methodArray = (isStaticCall ? (Class<?>)eventListener : eventListener.getClass()).getDeclaredMethods();
        int n2 = methodArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Method method = methodArray[n3];
            EventTarget eventTarget = method.getDeclaredAnnotation(EventTarget.class);
            if (eventTarget != null && method.getParameterTypes().length == 1 && IEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
                Class<?> eventClass = method.getParameterTypes()[0];
                TRACKED_LISTENER.computeIfAbsent(eventClass, eventClazz -> new ConcurrentHashMap()).remove(eventListener);
                if (TRACKED_LISTENER.get(eventClass).isEmpty()) {
                    TRACKED_LISTENER.remove(eventClass);
                    EVENT_HANDLER.remove(eventClass);
                } else {
                    updatedEvents.add(eventClass);
                }
            }
            ++n3;
        }
        ASMEventManager.rebuildEventHandler(updatedEvents);
    }

    private static void rebuildEventHandler(List<Class<? extends IEvent>> updatedEvents) {
        for (Class<? extends IEvent> event : updatedEvents) {
            ASMEventManager.rebuildEventHandler(event);
        }
    }

    private static void rebuildEventHandler(Class<? extends IEvent> event) {
        ArrayList<_1> methods = new ArrayList<_1>();
        HashMap<Object, String> instanceMappings = new HashMap<Object, String>();
        for (final Map.Entry<Object, List<Method>> entry : TRACKED_LISTENER.get(event).entrySet()) {
            for (final Method method : entry.getValue()) {
                methods.add(new Map.Entry<Method, Object>(){

                    @Override
                    public Method getKey() {
                        return method;
                    }

                    @Override
                    public Object getValue() {
                        return entry.getKey();
                    }

                    @Override
                    public Object setValue(Object value) {
                        return entry.getKey();
                    }
                });
            }
        }
        methods.sort((o1, o2) -> {
            EventTarget eventTarget1 = ((Method)o1.getKey()).getDeclaredAnnotation(EventTarget.class);
            EventTarget eventTarget2 = ((Method)o2.getKey()).getDeclaredAnnotation(EventTarget.class);
            return Byte.compare(eventTarget2.priority(), eventTarget1.priority());
        });
        ClassNode newHandlerNode = new ClassNode();
        newHandlerNode.visit(52, 33, "eventhandler/" + event.getSimpleName() + "EventHandler" + System.nanoTime(), null, "java/lang/Object", new String[]{IInjectionEventHandler.class.getName().replace(".", "/")});
        newHandlerNode.visitSource("EventAPI by Lenni0451", null);
        int index = 0;
        for (Object listener : TRACKED_LISTENER.get(event).keySet()) {
            instanceMappings.put(listener, "listener" + index);
            FieldNode fieldNode = new FieldNode(1, (String)instanceMappings.get(listener), "L" + listener.getClass().getName().replace(".", "/") + ";", null, null);
            newHandlerNode.fields.add(fieldNode);
            ++index;
        }
        MethodNode constructorNode = new MethodNode(1, "<init>", "()V", null, null);
        constructorNode.instructions.add(new VarInsnNode(25, 0));
        constructorNode.instructions.add(new MethodInsnNode(183, "java/lang/Object", "<init>", "()V"));
        constructorNode.instructions.add(new InsnNode(177));
        newHandlerNode.methods.add(constructorNode);
        MethodNode methodNode = new MethodNode(1, IInjectionEventHandler.class.getDeclaredMethods()[0].getName(), "(L" + IEvent.class.getName().replace(".", "/") + ";)V", null, null);
        InsnList instructions = new InsnList();
        for (Map.Entry entry : methods) {
            if (Modifier.isStatic(((Method)entry.getKey()).getModifiers())) {
                instructions.add(new VarInsnNode(25, 1));
                instructions.add(new TypeInsnNode(192, event.getName().replace(".", "/")));
                instructions.add(new MethodInsnNode(184, (entry.getValue() instanceof Class ? (Class<?>)entry.getValue() : entry.getValue().getClass()).getName().replace(".", "/"), ((Method)entry.getKey()).getName(), "(L" + event.getName().replace(".", "/") + ";)V"));
            } else {
                instructions.add(new VarInsnNode(25, 0));
                instructions.add(new FieldInsnNode(180, newHandlerNode.name, (String)instanceMappings.get(entry.getValue()), "L" + entry.getValue().getClass().getName().replace(".", "/") + ";"));
                instructions.add(new VarInsnNode(25, 1));
                instructions.add(new TypeInsnNode(192, event.getName().replace(".", "/")));
                instructions.add(new MethodInsnNode(182, entry.getValue().getClass().getName().replace(".", "/"), ((Method)entry.getKey()).getName(), "(L" + event.getName().replace(".", "/") + ";)V"));
            }
            if (!IStoppable.class.isAssignableFrom(event)) continue;
            instructions.add(new VarInsnNode(25, 1));
            instructions.add(new TypeInsnNode(193, IStoppable.class.getName().replace(".", "/")));
            LabelNode continueLabel = new LabelNode();
            instructions.add(new JumpInsnNode(153, continueLabel));
            instructions.add(new VarInsnNode(25, 1));
            instructions.add(new TypeInsnNode(192, IStoppable.class.getName().replace(".", "/")));
            instructions.add(new MethodInsnNode(185, IStoppable.class.getName().replace(".", "/"), IStoppable.class.getDeclaredMethods()[0].getName(), "()Z"));
            instructions.add(new JumpInsnNode(153, continueLabel));
            instructions.add(new InsnNode(177));
            instructions.add(continueLabel);
            instructions.add(new FrameNode(3, 0, null, 0, null));
        }
        instructions.add(new InsnNode(177));
        methodNode.instructions = instructions;
        newHandlerNode.methods.add(methodNode);
        try {
            Class<?> clazz = ASMUtils.defineClass(CLASS_LOADER, newHandlerNode);
            IInjectionEventHandler eventHandler = (IInjectionEventHandler)clazz.newInstance();
            for (Object object : TRACKED_LISTENER.get(event).keySet()) {
                Field field = clazz.getDeclaredField((String)instanceMappings.get(object));
                field.setAccessible(true);
                field.set(eventHandler, object);
            }
            EVENT_HANDLER.put(event, eventHandler);
        }
        catch (Throwable t2) {
            if (ERROR_LISTENER.isEmpty()) {
                throw new RuntimeException(t2);
            }
            ERROR_LISTENER.forEach(errorListener -> errorListener.catchException(t2));
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
}

