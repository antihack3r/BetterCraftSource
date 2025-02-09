// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import java.util.Locale;
import javax.annotation.Nullable;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
import java.util.Map;
import com.google.common.collect.Maps;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;

public class EnumTypeAdapterFactory implements TypeAdapterFactory
{
    @Nullable
    @Override
    public <T> TypeAdapter<T> create(final Gson p_create_1_, final TypeToken<T> p_create_2_) {
        final Class<T> oclass = (Class<T>)p_create_2_.getRawType();
        if (!oclass.isEnum()) {
            return null;
        }
        final Map<String, T> map = (Map<String, T>)Maps.newHashMap();
        T[] enumConstants;
        for (int length = (enumConstants = oclass.getEnumConstants()).length, i = 0; i < length; ++i) {
            final T t = enumConstants[i];
            map.put(this.getName(t), t);
        }
        return new TypeAdapter<T>() {
            @Override
            public void write(final JsonWriter p_write_1_, final T p_write_2_) throws IOException {
                if (p_write_2_ == null) {
                    p_write_1_.nullValue();
                }
                else {
                    p_write_1_.value(EnumTypeAdapterFactory.this.getName(p_write_2_));
                }
            }
            
            @Nullable
            @Override
            public T read(final JsonReader p_read_1_) throws IOException {
                if (p_read_1_.peek() == JsonToken.NULL) {
                    p_read_1_.nextNull();
                    return null;
                }
                return map.get(p_read_1_.nextString());
            }
        };
    }
    
    private String getName(final Object objectIn) {
        return (objectIn instanceof Enum) ? ((Enum)objectIn).name().toLowerCase(Locale.ROOT) : objectIn.toString().toLowerCase(Locale.ROOT);
    }
}
