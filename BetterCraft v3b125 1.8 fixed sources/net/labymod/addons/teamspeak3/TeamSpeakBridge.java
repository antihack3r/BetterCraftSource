/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.teamspeak3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.labymod.addons.teamspeak3.EnumTargetMode;
import net.labymod.addons.teamspeak3.TeamSpeak;
import net.labymod.addons.teamspeak3.TeamSpeakChannel;
import net.labymod.addons.teamspeak3.TeamSpeakController;
import net.labymod.addons.teamspeak3.TeamSpeakUser;
import net.labymod.utils.ModColor;

public class TeamSpeakBridge {
    public static TeamSpeakChannel getChannel(String clientName) {
        TeamSpeakUser teamspeakuser = TeamSpeakController.getInstance().getUser(clientName);
        return TeamSpeakBridge.getChannel(teamspeakuser);
    }

    protected static TeamSpeakChannel getChannel(TeamSpeakUser user) {
        return TeamSpeakController.getInstance().getChannel(user.getChannelId());
    }

    public static List<TeamSpeakUser> getUsers() {
        return TeamSpeakUser.getUsers();
    }

    public static List<TeamSpeakChannel> getChannels() {
        return TeamSpeakChannel.getChannels();
    }

    public static TeamSpeakUser getUser(String clientName) {
        return TeamSpeakController.getInstance().getUser(clientName);
    }

    public static void pokeClient(TeamSpeakUser user, String message) {
        TeamSpeakUser teamspeakuser;
        TeamSpeakController.getInstance().sendMessage("clientpoke msg=" + TeamSpeak.unFix(message) + " clid=" + user.getClientId());
        String s2 = String.valueOf(ModColor.cl("7")) + "You poked " + ModColor.cl("9") + user.getNickName() + ModColor.cl("7") + ".";
        if (!message.isEmpty()) {
            s2 = String.valueOf(ModColor.cl("7")) + "You poked " + ModColor.cl("9") + user.getNickName() + ModColor.cl("7") + " with message: " + message;
        }
        TeamSpeak.addChat(null, null, s2, EnumTargetMode.SERVER);
        TeamSpeak.addChat(null, null, s2, EnumTargetMode.CHANNEL);
        if (TeamSpeak.selectedChat >= 0 && (teamspeakuser = TeamSpeakController.getInstance().getUser(TeamSpeak.selectedChat)) != null && teamspeakuser.getClientId() == user.getClientId()) {
            TeamSpeak.addChat(teamspeakuser, null, s2, EnumTargetMode.USER);
        }
    }

    public static void messagePlayer(TeamSpeakUser user, String message) {
        TeamSpeakController.getInstance().sendMessage("sendtextmessage targetmode=1 target=" + user.getClientId() + " msg=" + TeamSpeak.unFix(TeamSpeak.toUrl(message)));
    }

    public static void messageChannel(String message) {
        TeamSpeakController.getInstance().sendMessage("sendtextmessage targetmode=2 msg=" + TeamSpeak.unFix(TeamSpeak.toUrl(message)));
    }

    public static void messageServer(String message) {
        TeamSpeakController.getInstance().sendMessage("sendtextmessage targetmode=3 msg=" + TeamSpeak.unFix(TeamSpeak.toUrl(message)));
    }

    public static void moveClient(int id2, int to2) {
        TeamSpeakController.getInstance().sendMessage("clientmove cid=" + to2 + " clid=" + id2);
    }

    public static List<TeamSpeakUser> getChannelUsers(int channelId) {
        ArrayList<TeamSpeakUser> list = new ArrayList<TeamSpeakUser>();
        list.addAll(TeamSpeakUser.getUsers());
        Collections.sort(list, new Comparator<TeamSpeakUser>(){

            @Override
            public int compare(TeamSpeakUser o1, TeamSpeakUser o2) {
                return o1 != null && o2 != null ? (o1.getTalkPower() < o2.getTalkPower() ? 1 : (o1.getTalkPower() > o2.getTalkPower() ? -1 : 0)) : 0;
            }
        });
        ArrayList<TeamSpeakUser> list1 = new ArrayList<TeamSpeakUser>();
        for (TeamSpeakUser teamspeakuser : list) {
            if (teamspeakuser == null || channelId != teamspeakuser.getChannelId()) continue;
            list1.add(teamspeakuser);
        }
        return list1;
    }

    public static void setNickname(String nickname) {
        TeamSpeakController.getInstance().sendMessage("clientupdate client_nickname=" + TeamSpeak.unFix(nickname));
    }

    public static void setAway(boolean away, String message) {
        TeamSpeakController.getInstance().sendMessage("clientupdate client_away=" + TeamSpeak.booleanToInteger(away));
        if (message.isEmpty()) {
            TeamSpeakController.getInstance().sendMessage("clientupdate client_away_message");
        } else {
            TeamSpeakController.getInstance().sendMessage("clientupdate client_away_message=" + TeamSpeak.unFix(message));
        }
    }

    public static void setInputMuted(boolean muted) {
        TeamSpeakController.getInstance().sendMessage("clientupdate client_input_muted=" + TeamSpeak.booleanToInteger(muted));
    }

    public static void setOutputMuted(boolean muted) {
        TeamSpeakController.getInstance().sendMessage("clientupdate client_output_muted=" + TeamSpeak.booleanToInteger(muted));
    }

    public static void setInputDeactivated(boolean muted) {
        TeamSpeakController.getInstance().sendMessage("clientupdate client_input_deactivated=" + TeamSpeak.booleanToInteger(muted));
    }

    public static void setChannelCommander(boolean commander) {
        TeamSpeakController.getInstance().sendMessage("clientupdate client_is_channel_commander=" + TeamSpeak.booleanToInteger(commander));
    }

    public static void setMetaData(String message) {
        TeamSpeakController.getInstance().sendMessage("clientupdate client_meta_data=" + TeamSpeak.unFix(message));
    }

    public static void sendTextMessage(int id2, String msg) {
        if (id2 == -2) {
            TeamSpeakBridge.messageServer(msg);
        } else if (id2 == -1) {
            TeamSpeakBridge.messageChannel(msg);
        } else {
            TeamSpeakUser teamspeakuser = TeamSpeakController.getInstance().getUser(id2);
            if (teamspeakuser != null) {
                TeamSpeakBridge.messagePlayer(teamspeakuser, msg);
            } else {
                TeamSpeak.error("User is offline");
            }
        }
    }
}

