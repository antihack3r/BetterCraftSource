/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtil {
    public static Object invokeStatic(Class<?> clazz, String method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m2 = clazz.getDeclaredMethod(method, new Class[0]);
        return m2.invoke(null, new Object[0]);
    }

    public static Object invoke(Object o2, String method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m2 = o2.getClass().getDeclaredMethod(method, new Class[0]);
        return m2.invoke(o2, new Object[0]);
    }

    public static <T> T getStatic(Class<?> clazz, String f2, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(f2);
        field.setAccessible(true);
        return type.cast(field.get(null));
    }

    public static void setStatic(Class<?> clazz, String f2, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(f2);
        field.setAccessible(true);
        field.set(null, value);
    }

    public static <T> T getSuper(Object o2, String f2, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
        Field field = o2.getClass().getSuperclass().getDeclaredField(f2);
        field.setAccessible(true);
        return type.cast(field.get(o2));
    }

    public static <T> T get(Object instance, Class<?> clazz, String f2, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(f2);
        field.setAccessible(true);
        return type.cast(field.get(instance));
    }

    public static <T> T get(Object o2, String f2, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
        Field field = o2.getClass().getDeclaredField(f2);
        field.setAccessible(true);
        return type.cast(field.get(o2));
    }

    public static <T> T getPublic(Object o2, String f2, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
        Field field = o2.getClass().getField(f2);
        field.setAccessible(true);
        return type.cast(field.get(o2));
    }

    public static void set(Object o2, String f2, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = o2.getClass().getDeclaredField(f2);
        field.setAccessible(true);
        field.set(o2, value);
    }

    public static final class ClassReflection {
        private final Class<?> handle;
        private final Map<String, Field> fields = new ConcurrentHashMap<String, Field>();
        private final Map<String, Method> methods = new ConcurrentHashMap<String, Method>();

        public ClassReflection(Class<?> handle) {
            this(handle, true);
        }

        public ClassReflection(Class<?> handle, boolean recursive) {
            this.handle = handle;
            this.scanFields(handle, recursive);
            this.scanMethods(handle, recursive);
        }

        private void scanFields(Class<?> host, boolean recursive) {
            if (recursive && host.getSuperclass() != null && host.getSuperclass() != Object.class) {
                this.scanFields(host.getSuperclass(), true);
            }
            for (Field field : host.getDeclaredFields()) {
                field.setAccessible(true);
                this.fields.put(field.getName(), field);
            }
        }

        private void scanMethods(Class<?> host, boolean recursive) {
            if (recursive && host.getSuperclass() != null && host.getSuperclass() != Object.class) {
                this.scanMethods(host.getSuperclass(), true);
            }
            for (Method method : host.getDeclaredMethods()) {
                method.setAccessible(true);
                this.methods.put(method.getName(), method);
            }
        }

        public Object newInstance() throws ReflectiveOperationException {
            return this.handle.getConstructor(new Class[0]).newInstance(new Object[0]);
        }

        public Field getField(String name) {
            return this.fields.get(name);
        }

        public void setFieldValue(String fieldName, Object instance, Object value) throws IllegalAccessException {
            this.getField(fieldName).set(instance, value);
        }

        public <T> T getFieldValue(String fieldName, Object instance, Class<T> type) throws IllegalAccessException {
            return type.cast(this.getField(fieldName).get(instance));
        }

        public <T> T invokeMethod(Class<T> type, String methodName, Object instance, Object ... args) throws InvocationTargetException, IllegalAccessException {
            return type.cast(this.getMethod(methodName).invoke(instance, args));
        }

        public Method getMethod(String name) {
            return this.methods.get(name);
        }

        public Collection<Field> getFields() {
            return Collections.unmodifiableCollection(this.fields.values());
        }

        public Collection<Method> getMethods() {
            return Collections.unmodifiableCollection(this.methods.values());
        }
    }
}
