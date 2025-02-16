/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.http.annotation.Immutable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Immutable
public class CloneUtils {
    public static <T> T cloneObject(T obj) throws CloneNotSupportedException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Cloneable) {
            Method m2;
            Class<?> clazz = obj.getClass();
            try {
                m2 = clazz.getMethod("clone", null);
            }
            catch (NoSuchMethodException ex2) {
                throw new NoSuchMethodError(ex2.getMessage());
            }
            try {
                Object result = m2.invoke(obj, (Object[])null);
                return (T)result;
            }
            catch (InvocationTargetException ex3) {
                Throwable cause = ex3.getCause();
                if (cause instanceof CloneNotSupportedException) {
                    throw (CloneNotSupportedException)cause;
                }
                throw new Error("Unexpected exception", cause);
            }
            catch (IllegalAccessException ex4) {
                throw new IllegalAccessError(ex4.getMessage());
            }
        }
        throw new CloneNotSupportedException();
    }

    public static Object clone(Object obj) throws CloneNotSupportedException {
        return CloneUtils.cloneObject(obj);
    }

    private CloneUtils() {
    }
}

