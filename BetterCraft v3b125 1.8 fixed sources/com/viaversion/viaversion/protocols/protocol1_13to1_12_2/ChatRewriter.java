/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParser;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.legacyimpl.NBTLegacyHoverEventSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;

public final class ChatRewriter {
    public static final GsonComponentSerializer HOVER_GSON_SERIALIZER = GsonComponentSerializer.builder().emitLegacyHoverEvent().legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get()).build();

    public static JsonObject emptyComponent() {
        JsonObject object = new JsonObject();
        object.addProperty("text", "");
        return object;
    }

    public static String emptyComponentString() {
        return "{\"text\":\"\"}";
    }

    public static String legacyTextToJsonString(String message, boolean itemData) {
        Object component = LegacyComponentSerializer.legacySection().deserialize(message);
        if (itemData) {
            component = ((TextComponent.Builder)((TextComponent.Builder)Component.text().decoration(TextDecoration.ITALIC, false)).append((Component)component)).build();
        }
        return (String)GsonComponentSerializer.gson().serialize(component);
    }

    public static String legacyTextToJsonString(String legacyText) {
        return ChatRewriter.legacyTextToJsonString(legacyText, false);
    }

    public static JsonElement legacyTextToJson(String legacyText) {
        return JsonParser.parseString(ChatRewriter.legacyTextToJsonString(legacyText, false));
    }

    public static String jsonToLegacyText(String value) {
        try {
            Object component = HOVER_GSON_SERIALIZER.deserialize(value);
            return LegacyComponentSerializer.legacySection().serialize((Component)component);
        }
        catch (Exception e2) {
            Via.getPlatform().getLogger().warning("Error converting json text to legacy: " + value);
            e2.printStackTrace();
            return "";
        }
    }

    @Deprecated
    public static void processTranslate(JsonElement value) {
        Via.getManager().getProtocolManager().getProtocol(Protocol1_13To1_12_2.class).getComponentRewriter().processText(value);
    }
}

