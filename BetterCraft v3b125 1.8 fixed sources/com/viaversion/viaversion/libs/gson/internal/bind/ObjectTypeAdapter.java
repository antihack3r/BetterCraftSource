/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.ToNumberPolicy;
import com.viaversion.viaversion.libs.gson.ToNumberStrategy;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.LinkedTreeMap;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ObjectTypeAdapter
extends TypeAdapter<Object> {
    private static final TypeAdapterFactory DOUBLE_FACTORY = ObjectTypeAdapter.newFactory(ToNumberPolicy.DOUBLE);
    private final Gson gson;
    private final ToNumberStrategy toNumberStrategy;

    private ObjectTypeAdapter(Gson gson, ToNumberStrategy toNumberStrategy) {
        this.gson = gson;
        this.toNumberStrategy = toNumberStrategy;
    }

    private static TypeAdapterFactory newFactory(final ToNumberStrategy toNumberStrategy) {
        return new TypeAdapterFactory(){

            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                if (type.getRawType() == Object.class) {
                    return new ObjectTypeAdapter(gson, toNumberStrategy);
                }
                return null;
            }
        };
    }

    public static TypeAdapterFactory getFactory(ToNumberStrategy toNumberStrategy) {
        if (toNumberStrategy == ToNumberPolicy.DOUBLE) {
            return DOUBLE_FACTORY;
        }
        return ObjectTypeAdapter.newFactory(toNumberStrategy);
    }

    private Object tryBeginNesting(JsonReader in2, JsonToken peeked) throws IOException {
        switch (peeked) {
            case BEGIN_ARRAY: {
                in2.beginArray();
                return new ArrayList();
            }
            case BEGIN_OBJECT: {
                in2.beginObject();
                return new LinkedTreeMap();
            }
        }
        return null;
    }

    private Object readTerminal(JsonReader in2, JsonToken peeked) throws IOException {
        switch (peeked) {
            case STRING: {
                return in2.nextString();
            }
            case NUMBER: {
                return this.toNumberStrategy.readNumber(in2);
            }
            case BOOLEAN: {
                return in2.nextBoolean();
            }
            case NULL: {
                in2.nextNull();
                return null;
            }
        }
        throw new IllegalStateException("Unexpected token: " + (Object)((Object)peeked));
    }

    @Override
    public Object read(JsonReader in2) throws IOException {
        JsonToken peeked = in2.peek();
        Object current = this.tryBeginNesting(in2, peeked);
        if (current == null) {
            return this.readTerminal(in2, peeked);
        }
        ArrayDeque<Object> stack = new ArrayDeque<Object>();
        while (true) {
            if (in2.hasNext()) {
                Object value;
                boolean isNesting;
                String name = null;
                if (current instanceof Map) {
                    name = in2.nextName();
                }
                boolean bl2 = isNesting = (value = this.tryBeginNesting(in2, peeked = in2.peek())) != null;
                if (value == null) {
                    value = this.readTerminal(in2, peeked);
                }
                if (current instanceof List) {
                    List list = (List)current;
                    list.add(value);
                } else {
                    Map map = (Map)current;
                    map.put(name, value);
                }
                if (!isNesting) continue;
                stack.addLast(current);
                current = value;
                continue;
            }
            if (current instanceof List) {
                in2.endArray();
            } else {
                in2.endObject();
            }
            if (stack.isEmpty()) {
                return current;
            }
            current = stack.removeLast();
        }
    }

    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        TypeAdapter<?> typeAdapter = this.gson.getAdapter(value.getClass());
        if (typeAdapter instanceof ObjectTypeAdapter) {
            out.beginObject();
            out.endObject();
            return;
        }
        typeAdapter.write(out, value);
    }
}

