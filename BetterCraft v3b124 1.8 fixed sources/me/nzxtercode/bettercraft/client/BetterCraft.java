/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client;

import com.TominoCZ.FBP.FBP;
import de.florianmichael.viamcp.ViaMCP;
import lumien.chunkanimator.ChunkAnimator;
import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.commands.CommandManager;
import me.nzxtercode.bettercraft.client.hud.HUDManager;
import me.nzxtercode.bettercraft.client.misc.Tor;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountManager;
import me.nzxtercode.bettercraft.client.misc.discord.DiscordRPCManager;
import me.nzxtercode.bettercraft.client.misc.irc.IRC;
import me.nzxtercode.bettercraft.client.mods.ModInstances;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import org.javapluginapi.team.JavaPluginApi;

public class BetterCraft {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static String clientAuthor = "Nzxter";
    public static String clientName = "BetterCraft";
    public static String clientVersion = "v3";
    public static String clientBuild = "b124";
    public static int clientVersionCheck = 3;
    public static int clientBuildCheck = 124;
    public static String clientPrefix = "\u00a78[\u00a7f" + clientName + "\u00a78] \u00a77";
    public static boolean seeToken = true;
    private static final BetterCraft INSTANCE = new BetterCraft();

    public static BetterCraft getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        Config.getInstance();
        Config.init();
        ViaMCP.getInstance();
        ViaMCP.init();
        LabyMod.getInstance().init();
        IRC.getInstance().init();
        DiscordRPCManager.getInstance().init();
        FBP.getInstance().init();
        ChunkAnimator.getInstance().init();
        AccountManager.getInstance().init();
        Tor.getInstance();
        Tor.init();
        JavaPluginApi.getInstance();
        JavaPluginApi.init();
        CommandManager.getInstance().addCommands();
        ModInstances.register(HUDManager.getInstance());
    }

    public void terminate() {
        DiscordRPCManager.getInstance().shutdown();
        AccountManager.getInstance().save();
        Tor.getInstance();
        Tor.stopTor();
        JavaPluginApi.getInstance();
        JavaPluginApi.stopPlugin();
    }
}

