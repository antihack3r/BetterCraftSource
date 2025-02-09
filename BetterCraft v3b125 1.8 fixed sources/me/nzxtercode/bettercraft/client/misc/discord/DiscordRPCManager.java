/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.discord;

import com.jagrosh.discordipc.mod.nzxter.DiscordRPC;
import java.util.concurrent.CompletableFuture;
import me.nzxtercode.bettercraft.client.misc.discord.DiscordRPCList;
import net.minecraft.client.Minecraft;

public class DiscordRPCManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private DiscordRPCList currentRPC = DiscordRPCList.CLIENT;
    private static final DiscordRPCManager INSTANCE = new DiscordRPCManager();

    public static DiscordRPCManager getInstance() {
        return INSTANCE;
    }

    private DiscordRPCManager() {
    }

    public void init() {
        CompletableFuture.runAsync(() -> DiscordRPC.getInstance().bootDiscordRPC(this.currentRPC.getConfigID(), this.currentRPC.getDetails(), this.currentRPC.getState(), this.currentRPC.getLargeImageKey(), this.currentRPC.getLargeImageText(), this.currentRPC.getSmallImageKey(), this.currentRPC.getSmallImageText(), this.currentRPC.getLabelsAndUrls()));
    }

    public void setDiscordRPC(DiscordRPCList discordRPC) {
        this.currentRPC = discordRPC;
        this.init();
    }
}

