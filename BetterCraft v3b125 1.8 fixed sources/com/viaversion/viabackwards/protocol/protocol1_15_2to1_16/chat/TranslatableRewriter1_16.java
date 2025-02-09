/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.chat;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.Protocol1_15_2To1_16;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.rewriter.ComponentRewriter;

public class TranslatableRewriter1_16
extends TranslatableRewriter<ClientboundPackets1_16> {
    private static final ChatColor[] COLORS = new ChatColor[]{new ChatColor("black", 0), new ChatColor("dark_blue", 170), new ChatColor("dark_green", 43520), new ChatColor("dark_aqua", 43690), new ChatColor("dark_red", 0xAA0000), new ChatColor("dark_purple", 0xAA00AA), new ChatColor("gold", 0xFFAA00), new ChatColor("gray", 0xAAAAAA), new ChatColor("dark_gray", 0x555555), new ChatColor("blue", 0x5555FF), new ChatColor("green", 0x55FF55), new ChatColor("aqua", 0x55FFFF), new ChatColor("red", 0xFF5555), new ChatColor("light_purple", 0xFF55FF), new ChatColor("yellow", 0xFFFF55), new ChatColor("white", 0xFFFFFF)};

    public TranslatableRewriter1_16(Protocol1_15_2To1_16 protocol) {
        super(protocol, ComponentRewriter.ReadType.JSON);
    }

    @Override
    public void processText(JsonElement value) {
        block8: {
            JsonObject hoverEvent;
            String colorName;
            super.processText(value);
            if (value == null || !value.isJsonObject()) {
                return;
            }
            JsonObject object = value.getAsJsonObject();
            JsonPrimitive color = object.getAsJsonPrimitive("color");
            if (color != null && !(colorName = color.getAsString()).isEmpty() && colorName.charAt(0) == '#') {
                int rgb = Integer.parseInt(colorName.substring(1), 16);
                String closestChatColor = this.getClosestChatColor(rgb);
                object.addProperty("color", closestChatColor);
            }
            if ((hoverEvent = object.getAsJsonObject("hoverEvent")) == null || !hoverEvent.has("contents")) {
                return;
            }
            try {
                JsonObject convertedObject;
                Component component = ChatRewriter.HOVER_GSON_SERIALIZER.deserializeFromTree(object);
                try {
                    convertedObject = (JsonObject)ChatRewriter.HOVER_GSON_SERIALIZER.serializeToTree(component);
                }
                catch (JsonParseException e2) {
                    JsonObject contents = hoverEvent.getAsJsonObject("contents");
                    if (contents.remove("tag") == null) {
                        throw e2;
                    }
                    component = ChatRewriter.HOVER_GSON_SERIALIZER.deserializeFromTree(object);
                    convertedObject = (JsonObject)ChatRewriter.HOVER_GSON_SERIALIZER.serializeToTree(component);
                }
                JsonObject processedHoverEvent = convertedObject.getAsJsonObject("hoverEvent");
                processedHoverEvent.remove("contents");
                object.add("hoverEvent", processedHoverEvent);
            }
            catch (Exception e3) {
                if (Via.getConfig().isSuppressConversionWarnings()) break block8;
                ViaBackwards.getPlatform().getLogger().severe("Error converting hover event component: " + object);
                e3.printStackTrace();
            }
        }
    }

    private String getClosestChatColor(int rgb) {
        int r2 = rgb >> 16 & 0xFF;
        int g2 = rgb >> 8 & 0xFF;
        int b2 = rgb & 0xFF;
        ChatColor closest = null;
        int smallestDiff = 0;
        for (ChatColor color : COLORS) {
            if (color.rgb == rgb) {
                return color.colorName;
            }
            int rAverage = (color.r + r2) / 2;
            int rDiff = color.r - r2;
            int gDiff = color.g - g2;
            int bDiff = color.b - b2;
            int diff = (2 + (rAverage >> 8)) * rDiff * rDiff + 4 * gDiff * gDiff + (2 + (255 - rAverage >> 8)) * bDiff * bDiff;
            if (closest != null && diff >= smallestDiff) continue;
            closest = color;
            smallestDiff = diff;
        }
        return closest.colorName;
    }

    private static final class ChatColor {
        private final String colorName;
        private final int rgb;
        private final int r;
        private final int g;
        private final int b;

        ChatColor(String colorName, int rgb) {
            this.colorName = colorName;
            this.rgb = rgb;
            this.r = rgb >> 16 & 0xFF;
            this.g = rgb >> 8 & 0xFF;
            this.b = rgb & 0xFF;
        }
    }
}

