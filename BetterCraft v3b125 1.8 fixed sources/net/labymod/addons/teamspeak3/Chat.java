/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.teamspeak3;

import java.util.ArrayList;
import net.labymod.addons.teamspeak3.EnumTargetMode;
import net.labymod.addons.teamspeak3.Message;
import net.labymod.addons.teamspeak3.TeamSpeakUser;

public class Chat {
    private TeamSpeakUser sender;
    private TeamSpeakUser chatOwner;
    private EnumTargetMode mode;
    private int global;
    private ArrayList<Message> log = new ArrayList();

    public Chat(TeamSpeakUser chatOwner, TeamSpeakUser sender, EnumTargetMode targetMode, String message) {
        this.sender = sender;
        this.mode = targetMode;
        this.chatOwner = chatOwner;
        this.log.add(new Message(sender, message));
    }

    public Chat(TeamSpeakUser chatOwner, TeamSpeakUser sender, EnumTargetMode targetMode) {
        this.sender = sender;
        this.mode = targetMode;
        this.chatOwner = chatOwner;
    }

    public Chat(int id2, EnumTargetMode targetMode) {
        this.sender = null;
        this.mode = targetMode;
        this.global = id2;
    }

    public TeamSpeakUser getSender() {
        return this.sender;
    }

    public TeamSpeakUser getChatOwner() {
        return this.chatOwner;
    }

    public ArrayList<Message> getLog() {
        return this.log;
    }

    public void addMessage(TeamSpeakUser sender, String msg) {
        this.log.add(new Message(sender, msg));
    }

    public EnumTargetMode getTargetMode() {
        return this.mode;
    }

    public int getSlotId() {
        return this.chatOwner == null ? this.global : this.chatOwner.getClientId();
    }
}

