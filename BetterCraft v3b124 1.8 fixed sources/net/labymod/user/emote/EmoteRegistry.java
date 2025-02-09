/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.nzxtercode.bettercraft.client.events.ClientTickEvent;
import net.labymod.api.EventManager;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.packets.PacketActionPlayResponse;
import net.labymod.labyconnect.packets.PacketActionRequest;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.user.UserManager;
import net.labymod.user.emote.EmoteLoader;
import net.labymod.user.emote.EmoteRenderer;
import net.labymod.user.emote.keys.provider.EmoteProvider;
import net.labymod.user.emote.keys.provider.KeyFrameStorage;
import net.labymod.user.emote.keys.provider.StoredEmote;
import net.labymod.user.gui.EmoteSelectorGui;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.lenni0451.eventapi.events.EventTarget;
import net.minecraft.client.entity.AbstractClientPlayer;

public class EmoteRegistry
implements Consumer<Object>,
ServerMessageEvent {
    private final EmoteSelectorGui emoteSelectorGui;
    protected Map<Short, KeyFrameStorage> emoteSources = new HashMap<Short, KeyFrameStorage>();
    protected Map<UUID, EmoteRenderer> playingEmotes = new ConcurrentHashMap<UUID, EmoteRenderer>();
    private boolean cleanPlayingMap = false;

    public EmoteRegistry() {
        this.emoteSelectorGui = new EmoteSelectorGui();
    }

    public void init() {
        EventManager eventManager = LabyMod.getInstance().getEventManager();
        eventManager.registerOnIncomingPacket(this);
        eventManager.register(this);
        new EmoteLoader(this).start();
    }

    @Override
    public void accept(Object packet) {
        if (!LabyMod.getSettings().emotes) {
            return;
        }
        UUID uuid = LabyModCore.getMinecraft().isEmotePacket(packet);
        if (uuid != null) {
            LabyMod.getInstance().getLabyConnect().getClientConnection().sendPacket(new PacketActionRequest(uuid));
        }
    }

    @Override
    public void onServerMessage(String messageKey, JsonElement serverMessage) {
        if (!messageKey.equals("emote_api")) {
            return;
        }
        JsonArray array = serverMessage.getAsJsonArray();
        int i2 = 0;
        while (i2 < array.size()) {
            JsonObject object = array.get(i2).getAsJsonObject();
            if (object.has("uuid") && object.has("emote_id")) {
                UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                short emoteId = object.get("emote_id").getAsShort();
                if (uuid.getLeastSignificantBits() == 0L) {
                    this.handleEmote(uuid, emoteId);
                }
            }
            ++i2;
        }
    }

    @EventTarget
    public void handleEvent(ClientTickEvent event) {
        if (!this.cleanPlayingMap) {
            return;
        }
        this.cleanPlayingMap = false;
        for (EmoteRenderer renderer : this.playingEmotes.values()) {
            if (!renderer.isAborted()) continue;
            this.abortEmote(renderer.getUuid(), false);
        }
    }

    public EmoteRenderer getEmoteRendererFor(AbstractClientPlayer abstractClientPlayer) {
        UUID uuid;
        UserManager userManager = LabyMod.getInstance().getUserManager();
        if ((userManager.isWhitelisted(uuid = abstractClientPlayer.getUniqueID()) || uuid.getLeastSignificantBits() == 0L) && !abstractClientPlayer.isInvisible()) {
            return LabyMod.getInstance().getEmoteRegistry().getPlayingEmotes().get(uuid);
        }
        return null;
    }

    public void playEmote(final short emoteId) {
        if (LabyModCore.getMinecraft().getPlayer() == null || !LabyMod.getSettings().emotes) {
            Debug.log(Debug.EnumDebugMode.EMOTE, "Playing emote canceled because emotes are disabled or player is null.");
            return;
        }
        if (!LabyMod.getInstance().getLabyConnect().isOnline()) {
            LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl('c')) + LabyMod.getMessage("emote_error_not_connected", new Object[0]));
            Debug.log(Debug.EnumDebugMode.EMOTE, "Playing emote canceled because player is not connected to labymod chat!");
            return;
        }
        final UUID uuid = LabyModCore.getMinecraft().getPlayer().getUniqueID();
        final byte[] emoteIdBytes = this.shortToBytes(emoteId);
        Debug.log(Debug.EnumDebugMode.EMOTE, "Request labymod chat to play emote id " + emoteId);
        LabyMod.getInstance().getUserManager().requestAction((short)1, emoteIdBytes, new FutureCallback<PacketActionPlayResponse>(){

            @Override
            public void onSuccess(PacketActionPlayResponse responsePacket) {
                if (responsePacket == null || !responsePacket.isAllowed()) {
                    Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, responsePacket == null ? "Response timed out" : "Response packet: " + responsePacket.getReason());
                    if (emoteId != -1) {
                        String message = null;
                        message = responsePacket == null ? LabyMod.getMessage("emote_error_no_response", new Object[0]) : (responsePacket.getReason().startsWith("illegal emote use") ? LabyMod.getMessage("emote_error_illegal_emote", new Object[0]) : responsePacket.getReason());
                        LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl('c')) + message);
                    }
                    return;
                }
                if (LabyModCore.getMinecraft().getPlayer() != null) {
                    LabyMod.getInstance().getUserManager().broadcastBitUpdate(emoteId != -1);
                }
                EmoteRegistry.this.handleEmote(uuid, emoteIdBytes);
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
                LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl('c')) + "Error: " + throwable.getMessage());
            }
        });
    }

    public void abortEmote(UUID uuid, boolean forced) {
        this.playingEmotes.remove(uuid);
        UUID clientUUID = LabyMod.getInstance().getPlayerUUID();
        if (uuid.equals(clientUUID)) {
            if (forced) {
                this.playEmote((short)-1);
            } else {
                LabyMod.getInstance().getUserManager().broadcastBitUpdate(false);
            }
        }
    }

    public EmoteRenderer handleEmote(UUID player, byte[] data) {
        if (data.length < 2) {
            return null;
        }
        short emoteId = this.bytesToShort(data);
        return this.handleEmote(player, emoteId);
    }

    public EmoteRenderer handleEmote(UUID player, short emoteId) {
        if (!LabyMod.getSettings().emotes) {
            return null;
        }
        if (emoteId == -1) {
            EmoteRenderer emoteRenderer = this.playingEmotes.get(player);
            if (emoteRenderer != null) {
                emoteRenderer.abort();
            }
            return null;
        }
        if (!player.equals(LabyMod.getInstance().getPlayerUUID()) && this.playingEmotes.containsKey(player)) {
            return null;
        }
        Debug.log(Debug.EnumDebugMode.EMOTE, String.valueOf(player.toString()) + " started playing emote " + emoteId);
        if (!this.emoteSources.containsKey(emoteId)) {
            if (player.equals(LabyMod.getInstance().getPlayerUUID())) {
                LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl('c')) + "Invalid emote id: " + emoteId + ". Maybe restart your game?");
            }
            Debug.log(Debug.EnumDebugMode.EMOTE, "Invalid emote id: " + emoteId);
            return null;
        }
        KeyFrameStorage keyFrameStorage = this.emoteSources.get(emoteId);
        EmoteRenderer prevRenderer = this.playingEmotes.get(player);
        if (keyFrameStorage != null) {
            if (prevRenderer != null && !prevRenderer.isVisible()) {
                prevRenderer = null;
            }
            try {
                EmoteRenderer emoteRenderer2 = new EmoteRenderer(player, emoteId, keyFrameStorage.getTimeout(), false, new StoredEmote(keyFrameStorage), prevRenderer);
                this.playingEmotes.put(player, emoteRenderer2);
                return emoteRenderer2;
            }
            catch (Exception error) {
                error.printStackTrace();
                LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl('c')) + "Error while playing emotes: " + error.getMessage());
                return null;
            }
        }
        if (prevRenderer != null) {
            prevRenderer.abort();
        }
        return null;
    }

    public void stream(UUID player, EmoteProvider emoteProvider) {
        EmoteRenderer prevRenderer = this.playingEmotes.get(player);
        if (prevRenderer != null) {
            prevRenderer.abort();
        }
        if (emoteProvider != null) {
            EmoteRenderer emoteRenderer = new EmoteRenderer(player, -3, 0L, true, emoteProvider, prevRenderer);
            this.playingEmotes.put(player, emoteRenderer);
        } else {
            this.playingEmotes.remove(player);
        }
    }

    public short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public byte[] shortToBytes(short value) {
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putShort(value);
        return bytes;
    }

    public Map<Short, KeyFrameStorage> getEmoteSources() {
        return this.emoteSources;
    }

    public Map<UUID, EmoteRenderer> getPlayingEmotes() {
        return this.playingEmotes;
    }

    public EmoteSelectorGui getEmoteSelectorGui() {
        return this.emoteSelectorGui;
    }

    public boolean isCleanPlayingMap() {
        return this.cleanPlayingMap;
    }

    public void setCleanPlayingMap(boolean cleanPlayingMap) {
        this.cleanPlayingMap = cleanPlayingMap;
    }
}

