/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.listeners;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.File;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.PluginMessageEvent;
import net.labymod.api.permissions.OldPluginMessage;
import net.labymod.api.permissions.Permissions;
import net.labymod.api.protocol.liquid.FixedLiquidBucketProtocol;
import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.gui.GuiShaderSelection;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class PluginMessageListener
implements PluginMessageEvent {
    private static final JsonParser jsonParser = new JsonParser();
    private long lastMCBrandMessage;
    private JsonArray mods;

    public PluginMessageListener() {
        block7: {
            this.lastMCBrandMessage = -1L;
            this.mods = new JsonArray();
            try {
                File modsFolder = new File("mods");
                if (!modsFolder.exists()) {
                    return;
                }
                File[] list = modsFolder.listFiles();
                if (list == null) break block7;
                File[] fileArray = list;
                int n2 = list.length;
                int n3 = 0;
                while (n3 < n2) {
                    File file = fileArray[n3];
                    if (file != null && !file.isDirectory() && file.getName().endsWith(".jar")) {
                        try {
                            JsonObject entry = new JsonObject();
                            entry.addProperty("hash", "sha256:" + Files.hash(file, Hashing.sha256()).toString());
                            entry.addProperty("name", file.getName());
                            this.mods.add(entry);
                        }
                        catch (Exception error) {
                            error.printStackTrace();
                        }
                    }
                    ++n3;
                }
            }
            catch (Exception error2) {
                error2.printStackTrace();
            }
        }
    }

    @Override
    public void receiveMessage(String channelName, PacketBuffer packetBuffer) {
        if (!channelName.equals("CCP") && !channelName.equals("LMC")) {
            Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "[IN] " + channelName);
        }
        try {
            boolean brandMessage;
            OldPluginMessage.handlePluginMessage(channelName, packetBuffer);
            boolean updatedPermissions = false;
            if (channelName.equals("LMC")) {
                String messageKey = "unknown";
                String messageContent = "couldn't read the content";
                try {
                    if (packetBuffer.readableBytes() <= 0) {
                        throw new Exception("There is no message key");
                    }
                    messageKey = this.readStringFromBuffer(Short.MAX_VALUE, packetBuffer);
                    if (packetBuffer.readableBytes() <= 0) {
                        throw new Exception("There is no message content");
                    }
                    messageContent = this.readStringFromBuffer(Short.MAX_VALUE, packetBuffer);
                    if (messageKey.equals("PERMISSIONS")) {
                        updatedPermissions = true;
                    }
                    JsonElement parsedServerMessage = jsonParser.parse(messageContent);
                    LabyMod.getInstance().getEventManager().callServerMessage(messageKey, parsedServerMessage);
                    Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "[IN] [LMC] " + messageKey + ": " + messageContent);
                }
                catch (Exception ex2) {
                    Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "Failed parsing JSON message \"" + messageContent + "\" (key: " + messageKey + ")");
                    ex2.printStackTrace();
                }
            }
            if (channelName.equals("LABYMOD") || updatedPermissions) {
                Permissions.getPermissionNotifyRenderer().checkChangedPermissions();
            }
            boolean bl2 = brandMessage = channelName.equals("MC|Brand") && this.lastMCBrandMessage + 1000L < System.currentTimeMillis();
            if (brandMessage) {
                this.lastMCBrandMessage = System.currentTimeMillis();
            }
            if (brandMessage && LabyMod.getSettings().improvedLavaFixedGhostBlocks && LabyMod.getInstance().getCurrentServerData() != null && Permissions.isAllowed(Permissions.Permission.IMPROVED_LAVA)) {
                FixedLiquidBucketProtocol.handleBucketAction(FixedLiquidBucketProtocol.Action.ENABLE, 0, 0, 0);
            }
            if (brandMessage) {
                LabyMod.getInstance().getChunkCachingProtocol().disable(null, true);
                JsonArray addons = new JsonArray();
                for (LabyModAddon addonInfo : AddonLoader.getAddons()) {
                    if (addonInfo.about == null || addonInfo.about.uuid == null) continue;
                    JsonObject entry = new JsonObject();
                    entry.addProperty("uuid", addonInfo.about.uuid.toString());
                    entry.addProperty("name", addonInfo.about.name);
                    addons.add(entry);
                }
                JsonObject ccp = new JsonObject();
                ccp.addProperty("enabled", LabyMod.getSettings().chunkCaching);
                ccp.addProperty("version", 2);
                JsonObject shadow = new JsonObject();
                shadow.addProperty("enabled", true);
                shadow.addProperty("version", 1);
                JsonObject obj = new JsonObject();
                obj.addProperty("version", "3.6.6");
                obj.add("ccp", ccp);
                obj.add("shadow", shadow);
                obj.add("addons", addons);
                if (LabyModCoreMod.isForge()) {
                    obj.add("mods", this.mods);
                }
                LabyMod.getInstance().getLabyModAPI().sendJsonMessageToServer("INFO", obj);
                if (LabyMod.getSettings().loadedShader != null && LabyMod.getSettings().betterShaderSelection) {
                    try {
                        GuiShaderSelection.loadShader(new ResourceLocation(LabyMod.getSettings().loadedShader));
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
            if (brandMessage) {
                Minecraft.getMinecraft().isSingleplayer();
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    public String readStringFromBuffer(int maxLength, PacketBuffer packetBuffer) {
        int i2 = this.readVarIntFromBuffer(packetBuffer);
        if (i2 > maxLength * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i2 + " > " + maxLength * 4 + ")");
        }
        if (i2 < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }
        ByteBuf byteBuf = packetBuffer.readBytes(i2);
        byte[] bytes = null;
        if (byteBuf.hasArray()) {
            bytes = byteBuf.array();
        } else {
            bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
        }
        String s2 = new String(bytes, Charsets.UTF_8);
        if (s2.length() > maxLength) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + i2 + " > " + maxLength + ")");
        }
        return s2;
    }

    public int readVarIntFromBuffer(PacketBuffer packetBuffer) {
        byte b0;
        int i2 = 0;
        int j2 = 0;
        do {
            b0 = packetBuffer.readByte();
            i2 |= (b0 & 0x7F) << j2++ * 7;
            if (j2 <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while ((b0 & 0x80) == 128);
        return i2;
    }
}

