/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.teamspeak3;

import net.labymod.addons.teamspeak3.TeamSpeakUser;

public class Message {
    private TeamSpeakUser user;
    private String message;

    public Message(TeamSpeakUser user, String message) {
        this.user = user;
        this.message = message;
    }

    public TeamSpeakUser getUser() {
        return this.user;
    }

    public String getMessage() {
        return this.message;
    }
}

