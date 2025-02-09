/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.listener;

import net.labymod.main.LabyMod;
import net.labymod.user.FamiliarManager;
import net.labymod.utils.Consumer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

public class ServerIncomingPacketListener
implements Consumer<Object> {
    private final LabyMod labymod;
    private final FamiliarManager familiarManager;

    public ServerIncomingPacketListener(LabyMod labymod) {
        this.labymod = labymod;
        this.familiarManager = labymod.getUserManager().getFamiliarManager();
    }

    public void register() {
        this.labymod.getLabyModAPI().getEventManager().registerOnIncomingPacket(this);
    }

    @Override
    public void accept(Object packetObject) {
        if (packetObject instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem packet = (S38PacketPlayerListItem)packetObject;
            if (LabyMod.getSettings().revealFamiliarUsers) {
                if (packet.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                    for (S38PacketPlayerListItem.AddPlayerData data : packet.getEntries()) {
                        if (data == null || data.getProfile() == null) continue;
                        this.familiarManager.queueUser(data.getProfile().getId());
                    }
                }
                if (packet.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                    for (S38PacketPlayerListItem.AddPlayerData data : packet.getEntries()) {
                        if (data == null || data.getProfile() == null) continue;
                        this.familiarManager.lostFamiliarUser(data.getProfile().getId());
                    }
                }
            }
        }
    }
}

