/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.JsonUtils;

public class TextureMetadataSectionSerializer
extends BaseMetadataSectionSerializer<TextureMetadataSection> {
    @Override
    public TextureMetadataSection deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
        boolean flag = JsonUtils.getBoolean(jsonobject, "blur", false);
        boolean flag1 = JsonUtils.getBoolean(jsonobject, "clamp", false);
        ArrayList<Integer> list = Lists.newArrayList();
        if (jsonobject.has("mipmaps")) {
            try {
                JsonArray jsonarray = jsonobject.getAsJsonArray("mipmaps");
                int i2 = 0;
                while (i2 < jsonarray.size()) {
                    JsonElement jsonelement = jsonarray.get(i2);
                    if (jsonelement.isJsonPrimitive()) {
                        try {
                            list.add(jsonelement.getAsInt());
                        }
                        catch (NumberFormatException numberformatexception) {
                            throw new JsonParseException("Invalid texture->mipmap->" + i2 + ": expected number, was " + jsonelement, numberformatexception);
                        }
                    } else if (jsonelement.isJsonObject()) {
                        throw new JsonParseException("Invalid texture->mipmap->" + i2 + ": expected number, was " + jsonelement);
                    }
                    ++i2;
                }
            }
            catch (ClassCastException classcastexception) {
                throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + jsonobject.get("mipmaps"), classcastexception);
            }
        }
        return new TextureMetadataSection(flag, flag1, list);
    }

    @Override
    public String getSectionName() {
        return "texture";
    }
}

