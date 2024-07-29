/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.user;

import com.mojang.authlib.GameProfile;
import java.util.Calendar;
import java.util.TimeZone;
import net.labymod.labyconnect.ClientConnection;
import net.labymod.labyconnect.LabyConnect;
import net.labymod.labyconnect.packets.PacketPlayChangeOptions;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.ServerInfo;
import net.labymod.labyconnect.user.UserStatus;
import net.labymod.main.LabyMod;

public class ClientProfile {
    private ClientConnection clientConnection;
    private LabyConnect chatClient;
    private long firstJoined = 0L;

    public ClientProfile(LabyConnect chatClient, ClientConnection clientConnection) {
        this.chatClient = chatClient;
        this.clientConnection = clientConnection;
    }

    public UserStatus getUserStatus() {
        return UserStatus.getById(LabyMod.getSettings().onlineStatus);
    }

    public void setUserStatus(UserStatus userStatus) {
        LabyMod.getSettings().onlineStatus = userStatus.getId();
    }

    public TimeZone getTimeZone() {
        return Calendar.getInstance().getTimeZone();
    }

    public void sendSettingsToServer() {
        this.clientConnection.sendPacket(new PacketPlayChangeOptions(LabyMod.getSettings().showConnectedIp, this.getUserStatus(), this.getTimeZone()));
    }

    public ChatUser buildClientUser() {
        GameProfile gameProfile = new GameProfile(LabyMod.getInstance().getPlayerUUID(), LabyMod.getInstance().getPlayerName());
        return new ChatUser(gameProfile, this.getUserStatus(), LabyMod.getSettings().motd, new ServerInfo(LabyMod.getInstance().getCurrentServerData() == null ? "" : LabyMod.getInstance().getCurrentServerData().getIp(), LabyMod.getInstance().getCurrentServerData() == null ? 25565 : LabyMod.getInstance().getCurrentServerData().getPort()), 0, System.currentTimeMillis(), 0L, this.getTimeZone().getID(), System.currentTimeMillis(), this.firstJoined, this.chatClient.getFriends().size(), false);
    }

    public long getFirstJoined() {
        return this.firstJoined;
    }

    public void setFirstJoined(long firstJoined) {
        this.firstJoined = firstJoined;
    }
}

