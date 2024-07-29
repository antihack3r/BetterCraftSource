/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.discord;

import me.nzxtercode.bettercraft.client.BetterCraft;

public final class DiscordRPCList
extends Enum<DiscordRPCList> {
    public static final /* enum */ DiscordRPCList CLIENT;
    public static final /* enum */ DiscordRPCList MINECRAFT;
    public static final /* enum */ DiscordRPCList LABYMOD;
    public static final /* enum */ DiscordRPCList BADLION;
    public static final /* enum */ DiscordRPCList LUNARCLIENT;
    public static final /* enum */ DiscordRPCList BLOCKCLIENT;
    private final String gameName;
    private final String configID;
    private final String largeImageText;
    private final String smallImageText;
    private final String details;
    private final String state;
    private static final /* synthetic */ DiscordRPCList[] ENUM$VALUES;

    static {
        BetterCraft.getInstance();
        StringBuilder stringBuilder = new StringBuilder("v");
        BetterCraft.getInstance();
        StringBuilder stringBuilder2 = stringBuilder.append(BetterCraft.clientVersion).append(" b");
        BetterCraft.getInstance();
        CLIENT = new DiscordRPCList(BetterCraft.clientName, "1193259969324265472", stringBuilder2.append(BetterCraft.clientBuild).toString(), "Made by Nzxter", "Nzxter", "Playing on bessererange.tk");
        MINECRAFT = new DiscordRPCList("Minecraft", "826834827667439616", "bessererange.tk", "", "Main Menu", "");
        LABYMOD = new DiscordRPCList("LabyMod", "810931666557861888", "MC 1.8.9 - LabyMod 3.7.7", "", "bessererange.tk", "Ingame");
        BADLION = new DiscordRPCList("Badlion", "810953489709531147", "Using Badlion Client Minecraft Launcher", "", "Playing Minecraft 1.8.9", "Playing on bessererange.tk");
        LUNARCLIENT = new DiscordRPCList("Lunarclient", "810957144391155722", "Lunar Client", "", "Playing Minecraft 1.8.9", "");
        BLOCKCLIENT = new DiscordRPCList("Blockclient", "811309620727513138", "by illuminator3", "", "Playing on bessererange.tk [b22]", "");
        ENUM$VALUES = new DiscordRPCList[]{CLIENT, MINECRAFT, LABYMOD, BADLION, LUNARCLIENT, BLOCKCLIENT};
    }

    private DiscordRPCList(String gameName, String configID, String largeImageText, String smallImageText, String details, String state) {
        this.gameName = gameName;
        this.configID = configID;
        this.largeImageText = largeImageText;
        this.smallImageText = smallImageText;
        this.details = details;
        this.state = state;
    }

    public String getGameName() {
        return this.gameName;
    }

    public String getConfigID() {
        return this.configID;
    }

    public String getLargeImageText() {
        return this.largeImageText;
    }

    public String getSmallImageText() {
        return this.smallImageText;
    }

    public String getDetails() {
        return this.details;
    }

    public String getState() {
        return this.state;
    }

    public static DiscordRPCList[] values() {
        DiscordRPCList[] discordRPCListArray = ENUM$VALUES;
        int n2 = discordRPCListArray.length;
        DiscordRPCList[] discordRPCListArray2 = new DiscordRPCList[n2];
        System.arraycopy(ENUM$VALUES, 0, discordRPCListArray2, 0, n2);
        return discordRPCListArray2;
    }

    public static DiscordRPCList valueOf(String string) {
        return Enum.valueOf(DiscordRPCList.class, string);
    }
}

