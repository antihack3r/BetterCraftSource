/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api;

import com.google.gson.JsonElement;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.labymod.api.EventManager;
import net.labymod.api.LabyModAddon;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.ClientConnection;
import net.labymod.labyconnect.LabyConnect;
import net.labymod.labyconnect.packets.PacketAddonDevelopment;
import net.labymod.labyconnect.packets.PacketAddonMessage;
import net.labymod.main.LabyMod;
import net.labymod.servermanager.Server;
import net.labymod.support.util.Debug;
import net.labymod.user.UserManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModTextureUtils;
import net.labymod.utils.ServerData;
import net.labymod.utils.texture.DynamicTextureManager;
import net.lenni0451.eventapi.manager.ASMEventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;

public class LabyModAPI {
    public static final String LABYMOD_API_CHANNEL = "LMC";
    private final LabyMod labyMod;
    private boolean crosshairHidden;

    public LabyModAPI(LabyMod labyMod) {
        this.labyMod = labyMod;
    }

    public void sendJsonMessageToServer(String messageKey, JsonElement message) {
        if (LabyModCore.getMinecraft().getPlayer() == null || Minecraft.getMinecraft().isSingleplayer()) {
            return;
        }
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeString(messageKey);
        packetBuffer.writeString(message.toString());
        Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "[OUT] [LMC] " + message.toString());
        LabyModCore.getMinecraft().sendPluginMessage(LABYMOD_API_CHANNEL, packetBuffer);
    }

    public void registerServerSupport(LabyModAddon labyModAddon, Server server) {
        this.labyMod.getServerManager().registerServerSupport(labyModAddon, server);
    }

    public void sendPluginMessage(String channelName, PacketBuffer packetBuffer) {
        LabyModCore.getMinecraft().sendPluginMessage(channelName, packetBuffer);
    }

    public void sendAddonMessage(String key, byte[] data) {
        LabyConnect chatClient = LabyMod.getInstance().getLabyConnect();
        ClientConnection clientConnection = chatClient.getClientConnection();
        if (clientConnection != null && chatClient.isOnline()) {
            clientConnection.sendPacket(new PacketAddonMessage(key, data));
        }
    }

    public void sendAddonMessage(String key, String json) {
        LabyConnect chatClient = LabyMod.getInstance().getLabyConnect();
        ClientConnection clientConnection = chatClient.getClientConnection();
        if (clientConnection != null && chatClient.isOnline()) {
            clientConnection.sendPacket(new PacketAddonMessage(key, json));
        }
    }

    public DrawUtils getDrawUtils() {
        return this.labyMod.getDrawUtils();
    }

    public ServerData getCurrentServer() {
        return this.labyMod.getCurrentServerData();
    }

    public boolean isCurrentlyPlayingOn(String address) {
        return this.labyMod.getCurrentServerData() != null && this.labyMod.getCurrentServerData().getIp().toLowerCase().contains(address.toLowerCase());
    }

    public boolean isIngame() {
        return this.labyMod.isInGame();
    }

    public boolean hasGameFocus() {
        return LabyModCore.getMinecraft().hasInGameFocus();
    }

    public boolean isCurrentScreenNull() {
        return LabyModCore.getMinecraft().isCurrentScreenNull();
    }

    public boolean isMinecraftChatOpen() {
        return LabyModCore.getMinecraft().isMinecraftChatOpen();
    }

    public UserManager getUserManager() {
        return this.labyMod.getUserManager();
    }

    public DynamicTextureManager getDynamicTextureManager() {
        return this.labyMod.getDynamicTextureManager();
    }

    @Deprecated
    public ModTextureUtils getModTextureUtils() {
        return ModTextureUtils.INSTANCE;
    }

    public LabyConnect getLabyModChatClient() {
        return this.labyMod.getLabyConnect();
    }

    public UUID getPlayerUUID() {
        return this.labyMod.getPlayerUUID();
    }

    public String getPlayerUsername() {
        return this.labyMod.getPlayerName();
    }

    public void displayMessageInChat(String message) {
        this.labyMod.displayMessageInChat(message);
    }

    public void connectToServer(String address) {
        this.labyMod.connectToServer(address);
    }

    public void updateCurrentGamemode(String gamemodeName) {
        this.labyMod.getLabyConnect().updatePlayingOnServerState(gamemodeName);
    }

    public void sendAddonDevelopmentPacket(PacketAddonDevelopment packetAddonDevelopment) {
        this.labyMod.getLabyConnect().getClientConnection().sendPacket(packetAddonDevelopment);
    }

    public void registerForgeListener(Object target) {
        ASMEventManager.register(target);
    }

    public EventManager getEventManager() {
        return this.labyMod.getEventManager();
    }

    public boolean isCrosshairHidden() {
        return this.crosshairHidden;
    }

    public void setCrosshairHidden(boolean crosshairHidden) {
        this.crosshairHidden = crosshairHidden;
    }
}

