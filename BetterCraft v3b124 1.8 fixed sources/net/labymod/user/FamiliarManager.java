/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import me.nzxtercode.bettercraft.client.events.ClientTickEvent;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.packets.PacketUserBadge;
import net.labymod.main.LabyMod;
import net.lenni0451.eventapi.events.EventTarget;
import net.lenni0451.eventapi.manager.ASMEventManager;
import net.minecraft.client.network.NetworkPlayerInfo;

public class FamiliarManager {
    private static final long REQUEST_COOLDOWN = 5000L;
    private List<UUID> queuedUsers;
    private List<UUID> familiarUsers;
    private long lastTimeQueueHandled;

    public FamiliarManager() {
        ASMEventManager.register(this);
        this.queuedUsers = new LinkedList<UUID>();
        this.familiarUsers = new ArrayList<UUID>();
        this.lastTimeQueueHandled = 0L;
    }

    @EventTarget
    public void onTick(ClientTickEvent event) {
        if (this.lastTimeQueueHandled + 5000L > System.currentTimeMillis()) {
            return;
        }
        this.requestQueuedUsers();
    }

    public boolean queueUser(UUID uuid) {
        return !this.familiarUsers.contains(uuid) && !this.queuedUsers.contains(uuid) && this.queuedUsers.add(uuid);
    }

    public void requestQueuedUsers() {
        if (this.queuedUsers.size() == 0) {
            return;
        }
        this.lastTimeQueueHandled = System.currentTimeMillis();
        List<UUID> currentList = this.queuedUsers;
        this.queuedUsers = new LinkedList<UUID>();
        try {
            UUID[] array = new UUID[currentList.size()];
            currentList.toArray(array);
            LabyMod.getInstance().getLabyConnect().getClientConnection().sendPacket(new PacketUserBadge(array));
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void newFamiliarUser(UUID uuid) {
        if (!this.familiarUsers.contains(uuid)) {
            this.familiarUsers.add(uuid);
        }
    }

    public boolean lostFamiliarUser(UUID uuid) {
        return this.familiarUsers.remove(uuid) && this.queuedUsers.remove(uuid);
    }

    public boolean isFamiliar(UUID uuid) {
        return this.familiarUsers.contains(uuid);
    }

    public void refresh() {
        if (LabyMod.getInstance().isInGame() && LabyModCore.getMinecraft().getConnection() != null) {
            Collection<NetworkPlayerInfo> players = LabyModCore.getMinecraft().getConnection().getPlayerInfoMap();
            for (NetworkPlayerInfo player : players) {
                if (player == null || player.getGameProfile() == null) continue;
                LabyMod.getInstance().getUserManager().getFamiliarManager().queueUser(player.getGameProfile().getId());
            }
        }
    }

    public void clear() {
        this.familiarUsers.clear();
        this.queuedUsers.clear();
    }
}

