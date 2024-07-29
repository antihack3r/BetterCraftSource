/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.labyconnect.ClientConnection;
import net.labymod.labyconnect.log.ChatlogManager;
import net.labymod.labyconnect.packets.EnumConnectionState;
import net.labymod.labyconnect.packets.PacketPlayServerStatusUpdate;
import net.labymod.labyconnect.user.ChatRequest;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.ClientProfile;
import net.labymod.labyconnect.user.EnumAlertDisplayType;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.utils.ServerData;
import net.minecraft.client.Minecraft;

public class LabyConnect
extends Thread
implements ServerMessageEvent {
    private static final long RECONNECT_INTERVAL = 60000L;
    private ClientConnection clientConnection;
    private ChatlogManager chatlogManager = new ChatlogManager();
    private List<ChatUser> friends = new ArrayList<ChatUser>();
    private List<ChatRequest> requests = new ArrayList<ChatRequest>();
    private List<ChatUser> sortFriends = new ArrayList<ChatUser>();
    private ClientProfile clientProfile;
    private PacketPlayServerStatusUpdate lastPacketPlayServerStatus;
    private EnumAlertDisplayType alertDisplayType;
    private boolean forcedLogout = false;
    private boolean viaServerList = false;

    public LabyConnect() {
        try {
            this.clientConnection = new ClientConnection(this);
            this.clientProfile = new ClientProfile(this, this.clientConnection);
        }
        catch (Throwable error) {
            error.printStackTrace();
        }
        this.updateAlertDisplayType();
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){

            @Override
            public void run() {
                if (LabyConnect.this.clientConnection != null) {
                    LabyConnect.this.clientConnection.disconnect(false);
                }
            }
        }));
        LabyMod.getInstance().getEventManager().register(this);
    }

    @Override
    public void onServerMessage(String messageKey, JsonElement serverMessage) {
        if (messageKey.equals("server_gamemode")) {
            try {
                JsonObject jsonObject = serverMessage.getAsJsonObject();
                if (jsonObject.has("show_gamemode")) {
                    boolean showGamemode = jsonObject.get("show_gamemode").getAsBoolean();
                    if (showGamemode) {
                        if (jsonObject.has("gamemode_name")) {
                            this.updatePlayingOnServerState(jsonObject.get("gamemode_name").getAsString());
                        }
                    } else {
                        this.updatePlayingOnServerState(null);
                    }
                }
            }
            catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        do {
            try {
                if (this.clientConnection.getCurrentConnectionState() == EnumConnectionState.OFFLINE && Minecraft.getMinecraft().getSession().getToken() != null && !this.forcedLogout) {
                    this.clientConnection.connect();
                }
            }
            catch (Exception error) {
                error.printStackTrace();
            }
            try {
                Thread.sleep(60000L);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        } while (this.clientConnection != null && !Debug.isActive());
    }

    public void updateAlertDisplayType() {
        try {
            this.alertDisplayType = EnumAlertDisplayType.valueOf(LabyMod.getSettings().alertDisplayType);
        }
        catch (Exception error) {
            this.alertDisplayType = EnumAlertDisplayType.ACHIEVEMENT;
            error.printStackTrace();
        }
    }

    public boolean isOnline() {
        return this.clientConnection != null && this.clientConnection.getCurrentConnectionState() == EnumConnectionState.PLAY;
    }

    public ChatUser getChatUser(ChatUser chatUser) {
        return this.getChatUserByUUID(chatUser.getGameProfile().getId());
    }

    public ChatUser getChatUserByUUID(UUID uuid) {
        for (ChatUser chatUser : this.friends) {
            if (!chatUser.getGameProfile().getId().equals(uuid)) continue;
            return chatUser;
        }
        return null;
    }

    public void updatePlayingOnServerState(String gamemode) {
        boolean viaServerlist;
        ServerData serverData = LabyMod.getInstance().getCurrentServerData();
        boolean bl2 = viaServerlist = this.viaServerList && serverData != null;
        PacketPlayServerStatusUpdate packet = serverData == null || !LabyMod.getInstance().isInGame() || Minecraft.getMinecraft().isSingleplayer() ? new PacketPlayServerStatusUpdate() : new PacketPlayServerStatusUpdate(serverData.getIp(), serverData.getPort(), gamemode == null ? "" : gamemode, viaServerlist);
        if (this.lastPacketPlayServerStatus != null && this.lastPacketPlayServerStatus.equals(packet)) {
            return;
        }
        this.lastPacketPlayServerStatus = packet;
        if (LabyMod.getSettings().showConnectedIp) {
            this.getClientConnection().sendPacket(packet);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    public void sortFriendList(final int sortMode) {
        ArrayList<ChatUser> sortedList = new ArrayList<ChatUser>();
        block4: for (ChatUser chatUser : this.friends) {
            switch (sortMode) {
                case 1: {
                    if (chatUser.isOnline()) break;
                    continue block4;
                }
                case 2: {
                    if (this.getChatlogManager().getChat(chatUser).getMessages().isEmpty()) continue block4;
                }
            }
            sortedList.add(chatUser);
        }
        Collections.sort(sortedList, new Comparator<ChatUser>(){

            @Override
            public int compare(ChatUser a2, ChatUser b2) {
                if (a2.isParty()) {
                    return Integer.MIN_VALUE;
                }
                switch (sortMode) {
                    case 0: {
                        long la2 = a2.isOnline() ? a2.getLastOnline() / 1000L + 1L : a2.getLastOnline() / 2000L;
                        long lb2 = b2.isOnline() ? b2.getLastOnline() / 1000L + 1L : b2.getLastOnline() / 2000L;
                        return (int)(lb2 - la2);
                    }
                    case 1: {
                        return 0;
                    }
                    case 2: {
                        long la3 = a2.isOnline() ? a2.getLastInteraction() / 1000L : a2.getLastInteraction() / 2000L;
                        long lb3 = b2.isOnline() ? b2.getLastInteraction() / 1000L : b2.getLastInteraction() / 2000L;
                        return (int)(lb3 - la3);
                    }
                }
                return 0;
            }
        });
        this.sortFriends = sortedList;
    }

    public ClientConnection getClientConnection() {
        return this.clientConnection;
    }

    public ChatlogManager getChatlogManager() {
        return this.chatlogManager;
    }

    public List<ChatUser> getFriends() {
        return this.friends;
    }

    public List<ChatRequest> getRequests() {
        return this.requests;
    }

    public List<ChatUser> getSortFriends() {
        return this.sortFriends;
    }

    public ClientProfile getClientProfile() {
        return this.clientProfile;
    }

    public PacketPlayServerStatusUpdate getLastPacketPlayServerStatus() {
        return this.lastPacketPlayServerStatus;
    }

    public EnumAlertDisplayType getAlertDisplayType() {
        return this.alertDisplayType;
    }

    public boolean isForcedLogout() {
        return this.forcedLogout;
    }

    public boolean isViaServerList() {
        return this.viaServerList;
    }

    public void setForcedLogout(boolean forcedLogout) {
        this.forcedLogout = forcedLogout;
    }

    public void setViaServerList(boolean viaServerList) {
        this.viaServerList = viaServerList;
    }
}

