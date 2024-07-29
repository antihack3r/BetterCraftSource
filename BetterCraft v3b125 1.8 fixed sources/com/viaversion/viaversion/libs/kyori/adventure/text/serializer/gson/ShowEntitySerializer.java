/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.SerializerFactory;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

final class ShowEntitySerializer
extends TypeAdapter<HoverEvent.ShowEntity> {
    private final Gson gson;

    static TypeAdapter<HoverEvent.ShowEntity> create(Gson gson) {
        return new ShowEntitySerializer(gson).nullSafe();
    }

    private ShowEntitySerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public HoverEvent.ShowEntity read(JsonReader in2) throws IOException {
        in2.beginObject();
        Key type = null;
        UUID id2 = null;
        Component name = null;
        while (in2.hasNext()) {
            String fieldName = in2.nextName();
            if (fieldName.equals("type")) {
                type = (Key)this.gson.fromJson(in2, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals("id")) {
                id2 = UUID.fromString(in2.nextString());
                continue;
            }
            if (fieldName.equals("name")) {
                name = (Component)this.gson.fromJson(in2, SerializerFactory.COMPONENT_TYPE);
                continue;
            }
            in2.skipValue();
        }
        if (type == null || id2 == null) {
            throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
        }
        in2.endObject();
        return HoverEvent.ShowEntity.showEntity(type, id2, name);
    }

    @Override
    public void write(JsonWriter out, HoverEvent.ShowEntity value) throws IOException {
        out.beginObject();
        out.name("type");
        this.gson.toJson((Object)value.type(), SerializerFactory.KEY_TYPE, out);
        out.name("id");
        out.value(value.id().toString());
        @Nullable Component name = value.name();
        if (name != null) {
            out.name("name");
            this.gson.toJson((Object)name, SerializerFactory.COMPONENT_TYPE, out);
        }
        out.endObject();
    }
}

