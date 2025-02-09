/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.teamspeak3;

import net.labymod.addons.teamspeak3.Chat;
import net.labymod.addons.teamspeak3.ControlListener;
import net.labymod.addons.teamspeak3.EnumTargetMode;
import net.labymod.addons.teamspeak3.TeamSpeak;
import net.labymod.addons.teamspeak3.TeamSpeakController;
import net.labymod.addons.teamspeak3.TeamSpeakUser;
import net.labymod.utils.ModColor;

public class TeamSpeakListener
implements ControlListener {
    @Override
    public void onPokeRecieved(TeamSpeakUser user, String pokeMessage) {
        if (user != null) {
            String s2 = String.valueOf(ModColor.cl("9")) + user.getNickName() + ModColor.cl("a") + " pokes you.";
            if (!pokeMessage.isEmpty()) {
                s2 = String.valueOf(ModColor.cl("9")) + user.getNickName() + ModColor.cl("a") + " pokes you: " + pokeMessage;
            }
            TeamSpeak.infoAll(s2);
            TeamSpeak.overlayWindows.openInfo(user.getClientId(), "You Have Been Poked", s2);
        }
    }

    @Override
    public void onClientDisconnected(TeamSpeakUser user, String reason) {
        if (user != null) {
            TeamSpeak.addChat(null, null, String.valueOf(ModColor.cl("9")) + user.getNickName() + ModColor.cl("7") + " disconnected from the Server (" + reason + ")", EnumTargetMode.SERVER);
            for (Chat chat : TeamSpeak.chats) {
                if (chat.getChatOwner() == null || chat.getTargetMode() != EnumTargetMode.USER || chat.getChatOwner().getClientId() != user.getClientId()) continue;
                chat.addMessage(null, String.valueOf(ModColor.cl("7")) + "Your chat partner has disconnected.");
            }
        }
    }

    @Override
    public void onClientTimout(TeamSpeakUser user) {
        if (user != null) {
            TeamSpeak.addChat(null, null, String.valueOf(ModColor.cl("9")) + user.getNickName() + ModColor.cl("7") + " timed out.", EnumTargetMode.SERVER);
        }
    }

    @Override
    public void onClientConnect(TeamSpeakUser user) {
        TeamSpeak.addChat(null, null, String.valueOf(ModColor.cl("9")) + user.getNickName() + ModColor.cl("7") + " connected to the Server.", EnumTargetMode.SERVER);
    }

    @Override
    public void onMessageRecieved(TeamSpeakUser target, TeamSpeakUser user, String message) {
        if (user != null && target != null) {
            TeamSpeak.addChat(target, user, message, EnumTargetMode.USER);
            TeamSpeakController.getInstance().me.getClientId();
            user.getClientId();
        }
    }

    @Override
    public void onChannelMessageRecieved(TeamSpeakUser user, String message) {
        TeamSpeak.addChat(null, user, message, EnumTargetMode.CHANNEL);
        if (TeamSpeakController.getInstance().me != null) {
            TeamSpeakController.getInstance().me.getClientId();
            user.getClientId();
        }
    }

    @Override
    public void onServerMessageRecieved(TeamSpeakUser user, String message) {
        TeamSpeak.addChat(null, user, message, EnumTargetMode.SERVER);
    }

    @Override
    public void onClientStartTyping(TeamSpeakUser user) {
    }

    @Override
    public void onDisconnect() {
    }

    @Override
    public void onConnect() {
        TeamSpeak.scrollChannel = 0;
    }

    @Override
    public void onError(int errorId, String errorMessage) {
        String s2 = String.valueOf(ModColor.cl("c")) + TeamSpeak.fix(errorMessage) + " (Error " + errorId + ")";
        TeamSpeak.error(s2);
    }
}

