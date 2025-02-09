/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Json {
    public static float getFloat(JsonObject obj, String field, float def) {
        JsonElement jsonelement = obj.get(field);
        return jsonelement == null ? def : jsonelement.getAsFloat();
    }

    public static boolean getBoolean(JsonObject obj, String field, boolean def) {
        JsonElement jsonelement = obj.get(field);
        return jsonelement == null ? def : jsonelement.getAsBoolean();
    }

    public static String getString(JsonObject jsonObj, String field) {
        return Json.getString(jsonObj, field, null);
    }

    public static String getString(JsonObject jsonObj, String field, String def) {
        JsonElement jsonelement = jsonObj.get(field);
        return jsonelement == null ? def : jsonelement.getAsString();
    }

    public static float[] parseFloatArray(JsonElement jsonElement, int len) {
        return Json.parseFloatArray(jsonElement, len, null);
    }

    public static float[] parseFloatArray(JsonElement jsonElement, int len, float[] def) {
        if (jsonElement == null) {
            return def;
        }
        JsonArray jsonarray = jsonElement.getAsJsonArray();
        if (jsonarray.size() != len) {
            throw new JsonParseException("Wrong array length: " + jsonarray.size() + ", should be: " + len + ", array: " + jsonarray);
        }
        float[] afloat = new float[jsonarray.size()];
        int i2 = 0;
        while (i2 < afloat.length) {
            afloat[i2] = jsonarray.get(i2).getAsFloat();
            ++i2;
        }
        return afloat;
    }

    public static int[] parseIntArray(JsonElement jsonElement, int len) {
        return Json.parseIntArray(jsonElement, len, null);
    }

    public static int[] parseIntArray(JsonElement jsonElement, int len, int[] def) {
        if (jsonElement == null) {
            return def;
        }
        JsonArray jsonarray = jsonElement.getAsJsonArray();
        if (jsonarray.size() != len) {
            throw new JsonParseException("Wrong array length: " + jsonarray.size() + ", should be: " + len + ", array: " + jsonarray);
        }
        int[] aint = new int[jsonarray.size()];
        int i2 = 0;
        while (i2 < aint.length) {
            aint[i2] = jsonarray.get(i2).getAsInt();
            ++i2;
        }
        return aint;
    }
}

