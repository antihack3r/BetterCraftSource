/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import net.labymod.api.events.MessageModifyChatEvent;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.utils.ModUtils;
import net.labymod.utils.manager.ConfigManager;

public class TagManager {
    private static ConfigManager<TagConfig> configManager;
    private static JsonParser jsonParser;

    static {
        jsonParser = new JsonParser();
    }

    public static void init() {
        configManager = new ConfigManager<TagConfig>(new File("LabyMod/", "tags.json"), TagConfig.class);
    }

    public static void save() {
        configManager.save();
    }

    public static ConfigManager<TagConfig> getConfigManager() {
        return configManager;
    }

    public static String getTaggedMessage(String message) {
        if (!Permissions.isAllowed(Permissions.Permission.TAGS)) {
            return null;
        }
        try {
            for (Map.Entry<String, String> tagEntry : configManager.getSettings().getTags().entrySet()) {
                if (!message.toLowerCase().contains(tagEntry.getKey().toLowerCase())) continue;
                String regex = "(?i)" + tagEntry.getKey();
                Pattern pattern = Pattern.compile(regex);
                String replacement = ModUtils.translateAlternateColorCodes('&', tagEntry.getValue());
                if ((message.startsWith("[") || message.startsWith("{")) && (message.endsWith("]") || message.endsWith("}"))) {
                    JsonElement element = jsonParser.parse(message);
                    TagManager.replaceObject(element, pattern, replacement);
                    return element.toString();
                }
                return pattern.matcher(message).replaceAll(replacement);
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        return null;
    }

    private static void replaceObject(JsonElement element, Pattern pattern, String replacement) {
        if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            int i2 = 0;
            while (i2 < jsonArray.size()) {
                TagManager.replaceObject(jsonArray.get(i2), pattern, replacement);
                ++i2;
            }
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("extra")) {
                TagManager.replaceObject(object.get("extra"), pattern, replacement);
            }
            if (object.has("with")) {
                TagManager.replaceObject(object.get("with"), pattern, replacement);
            }
            if (object.has("text")) {
                String text = object.get("text").getAsString();
                text = pattern.matcher(text).replaceAll(replacement);
                object.addProperty("text", text);
            }
        }
    }

    public static Object tagComponent(Object chatComponent) {
        for (MessageModifyChatEvent a2 : LabyMod.getInstance().getEventManager().getMessageModifyChat()) {
            chatComponent = a2.onModifyChatMessage(chatComponent);
        }
        if (TagManager.getConfigManager() == null || TagManager.getConfigManager().getSettings().getTags().isEmpty() || !Permissions.isAllowed(Permissions.Permission.TAGS)) {
            return chatComponent;
        }
        return LabyModCore.getMinecraft().getTaggedChatComponent(chatComponent);
    }

    public static class TagConfig {
        private Map<String, String> tags = new HashMap<String, String>();

        public Map<String, String> getTags() {
            return this.tags;
        }
    }
}

