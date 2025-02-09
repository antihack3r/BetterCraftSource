/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.internal.reflect;

import com.google.gson.JsonIOException;
import com.google.gson.internal.reflect.ReflectionAccessor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class UnsafeReflectionAccessor
extends ReflectionAccessor {
    private static Class unsafeClass;
    private final Object theUnsafe = UnsafeReflectionAccessor.getUnsafeInstance();
    private final Field overrideField = UnsafeReflectionAccessor.getOverrideField();

    UnsafeReflectionAccessor() {
    }

    @Override
    public void makeAccessible(AccessibleObject ao2) {
        boolean success = this.makeAccessibleWithUnsafe(ao2);
        if (!success) {
            try {
                ao2.setAccessible(true);
            }
            catch (SecurityException e2) {
                throw new JsonIOException("Gson couldn't modify fields for " + ao2 + "\nand sun.misc.Unsafe not found.\nEither write a custom type adapter, or make fields accessible, or include sun.misc.Unsafe.", e2);
            }
        }
    }

    boolean makeAccessibleWithUnsafe(AccessibleObject ao2) {
        if (this.theUnsafe != null && this.overrideField != null) {
            try {
                Method method = unsafeClass.getMethod("objectFieldOffset", Field.class);
                long overrideOffset = (Long)method.invoke(this.theUnsafe, this.overrideField);
                Method putBooleanMethod = unsafeClass.getMethod("putBoolean", Object.class, Long.TYPE, Boolean.TYPE);
                putBooleanMethod.invoke(this.theUnsafe, ao2, overrideOffset, true);
                return true;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return false;
    }

    private static Object getUnsafeInstance() {
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return unsafeField.get(null);
        }
        catch (Exception e2) {
            return null;
        }
    }

    private static Field getOverrideField() {
        try {
            return AccessibleObject.class.getDeclaredField("override");
        }
        catch (Exception e2) {
            return null;
        }
    }
}

