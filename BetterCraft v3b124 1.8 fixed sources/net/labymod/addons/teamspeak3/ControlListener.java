/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.teamspeak3;

import net.labymod.addons.teamspeak3.TeamSpeakUser;

public interface ControlListener {
    public void onPokeRecieved(TeamSpeakUser var1, String var2);

    public void onClientDisconnected(TeamSpeakUser var1, String var2);

    public void onClientTimout(TeamSpeakUser var1);

    public void onClientConnect(TeamSpeakUser var1);

    public void onMessageRecieved(TeamSpeakUser var1, TeamSpeakUser var2, String var3);

    public void onClientStartTyping(TeamSpeakUser var1);

    public void onDisconnect();

    public void onConnect();

    public void onChannelMessageRecieved(TeamSpeakUser var1, String var2);

    public void onServerMessageRecieved(TeamSpeakUser var1, String var2);

    public void onError(int var1, String var2);
}

