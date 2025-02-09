/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelperUtils {
    public static Field findField(Class<?> clazz, String ... fieldNames) {
        Exception failed = null;
        String[] stringArray = fieldNames;
        int n2 = fieldNames.length;
        int n3 = 0;
        while (n3 < n2) {
            String fieldName = stringArray[n3];
            try {
                Field f2 = clazz.getDeclaredField(fieldName);
                f2.setAccessible(true);
                return f2;
            }
            catch (Exception e2) {
                failed = e2;
                ++n3;
            }
        }
        throw new UnableToFindFieldException(fieldNames, failed);
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, int fieldIndex) {
        try {
            Field f2 = classToAccess.getDeclaredFields()[fieldIndex];
            f2.setAccessible(true);
            return (T)f2.get(instance);
        }
        catch (Exception e2) {
            throw new UnableToAccessFieldException(new String[0], e2);
        }
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String ... fieldNames) {
        try {
            return (T)ReflectionHelperUtils.findField(classToAccess, fieldNames).get(instance);
        }
        catch (Exception e2) {
            throw new UnableToAccessFieldException(fieldNames, e2);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex) {
        try {
            Field f2 = classToAccess.getDeclaredFields()[fieldIndex];
            f2.setAccessible(true);
            f2.set(instance, value);
        }
        catch (Exception e2) {
            throw new UnableToAccessFieldException(new String[0], e2);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String ... fieldNames) {
        try {
            ReflectionHelperUtils.findField(classToAccess, fieldNames).set(instance, value);
        }
        catch (Exception e2) {
            throw new UnableToAccessFieldException(fieldNames, e2);
        }
    }

    public static Class<? super Object> getClass(ClassLoader loader, String ... classNames) {
        Exception err = null;
        String[] stringArray = classNames;
        int n2 = classNames.length;
        int n3 = 0;
        while (n3 < n2) {
            String className = stringArray[n3];
            try {
                return Class.forName(className, false, loader);
            }
            catch (Exception e2) {
                err = e2;
                ++n3;
            }
        }
        throw new UnableToFindClassException(classNames, err);
    }

    public static <E> Method findMethod(Class<? super E> clazz, E instance, String[] methodNames, Class<?> ... methodTypes) {
        Exception failed = null;
        String[] stringArray = methodNames;
        int n2 = methodNames.length;
        int n3 = 0;
        while (n3 < n2) {
            String methodName = stringArray[n3];
            try {
                Method m2 = clazz.getDeclaredMethod(methodName, methodTypes);
                m2.setAccessible(true);
                return m2;
            }
            catch (Exception e2) {
                failed = e2;
                ++n3;
            }
        }
        throw new UnableToFindMethodException(methodNames, failed);
    }

    public static class UnableToAccessFieldException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private String[] fieldNameList;

        public UnableToAccessFieldException(String[] fieldNames, Exception e2) {
            super(e2);
            this.fieldNameList = fieldNames;
        }
    }

    public static class UnableToFindClassException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private String[] classNames;

        public UnableToFindClassException(String[] classNames, Exception err) {
            super(err);
            this.classNames = classNames;
        }
    }

    public static class UnableToFindFieldException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private String[] fieldNameList;

        public UnableToFindFieldException(String[] fieldNameList, Exception e2) {
            super(e2);
            this.fieldNameList = fieldNameList;
        }
    }

    public static class UnableToFindMethodException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private String[] methodNames;

        public UnableToFindMethodException(String[] methodNames, Exception failed) {
            super(failed);
            this.methodNames = methodNames;
        }
    }
}

