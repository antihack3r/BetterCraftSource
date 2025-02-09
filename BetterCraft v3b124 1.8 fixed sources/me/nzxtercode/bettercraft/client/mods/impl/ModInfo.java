/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods.impl;

import me.nzxtercode.bettercraft.client.hud.ScreenPosition;
import me.nzxtercode.bettercraft.client.misc.irc.IRC;
import me.nzxtercode.bettercraft.client.mods.ModRender;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import me.nzxtercode.bettercraft.client.utils.ProtocolVersionUtils;
import me.nzxtercode.bettercraft.client.utils.TimeHelperUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class ModInfo
extends ModRender {
    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void render(ScreenPosition pos) {
        try {
            this.font.drawString("Network Settings", 5, 5, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Remote Adresse: \u00a7r" + this.mc.getNetHandler().getNetworkManager().getRemoteAddress(), 10, 15, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Server Brand: \u00a7r" + this.mc.thePlayer.getClientBrand().replaceAll("git:", "").replaceAll("Bootstrap", "").replaceAll("SNAPSHOT", "").replaceAll(":", "").replaceAll("SkillCord ", "").replaceAll("MelonBungee ", "").replaceAll("unknown", "").replaceAll("<- Wer das liest ist dumm", "") + " " + (Minecraft.getMinecraft().getCurrentServerData().gameVersion.split(" ").length > 1 ? (Minecraft.getMinecraft().getCurrentServerData().gameVersion.split(" ")[1].startsWith("1.") ? Minecraft.getMinecraft().getCurrentServerData().gameVersion.split(" ")[1] : ProtocolVersionUtils.getKnownAs(Minecraft.getMinecraft().getCurrentServerData().version)) : ProtocolVersionUtils.getKnownAs(Minecraft.getMinecraft().getCurrentServerData().version)), 10, 25, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Ticks per second: \u00a7r" + (double)Math.round(TimeHelperUtils.lastTps * 10.0) / 10.0), 10, 35, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Lag Meter: \u00a7r" + TimeHelperUtils.getFormattedLag()).replaceAll("Lag Meter: \u00a7r0", "Lag Meter: \u00a7rNo Lag"), 10, 45, ColorUtils.rainbowEffect());
            this.font.drawString("World Settings", 5, 65, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Spawn: \u00a7r" + this.mc.theWorld.getSpawnPoint().toString().replaceAll("BlockPos", ""), 10, 75, ColorUtils.rainbowEffect());
            try {
                StringBuilder stringBuilder = new StringBuilder("\u00a7f-> Block: \u00a7r").append(this.mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock().getLocalizedName().replaceAll("tile.air.name", "Air").replaceAll("tile.skull.skeleton.name", "Skull")).append(", ");
                this.mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock();
                this.font.drawString(stringBuilder.append(Block.getIdFromBlock(this.mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock())).toString(), 10, 85, ColorUtils.rainbowEffect());
            }
            catch (Exception e2) {
                this.font.drawString("\u00a7f-> Block: \u00a7rEntity", 10, 85, ColorUtils.rainbowEffect());
            }
            this.font.drawString("\u00a7f-> Biom: \u00a7r" + this.mc.theWorld.getBiomeGenForCoords((BlockPos)this.mc.thePlayer.getPosition()).biomeName, 10, 95, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Time: \u00a7r" + String.format("%02d:%02d", (int)((Math.floor((double)this.mc.theWorld.getWorldInfo().getWorldTime() / 1000.0) + 6.0) % 24.0), (int)Math.floor((double)(this.mc.theWorld.getWorldInfo().getWorldTime() % 1000L) / 1000.0 * 60.0))), 10, 105, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Entitys: \u00a7r" + this.mc.theWorld.loadedEntityList.size()), 10, 115, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Online: \u00a7r" + this.mc.getNetHandler().getPlayerInfoMap().size()), 10, 125, ColorUtils.rainbowEffect());
            this.font.drawString("Chat Settings", 5, 145, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Status: \u00a7r" + (!IRC.getInstance().isUserConnected(this.mc.getSession().getUsername()) ? "Offline" : "Online"), 10, 155, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Users: \u00a7r" + IRC.getInstance().getUsers(IRC.getInstance().currentChannel).length), 10, 165, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Chat: \u00a7r" + IRC.getInstance().getUnreadMessages()), 10, 175, ColorUtils.rainbowEffect());
            this.font.drawString("Misc Settings", 5, 195, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Name: \u00a7r" + this.mc.getSession().getUsername(), 10, 205, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Ram: \u00a7r" + Long.valueOf(Runtime.getRuntime().freeMemory() / 1024L / 1024L) + " / " + Long.valueOf(Runtime.getRuntime().maxMemory() / 1024L / 1024L) + " MB", 10, 215, ColorUtils.rainbowEffect());
        }
        catch (Exception e3) {
            this.font.drawString("Network Settings", 5, 5, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Remote Adresse: \u00a7rReading...", 10, 15, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Server Brand: \u00a7rReading...", 10, 25, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Ticks per second: \u00a7rReading..."), 10, 35, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Lag Meter: \u00a7rReading..."), 10, 45, ColorUtils.rainbowEffect());
            this.font.drawString("World Settings", 5, 65, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Spawn: \u00a7rReading...", 10, 75, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Block: \u00a7rReading...", 10, 85, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Biom: \u00a7rReading...", 10, 95, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Time: \u00a7rReading..."), 10, 105, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Entitys: \u00a7rReading..."), 10, 115, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Online: \u00a7rReading..."), 10, 125, ColorUtils.rainbowEffect());
            this.font.drawString("Chat Settings", 5, 145, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Status: \u00a7rReading...", 10, 155, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Users: \u00a7rReading..."), 10, 165, ColorUtils.rainbowEffect());
            this.font.drawString(String.valueOf("\u00a7f-> Chat: \u00a7rReading..."), 10, 175, ColorUtils.rainbowEffect());
            this.font.drawString("Misc Settings", 5, 195, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Name: \u00a7rReading...", 10, 205, ColorUtils.rainbowEffect());
            this.font.drawString("\u00a7f-> Ram: \u00a7rReading...", 10, 215, ColorUtils.rainbowEffect());
        }
    }
}

