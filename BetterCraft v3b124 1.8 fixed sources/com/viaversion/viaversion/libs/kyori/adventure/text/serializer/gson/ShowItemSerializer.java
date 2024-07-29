/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.api.BinaryTagHolder;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.SerializerFactory;
import java.io.IOException;

final class ShowItemSerializer
extends TypeAdapter<HoverEvent.ShowItem> {
    private final Gson gson;

    static TypeAdapter<HoverEvent.ShowItem> create(Gson gson) {
        return new ShowItemSerializer(gson).nullSafe();
    }

    private ShowItemSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public HoverEvent.ShowItem read(JsonReader in2) throws IOException {
        in2.beginObject();
        Key key = null;
        int count = 1;
        BinaryTagHolder nbt = null;
        while (in2.hasNext()) {
            String fieldName = in2.nextName();
            if (fieldName.equals("id")) {
                key = (Key)this.gson.fromJson(in2, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals("count")) {
                count = in2.nextInt();
                continue;
            }
            if (fieldName.equals("tag")) {
                JsonToken token = in2.peek();
                if (token == JsonToken.STRING || token == JsonToken.NUMBER) {
                    nbt = BinaryTagHolder.binaryTagHolder(in2.nextString());
                    continue;
                }
                if (token == JsonToken.BOOLEAN) {
                    nbt = BinaryTagHolder.binaryTagHolder(String.valueOf(in2.nextBoolean()));
                    continue;
                }
                if (token == JsonToken.NULL) {
                    in2.nextNull();
                    continue;
                }
                throw new JsonParseException("Expected tag to be a string");
            }
            in2.skipValue();
        }
        if (key == null) {
            throw new JsonParseException("Not sure how to deserialize show_item hover event");
        }
        in2.endObject();
        return HoverEvent.ShowItem.showItem(key, count, nbt);
    }

    @Override
    public void write(JsonWriter out, HoverEvent.ShowItem value) throws IOException {
        BinaryTagHolder nbt;
        out.beginObject();
        out.name("id");
        this.gson.toJson((Object)value.item(), SerializerFactory.KEY_TYPE, out);
        int count = value.count();
        if (count != 1) {
            out.name("count");
            out.value(count);
        }
        if ((nbt = value.nbt()) != null) {
            out.name("tag");
            out.value(nbt.string());
        }
        out.endObject();
    }
}

