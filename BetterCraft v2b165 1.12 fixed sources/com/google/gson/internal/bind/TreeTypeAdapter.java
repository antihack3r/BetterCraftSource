// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.JsonSerializationContext;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;

public final class TreeTypeAdapter<T> extends TypeAdapter<T>
{
    private final JsonSerializer<T> serializer;
    private final JsonDeserializer<T> deserializer;
    private final Gson gson;
    private final TypeToken<T> typeToken;
    private final TypeAdapterFactory skipPast;
    private final GsonContextImpl context;
    private TypeAdapter<T> delegate;
    
    public TreeTypeAdapter(final JsonSerializer<T> serializer, final JsonDeserializer<T> deserializer, final Gson gson, final TypeToken<T> typeToken, final TypeAdapterFactory skipPast) {
        this.context = new GsonContextImpl();
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.gson = gson;
        this.typeToken = typeToken;
        this.skipPast = skipPast;
    }
    
    @Override
    public T read(final JsonReader in) throws IOException {
        if (this.deserializer == null) {
            return this.delegate().read(in);
        }
        final JsonElement value = Streams.parse(in);
        if (value.isJsonNull()) {
            return null;
        }
        return this.deserializer.deserialize(value, this.typeToken.getType(), this.context);
    }
    
    @Override
    public void write(final JsonWriter out, final T value) throws IOException {
        if (this.serializer == null) {
            this.delegate().write(out, value);
            return;
        }
        if (value == null) {
            out.nullValue();
            return;
        }
        final JsonElement tree = this.serializer.serialize(value, this.typeToken.getType(), this.context);
        Streams.write(tree, out);
    }
    
    private TypeAdapter<T> delegate() {
        final TypeAdapter<T> d = this.delegate;
        return (d != null) ? d : (this.delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken));
    }
    
    public static TypeAdapterFactory newFactory(final TypeToken<?> exactType, final Object typeAdapter) {
        return new SingleTypeFactory(typeAdapter, exactType, false, null);
    }
    
    public static TypeAdapterFactory newFactoryWithMatchRawType(final TypeToken<?> exactType, final Object typeAdapter) {
        final boolean matchRawType = exactType.getType() == exactType.getRawType();
        return new SingleTypeFactory(typeAdapter, exactType, matchRawType, null);
    }
    
    public static TypeAdapterFactory newTypeHierarchyFactory(final Class<?> hierarchyType, final Object typeAdapter) {
        return new SingleTypeFactory(typeAdapter, null, false, hierarchyType);
    }
    
    private static final class SingleTypeFactory implements TypeAdapterFactory
    {
        private final TypeToken<?> exactType;
        private final boolean matchRawType;
        private final Class<?> hierarchyType;
        private final JsonSerializer<?> serializer;
        private final JsonDeserializer<?> deserializer;
        
        SingleTypeFactory(final Object typeAdapter, final TypeToken<?> exactType, final boolean matchRawType, final Class<?> hierarchyType) {
            this.serializer = ((typeAdapter instanceof JsonSerializer) ? ((JsonSerializer)typeAdapter) : null);
            this.deserializer = ((typeAdapter instanceof JsonDeserializer) ? ((JsonDeserializer)typeAdapter) : null);
            $Gson$Preconditions.checkArgument(this.serializer != null || this.deserializer != null);
            this.exactType = exactType;
            this.matchRawType = matchRawType;
            this.hierarchyType = hierarchyType;
        }
        
        @Override
        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
            final boolean matches = (this.exactType != null) ? (this.exactType.equals(type) || (this.matchRawType && this.exactType.getType() == type.getRawType())) : this.hierarchyType.isAssignableFrom(type.getRawType());
            return matches ? new TreeTypeAdapter<T>((JsonSerializer<T>)this.serializer, (JsonDeserializer<T>)this.deserializer, gson, type, this) : null;
        }
    }
    
    private final class GsonContextImpl implements JsonSerializationContext, JsonDeserializationContext
    {
        @Override
        public JsonElement serialize(final Object src) {
            return TreeTypeAdapter.this.gson.toJsonTree(src);
        }
        
        @Override
        public JsonElement serialize(final Object src, final Type typeOfSrc) {
            return TreeTypeAdapter.this.gson.toJsonTree(src, typeOfSrc);
        }
        
        @Override
        public <R> R deserialize(final JsonElement json, final Type typeOfT) throws JsonParseException {
            return TreeTypeAdapter.this.gson.fromJson(json, typeOfT);
        }
    }
}
