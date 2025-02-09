/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.utils;

import com.viaversion.viarewind.ViaRewind;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParser;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class ChatUtil {
    private static final Pattern UNUSED_COLOR_PATTERN = Pattern.compile("(?>(?>\u00a7[0-fk-or])*(\u00a7r|\\Z))|(?>(?>\u00a7[0-f])*(\u00a7[0-f]))");
    private static final ComponentRewriter<ClientboundPacketType> LEGACY_REWRITER = new ComponentRewriter<ClientboundPacketType>(null, ComponentRewriter.ReadType.JSON){

        @Override
        protected void handleTranslate(JsonObject object, String translate) {
            String text = Protocol1_13To1_12_2.MAPPINGS.getMojangTranslation().get(translate);
            if (text != null) {
                object.addProperty("translate", text);
            }
        }
    };

    public static String jsonToLegacy(String json) {
        if (json == null || json.equals("null") || json.isEmpty()) {
            return "";
        }
        try {
            return ChatUtil.jsonToLegacy(JsonParser.parseString(json));
        }
        catch (Exception e2) {
            ViaRewind.getPlatform().getLogger().log(Level.WARNING, "Could not convert component to legacy text: " + json, e2);
            return "";
        }
    }

    public static String jsonToLegacy(JsonElement component) {
        if (component.isJsonNull() || component.isJsonArray() && component.getAsJsonArray().isEmpty() || component.isJsonObject() && component.getAsJsonObject().isEmpty()) {
            return "";
        }
        if (component.isJsonPrimitive()) {
            return component.getAsString();
        }
        try {
            LEGACY_REWRITER.processText(component);
            String legacy = LegacyComponentSerializer.legacySection().serialize(ChatRewriter.HOVER_GSON_SERIALIZER.deserializeFromTree(component));
            while (legacy.startsWith("\u00a7f")) {
                legacy = legacy.substring(2);
            }
            return legacy;
        }
        catch (Exception ex2) {
            ViaRewind.getPlatform().getLogger().log(Level.WARNING, "Could not convert component to legacy text: " + component, ex2);
            return "";
        }
    }

    public static String legacyToJson(String legacy) {
        if (legacy == null) {
            return "";
        }
        return (String)GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

    public static String removeUnusedColor(String legacy, char last) {
        if (legacy == null) {
            return null;
        }
        legacy = UNUSED_COLOR_PATTERN.matcher(legacy).replaceAll("$1$2");
        StringBuilder builder = new StringBuilder();
        for (int i2 = 0; i2 < legacy.length(); ++i2) {
            char current = legacy.charAt(i2);
            if (current != '\u00a7' || i2 == legacy.length() - 1) {
                builder.append(current);
                continue;
            }
            if ((current = legacy.charAt(++i2)) == last) continue;
            builder.append('\u00a7').append(current);
            last = current;
        }
        return builder.toString();
    }
}

