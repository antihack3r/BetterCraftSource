/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.sticker;

import com.google.common.util.concurrent.FutureCallback;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.packets.PacketActionPlayResponse;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.user.User;
import net.labymod.user.gui.StickerSelectorGui;
import net.labymod.user.sticker.StickerLoader;
import net.labymod.user.sticker.data.Sticker;
import net.labymod.user.sticker.data.StickerData;
import net.labymod.user.sticker.data.StickerPack;
import net.labymod.utils.ModColor;

public class StickerRegistry {
    public static final long STICKER_DURATION = 4000L;
    private StickerSelectorGui stickerSelectorGui = new StickerSelectorGui();
    protected StickerData stickerData;

    public void init() {
        new StickerLoader(this).start();
    }

    public void playSticker(final User user, final short stickerId) {
        if (LabyModCore.getMinecraft().getPlayer() == null || !LabyMod.getSettings().stickers) {
            Debug.log(Debug.EnumDebugMode.STICKER, "Playing sticker canceled because sticker are disabled or player is null.");
            return;
        }
        if (!LabyMod.getInstance().getLabyConnect().isOnline()) {
            LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl('c')) + LabyMod.getMessage("sticker_error_not_connected", new Object[0]));
            Debug.log(Debug.EnumDebugMode.STICKER, "Playing sticker canceled because player is not connected to labymod chat!");
            return;
        }
        byte[] stickerIdBytes = this.shortToBytes(stickerId);
        Debug.log(Debug.EnumDebugMode.STICKER, "Request labymod chat to play sticker id " + stickerId);
        LabyMod.getInstance().getUserManager().requestAction((short)3, stickerIdBytes, new FutureCallback<PacketActionPlayResponse>(){

            @Override
            public void onSuccess(PacketActionPlayResponse responsePacket) {
                if (responsePacket == null || !responsePacket.isAllowed()) {
                    Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, responsePacket == null ? "Response timed out" : "Response packet: " + responsePacket.getReason());
                    if (stickerId != -1) {
                        String message = null;
                        message = responsePacket == null ? LabyMod.getMessage("sticker_no_response", new Object[0]) : (responsePacket.getReason().startsWith("illegal sticker use") ? LabyMod.getMessage("sticker_error_illegal_sticker", new Object[0]) : responsePacket.getReason());
                        LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl('c')) + message);
                    }
                    return;
                }
                if (LabyModCore.getMinecraft().getPlayer() != null) {
                    LabyMod.getInstance().getUserManager().broadcastBitUpdate(stickerId != -1);
                }
                StickerRegistry.this.handleSticker(user, stickerId);
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
                LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl('c')) + "Error: " + throwable.getMessage());
            }
        });
    }

    public Sticker getSticker(short id2) {
        StickerPack[] stickerPackArray = this.stickerData.getPacks();
        int n2 = stickerPackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            StickerPack stickerPack = stickerPackArray[n3];
            Sticker[] stickerArray = stickerPack.getStickers();
            int n4 = stickerArray.length;
            int n5 = 0;
            while (n5 < n4) {
                Sticker sticker = stickerArray[n5];
                if (sticker.getId() == id2) {
                    return sticker;
                }
                ++n5;
            }
            ++n3;
        }
        return null;
    }

    public void handleSticker(User user, short id2) {
        user.setPlayingSticker(id2);
        if (id2 != -1) {
            user.setStickerStartedPlaying(System.currentTimeMillis());
        }
    }

    public void stopSticker(User user) {
        this.handleSticker(user, (short)-1);
    }

    public short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public byte[] shortToBytes(short value) {
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putShort(value);
        return bytes;
    }

    public StickerSelectorGui getStickerSelectorGui() {
        return this.stickerSelectorGui;
    }

    public StickerData getStickerData() {
        return this.stickerData;
    }
}

