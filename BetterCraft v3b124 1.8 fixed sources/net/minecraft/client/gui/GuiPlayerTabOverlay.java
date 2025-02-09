/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.List;
import me.nzxtercode.bettercraft.client.gui.section.GuiUISettings;
import me.nzxtercode.bettercraft.client.misc.irc.IRC;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

public class GuiPlayerTabOverlay
extends Gui {
    private static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new PlayerComparator());
    private final Minecraft mc;
    private final GuiIngame guiIngame;
    private IChatComponent footer;
    private IChatComponent header;
    private long lastTimeOpened;
    private boolean isBeingRendered;

    public GuiPlayerTabOverlay(Minecraft mcIn, GuiIngame guiIngameIn) {
        this.mc = mcIn;
        this.guiIngame = guiIngameIn;
    }

    public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    public void updatePlayerList(boolean willBeRendered) {
        if (willBeRendered && !this.isBeingRendered) {
            this.lastTimeOpened = Minecraft.getSystemTime();
        }
        this.isBeingRendered = willBeRendered;
    }

    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        boolean flag;
        int l3;
        NetHandlerPlayClient nethandlerplayclient = this.mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> list = field_175252_a.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        int i2 = 0;
        int j2 = 0;
        for (NetworkPlayerInfo networkplayerinfo : list) {
            int k2 = this.mc.fontRendererObj.getStringWidth(this.getPlayerName(networkplayerinfo)) + 25;
            i2 = Math.max(i2, k2);
            if (scoreObjectiveIn == null || scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) continue;
            k2 = this.mc.fontRendererObj.getStringWidth(" " + scoreboardIn.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
            j2 = Math.max(j2, k2);
        }
        list = list.subList(0, Math.min(list.size(), 80));
        int i4 = l3 = list.size();
        int j4 = 1;
        while (i4 > 20) {
            i4 = (l3 + ++j4 - 1) / j4;
        }
        boolean bl2 = flag = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
        int l2 = scoreObjectiveIn != null ? (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS ? 90 : j2) : 0;
        int i1 = Math.min(j4 * ((flag ? 9 : 0) + i2 + l2 + 13), width - 50) / j4;
        int j1 = width / 2 - (i1 * j4 + (j4 - 1) * 5) / 2;
        int k1 = 10;
        int l1 = i1 * j4 + (j4 - 1) * 5;
        List<String> list1 = null;
        List<String> list2 = null;
        if (this.header != null) {
            list1 = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);
            for (String s2 : list1) {
                l1 = Math.max(l1, this.mc.fontRendererObj.getStringWidth(s2));
            }
        }
        if (this.footer != null) {
            list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);
            for (String s2 : list2) {
                l1 = Math.max(l1, this.mc.fontRendererObj.getStringWidth(s2));
            }
        }
        if (list1 != null) {
            if (!GuiUISettings.enabledBackgrounds[0]) {
                GuiPlayerTabOverlay.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);
            }
            for (String s3 : list1) {
                int i22 = this.mc.fontRendererObj.getStringWidth(s3);
                this.mc.fontRendererObj.drawStringWithShadow(s3, width / 2 - i22 / 2, k1, -1);
                k1 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
            ++k1;
        }
        if (!GuiUISettings.enabledBackgrounds[0]) {
            GuiPlayerTabOverlay.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + i4 * 9, Integer.MIN_VALUE);
        }
        int k4 = 0;
        while (k4 < l3) {
            NetworkPlayerInfo networkPlayer;
            int l4 = k4 / i4;
            int i5 = k4 % i4;
            int j22 = j1 + l4 * i1 + l4 * 5;
            int k2 = k1 + i5 * 9;
            if (!GuiUISettings.enabledBackgrounds[0]) {
                GuiPlayerTabOverlay.drawRect(j22, k2, j22 + i1, k2 + 8, 0x20FFFFFF);
            }
            boolean isBedrock = (networkPlayer = list.get(k4)).getGameProfile().getName().startsWith("!") || networkPlayer.getGameProfile().getName().startsWith(".");
            boolean isLabyMod = LabyMod.getInstance().getUserManager().getFamiliarManager().isFamiliar(networkPlayer.getGameProfile().getId());
            GuiPlayerTabOverlay.drawRect(j22, k2, j22 + i1, k2 + 8, IRC.getInstance().isUserConnected(networkPlayer.getGameProfile().getName()) ? Integer.MAX_VALUE : (isLabyMod ? -1439786242 : (isBedrock ? -41333432 : Integer.MIN_VALUE)));
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            if (k4 < list.size()) {
                int k5;
                int l5;
                NetworkPlayerInfo networkplayerinfo1 = list.get(k4);
                String s1 = this.getPlayerName(networkplayerinfo1);
                GameProfile gameprofile = networkplayerinfo1.getGameProfile();
                if (flag) {
                    EntityPlayer entityplayer = this.mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                    boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                    this.mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
                    int l22 = 8 + (flag1 ? 8 : 0);
                    int i3 = 8 * (flag1 ? -1 : 1);
                    Gui.drawScaledCustomSizeModalRect(j22, k2, 8.0f, l22, 8, i3, 8, 8, 64.0f, 64.0f);
                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int j3 = 8 + (flag1 ? 8 : 0);
                        int k3 = 8 * (flag1 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(j22, k2, 40.0f, j3, 8, k3, 8, 8, 64.0f, 64.0f);
                    }
                    j22 += 9;
                }
                if (networkplayerinfo1.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    s1 = (Object)((Object)EnumChatFormatting.ITALIC) + s1;
                    this.mc.fontRendererObj.drawStringWithShadow(s1, j22, k2, -1862270977);
                } else if (networkplayerinfo1.getGameType() == WorldSettings.GameType.CREATIVE) {
                    s1 = "\u00a78[\u00a7f!\u00a78]\u00a7r " + s1;
                    this.mc.fontRendererObj.drawStringWithShadow(s1, j22, k2, -1);
                } else {
                    this.mc.fontRendererObj.drawStringWithShadow(s1, j22, k2, -1);
                }
                if (scoreObjectiveIn != null && networkplayerinfo1.getGameType() != WorldSettings.GameType.SPECTATOR && (l5 = (k5 = j22 + i2 + 1) + l2) - k5 > 5) {
                    this.drawScoreboardValues(scoreObjectiveIn, k2, gameprofile.getName(), k5, l5, networkplayerinfo1);
                }
                this.drawPing(i1, j22 - (flag ? 9 : 0), k2, networkplayerinfo1);
            }
            ++k4;
        }
        if (list2 != null) {
            k1 = k1 + i4 * 9 + 1;
            if (!GuiUISettings.enabledBackgrounds[0]) {
                GuiPlayerTabOverlay.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);
            }
            for (String s4 : list2) {
                int j5 = this.mc.fontRendererObj.getStringWidth(s4);
                this.mc.fontRendererObj.drawStringWithShadow(s4, width / 2 - j5 / 2, k1, -1);
                k1 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawString(this.mc.fontRendererObj, String.valueOf(networkPlayerInfoIn.getResponseTime()), p_175245_2_ + p_175245_1_ - this.mc.fontRendererObj.getStringWidth(String.valueOf(networkPlayerInfoIn.getResponseTime())), p_175245_3_, ColorUtils.rainbowEffect());
    }

    private void drawScoreboardValues(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_) {
        int i2 = p_175247_1_.getScoreboard().getValueFromObjective(p_175247_3_, p_175247_1_).getScorePoints();
        if (p_175247_1_.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
            boolean flag;
            this.mc.getTextureManager().bindTexture(icons);
            if (this.lastTimeOpened == p_175247_6_.func_178855_p()) {
                if (i2 < p_175247_6_.func_178835_l()) {
                    p_175247_6_.func_178846_a(Minecraft.getSystemTime());
                    p_175247_6_.func_178844_b(this.guiIngame.getUpdateCounter() + 20);
                } else if (i2 > p_175247_6_.func_178835_l()) {
                    p_175247_6_.func_178846_a(Minecraft.getSystemTime());
                    p_175247_6_.func_178844_b(this.guiIngame.getUpdateCounter() + 10);
                }
            }
            if (Minecraft.getSystemTime() - p_175247_6_.func_178847_n() > 1000L || this.lastTimeOpened != p_175247_6_.func_178855_p()) {
                p_175247_6_.func_178836_b(i2);
                p_175247_6_.func_178857_c(i2);
                p_175247_6_.func_178846_a(Minecraft.getSystemTime());
            }
            p_175247_6_.func_178843_c(this.lastTimeOpened);
            p_175247_6_.func_178836_b(i2);
            int j2 = MathHelper.ceiling_float_int((float)Math.max(i2, p_175247_6_.func_178860_m()) / 2.0f);
            int k2 = Math.max(MathHelper.ceiling_float_int(i2 / 2), Math.max(MathHelper.ceiling_float_int(p_175247_6_.func_178860_m() / 2), 10));
            boolean bl2 = flag = p_175247_6_.func_178858_o() > (long)this.guiIngame.getUpdateCounter() && (p_175247_6_.func_178858_o() - (long)this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;
            if (j2 > 0) {
                float f2 = Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)k2, 9.0f);
                if (f2 > 3.0f) {
                    int l2 = j2;
                    while (l2 < k2) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)l2 * f2, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                        ++l2;
                    }
                    int j1 = 0;
                    while (j1 < j2) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                        if (flag) {
                            if (j1 * 2 + 1 < p_175247_6_.func_178860_m()) {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, 70, 0, 9, 9);
                            }
                            if (j1 * 2 + 1 == p_175247_6_.func_178860_m()) {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, 79, 0, 9, 9);
                            }
                        }
                        if (j1 * 2 + 1 < i2) {
                            this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, j1 >= 10 ? 160 : 52, 0, 9, 9);
                        }
                        if (j1 * 2 + 1 == i2) {
                            this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, j1 >= 10 ? 169 : 61, 0, 9, 9);
                        }
                        ++j1;
                    }
                } else {
                    float f1 = MathHelper.clamp_float((float)i2 / 20.0f, 0.0f, 1.0f);
                    int i1 = (int)((1.0f - f1) * 255.0f) << 16 | (int)(f1 * 255.0f) << 8;
                    String s2 = "" + (float)i2 / 2.0f;
                    if (p_175247_5_ - this.mc.fontRendererObj.getStringWidth(String.valueOf(s2) + "hp") >= p_175247_4_) {
                        s2 = String.valueOf(s2) + "hp";
                    }
                    this.mc.fontRendererObj.drawStringWithShadow(s2, (p_175247_5_ + p_175247_4_) / 2 - this.mc.fontRendererObj.getStringWidth(s2) / 2, p_175247_2_, i1);
                }
            }
        } else {
            String s1 = "" + (Object)((Object)EnumChatFormatting.YELLOW) + i2;
            this.mc.fontRendererObj.drawStringWithShadow(s1, p_175247_5_ - this.mc.fontRendererObj.getStringWidth(s1), p_175247_2_, 0xFFFFFF);
        }
    }

    public void setFooter(IChatComponent footerIn) {
        this.footer = footerIn;
    }

    public void setHeader(IChatComponent headerIn) {
        this.header = headerIn;
    }

    public void resetFooterHeader() {
        this.header = null;
        this.footer = null;
    }

    static class PlayerComparator
    implements Comparator<NetworkPlayerInfo> {
        private PlayerComparator() {
        }

        @Override
        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
            ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare((Comparable<?>)((Object)(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "")), (Comparable<?>)((Object)(scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : ""))).compare((Comparable<?>)((Object)p_compare_1_.getGameProfile().getName()), (Comparable<?>)((Object)p_compare_2_.getGameProfile().getName())).result();
        }
    }
}

