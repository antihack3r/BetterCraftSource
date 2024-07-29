/*
 * Decompiled with CFR 0.152.
 */
package net.lenni0451.eventapi.manager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import net.lenni0451.eventapi.events.EventTarget;
import net.lenni0451.eventapi.events.IEvent;
import net.lenni0451.eventapi.events.types.IStoppable;
import net.lenni0451.eventapi.injection.javassist.IInjectedListener;
import net.lenni0451.eventapi.injection.javassist.IInjectionPipeline;
import net.lenni0451.eventapi.listener.IErrorListener;
import net.lenni0451.eventapi.listener.IEventListener;

public class InjectionEventManager {
    private static final Object callLock = new Object();
    private static final Map<Class<? extends IEvent>, IInjectionPipeline> EVENT_PIPELINE = new ConcurrentHashMap<Class<? extends IEvent>, IInjectionPipeline>();
    private static final Map<Class<? extends IEvent>, IEventListener[]> EVENT_LISTENER = new ConcurrentHashMap<Class<? extends IEvent>, IEventListener[]>();
    private static final List<IErrorListener> ERROR_LISTENER = new CopyOnWriteArrayList<IErrorListener>();

    public static IEventListener[] getListener(Class<? extends IEvent> eventType) {
        return EVENT_LISTENER.get(eventType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void call(IEvent event) {
        Object object = callLock;
        synchronized (object) {
            if (event != null && EVENT_PIPELINE.containsKey(event.getClass())) {
                try {
                    EVENT_PIPELINE.get(event.getClass()).call(event);
                    if (EVENT_PIPELINE.containsKey(IEvent.class)) {
                        EVENT_PIPELINE.get(IEvent.class).call(event);
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
    }

    public static <T extends IEventListener> void register(T listener) {
        InjectionEventManager.register(IEvent.class, listener);
    }

    public static void register(Object listener) {
        ClassPool cp2 = ClassPool.getDefault();
        try {
            cp2.get(IInjectedListener.class.getName());
        }
        catch (Throwable t2) {
            ClassPool.getDefault().insertClassPath(new ClassClassPath(IInjectedListener.class));
        }
        Method[] methodArray = listener.getClass().getMethods();
        int n2 = methodArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Method method = methodArray[n3];
            if (method.isAnnotationPresent(EventTarget.class)) {
                EventTarget methodAnnotation = method.getDeclaredAnnotation(EventTarget.class);
                Class<?>[] methodArguments = method.getParameterTypes();
                if (methodArguments.length == 1 && IEvent.class.isAssignableFrom(methodArguments[0]) && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                    Object listenerObject;
                    Class<?> newListenerClass;
                    method.setAccessible(true);
                    Class<?> eventType = methodArguments[0];
                    String methodName = method.getName();
                    CtClass newListener = cp2.makeClass("InjectionListener_" + System.nanoTime());
                    try {
                        newListener.addInterface(cp2.get(IInjectedListener.class.getName()));
                    }
                    catch (Throwable e2) {
                        throw new RuntimeException("Class could not implement IReflectedListener", e2);
                    }
                    try {
                        newListener.addField(CtField.make("private final " + listener.getClass().getName() + " instance;", newListener));
                    }
                    catch (Exception e3) {
                        throw new RuntimeException("Could not add global variables to class", e3);
                    }
                    try {
                        CtConstructor construct = CtNewConstructor.make("public " + newListener.getName() + "(" + listener.getClass().getName() + " ob) {this.instance = ob;}", newListener);
                        newListener.addConstructor(construct);
                    }
                    catch (Throwable e4) {
                        throw new RuntimeException("Could not create new constructor", e4);
                    }
                    StringBuilder sourceBuilder = new StringBuilder().append("{");
                    sourceBuilder.append("this.instance." + methodName + "((" + eventType.getName() + ") $1);");
                    sourceBuilder.append("}");
                    try {
                        CtMethod onEventMethod = CtNewMethod.make(CtClass.voidType, cp2.get(IEventListener.class.getName()).getDeclaredMethods()[0].getName(), new CtClass[]{cp2.get(IEvent.class.getName())}, new CtClass[]{cp2.get(Throwable.class.getName())}, sourceBuilder.toString(), newListener);
                        newListener.addMethod(onEventMethod);
                    }
                    catch (Throwable e5) {
                        throw new RuntimeException("Could not create new on event method", e5);
                    }
                    try {
                        CtMethod getInstanceMethod = CtNewMethod.make(cp2.get(Object.class.getName()), cp2.get(IInjectedListener.class.getName()).getDeclaredMethods()[0].getName(), new CtClass[0], new CtClass[0], "{return this.instance;}", newListener);
                        newListener.addMethod(getInstanceMethod);
                    }
                    catch (Exception e6) {
                        throw new RuntimeException("Could not create new get instance method", e6);
                    }
                    try {
                        CtMethod getPriorityMethod = CtNewMethod.make(CtClass.byteType, cp2.get(IEventListener.class.getName()).getDeclaredMethods()[1].getName(), new CtClass[0], new CtClass[0], "{return (byte) " + methodAnnotation.priority() + ";}", newListener);
                        newListener.addMethod(getPriorityMethod);
                    }
                    catch (Exception e7) {
                        throw new RuntimeException("Could not create new get priority method", e7);
                    }
                    try {
                        newListenerClass = newListener.toClass();
                    }
                    catch (Throwable e8) {
                        throw new RuntimeException("Could not compile class", e8);
                    }
                    try {
                        listenerObject = newListenerClass.getConstructors()[0].newInstance(listener);
                    }
                    catch (Throwable e9) {
                        throw new RuntimeException("Could not instantiate new class", e9);
                    }
                    InjectionEventManager.register(eventType, (IEventListener)listenerObject);
                }
            }
            ++n3;
        }
    }

    public static <T extends IEventListener> void register(Class<? extends IEvent> eventType, T listener) {
        IEventListener[] eventListener = EVENT_LISTENER.computeIfAbsent(eventType, c2 -> new IEventListener[0]);
        IEventListener[] newEventListener = new IEventListener[eventListener.length + 1];
        EVENT_LISTENER.put(eventType, newEventListener);
        int i2 = 0;
        while (i2 <= eventListener.length) {
            newEventListener[i2] = i2 != eventListener.length ? eventListener[i2] : listener;
            ++i2;
        }
        Arrays.sort(newEventListener, (o1, o2) -> Byte.compare(o2.getPriority(), o1.getPriority()));
        EVENT_PIPELINE.put(eventType, InjectionEventManager.rebuildPipeline(newEventListener));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void unregister(Object listener) {
        Object object = callLock;
        synchronized (object) {
            for (Map.Entry<Class<? extends IEvent>, IEventListener[]> entry : EVENT_LISTENER.entrySet()) {
                ArrayList<IEventListener> currentListener = new ArrayList<IEventListener>();
                Collections.addAll(currentListener, EVENT_LISTENER.computeIfAbsent(entry.getKey(), c2 -> new IEventListener[0]));
                int oldSize = currentListener.size();
                currentListener.removeIf(eventListener -> eventListener.equals(listener) || eventListener instanceof IInjectedListener && ((IInjectedListener)eventListener).getInstance().equals(listener));
                if (oldSize == currentListener.size()) continue;
                IEventListener[] newEventListener = currentListener.toArray(new IEventListener[0]);
                EVENT_LISTENER.put(entry.getKey(), newEventListener);
                EVENT_PIPELINE.put(entry.getKey(), InjectionEventManager.rebuildPipeline(newEventListener));
            }
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

    private static IInjectionPipeline rebuildPipeline(IEventListener[] eventListener) {
        IInjectionPipeline pipelineObject;
        Class<?> pipelineClass;
        CtMethod method;
        ClassPool cp2 = ClassPool.getDefault();
        String methodName = null;
        try {
            CtMethod[] ctMethodArray = cp2.get(InjectionEventManager.class.getName()).getDeclaredMethods();
            int n2 = ctMethodArray.length;
            int n3 = 0;
            while (n3 < n2) {
                CtMethod method2 = ctMethodArray[n3];
                if (method2.getReturnType().getSimpleName().equals(cp2.get(IEventListener[].class.getName()).getSimpleName())) {
                    methodName = method2.getName();
                    break;
                }
                ++n3;
            }
            if (methodName == null) {
                throw new NullPointerException();
            }
        }
        catch (Throwable e2) {
            throw new IllegalStateException("Could not find method name to get listener array", e2);
        }
        StringBuilder sourceBuilder = new StringBuilder().append("{");
        sourceBuilder.append(String.valueOf(IEventListener.class.getName()) + "[] listener = " + InjectionEventManager.class.getName() + "." + methodName + "($1.getClass());");
        int i2 = 0;
        while (i2 < eventListener.length) {
            try {
                sourceBuilder.append("listener[" + i2 + "]." + cp2.get(IEventListener.class.getName()).getDeclaredMethods()[0].getName() + "($1);");
                sourceBuilder.append("if($1 instanceof " + IStoppable.class.getName() + " && ((" + IStoppable.class.getName() + ") $1).isStopped()) return;");
            }
            catch (NotFoundException e3) {
                if (ERROR_LISTENER.isEmpty()) {
                    throw new RuntimeException(e3);
                }
                ERROR_LISTENER.forEach(errorListener -> errorListener.catchException(e3));
            }
            ++i2;
        }
        sourceBuilder.append("}");
        CtClass newPipeline = cp2.makeClass("InjectionPipeline_" + System.nanoTime());
        try {
            newPipeline.addInterface(cp2.get(IInjectionPipeline.class.getName()));
        }
        catch (Throwable e4) {
            throw new RuntimeException("Class could not implement IInjectionPipeline", e4);
        }
        try {
            method = CtNewMethod.make(CtClass.voidType, cp2.get(IInjectionPipeline.class.getName()).getDeclaredMethods()[0].getName(), new CtClass[]{cp2.get(IEvent.class.getName())}, new CtClass[]{cp2.get(Throwable.class.getName())}, sourceBuilder.toString(), newPipeline);
        }
        catch (Throwable e5) {
            throw new RuntimeException("Could not create new call method", e5);
        }
        try {
            newPipeline.addMethod(method);
        }
        catch (Throwable e6) {
            throw new RuntimeException("Could not add call method to class", e6);
        }
        try {
            pipelineClass = newPipeline.toClass();
        }
        catch (Throwable e7) {
            throw new RuntimeException("Could not compile class", e7);
        }
        try {
            pipelineObject = (IInjectionPipeline)pipelineClass.newInstance();
        }
        catch (Throwable e8) {
            throw new RuntimeException("Could not instantiate new class", e8);
        }
        return pipelineObject;
    }
}

