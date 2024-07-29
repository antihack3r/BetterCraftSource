/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.teamspeak3;

import java.util.ArrayList;
import net.labymod.addons.teamspeak3.Chat;
import net.labymod.addons.teamspeak3.EnumTargetMode;
import net.labymod.addons.teamspeak3.TeamSpeakBridge;
import net.labymod.addons.teamspeak3.TeamSpeakChannel;
import net.labymod.addons.teamspeak3.TeamSpeakController;
import net.labymod.addons.teamspeak3.TeamSpeakListener;
import net.labymod.addons.teamspeak3.TeamSpeakOverlayWindow;
import net.labymod.addons.teamspeak3.TeamSpeakUser;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TeamSpeak {
    private static DrawUtils draw = new DrawUtils();
    private static final Logger logger = LogManager.getLogger("TeamSpeak");
    public static String chatPrefix = String.valueOf(ModColor.cl("8")) + "[" + ModColor.cl("5") + ModColor.cl("l") + "TeamSpeak" + ModColor.cl("8") + "] " + ModColor.cl("7");
    public static TeamSpeakOverlayWindow overlayWindows;
    public static String inputString;
    public static ArrayList<Chat> chats;
    public static int selectedChat;
    public static int selectedChannel;
    public static int selectedUser;
    public static boolean defaultScreen;
    public static int ySplit;
    public static int xSplit;
    public static int scrollChat;
    public static int scrollChannel;
    public static boolean callBack;
    public static int callBackClient;
    public static boolean teamSpeakGroupPrefix;
    public static ArrayList<Integer> outOfView;

    static {
        inputString = "";
        chats = new ArrayList();
        selectedChat = -1;
        selectedChannel = -1;
        selectedUser = -1;
        defaultScreen = false;
        scrollChat = 0;
        scrollChannel = 0;
        callBack = false;
        callBackClient = 0;
        teamSpeakGroupPrefix = false;
        outOfView = new ArrayList();
    }

    public static void init() {
        TeamSpeak.setupChat();
        new TeamSpeakController(new TeamSpeakListener());
        overlayWindows = new TeamSpeakOverlayWindow();
    }

    public static void setupChat() {
        chats.clear();
        chats.add(new Chat(-2, EnumTargetMode.SERVER));
        chats.add(new Chat(-1, EnumTargetMode.CHANNEL));
    }

    public static void addChat(TeamSpeakUser target, TeamSpeakUser sender, String message, EnumTargetMode mode) {
        if (message != null) {
            message = TeamSpeak.fix(message);
            message = TeamSpeak.colors(message);
            message = TeamSpeak.url(message);
        }
        if (mode == EnumTargetMode.USER) {
            TeamSpeakUser teamspeakuser = sender;
            if (sender == null) {
                teamspeakuser = target;
            } else if (sender.getClientId() == TeamSpeakController.getInstance().me().getClientId()) {
                teamspeakuser = target;
            }
            if (teamspeakuser == null) {
                TeamSpeak.error("User is offline");
                return;
            }
            Chat chat = null;
            for (Chat chat1 : chats) {
                if (chat1.getSlotId() != teamspeakuser.getClientId()) continue;
                chat = chat1;
                break;
            }
            if (sender == null) {
                if (chat != null) {
                    chat.addMessage(null, message);
                }
            } else if (chat == null) {
                if (message == null) {
                    chats.add(new Chat(teamspeakuser, sender, mode));
                } else {
                    chats.add(new Chat(teamspeakuser, sender, mode, message));
                }
            } else if (message == null) {
                selectedChat = teamspeakuser.getClientId();
            } else {
                chat.addMessage(sender, message);
            }
        }
        if (mode == EnumTargetMode.CHANNEL) {
            Chat chat2 = null;
            for (Chat chat4 : chats) {
                if (chat4.getSlotId() != -1) continue;
                chat2 = chat4;
                break;
            }
            if (chat2 != null) {
                chat2.addMessage(sender, message);
            }
        }
        if (mode == EnumTargetMode.SERVER) {
            Chat chat3 = null;
            for (Chat chat5 : chats) {
                if (chat5.getSlotId() != -2) continue;
                chat3 = chat5;
                break;
            }
            if (chat3 != null) {
                chat3.addMessage(sender, message);
            }
        }
    }

    public static String replaceColor(String message, String name, String code) {
        return message.replaceAll("(?i)Color=" + name, ModColor.cl(code)).replace("[" + ModColor.cl(code) + "]", ModColor.cl(code));
    }

    public static String replaceHtml(String message, String tag, String toPrefix, String toSuffix) {
        return message.replaceAll("(?i)" + tag, toPrefix).replace("[" + toPrefix + "]", toPrefix).replace("[/" + toPrefix + "]", toSuffix);
    }

    public static String colors(String message) {
        message = TeamSpeak.replaceColor(message, "RED", "c");
        message = TeamSpeak.replaceColor(message, "BLUE", "9");
        message = TeamSpeak.replaceColor(message, "GREEN", "2");
        message = TeamSpeak.replaceColor(message, "YELLOW", "e");
        message = TeamSpeak.replaceColor(message, "GOLD", "6");
        message = TeamSpeak.replaceColor(message, "AQUA", "3");
        message = TeamSpeak.replaceColor(message, "WHITE", "f");
        message = TeamSpeak.replaceColor(message, "BLACK", "0");
        message = message.replaceAll("(?i)/Color", ModColor.cl("7")).replace("[" + ModColor.cl("7") + "]", ModColor.cl("7"));
        return message;
    }

    public static String url(String message) {
        message = TeamSpeak.replaceHtml(message, "URL", String.valueOf(ModColor.cl("9")) + ModColor.cl("n"), ModColor.cl("7"));
        return message;
    }

    public static String toUrl(String message) {
        ArrayList<String> arraylist = ModUtils.extractDomains(message);
        if (!arraylist.isEmpty()) {
            for (String s2 : arraylist) {
                message = message.replace(s2, "[URL]" + s2 + "[/URL]");
            }
        }
        return message;
    }

    public static String fix(String message) {
        message = message.replace("\\/", "/");
        message = message.replace("\\p", "|");
        message = message.replace("\\s", " ");
        message = message.replace("\\\\", "\\");
        message = message.replace("\\n", " ");
        return message;
    }

    public static String unFix(String message) {
        message = message.replace("\\", "\\\\");
        message = message.replace("/", "\\/");
        message = message.replace("|", "\\p");
        message = message.replace(" ", "\\s");
        return message;
    }

    public static String toStarSpacer(String channelName, int xSplit) {
        String s2 = channelName.split("]", 2)[1];
        String s1 = "";
        int i2 = xSplit / 10;
        int j2 = 0;
        while (j2 <= i2) {
            s1 = String.valueOf(s1) + s2;
            ++j2;
        }
        if (s1.length() > i2) {
            s1 = s1.substring(0, i2);
        }
        return s1;
    }

    public static String toCenterSpacer(String channelName) {
        return channelName.split("]", 2)[1];
    }

    public static boolean isSpacer(String channelName) {
        return channelName.toLowerCase().startsWith("[spacer") && channelName.contains("]");
    }

    public static boolean isStarSpacer(String channelName) {
        return channelName.toLowerCase().startsWith("[*spacer") && channelName.contains("]");
    }

    public static boolean isCenterSpacer(String channelName) {
        return channelName.toLowerCase().startsWith("[cspacer") && channelName.contains("]");
    }

    public static String country(String country) {
        return country == null ? "Unknown" : (country.equalsIgnoreCase("DE") ? "Germany" : (country.equalsIgnoreCase("AT") ? "Austria" : (country.equalsIgnoreCase("TR") ? "Turkey" : country)));
    }

    public static String status(boolean status, String on2, String off) {
        return status ? on2 : off;
    }

    public static TeamSpeakChannel getFromOrder(int channelOrder) {
        for (TeamSpeakChannel teamspeakchannel : TeamSpeakBridge.getChannels()) {
            if (teamspeakchannel.getChannelOrder() != channelOrder) continue;
            return teamspeakchannel;
        }
        return null;
    }

    public static boolean isChannelNotInView(int channelId) {
        for (int i2 : outOfView) {
            if (i2 != channelId) continue;
            return true;
        }
        return false;
    }

    public static void updateScroll(int channelId, boolean extend) {
        if (TeamSpeak.isChannelNotInView(channelId)) {
            scrollChannel = extend ? (scrollChannel -= 10) : (scrollChannel += 10);
        }
    }

    public static void setDefaultScreen() {
        if (!defaultScreen) {
            defaultScreen = true;
            xSplit = draw.getWidth() / 3 * 2;
            ySplit = draw.getHeight() / 4 * 3;
        }
    }

    public static int booleanToInteger(boolean input) {
        return input ? 1 : 0;
    }

    public static void print(String msg) {
        logger.info(msg);
    }

    public static void error(String errorMessage) {
        String s2 = String.valueOf(ModColor.cl("c")) + errorMessage;
        if (selectedChat == -1) {
            TeamSpeak.addChat(null, null, s2, EnumTargetMode.CHANNEL);
        } else if (selectedChat == -2) {
            TeamSpeak.addChat(null, null, s2, EnumTargetMode.SERVER);
        } else {
            TeamSpeakUser teamspeakuser = TeamSpeakController.getInstance().getUser(selectedChat);
            if (teamspeakuser != null) {
                TeamSpeak.addChat(teamspeakuser, null, s2, EnumTargetMode.USER);
            } else {
                TeamSpeak.addChat(null, null, s2, EnumTargetMode.SERVER);
            }
        }
    }

    public static void info(String message) {
        String s2 = String.valueOf(ModColor.cl("c")) + message;
        if (selectedChat == -1) {
            TeamSpeak.addChat(null, null, s2, EnumTargetMode.CHANNEL);
        } else if (selectedChat == -2) {
            TeamSpeak.addChat(null, null, s2, EnumTargetMode.SERVER);
        } else {
            TeamSpeakUser teamspeakuser = TeamSpeakController.getInstance().getUser(selectedChat);
            if (teamspeakuser != null) {
                TeamSpeak.addChat(teamspeakuser, null, s2, EnumTargetMode.USER);
            } else {
                TeamSpeak.addChat(null, null, s2, EnumTargetMode.SERVER);
            }
        }
    }

    public static void infoAll(String message) {
        String s2 = String.valueOf(ModColor.cl("c")) + message;
        TeamSpeak.addChat(null, null, s2, EnumTargetMode.CHANNEL);
        TeamSpeak.addChat(null, null, s2, EnumTargetMode.SERVER);
        if (selectedChat >= 0) {
            TeamSpeakUser teamspeakuser = TeamSpeakController.getInstance().getUser(selectedChat);
            if (teamspeakuser != null) {
                TeamSpeak.addChat(teamspeakuser, null, s2, EnumTargetMode.USER);
            } else {
                TeamSpeak.addChat(null, null, s2, EnumTargetMode.SERVER);
            }
        }
    }

    public static String getTalkColor(TeamSpeakUser user) {
        String s2 = ModColor.cl("9");
        if (user == null) {
            return s2;
        }
        if (user.isChannelCommander()) {
            s2 = ModColor.cl("6");
        }
        if (user.isTalking()) {
            s2 = user.isChannelCommander() ? ModColor.cl("e") : ModColor.cl("b");
        }
        if (!user.hasClientInputHardware()) {
            s2 = ModColor.cl("8");
        }
        if (user.hasClientInputMuted()) {
            s2 = ModColor.cl("7");
        }
        if (!user.hasClientOutputHardware()) {
            s2 = ModColor.cl("8");
        }
        if (user.hasClientOutputMuted()) {
            s2 = ModColor.cl("8");
        }
        if (user.isAway()) {
            s2 = ModColor.cl("7");
        }
        return s2;
    }

    public static String getAway(TeamSpeakUser user) {
        String s2 = "";
        if (user == null) {
            return s2;
        }
        if (user.isAway() && !user.getAwayMessage().isEmpty()) {
            s2 = String.valueOf(ModColor.cl("7")) + " [" + user.getAwayMessage() + "]";
        }
        return s2;
    }
}

