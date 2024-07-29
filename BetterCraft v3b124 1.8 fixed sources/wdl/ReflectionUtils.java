/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Field stealField(Class<?> typeOfClass, Class<?> typeOfField) {
        Field[] fields;
        Field[] fieldArray = fields = typeOfClass.getDeclaredFields();
        int n2 = fields.length;
        int n3 = 0;
        while (n3 < n2) {
            Field f2 = fieldArray[n3];
            if (f2.getType().equals(typeOfField)) {
                try {
                    f2.setAccessible(true);
                    return f2;
                }
                catch (Exception e2) {
                    throw new RuntimeException("WorldDownloader: Couldn't steal Field of type \"" + typeOfField + "\" from class \"" + typeOfClass + "\" !", e2);
                }
            }
            ++n3;
        }
        throw new RuntimeException("WorldDownloader: Couldn't steal Field of type \"" + typeOfField + "\" from class \"" + typeOfClass + "\" !");
    }

    public static <T> T stealAndGetField(Object object, Class<T> typeOfField) {
        Class<?> typeOfObject;
        if (object instanceof Class) {
            typeOfObject = (Class<?>)object;
            object = null;
        } else {
            typeOfObject = object.getClass();
        }
        try {
            Field f2 = ReflectionUtils.stealField(typeOfObject, typeOfField);
            return typeOfField.cast(f2.get(object));
        }
        catch (Exception e2) {
            throw new RuntimeException("WorldDownloader: Couldn't get Field of type \"" + typeOfField + "\" from object \"" + object + "\" !", e2);
        }
    }

    public static void stealAndSetField(Object object, Class<?> typeOfField, Object value) {
        Class<?> typeOfObject;
        if (object instanceof Class) {
            typeOfObject = (Class<?>)object;
            object = null;
        } else {
            typeOfObject = object.getClass();
        }
        try {
            Field f2 = ReflectionUtils.stealField(typeOfObject, typeOfField);
            f2.set(object, value);
        }
        catch (Exception e2) {
            throw new RuntimeException("WorldDownloader: Couldn't set Field of type \"" + typeOfField + "\" from object \"" + object + "\" to " + value + "!", e2);
        }
    }

    public static <T> T stealAndGetField(Object object, Class<?> typeOfObject, Class<T> typeOfField) {
        try {
            Field f2 = ReflectionUtils.stealField(typeOfObject, typeOfField);
            return typeOfField.cast(f2.get(object));
        }
        catch (Exception e2) {
            throw new RuntimeException("WorldDownloader: Couldn't get Field of type \"" + typeOfField + "\" from object \"" + object + "\" !", e2);
        }
    }

    public static void stealAndSetField(Object object, Class<?> typeOfObject, Class<?> typeOfField, Object value) {
        try {
            Field f2 = ReflectionUtils.stealField(typeOfObject, typeOfField);
            f2.set(object, value);
        }
        catch (Exception e2) {
            throw new RuntimeException("WorldDownloader: Couldn't set Field of type \"" + typeOfField + "\" from object \"" + object + "\" to " + value + "!", e2);
        }
    }
}

