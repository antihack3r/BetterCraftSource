/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.servermanager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.PluginMessageEvent;
import net.labymod.api.events.TabListEvent;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.servermanager.ChatDisplayAction;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.PacketBuffer;

public abstract class Server {
    protected JsonParser jsonParser = new JsonParser();
    private String name;
    private String[] addressNames;
    protected int kills;
    private JsonObject config;
    private LabyModAddon bindedAddon;

    public Server(String name, String ... addressNames) {
        this.name = name;
        this.addressNames = addressNames;
        LabyMod.getInstance().getEventManager().register(new TabListEvent(){

            @Override
            public void onUpdate(TabListEvent.Type type, String formattedText, String unformattedText) {
                if (!LabyMod.getInstance().getServerManager().isServer(Server.this.getClass())) {
                    return;
                }
                try {
                    Server.this.handleTabInfoMessage(type, formattedText, unformattedText);
                }
                catch (Throwable e2) {
                    e2.printStackTrace();
                }
            }
        });
        LabyMod.getInstance().getEventManager().register(new PluginMessageEvent(){

            @Override
            public void receiveMessage(String channelName, PacketBuffer packetBuffer) {
                try {
                    Server.this.handlePluginMessage(channelName, packetBuffer);
                }
                catch (Throwable e2) {
                    e2.printStackTrace();
                }
            }
        });
        Map<String, JsonObject> list = LabyMod.getInstance().getServerManager().getServerConfigManager().getSettings().getServerConfigs();
        if (list.keySet().contains(name)) {
            this.config = list.get(name);
        } else {
            this.config = new JsonObject();
            LabyMod.getInstance().getServerManager().getConfig().getServerConfigs().put(name, this.config);
            LabyMod.getInstance().getServerManager().getServerConfigManager().save();
        }
        this.initConfig(this.config);
        this.loadConfig();
    }

    public abstract void onJoin(ServerData var1);

    public abstract ChatDisplayAction handleChatMessage(String var1, String var2) throws Exception;

    public abstract void handlePluginMessage(String var1, PacketBuffer var2) throws Exception;

    public abstract void handleTabInfoMessage(TabListEvent.Type var1, String var2, String var3) throws Exception;

    public void reset() {
        this.kills = 0;
    }

    public void draw() {
    }

    public void addModuleLines(List<DisplayLine> lines) {
    }

    public void loopSecond() {
    }

    public boolean isAllowed(Permissions.Permission permission) {
        return permission.isDefaultEnabled();
    }

    @Deprecated
    protected void initConfig(JsonObject config) {
    }

    public void loadConfig() {
    }

    public void saveConfig() {
        LabyMod.getInstance().getServerManager().getConfig().getServerConfigs().put(this.name, this.config);
        LabyMod.getInstance().getServerManager().getServerConfigManager().save();
    }

    public abstract void fillSubSettings(List<SettingsElement> var1);

    public void bindAddon(LabyModAddon labyModAddon) {
        this.bindedAddon = labyModAddon;
        if (this.bindedAddon != null) {
            this.fillSubSettings(this.bindedAddon.getSubSettings());
        }
    }

    public boolean getBooleanAttribute(String attribute, boolean defaultValue) {
        if (!this.config.has(attribute)) {
            this.config.addProperty(attribute, defaultValue);
        }
        return this.config.get(attribute).getAsBoolean();
    }

    public void setBooleanAttribute(String attribute, boolean value) {
        this.config.addProperty(attribute, value);
    }

    public void sendPluginMessage(String channelName, Consumer<PacketBuffer> packetBufferConsumer) {
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBufferConsumer.accept(packetBuffer);
        LabyModCore.getMinecraft().sendPluginMessage(channelName, packetBuffer);
    }

    public String getName() {
        return this.name;
    }

    public String[] getAddressNames() {
        return this.addressNames;
    }

    public JsonObject getConfig() {
        return this.config;
    }

    public LabyModAddon getBindedAddon() {
        return this.bindedAddon;
    }

    public static class DisplayLine {
        private String key;

        public String getKey() {
            return this.key;
        }
    }
}

