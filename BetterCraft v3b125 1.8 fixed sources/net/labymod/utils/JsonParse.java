/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonParse {
    private static JsonParser jsonParser = new JsonParser();

    public static String parse(String input, String ... value) {
        JsonElement data = jsonParser.parse(input);
        JsonObject rootobj = data.getAsJsonObject();
        JsonObject currentObject = null;
        String output = "";
        int i2 = 0;
        while (i2 < value.length) {
            if (currentObject == null) {
                if (value.length == 1) {
                    output = rootobj.get(value[i2]).getAsString();
                } else {
                    currentObject = rootobj.getAsJsonObject(value[i2]);
                }
            } else if (i2 == value.length - 1) {
                output = currentObject.get(value[i2]).getAsString();
            } else {
                currentObject = currentObject.getAsJsonObject(value[i2]);
            }
            ++i2;
        }
        return output;
    }

    public static JsonElement parse(String input) {
        return jsonParser.parse(input);
    }
}

