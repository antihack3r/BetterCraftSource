/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson.internal;

import com.viaversion.viaversion.libs.gson.internal.ConstructorConstructor;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class UnsafeAllocator {
    public static final UnsafeAllocator INSTANCE = UnsafeAllocator.create();

    public abstract <T> T newInstance(Class<T> var1) throws Exception;

    private static void assertInstantiable(Class<?> c2) {
        String exceptionMessage = ConstructorConstructor.checkInstantiable(c2);
        if (exceptionMessage != null) {
            throw new AssertionError((Object)("UnsafeAllocator is used for non-instantiable type: " + exceptionMessage));
        }
    }

    private static UnsafeAllocator create() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field f2 = unsafeClass.getDeclaredField("theUnsafe");
            f2.setAccessible(true);
            final Object unsafe = f2.get(null);
            final Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
            return new UnsafeAllocator(){

                @Override
                public <T> T newInstance(Class<T> c2) throws Exception {
                    UnsafeAllocator.assertInstantiable(c2);
                    return (T)allocateInstance.invoke(unsafe, c2);
                }
            };
        }
        catch (Exception unsafeClass) {
            try {
                Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
                getConstructorId.setAccessible(true);
                final int constructorId = (Integer)getConstructorId.invoke(null, Object.class);
                final Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);
                newInstance.setAccessible(true);
                return new UnsafeAllocator(){

                    @Override
                    public <T> T newInstance(Class<T> c2) throws Exception {
                        UnsafeAllocator.assertInstantiable(c2);
                        return (T)newInstance.invoke(null, c2, constructorId);
                    }
                };
            }
            catch (Exception getConstructorId) {
                try {
                    final Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
                    newInstance.setAccessible(true);
                    return new UnsafeAllocator(){

                        @Override
                        public <T> T newInstance(Class<T> c2) throws Exception {
                            UnsafeAllocator.assertInstantiable(c2);
                            return (T)newInstance.invoke(null, c2, Object.class);
                        }
                    };
                }
                catch (Exception exception) {
                    return new UnsafeAllocator(){

                        @Override
                        public <T> T newInstance(Class<T> c2) {
                            throw new UnsupportedOperationException("Cannot allocate " + c2 + ". Usage of JDK sun.misc.Unsafe is enabled, but it could not be used. Make sure your runtime is configured correctly.");
                        }
                    };
                }
            }
        }
    }
}

