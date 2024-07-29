/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.discord;

import me.nzxtercode.bettercraft.client.BetterCraft;
import net.minecraft.client.Minecraft;

public final class DiscordRPCList
extends Enum<DiscordRPCList> {
    public static final /* enum */ DiscordRPCList CLIENT;
    public static final /* enum */ DiscordRPCList MINECRAFT;
    public static final /* enum */ DiscordRPCList LABYMOD;
    public static final /* enum */ DiscordRPCList ALLTHEMODS5;
    private String gameName;
    private long configID;
    private String details;
    private String state;
    private String largeImageKey;
    private String largeImageText;
    private String smallImageKey;
    private String smallImageText;
    private String[] labels;
    private String[] urls;
    private static final /* synthetic */ DiscordRPCList[] ENUM$VALUES;

    static {
        BetterCraft.getInstance();
        String string = Minecraft.getMinecraft().getSession().getUsername();
        BetterCraft.getInstance();
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(BetterCraft.clientVersion)).append(" ");
        BetterCraft.getInstance();
        String string2 = stringBuilder.append(BetterCraft.clientBuild).toString();
        StringBuilder stringBuilder2 = new StringBuilder("Made by ");
        BetterCraft.getInstance();
        CLIENT = new DiscordRPCList(BetterCraft.clientName, 1193259969324265472L, string, "Playing on bessererange.tk", string2, stringBuilder2.append(BetterCraft.clientAuthor).toString(), "Download", "https://nzxter.de.cool", "Discord", "https://discord.gg/RwYpk34nsC");
        MINECRAFT = new DiscordRPCList("Minecraft", 826834827667439616L, "Main Menu", "", "minecraft", "bessererange.tk", "", "");
        LABYMOD = new DiscordRPCList("LabyMod", 810931666557861888L, "bessererange.tk", "Ingame", "labymod", "MC 1.8.9 - LabyMod 3.7.7", "", "");
        ALLTHEMODS5 = new DiscordRPCList("AllTheMods5", 811263861217820703L, "242 Mods", "Dimension: Overworld", "All The Mods 5", "In the Overworld");
        ENUM$VALUES = new DiscordRPCList[]{CLIENT, MINECRAFT, LABYMOD, ALLTHEMODS5};
    }

    private DiscordRPCList(String gameName, long configID, String details, String state, String largeImageText, String smallImageText) {
        this(gameName, configID, details, state, "1024", largeImageText, "512", smallImageText);
    }

    private DiscordRPCList(String gameName, long configID, String details, String state, String largeImageKey, String largeImageText, String smalImageKey, String smallImageText) {
        this(gameName, configID, details, state, largeImageKey, largeImageText, smalImageKey, smallImageText, new String[0], new String[0]);
    }

    private DiscordRPCList(String gameName, long configID, String details, String state, String largeImageText, String smallImageText, String ... optionalParams) {
        this(gameName, configID, details, state, "1024", largeImageText, "512", smallImageText, DiscordRPCList.extractLabels(optionalParams), DiscordRPCList.extractUrls(optionalParams));
    }

    private DiscordRPCList(String gameName, long configID, String details, String state, String largeImageKey, String largeImageText, String smallImageKey, String smallImageText, String[] labels, String[] urls) {
        this.gameName = gameName;
        this.configID = configID;
        this.details = details;
        this.state = state;
        this.largeImageKey = largeImageKey;
        this.largeImageText = largeImageText;
        this.smallImageKey = smallImageKey;
        this.smallImageText = smallImageText;
        this.labels = labels != null ? labels : new String[]{};
        this.urls = urls != null ? urls : new String[]{};
    }

    private static String[] extractLabels(String ... params) {
        int length = params.length;
        int splitIndex = length / 2;
        String[] labels = new String[splitIndex];
        System.arraycopy(params, 0, labels, 0, splitIndex);
        return labels;
    }

    private static String[] extractUrls(String ... params) {
        int length = params.length;
        int splitIndex = length / 2;
        String[] urls = new String[length - splitIndex];
        System.arraycopy(params, splitIndex, urls, 0, urls.length);
        return urls;
    }

    public String[] getLabelsAndUrls() {
        return DiscordRPCList.concatenateArrays(this.labels, this.urls);
    }

    private static String[] concatenateArrays(String[] array1, String[] array2) {
        String[] result = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    public String getGameName() {
        return this.gameName;
    }

    public long getConfigID() {
        return this.configID;
    }

    public String getDetails() {
        return this.details;
    }

    public String getState() {
        return this.state;
    }

    public String getLargeImageKey() {
        return this.largeImageKey;
    }

    public String getLargeImageText() {
        return this.largeImageText;
    }

    public String getSmallImageKey() {
        return this.smallImageKey;
    }

    public String getSmallImageText() {
        return this.smallImageText;
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

