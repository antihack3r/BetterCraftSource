/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.$Gson$Types;
import com.viaversion.viaversion.libs.gson.internal.bind.TypeAdapterRuntimeTypeWrapper;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public final class ArrayTypeAdapter<E>
extends TypeAdapter<Object> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Type type = typeToken.getType();
            if (!(type instanceof GenericArrayType || type instanceof Class && ((Class)type).isArray())) {
                return null;
            }
            Type componentType = $Gson$Types.getArrayComponentType(type);
            TypeAdapter<?> componentTypeAdapter = gson.getAdapter(TypeToken.get(componentType));
            ArrayTypeAdapter arrayAdapter = new ArrayTypeAdapter(gson, componentTypeAdapter, $Gson$Types.getRawType(componentType));
            return arrayAdapter;
        }
    };
    private final Class<E> componentType;
    private final TypeAdapter<E> componentTypeAdapter;

    public ArrayTypeAdapter(Gson context, TypeAdapter<E> componentTypeAdapter, Class<E> componentType) {
        this.componentTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(context, componentTypeAdapter, componentType);
        this.componentType = componentType;
    }

    @Override
    public Object read(JsonReader in2) throws IOException {
        if (in2.peek() == JsonToken.NULL) {
            in2.nextNull();
            return null;
        }
        ArrayList<E> list = new ArrayList<E>();
        in2.beginArray();
        while (in2.hasNext()) {
            E instance = this.componentTypeAdapter.read(in2);
            list.add(instance);
        }
        in2.endArray();
        int size = list.size();
        if (this.componentType.isPrimitive()) {
            Object array = Array.newInstance(this.componentType, size);
            for (int i2 = 0; i2 < size; ++i2) {
                Array.set(array, i2, list.get(i2));
            }
            return array;
        }
        Object[] array = (Object[])Array.newInstance(this.componentType, size);
        return list.toArray(array);
    }

    @Override
    public void write(JsonWriter out, Object array) throws IOException {
        if (array == null) {
            out.nullValue();
            return;
        }
        out.beginArray();
        int length = Array.getLength(array);
        for (int i2 = 0; i2 < length; ++i2) {
            Object value = Array.get(array, i2);
            this.componentTypeAdapter.write(out, value);
        }
        out.endArray();
    }
}

