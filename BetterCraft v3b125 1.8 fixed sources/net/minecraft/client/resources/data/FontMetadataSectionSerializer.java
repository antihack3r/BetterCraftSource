/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class FontMetadataSectionSerializer
extends BaseMetadataSectionSerializer<FontMetadataSection> {
    @Override
    public FontMetadataSection deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
        float[] afloat = new float[256];
        float[] afloat1 = new float[256];
        float[] afloat2 = new float[256];
        float f2 = 1.0f;
        float f1 = 0.0f;
        float f22 = 0.0f;
        if (jsonobject.has("characters")) {
            if (!jsonobject.get("characters").isJsonObject()) {
                throw new JsonParseException("Invalid font->characters: expected object, was " + jsonobject.get("characters"));
            }
            JsonObject jsonobject1 = jsonobject.getAsJsonObject("characters");
            if (jsonobject1.has("default")) {
                if (!jsonobject1.get("default").isJsonObject()) {
                    throw new JsonParseException("Invalid font->characters->default: expected object, was " + jsonobject1.get("default"));
                }
                JsonObject jsonobject2 = jsonobject1.getAsJsonObject("default");
                f2 = JsonUtils.getFloat(jsonobject2, "width", f2);
                Validate.inclusiveBetween(0.0, 3.4028234663852886E38, f2, "Invalid default width");
                f1 = JsonUtils.getFloat(jsonobject2, "spacing", f1);
                Validate.inclusiveBetween(0.0, 3.4028234663852886E38, f1, "Invalid default spacing");
                f22 = JsonUtils.getFloat(jsonobject2, "left", f1);
                Validate.inclusiveBetween(0.0, 3.4028234663852886E38, f22, "Invalid default left");
            }
            int i2 = 0;
            while (i2 < 256) {
                JsonElement jsonelement = jsonobject1.get(Integer.toString(i2));
                float f3 = f2;
                float f4 = f1;
                float f5 = f22;
                if (jsonelement != null) {
                    JsonObject jsonobject3 = JsonUtils.getJsonObject(jsonelement, "characters[" + i2 + "]");
                    f3 = JsonUtils.getFloat(jsonobject3, "width", f2);
                    Validate.inclusiveBetween(0.0, 3.4028234663852886E38, f3, "Invalid width");
                    f4 = JsonUtils.getFloat(jsonobject3, "spacing", f1);
                    Validate.inclusiveBetween(0.0, 3.4028234663852886E38, f4, "Invalid spacing");
                    f5 = JsonUtils.getFloat(jsonobject3, "left", f22);
                    Validate.inclusiveBetween(0.0, 3.4028234663852886E38, f5, "Invalid left");
                }
                afloat[i2] = f3;
                afloat1[i2] = f4;
                afloat2[i2] = f5;
                ++i2;
            }
        }
        return new FontMetadataSection(afloat, afloat2, afloat1);
    }

    @Override
    public String getSectionName() {
        return "font";
    }
}

