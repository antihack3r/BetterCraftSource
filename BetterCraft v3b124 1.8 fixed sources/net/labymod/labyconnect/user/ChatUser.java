/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.user;

import com.mojang.authlib.GameProfile;
import java.beans.ConstructorProperties;
import net.labymod.labyconnect.GameIconHelper;
import net.labymod.labyconnect.user.ChatRequest;
import net.labymod.labyconnect.user.ServerInfo;
import net.labymod.labyconnect.user.UserStatus;

public class ChatUser {
    private GameProfile gameProfile;
    private UserStatus status;
    private String statusMessage;
    private ServerInfo currentServerInfo;
    private int unreadMessages = 0;
    private long lastInteraction = System.currentTimeMillis();
    private long lastTyping;
    private String timeZone;
    private long lastOnline;
    private long firstJoined;
    private int contactAmount;
    private boolean party;

    public void increaseUnreadMessages() {
        ++this.unreadMessages;
        GameIconHelper.updateIcon(false, true);
    }

    public void setUnreadMessages(int amount) {
        boolean changed = this.unreadMessages != amount;
        this.unreadMessages = amount;
        if (changed) {
            GameIconHelper.updateIcon(amount == 0, false);
        }
    }

    public boolean isFriendRequest() {
        return this instanceof ChatRequest;
    }

    public boolean isOnline() {
        return this.status != UserStatus.OFFLINE;
    }

    public boolean equals(ChatUser chatUser) {
        return this.party && chatUser.party == this.party || !chatUser.party && !this.party && chatUser.getGameProfile().getId().equals(this.gameProfile.getId());
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public ServerInfo getCurrentServerInfo() {
        return this.currentServerInfo;
    }

    public int getUnreadMessages() {
        return this.unreadMessages;
    }

    public long getLastInteraction() {
        return this.lastInteraction;
    }

    public long getLastTyping() {
        return this.lastTyping;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public long getLastOnline() {
        return this.lastOnline;
    }

    public long getFirstJoined() {
        return this.firstJoined;
    }

    public int getContactAmount() {
        return this.contactAmount;
    }

    public boolean isParty() {
        return this.party;
    }

    @ConstructorProperties(value={"gameProfile", "status", "statusMessage", "currentServerInfo", "unreadMessages", "lastInteraction", "lastTyping", "timeZone", "lastOnline", "firstJoined", "contactAmount", "party"})
    public ChatUser(GameProfile gameProfile, UserStatus status, String statusMessage, ServerInfo currentServerInfo, int unreadMessages, long lastInteraction, long lastTyping, String timeZone, long lastOnline, long firstJoined, int contactAmount, boolean party) {
        this.gameProfile = gameProfile;
        this.status = status;
        this.statusMessage = statusMessage;
        this.currentServerInfo = currentServerInfo;
        this.unreadMessages = unreadMessages;
        this.lastInteraction = lastInteraction;
        this.lastTyping = lastTyping;
        this.timeZone = timeZone;
        this.lastOnline = lastOnline;
        this.firstJoined = firstJoined;
        this.contactAmount = contactAmount;
        this.party = party;
    }

    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setCurrentServerInfo(ServerInfo currentServerInfo) {
        this.currentServerInfo = currentServerInfo;
    }

    public void setLastInteraction(long lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    public void setLastTyping(long lastTyping) {
        this.lastTyping = lastTyping;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public void setFirstJoined(long firstJoined) {
        this.firstJoined = firstJoined;
    }

    public void setContactAmount(int contactAmount) {
        this.contactAmount = contactAmount;
    }

    public void setParty(boolean party) {
        this.party = party;
    }
}

