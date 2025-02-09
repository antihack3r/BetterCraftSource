/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.discord;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.misc.discord.DiscordRPCList;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class DiscordRPCManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private volatile boolean running = false;
    private DiscordRPCList currentRPC = DiscordRPCList.CLIENT;
    private static final DiscordRPCManager INSTANCE = new DiscordRPCManager();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r2 -> {
        Thread t2 = new Thread(r2);
        t2.setDaemon(true);
        return t2;
    });

    public static DiscordRPCManager getInstance() {
        return INSTANCE;
    }

    private DiscordRPCManager() {
        this.currentRPC = DiscordRPCList.CLIENT;
    }

    public void init() {
        this.running = true;
        DiscordRPC.discordInitialize(this.currentRPC.getConfigID(), null, true);
        this.updateDiscordRPC();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        this.scheduler.scheduleAtFixedRate(() -> {
            if (this.running) {
                DiscordRPC.discordRunCallbacks();
            }
        }, 0L, 2L, TimeUnit.SECONDS);
    }

    public void shutdown() {
        this.running = false;
        DiscordRPC.discordShutdown();
    }

    public void setDiscordRPC(DiscordRPCList discordRPC) {
        this.currentRPC = discordRPC;
        this.shutdown();
        this.init();
    }

    private void updateDiscordRPC() {
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        String largeImageKey = this.currentRPC.getGameName().toLowerCase();
        String smallImageKey = this.currentRPC.getGameName().toLowerCase();
        BetterCraft.getInstance();
        presence.largeImageKey = !largeImageKey.contains(BetterCraft.clientName) ? largeImageKey : "1024";
        presence.largeImageText = this.currentRPC.getLargeImageText();
        BetterCraft.getInstance();
        presence.smallImageKey = !smallImageKey.contains(BetterCraft.clientName) ? smallImageKey : "512";
        presence.smallImageText = this.currentRPC.getSmallImageText();
        presence.details = this.currentRPC.getDetails();
        presence.state = this.currentRPC.getState();
        DiscordRPC.discordUpdatePresence(presence);
    }
}

