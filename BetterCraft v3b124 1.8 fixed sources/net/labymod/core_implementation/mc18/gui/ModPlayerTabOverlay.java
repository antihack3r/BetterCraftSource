/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.user.FamiliarManager;
import net.labymod.user.User;
import net.labymod.user.UserManager;
import net.labymod.user.group.LabyGroup;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TagManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;
import org.lwjgl.opengl.GL11;

public class ModPlayerTabOverlay
extends GuiPlayerTabOverlay {
    private static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new PlayerComparator());
    private final Minecraft mc;
    private final GuiIngame guiIngame;
    private IChatComponent footer;
    private IChatComponent header;
    private long lastTimeOpened;
    private boolean isBeingRendered;

    public ModPlayerTabOverlay(Minecraft mcIn, GuiIngame guiIngameIn) {
        super(mcIn, guiIngameIn);
        this.mc = mcIn;
        this.guiIngame = guiIngameIn;
    }

    @Override
    public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String name = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        String tagName = TagManager.getTaggedMessage(name);
        if (tagName != null) {
            return tagName;
        }
        return name;
    }

    @Override
    public void updatePlayerList(boolean willBeRendered) {
        if (willBeRendered && !this.isBeingRendered) {
            this.lastTimeOpened = Minecraft.getSystemTime();
        }
        this.isBeingRendered = willBeRendered;
    }

    @Override
    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        if (LabyMod.getSettings().oldTablist && Permissions.isAllowed(Permissions.Permission.ANIMATIONS)) {
            this.oldTabOverlay(width, scoreboardIn, scoreObjectiveIn);
        } else {
            this.newTabOverlay(width, scoreboardIn, scoreObjectiveIn);
        }
    }

    public void newTabOverlay(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        boolean var10;
        int var9;
        UserManager userManager = LabyMod.getInstance().getUserManager();
        FamiliarManager familiarManager = userManager.getFamiliarManager();
        int familiarCount = 0;
        int totalCount = 0;
        NetHandlerPlayClient nethandlerplayclient = LabyModCore.getMinecraft().getPlayer().sendQueue;
        List<NetworkPlayerInfo> var5 = field_175252_a.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        int var6 = 0;
        int var7 = 0;
        for (NetworkPlayerInfo networkplayerinfo : var5) {
            int k2 = LabyModCore.getMinecraft().getFontRenderer().getStringWidth(this.getPlayerName(networkplayerinfo));
            if (LabyMod.getSettings().revealFamiliarUsers) {
                UUID uuid = networkplayerinfo.getGameProfile().getId();
                if (familiarManager.isFamiliar(uuid)) {
                    k2 += 10;
                    ++familiarCount;
                }
                ++totalCount;
            }
            var6 = Math.max(var6, k2);
            if (scoreObjectiveIn == null || scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) continue;
            k2 = LabyModCore.getMinecraft().getFontRenderer().getStringWidth(" " + scoreboardIn.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
            var7 = Math.max(var7, k2);
        }
        var5 = var5.subList(0, Math.min(var5.size(), 80));
        int var8 = var9 = var5.size();
        int j4 = 1;
        while (var9 > 20) {
            var9 = (var8 + ++j4 - 1) / j4;
        }
        boolean bl2 = var10 = this.mc.isIntegratedServerRunning() || LabyModCore.getMinecraft().getConnection().getNetworkManager().getIsencrypted();
        int var11 = scoreObjectiveIn != null ? (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS ? 90 : var7) : 0;
        int var12 = Math.min(j4 * ((var10 ? 9 : 0) + var6 + var11 + 13), width - 50) / j4;
        int var13 = width / 2 - (var12 * j4 + (j4 - 1) * 5) / 2;
        int var14 = 10;
        int var15 = var12 * j4 + (j4 - 1) * 5;
        List<String> var16 = null;
        List<String> var17 = null;
        if (this.header != null) {
            var16 = LabyModCore.getMinecraft().getFontRenderer().listFormattedStringToWidth(this.header.getFormattedText(), width - 50);
            for (String s2 : var16) {
                var15 = Math.max(var15, LabyModCore.getMinecraft().getFontRenderer().getStringWidth(s2));
            }
        }
        if (this.footer != null) {
            var17 = LabyModCore.getMinecraft().getFontRenderer().listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);
            for (String s2 : var17) {
                var15 = Math.max(var15, LabyModCore.getMinecraft().getFontRenderer().getStringWidth(s2));
            }
        }
        if (var16 != null) {
            ModPlayerTabOverlay.drawRect(width / 2 - var15 / 2 - 1, var14 - 1, width / 2 + var15 / 2 + 2, var14 + var16.size() * LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT, Integer.MIN_VALUE);
            for (String s3 : var16) {
                int i2 = LabyModCore.getMinecraft().getFontRenderer().getStringWidth(s3);
                LabyModCore.getMinecraft().getFontRenderer().drawStringWithShadow(s3, width / 2 - i2 / 2, var14, -1);
                var14 += LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT;
            }
            ++var14;
        }
        ModPlayerTabOverlay.drawRect(width / 2 - var15 / 2 - 1, var14 - 1, width / 2 + var15 / 2 + 2, var14 + var9 * 9, Integer.MIN_VALUE);
        int var18 = 0;
        while (var18 < var8) {
            int var19 = var18 / var9;
            int var20 = var18 % var9;
            int var21 = var13 + var19 * var12 + var19 * 5;
            int var22 = var14 + var20 * 9;
            ModPlayerTabOverlay.drawRect(var21, var22, var21 + var12, var22 + 8, 0x20FFFFFF);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            if (var18 < var5.size()) {
                int var23;
                int var24;
                NetworkPlayerInfo networkplayerinfo2 = var5.get(var18);
                String s4 = this.getPlayerName(networkplayerinfo2);
                GameProfile gameprofile = networkplayerinfo2.getGameProfile();
                if (var10) {
                    EntityPlayer entityplayer = LabyModCore.getMinecraft().getWorld().getPlayerEntityByUUID(gameprofile.getId());
                    boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                    this.mc.getTextureManager().bindTexture(networkplayerinfo2.getLocationSkin());
                    int l2 = 8 + (flag1 ? 8 : 0);
                    int i3 = 8 * (flag1 ? -1 : 1);
                    Gui.drawScaledCustomSizeModalRect(var21, var22, 8.0f, l2, 8, i3, 8, 8, 64.0f, 64.0f);
                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int j5 = 8 + (flag1 ? 8 : 0);
                        int k2 = 8 * (flag1 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(var21, var22, 40.0f, j5, 8, k2, 8, 8, 64.0f, 64.0f);
                    }
                    var21 += 9;
                }
                if (networkplayerinfo2.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    s4 = (Object)((Object)EnumChatFormatting.ITALIC) + s4;
                    LabyModCore.getMinecraft().getFontRenderer().drawStringWithShadow(s4, var21, var22, -1862270977);
                } else {
                    User user;
                    boolean badgeVisible = false;
                    if (LabyMod.getSettings().revealFamiliarUsers && (user = userManager.getUser(networkplayerinfo2.getGameProfile().getId())).isFamiliar()) {
                        LabyGroup group = user.getGroup();
                        if (group != null) {
                            group.renderBadge(var21 - 1, var22, 8.0, 8.0, true);
                        }
                        badgeVisible = true;
                    }
                    LabyModCore.getMinecraft().getFontRenderer().drawStringWithShadow(s4, var21 + (badgeVisible ? 8 : 0), var22, -1);
                }
                if (scoreObjectiveIn != null && networkplayerinfo2.getGameType() != WorldSettings.GameType.SPECTATOR && (var24 = (var23 = var21 + var6 + 1) + var11) - var23 > 5) {
                    this.drawScoreboardValues(scoreObjectiveIn, var22, gameprofile.getName(), var23, var24, networkplayerinfo2);
                }
                this.drawPing(var12, var21 - (var10 ? 9 : 0), var22, networkplayerinfo2);
            }
            ++var18;
        }
        if (var17 != null) {
            var14 = var14 + var9 * 9 + 1;
            ModPlayerTabOverlay.drawRect(width / 2 - var15 / 2 - 1, var14 - 1, width / 2 + var15 / 2 + 2, var14 + var17.size() * LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT, Integer.MIN_VALUE);
            for (String s5 : var17) {
                int j6 = LabyModCore.getMinecraft().getFontRenderer().getStringWidth(s5);
                LabyModCore.getMinecraft().getFontRenderer().drawStringWithShadow(s5, width / 2 - j6 / 2, var14, -1);
                var14 += LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT;
            }
        }
        if (LabyMod.getSettings().revealFamiliarUsers && LabyMod.getSettings().revealFamiliarUsersPercentage) {
            int percent = (int)(totalCount == 0 ? 0L : Math.round(100.0 / (double)totalCount * (double)familiarCount));
            String displayString = String.valueOf(ModColor.cl('7')) + familiarCount + ModColor.cl('8') + "/" + ModColor.cl('7') + totalCount + " " + ModColor.cl('a') + percent + "%";
            LabyMod.getInstance().getDrawUtils().drawRightString(displayString, width / 2 + var15 / 2, 3.0, 0.7);
        }
    }

    public void oldTabOverlay(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        UserManager userManager = LabyMod.getInstance().getUserManager();
        FamiliarManager familiarManager = userManager.getFamiliarManager();
        int familiarCount = 0;
        int totalCount = 0;
        try {
            int var7;
            NetHandlerPlayClient var4 = LabyModCore.getMinecraft().getPlayer().sendQueue;
            List<NetworkPlayerInfo> var5 = field_175252_a.sortedCopy(var4.getPlayerInfoMap());
            int var6 = var7 = LabyModCore.getMinecraft().getPlayer().sendQueue.currentServerMaxPlayers;
            ScaledResolution var8 = new ScaledResolution(Minecraft.getMinecraft());
            int var9 = 0;
            int var10 = var8.getScaledWidth();
            int var11 = 0;
            int var12 = 0;
            int var13 = 0;
            var9 = 1;
            while (var7 > 20) {
                var7 = (var6 + ++var9 - 1) / var9;
            }
            int var14 = 300 / var9;
            if (var14 > 150) {
                var14 = 150;
            }
            int var15 = (var10 - var9 * var14) / 2;
            int var16 = 10;
            ModPlayerTabOverlay.drawRect(var15 - 1, 9, var15 + var14 * var9, 10 + 9 * var7, Integer.MIN_VALUE);
            var11 = 0;
            while (var11 < var6) {
                var12 = var15 + var11 % var9 * var14;
                var13 = 10 + var11 / var9 * 9;
                ModPlayerTabOverlay.drawRect(var12, var13, var12 + var14 - 1, var13 + 8, 0x20FFFFFF);
                GlStateManager.enableAlpha();
                if (var11 < var5.size()) {
                    int var20;
                    int var21;
                    User user;
                    NetworkPlayerInfo var17 = var5.get(var11);
                    String name = var17.getGameProfile().getName();
                    ScorePlayerTeam var18 = LabyModCore.getMinecraft().getWorld().getScoreboard().getPlayersTeam(name);
                    String var19 = this.getPlayerName(var17);
                    boolean badgeVisible = false;
                    if (LabyMod.getSettings().revealFamiliarUsers && (user = userManager.getUser(var17.getGameProfile().getId())).isFamiliar()) {
                        LabyGroup group = user.getGroup();
                        if (group != null) {
                            group.renderBadge(var12, var13, 8.0, 8.0, true);
                        }
                        ++familiarCount;
                    }
                    ++totalCount;
                    LabyMod.getInstance().getDrawUtils().drawString(var19, var12 + 0, var13);
                    if (scoreObjectiveIn != null && (var21 = var12 + var14 - 12 - 5) - (var20 = var12 + LabyMod.getInstance().getDrawUtils().getStringWidth(var19) + 5) > 5) {
                        Score var22 = scoreboardIn.getValueFromObjective(name, scoreObjectiveIn);
                        String var23 = "" + (Object)((Object)EnumChatFormatting.YELLOW) + var22.getScorePoints();
                        LabyMod.getInstance().getDrawUtils().drawString(var23, var21 - LabyMod.getInstance().getDrawUtils().getStringWidth(var23), var13, 1.6777215E7);
                    }
                    this.drawPing(50, var12 + var14 - 52, var13, var17);
                }
                ++var11;
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();
            if (LabyMod.getSettings().revealFamiliarUsers && LabyMod.getSettings().revealFamiliarUsersPercentage) {
                int percent = (int)(totalCount == 0 ? 0L : Math.round(100.0 / (double)totalCount * (double)familiarCount));
                String displayString = String.valueOf(ModColor.cl('7')) + familiarCount + ModColor.cl('8') + "/" + ModColor.cl('7') + totalCount + " " + ModColor.cl('a') + percent + "%";
                LabyMod.getInstance().getDrawUtils().drawRightString(displayString, var15 + var14 * var9, 3.0, 0.7);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn) {
        boolean useColors;
        String c2;
        if (!LabyMod.getSettings().tabPing) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(icons);
            boolean var5 = false;
            int var6 = networkPlayerInfoIn.getResponseTime() < 0 ? 5 : (networkPlayerInfoIn.getResponseTime() < 150 ? 0 : (networkPlayerInfoIn.getResponseTime() < 300 ? 1 : (networkPlayerInfoIn.getResponseTime() < 600 ? 2 : (networkPlayerInfoIn.getResponseTime() < 1000 ? 3 : 4))));
            this.zLevel += 100.0f;
            this.drawTexturedModalRect(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0, 176 + var6 * 8, 10, 8);
        } else {
            this.zLevel += 100.0f;
        }
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        int ping = networkPlayerInfoIn.getResponseTime();
        if (ping >= 1000) {
            ping = 999;
        }
        if (ping < 0) {
            ping = 0;
        }
        String string = c2 = (useColors = LabyMod.getSettings().tabPing_colored) ? "a" : "f";
        if (useColors) {
            if (ping > 150) {
                c2 = "2";
            }
            if (ping > 300) {
                c2 = "c";
            }
            if (ping > 600) {
                c2 = "4";
            }
        }
        if (LabyMod.getSettings().tabPing) {
            draw.drawCenteredString(String.valueOf(ModColor.cl(c2)) + (ping == 0 ? "?" : Integer.valueOf(ping)), (p_175245_2_ + p_175245_1_) * 2 - 12, p_175245_3_ * 2 + 5);
        }
        GL11.glPopMatrix();
        this.zLevel -= 100.0f;
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
            int j2 = LabyModCore.getMath().ceiling_float_int((float)Math.max(i2, p_175247_6_.func_178860_m()) / 2.0f);
            int k2 = Math.max(LabyModCore.getMath().ceiling_float_int(i2 / 2), Math.max(LabyModCore.getMath().ceiling_float_int(p_175247_6_.func_178860_m() / 2), 10));
            boolean bl2 = flag = p_175247_6_.func_178858_o() > (long)this.guiIngame.getUpdateCounter() && (p_175247_6_.func_178858_o() - (long)this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;
            if (j2 > 0) {
                float f2 = Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)k2, 9.0f);
                if (f2 > 3.0f) {
                    int l2 = j2;
                    while (l2 < k2) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)l2 * f2, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                        ++l2;
                    }
                    int j22 = 0;
                    while (j22 < j2) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)j22 * f2, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                        if (flag) {
                            if (j22 * 2 + 1 < p_175247_6_.func_178860_m()) {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)j22 * f2, (float)p_175247_2_, 70, 0, 9, 9);
                            }
                            if (j22 * 2 + 1 == p_175247_6_.func_178860_m()) {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)j22 * f2, (float)p_175247_2_, 79, 0, 9, 9);
                            }
                        }
                        if (j22 * 2 + 1 < i2) {
                            this.drawTexturedModalRect((float)p_175247_4_ + (float)j22 * f2, (float)p_175247_2_, j22 >= 10 ? 160 : 52, 0, 9, 9);
                        }
                        if (j22 * 2 + 1 == i2) {
                            this.drawTexturedModalRect((float)p_175247_4_ + (float)j22 * f2, (float)p_175247_2_, j22 >= 10 ? 169 : 61, 0, 9, 9);
                        }
                        ++j22;
                    }
                } else {
                    float f22 = LabyModCore.getMath().clamp_float((float)i2 / 20.0f, 0.0f, 1.0f);
                    int i22 = (int)((1.0f - f22) * 255.0f) << 16 | (int)(f22 * 255.0f) << 8;
                    String s2 = "" + (float)i2 / 2.0f;
                    if (p_175247_5_ - LabyModCore.getMinecraft().getFontRenderer().getStringWidth(String.valueOf(s2) + "hp") >= p_175247_4_) {
                        s2 = String.valueOf(s2) + "hp";
                    }
                    LabyModCore.getMinecraft().getFontRenderer().drawStringWithShadow(s2, (p_175247_5_ + p_175247_4_) / 2 - LabyModCore.getMinecraft().getFontRenderer().getStringWidth(s2) / 2, p_175247_2_, i22);
                }
            }
        } else {
            String s2 = "" + (Object)((Object)EnumChatFormatting.YELLOW) + i2;
            LabyModCore.getMinecraft().getFontRenderer().drawStringWithShadow(s2, p_175247_5_ - LabyModCore.getMinecraft().getFontRenderer().getStringWidth(s2), p_175247_2_, 0xFFFFFF);
        }
    }

    @Override
    public void setFooter(IChatComponent footerIn) {
        this.footer = footerIn;
        LabyMod.getInstance().getEventManager().callAllFooter(LabyModCore.getMinecraft().getChatComponent(footerIn));
    }

    @Override
    public void setHeader(IChatComponent headerIn) {
        this.header = headerIn;
        LabyMod.getInstance().getEventManager().callAllHeader(LabyModCore.getMinecraft().getChatComponent(headerIn));
    }

    public void func_181030_a() {
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
            ScorePlayerTeam scoreplayerteam2 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare((Comparable<?>)((Object)(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "")), (Comparable<?>)((Object)(scoreplayerteam2 != null ? scoreplayerteam2.getRegisteredName() : ""))).compare((Comparable<?>)((Object)p_compare_1_.getGameProfile().getName()), (Comparable<?>)((Object)p_compare_2_.getGameProfile().getName())).result();
        }
    }
}

