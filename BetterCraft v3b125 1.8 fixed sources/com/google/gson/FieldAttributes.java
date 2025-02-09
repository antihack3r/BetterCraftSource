/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.internal.$Gson$Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class FieldAttributes {
    private final Field field;

    public FieldAttributes(Field f2) {
        $Gson$Preconditions.checkNotNull(f2);
        this.field = f2;
    }

    public Class<?> getDeclaringClass() {
        return this.field.getDeclaringClass();
    }

    public String getName() {
        return this.field.getName();
    }

    public Type getDeclaredType() {
        return this.field.getGenericType();
    }

    public Class<?> getDeclaredClass() {
        return this.field.getType();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return this.field.getAnnotation(annotation);
    }

    public Collection<Annotation> getAnnotations() {
        return Arrays.asList(this.field.getAnnotations());
    }

    public boolean hasModifier(int modifier) {
        return (this.field.getModifiers() & modifier) != 0;
    }

    Object get(Object instance) throws IllegalAccessException {
        return this.field.get(instance);
    }

    boolean isSynthetic() {
        return this.field.isSynthetic();
    }
}

