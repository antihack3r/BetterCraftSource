/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.util;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.GsonBuilder;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GsonUtil {
    private static final Gson GSON = new GsonBuilder().create();

    public static Gson getGson() {
        return GSON;
    }

    public static String toSortedString(@Nullable JsonElement element, @Nullable Comparator<String> comparator) {
        if (element == null) {
            return null;
        }
        if (comparator != null) {
            return GsonUtil.sort(element, comparator).toString();
        }
        return GsonUtil.sort(element, Comparator.naturalOrder()).toString();
    }

    public static JsonElement sort(@Nullable JsonElement element, Comparator<String> comparator) {
        if (element == null) {
            return null;
        }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i2 = 0; i2 < array.size(); ++i2) {
                array.set(i2, GsonUtil.sort(array.get(i2), comparator));
            }
            return array;
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            JsonObject sorted = new JsonObject();
            ArrayList<String> keys = new ArrayList<String>(object.keySet());
            keys.sort(comparator);
            for (String key : keys) {
                sorted.add(key, GsonUtil.sort(object.get(key), comparator));
            }
            return sorted;
        }
        return element;
    }
}

