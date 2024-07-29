/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson;

import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonNull;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;

public enum LongSerializationPolicy {
    DEFAULT{

        @Override
        public JsonElement serialize(Long value) {
            if (value == null) {
                return JsonNull.INSTANCE;
            }
            return new JsonPrimitive(value);
        }
    }
    ,
    STRING{

        @Override
        public JsonElement serialize(Long value) {
            if (value == null) {
                return JsonNull.INSTANCE;
            }
            return new JsonPrimitive(value.toString());
        }
    };


    public abstract JsonElement serialize(Long var1);
}

