/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import me.nzxtercode.bettercraft.client.events.RenderEvents;
import me.nzxtercode.bettercraft.client.events.types.TypePrePost;
import me.nzxtercode.bettercraft.client.gui.section.GuiUISettings;
import me.nzxtercode.bettercraft.client.hud.HUDManager;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.lenni0451.eventapi.manager.ASMEventManager;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.GuiStreamIndicator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;
import net.optifine.CustomColors;

public class GuiIngame
extends Gui {
    private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    private final Random rand = new Random();
    private final Minecraft mc;
    private final RenderItem itemRenderer;
    private final GuiNewChat persistantChatGUI;
    private final GuiStreamIndicator streamIndicator;
    private int updateCounter;
    private String recordPlaying = "";
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;
    public float prevVignetteBrightness = 1.0f;
    private int remainingHighlightTicks;
    private ItemStack highlightingItemStack;
    private final GuiOverlayDebug overlayDebug;
    private final GuiSpectator spectatorGui;
    private final GuiPlayerTabOverlay overlayPlayerList;
    private int titlesTimer;
    private String displayedTitle = "";
    private String displayedSubTitle = "";
    private int titleFadeIn;
    private int titleDisplayTime;
    private int titleFadeOut;
    private int playerHealth = 0;
    private int lastPlayerHealth = 0;
    private long lastSystemTime = 0L;
    private long healthUpdateCounter = 0L;

    public GuiIngame(Minecraft mcIn) {
        this.mc = mcIn;
        this.itemRenderer = mcIn.getRenderItem();
        this.overlayDebug = new GuiOverlayDebug(mcIn);
        this.spectatorGui = new GuiSpectator(mcIn);
        this.persistantChatGUI = new GuiNewChat(mcIn);
        this.streamIndicator = new GuiStreamIndicator(mcIn);
        this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
        this.setDefaultTitlesTimes();
    }

    public void setDefaultTitlesTimes() {
        this.titleFadeIn = 10;
        this.titleDisplayTime = 70;
        this.titleFadeOut = 20;
    }

    public void renderGameOverlay(float partialTicks) {
        ScoreObjective scoreobjective1;
        int i1;
        float f2;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        ASMEventManager.call(new RenderEvents.GameOverlay(TypePrePost.PRE, scaledresolution, partialTicks));
        int i2 = scaledresolution.getScaledWidth();
        int j2 = scaledresolution.getScaledHeight();
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();
        if (Config.isVignetteEnabled()) {
            this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledresolution);
        } else {
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
        ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);
        if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            this.renderPumpkinOverlay(scaledresolution);
        }
        if (!this.mc.thePlayer.isPotionActive(Potion.confusion) && (f2 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks) > 0.0f) {
            this.renderPortal(f2, scaledresolution);
        }
        if (this.mc.playerController.isSpectator()) {
            this.spectatorGui.renderTooltip(scaledresolution, partialTicks);
        } else {
            this.renderTooltip(scaledresolution, partialTicks);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(icons);
        GlStateManager.enableBlend();
        if (this.showCrosshair()) {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlpha();
            this.drawTexturedModalRect(i2 / 2 - 7, j2 / 2 - 7, 0, 0, 16, 16);
        }
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        this.mc.mcProfiler.startSection("bossHealth");
        this.renderBossHealth();
        this.mc.mcProfiler.endSection();
        if (this.mc.playerController.shouldDrawHUD()) {
            this.renderPlayerStats(scaledresolution);
        }
        GlStateManager.disableBlend();
        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int j1 = this.mc.thePlayer.getSleepTimer();
            float f1 = (float)j1 / 100.0f;
            if (f1 > 1.0f) {
                f1 = 1.0f - (float)(j1 - 100) / 10.0f;
            }
            int k2 = (int)(220.0f * f1) << 24 | 0x101020;
            GuiIngame.drawRect(0, 0, i2, j2, k2);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int k1 = i2 / 2 - 91;
        if (this.mc.thePlayer.isRidingHorse()) {
            this.renderHorseJumpBar(scaledresolution, k1);
        } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
            this.renderExpBar(scaledresolution, k1);
        }
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.renderSelectedItem(scaledresolution);
        } else if (this.mc.thePlayer.isSpectator()) {
            this.spectatorGui.renderSelectedItem(scaledresolution);
        }
        if (this.mc.isDemo()) {
            this.renderDemo(scaledresolution);
        }
        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.renderDebugInfo(scaledresolution);
        }
        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            float f22 = (float)this.recordPlayingUpFor - partialTicks;
            int l1 = (int)(f22 * 255.0f / 20.0f);
            if (l1 > 255) {
                l1 = 255;
            }
            if (l1 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(i2 / 2, j2 - 68, 0.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int l2 = 0xFFFFFF;
                if (this.recordIsPlaying) {
                    l2 = MathHelper.hsvToRGB(f22 / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                }
                this.getFontRenderer().drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, l2 + (l1 << 24 & 0xFF000000));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }
        if (this.titlesTimer > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            float f3 = (float)this.titlesTimer - partialTicks;
            int i22 = 255;
            if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
                float f4 = (float)(this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - f3;
                i22 = (int)(f4 * 255.0f / (float)this.titleFadeIn);
            }
            if (this.titlesTimer <= this.titleFadeOut) {
                i22 = (int)(f3 * 255.0f / (float)this.titleFadeOut);
            }
            if ((i22 = MathHelper.clamp_int(i22, 0, 255)) > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(i2 / 2, j2 / 2, 0.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0f, 4.0f, 4.0f);
                int j22 = i22 << 24 & 0xFF000000;
                this.getFontRenderer().drawString(this.displayedTitle, -this.getFontRenderer().getStringWidth(this.displayedTitle) / 2, -10.0f, 0xFFFFFF | j22, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                this.getFontRenderer().drawString(this.displayedSubTitle, -this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2, 5.0f, 0xFFFFFF | j22, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }
        HUDManager.getInstance().render();
        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());
        if (scoreplayerteam != null && (i1 = scoreplayerteam.getChatFormat().getColorIndex()) >= 0) {
            scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
        }
        ScoreObjective scoreObjective = scoreobjective1 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
        if (scoreobjective1 != null) {
            this.renderScoreboard(scoreobjective1, scaledresolution);
        }
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, j2 - 48, 0.0f);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);
        if (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() > 1 || scoreobjective1 != null)) {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(i2, scoreboard, scoreobjective1);
        } else {
            this.overlayPlayerList.updatePlayerList(false);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        ASMEventManager.call(new RenderEvents.GameOverlay(TypePrePost.POST, scaledresolution, partialTicks));
    }

    protected void renderTooltip(ScaledResolution sr2, float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            ASMEventManager.call(new RenderEvents.ToolTip(TypePrePost.PRE, sr2, partialTicks));
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int i2 = sr2.getScaledWidth() / 2;
            float f2 = this.zLevel;
            this.zLevel = -90.0f;
            if (GuiUISettings.enabledUI[0]) {
                if (!(this.mc.currentScreen instanceof GuiChat)) {
                    try {
                        this.drawTexturedModalRect(i2 - 91, sr2.getScaledHeight() - 22, 0, 0, 182, 22);
                        this.drawTexturedModalRect(i2 - 91 - 1 + entityplayer.inventory.currentItem * 20, sr2.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
                        GuiIngame.drawRect(0, sr2.getScaledHeight() - 23, sr2.getScaledWidth(), sr2.getScaledHeight(), Integer.MIN_VALUE);
                        this.drawString(this.mc.fontRendererObj, "\u00a7fFPS: \u00a7r" + Minecraft.debugFPS + " \u00a7fPing: \u00a7r" + String.valueOf(this.mc.getNetHandler().getPlayerInfo(this.mc.thePlayer.getUniqueID()).getResponseTime()), 5, sr2.getScaledHeight() - 22, ColorUtils.rainbowEffect());
                        this.drawString(this.mc.fontRendererObj, "\u00a7fX: \u00a7r" + Math.round(this.mc.thePlayer.posX) + " \u00a7fY: \u00a7r" + Math.round(this.mc.thePlayer.posY) + " \u00a7fZ: \u00a7r" + Math.round(this.mc.thePlayer.posZ), 5, sr2.getScaledHeight() - 12, ColorUtils.rainbowEffect());
                        SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
                        Date todayy = Calendar.getInstance().getTime();
                        String time = dff.format(todayy);
                        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                        Date today = Calendar.getInstance().getTime();
                        String date = df2.format(today);
                        this.drawString(this.mc.fontRendererObj, "\u00a7fTime: \u00a7r" + time, sr2.getScaledWidth() - 72, sr2.getScaledHeight() - 22, ColorUtils.rainbowEffect());
                        this.drawString(this.mc.fontRendererObj, "\u00a7fDate: \u00a7r" + date, sr2.getScaledWidth() - 72, sr2.getScaledHeight() - 12, ColorUtils.rainbowEffect());
                    }
                    catch (Exception dff) {}
                }
            } else {
                this.drawTexturedModalRect(i2 - 91, sr2.getScaledHeight() - 22, 0, 0, 182, 22);
                this.drawTexturedModalRect(i2 - 91 - 1 + entityplayer.inventory.currentItem * 20, sr2.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            }
            this.zLevel = f2;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();
            if (GuiUISettings.enabledUI[0]) {
                if (!(this.mc.currentScreen instanceof GuiChat)) {
                    int j2 = 0;
                    while (j2 < 9) {
                        int k2 = sr2.getScaledWidth() / 2 - 90 + j2 * 20 + 2;
                        int l2 = sr2.getScaledHeight() - 16 - 3;
                        this.renderHotbarItem(j2, k2, l2, partialTicks, entityplayer);
                        ++j2;
                    }
                }
            } else {
                int j3 = 0;
                while (j3 < 9) {
                    int k3 = sr2.getScaledWidth() / 2 - 90 + j3 * 20 + 2;
                    int l3 = sr2.getScaledHeight() - 16 - 3;
                    this.renderHotbarItem(j3, k3, l3, partialTicks, entityplayer);
                    ++j3;
                }
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            ASMEventManager.call(new RenderEvents.ToolTip(TypePrePost.PRE, sr2, partialTicks));
        }
    }

    public void renderHorseJumpBar(ScaledResolution scaledRes, int x2) {
        this.mc.mcProfiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        float f2 = this.mc.thePlayer.getHorseJumpPower();
        int i2 = 182;
        int j2 = (int)(f2 * (float)(i2 + 1));
        int k2 = scaledRes.getScaledHeight() - 32 + 3;
        this.drawTexturedModalRect(x2, k2, 0, 84, i2, 5);
        if (j2 > 0) {
            this.drawTexturedModalRect(x2, k2, 0, 89, j2, 5);
        }
        this.mc.mcProfiler.endSection();
    }

    public void renderExpBar(ScaledResolution scaledRes, int x2) {
        this.mc.mcProfiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        int i2 = this.mc.thePlayer.xpBarCap();
        if (i2 > 0) {
            int j2 = 182;
            int k2 = (int)(this.mc.thePlayer.experience * (float)(j2 + 1));
            int l2 = scaledRes.getScaledHeight() - 32 + 3;
            this.drawTexturedModalRect(x2, l2, 0, 64, j2, 5);
            if (k2 > 0) {
                this.drawTexturedModalRect(x2, l2, 0, 69, k2, 5);
            }
        }
        this.mc.mcProfiler.endSection();
        if (this.mc.thePlayer.experienceLevel > 0) {
            this.mc.mcProfiler.startSection("expLevel");
            int k1 = 8453920;
            if (Config.isCustomColors()) {
                k1 = CustomColors.getExpBarTextColor(k1);
            }
            String s2 = "" + this.mc.thePlayer.experienceLevel;
            int l1 = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(s2)) / 2;
            int i1 = scaledRes.getScaledHeight() - 31 - 4;
            boolean j1 = false;
            this.getFontRenderer().drawString(s2, l1 + 1, i1, 0);
            this.getFontRenderer().drawString(s2, l1 - 1, i1, 0);
            this.getFontRenderer().drawString(s2, l1, i1 + 1, 0);
            this.getFontRenderer().drawString(s2, l1, i1 - 1, 0);
            this.getFontRenderer().drawString(s2, l1, i1, k1);
            this.mc.mcProfiler.endSection();
        }
    }

    public void renderSelectedItem(ScaledResolution scaledRes) {
        this.mc.mcProfiler.startSection("selectedItemName");
        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
            int k2;
            String s2 = this.highlightingItemStack.getDisplayName();
            if (this.highlightingItemStack.hasDisplayName()) {
                s2 = (Object)((Object)EnumChatFormatting.ITALIC) + s2;
            }
            int i2 = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(s2)) / 2;
            int j2 = scaledRes.getScaledHeight() - 59;
            if (!this.mc.playerController.shouldDrawHUD()) {
                j2 += 14;
            }
            if ((k2 = (int)((float)this.remainingHighlightTicks * 256.0f / 10.0f)) > 255) {
                k2 = 255;
            }
            if (k2 > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                this.getFontRenderer().drawStringWithShadow(s2, i2, j2, 0xFFFFFF + (k2 << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
        this.mc.mcProfiler.endSection();
    }

    public void renderDemo(ScaledResolution scaledRes) {
        this.mc.mcProfiler.startSection("demo");
        String s2 = "";
        s2 = this.mc.theWorld.getTotalWorldTime() >= 120500L ? I18n.format("demo.demoExpired", new Object[0]) : I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime())));
        int i2 = this.getFontRenderer().getStringWidth(s2);
        this.getFontRenderer().drawStringWithShadow(s2, scaledRes.getScaledWidth() - i2 - 10, 5.0f, 0xFFFFFF);
        this.mc.mcProfiler.endSection();
    }

    protected boolean showCrosshair() {
        if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
            return false;
        }
        if (this.mc.playerController.isSpectator()) {
            BlockPos blockpos;
            if (this.mc.pointedEntity != null) {
                return true;
            }
            return this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.theWorld.getTileEntity(blockpos = this.mc.objectMouseOver.getBlockPos()) instanceof IInventory;
        }
        return true;
    }

    public void renderStreamIndicator(ScaledResolution scaledRes) {
        this.streamIndicator.render(scaledRes.getScaledWidth() - 10, 10);
    }

    private void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(objective);
        ArrayList<Score> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>(){

            @Override
            public boolean apply(Score p_apply_1_) {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
        }));
        collection = list.size() > 15 ? Lists.newArrayList(Iterables.skip(list, collection.size() - 15)) : list;
        int i2 = this.getFontRenderer().getStringWidth(objective.getDisplayName());
        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s2 = String.valueOf(ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName())) + ": " + (Object)((Object)EnumChatFormatting.RED) + score.getScorePoints();
            i2 = Math.max(i2, this.getFontRenderer().getStringWidth(s2));
        }
        int i1 = collection.size() * this.getFontRenderer().FONT_HEIGHT;
        int j1 = scaledRes.getScaledHeight() / 2 + i1 / 3;
        int k1 = 3;
        int l1 = scaledRes.getScaledWidth() - i2 - k1;
        int j2 = 0;
        for (Score score1 : collection) {
            ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
            String s2 = "" + (Object)((Object)EnumChatFormatting.RED) + score1.getScorePoints();
            int k2 = j1 - ++j2 * this.getFontRenderer().FONT_HEIGHT;
            int l2 = scaledRes.getScaledWidth() - k1 + 2;
            if (!GuiUISettings.enabledBackgrounds[1]) {
                GuiIngame.drawRect(l1 - 2, k2, l2, k2 + this.getFontRenderer().FONT_HEIGHT, 0x50000000);
            }
            this.getFontRenderer().drawString(s1, l1, k2, 0x20FFFFFF);
            this.getFontRenderer().drawString(s2, l2 - this.getFontRenderer().getStringWidth(s2), k2, 0x20FFFFFF);
            if (j2 != collection.size()) continue;
            String s3 = objective.getDisplayName();
            if (!GuiUISettings.enabledBackgrounds[1]) {
                GuiIngame.drawRect(l1 - 2, k2 - this.getFontRenderer().FONT_HEIGHT - 1, l2, k2 - 1, 0x60000000);
            }
            GuiIngame.drawRect(l1 - 2, k2 - 1, l2, k2, 0x50000000);
            this.getFontRenderer().drawString(s3, l1 + i2 / 2 - this.getFontRenderer().getStringWidth(s3) / 2, k2 - this.getFontRenderer().FONT_HEIGHT, 0x20FFFFFF);
        }
    }

    private void renderPlayerStats(ScaledResolution scaledRes) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            boolean flag;
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int i2 = MathHelper.ceiling_float_int(entityplayer.getHealth());
            boolean bl2 = flag = this.healthUpdateCounter > (long)this.updateCounter && (this.healthUpdateCounter - (long)this.updateCounter) / 3L % 2L == 1L;
            if (i2 < this.playerHealth && entityplayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 20;
            } else if (i2 > this.playerHealth && entityplayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 10;
            }
            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
                this.playerHealth = i2;
                this.lastPlayerHealth = i2;
                this.lastSystemTime = Minecraft.getSystemTime();
            }
            this.playerHealth = i2;
            int j2 = this.lastPlayerHealth;
            this.rand.setSeed(this.updateCounter * 312871);
            boolean flag1 = false;
            FoodStats foodstats = entityplayer.getFoodStats();
            int k2 = foodstats.getFoodLevel();
            int l2 = foodstats.getPrevFoodLevel();
            IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int i1 = scaledRes.getScaledWidth() / 2 - 91;
            int j1 = scaledRes.getScaledWidth() / 2 + 91;
            int k1 = scaledRes.getScaledHeight() - 39;
            float f2 = (float)iattributeinstance.getAttributeValue();
            float f1 = entityplayer.getAbsorptionAmount();
            int l1 = MathHelper.ceiling_float_int((f2 + f1) / 2.0f / 10.0f);
            int i22 = Math.max(10 - (l1 - 2), 3);
            int j22 = k1 - (l1 - 1) * i22 - 10;
            float f22 = f1;
            int k22 = entityplayer.getTotalArmorValue();
            int l22 = -1;
            if (entityplayer.isPotionActive(Potion.regeneration)) {
                l22 = this.updateCounter % MathHelper.ceiling_float_int(f2 + 5.0f);
            }
            this.mc.mcProfiler.startSection("armor");
            int i3 = 0;
            while (i3 < 10) {
                if (k22 > 0) {
                    int j3 = i1 + i3 * 8;
                    if (i3 * 2 + 1 < k22) {
                        this.drawTexturedModalRect(j3, j22, 34, 9, 9, 9);
                    }
                    if (i3 * 2 + 1 == k22) {
                        this.drawTexturedModalRect(j3, j22, 25, 9, 9, 9);
                    }
                    if (i3 * 2 + 1 > k22) {
                        this.drawTexturedModalRect(j3, j22, 16, 9, 9, 9);
                    }
                }
                ++i3;
            }
            this.mc.mcProfiler.endStartSection("health");
            int i6 = MathHelper.ceiling_float_int((f2 + f1) / 2.0f) - 1;
            while (i6 >= 0) {
                int j6 = 16;
                if (entityplayer.isPotionActive(Potion.poison)) {
                    j6 += 36;
                } else if (entityplayer.isPotionActive(Potion.wither)) {
                    j6 += 72;
                }
                int k3 = 0;
                if (flag) {
                    k3 = 1;
                }
                int l3 = MathHelper.ceiling_float_int((float)(i6 + 1) / 10.0f) - 1;
                int i4 = i1 + i6 % 10 * 8;
                int j4 = k1 - l3 * i22;
                if (i2 <= 4) {
                    j4 += this.rand.nextInt(2);
                }
                if (i6 == l22) {
                    j4 -= 2;
                }
                int k4 = 0;
                if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
                    k4 = 5;
                }
                this.drawTexturedModalRect(i4, j4, 16 + k3 * 9, 9 * k4, 9, 9);
                if (flag) {
                    if (i6 * 2 + 1 < j2) {
                        this.drawTexturedModalRect(i4, j4, j6 + 54, 9 * k4, 9, 9);
                    }
                    if (i6 * 2 + 1 == j2) {
                        this.drawTexturedModalRect(i4, j4, j6 + 63, 9 * k4, 9, 9);
                    }
                }
                if (f22 <= 0.0f) {
                    if (i6 * 2 + 1 < i2) {
                        this.drawTexturedModalRect(i4, j4, j6 + 36, 9 * k4, 9, 9);
                    }
                    if (i6 * 2 + 1 == i2) {
                        this.drawTexturedModalRect(i4, j4, j6 + 45, 9 * k4, 9, 9);
                    }
                } else {
                    if (f22 == f1 && f1 % 2.0f == 1.0f) {
                        this.drawTexturedModalRect(i4, j4, j6 + 153, 9 * k4, 9, 9);
                    } else {
                        this.drawTexturedModalRect(i4, j4, j6 + 144, 9 * k4, 9, 9);
                    }
                    f22 -= 2.0f;
                }
                --i6;
            }
            Entity entity = entityplayer.ridingEntity;
            if (entity == null) {
                this.mc.mcProfiler.endStartSection("food");
                int k6 = 0;
                while (k6 < 10) {
                    int j7 = k1;
                    int l7 = 16;
                    int k8 = 0;
                    if (entityplayer.isPotionActive(Potion.hunger)) {
                        l7 += 36;
                        k8 = 13;
                    }
                    if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0f && this.updateCounter % (k2 * 3 + 1) == 0) {
                        j7 = k1 + (this.rand.nextInt(3) - 1);
                    }
                    if (flag1) {
                        k8 = 1;
                    }
                    int j9 = j1 - k6 * 8 - 9;
                    this.drawTexturedModalRect(j9, j7, 16 + k8 * 9, 27, 9, 9);
                    if (flag1) {
                        if (k6 * 2 + 1 < l2) {
                            this.drawTexturedModalRect(j9, j7, l7 + 54, 27, 9, 9);
                        }
                        if (k6 * 2 + 1 == l2) {
                            this.drawTexturedModalRect(j9, j7, l7 + 63, 27, 9, 9);
                        }
                    }
                    if (k6 * 2 + 1 < k2) {
                        this.drawTexturedModalRect(j9, j7, l7 + 36, 27, 9, 9);
                    }
                    if (k6 * 2 + 1 == k2) {
                        this.drawTexturedModalRect(j9, j7, l7 + 45, 27, 9, 9);
                    }
                    ++k6;
                }
            } else if (entity instanceof EntityLivingBase) {
                this.mc.mcProfiler.endStartSection("mountHealth");
                EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
                int i7 = (int)Math.ceil(entitylivingbase.getHealth());
                float f3 = entitylivingbase.getMaxHealth();
                int j8 = (int)(f3 + 0.5f) / 2;
                if (j8 > 30) {
                    j8 = 30;
                }
                int i9 = k1;
                int k9 = 0;
                while (j8 > 0) {
                    int l4 = Math.min(j8, 10);
                    j8 -= l4;
                    int i5 = 0;
                    while (i5 < l4) {
                        int j5 = 52;
                        int k5 = 0;
                        if (flag1) {
                            k5 = 1;
                        }
                        int l5 = j1 - i5 * 8 - 9;
                        this.drawTexturedModalRect(l5, i9, j5 + k5 * 9, 9, 9, 9);
                        if (i5 * 2 + 1 + k9 < i7) {
                            this.drawTexturedModalRect(l5, i9, j5 + 36, 9, 9, 9);
                        }
                        if (i5 * 2 + 1 + k9 == i7) {
                            this.drawTexturedModalRect(l5, i9, j5 + 45, 9, 9, 9);
                        }
                        ++i5;
                    }
                    i9 -= 10;
                    k9 += 20;
                }
            }
            this.mc.mcProfiler.endStartSection("air");
            if (entityplayer.isInsideOfMaterial(Material.water)) {
                int l6 = this.mc.thePlayer.getAir();
                int k7 = MathHelper.ceiling_double_int((double)(l6 - 2) * 10.0 / 300.0);
                int i8 = MathHelper.ceiling_double_int((double)l6 * 10.0 / 300.0) - k7;
                int l8 = 0;
                while (l8 < k7 + i8) {
                    if (l8 < k7) {
                        this.drawTexturedModalRect(j1 - l8 * 8 - 9, j22, 16, 18, 9, 9);
                    } else {
                        this.drawTexturedModalRect(j1 - l8 * 8 - 9, j22, 25, 18, 9, 9);
                    }
                    ++l8;
                }
            }
            this.mc.mcProfiler.endSection();
        }
    }

    private void renderBossHealth() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime;
            FontRenderer fontrenderer = this.mc.fontRendererObj;
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i2 = scaledresolution.getScaledWidth();
            int j2 = 182;
            int k2 = i2 / 2 - j2 / 2;
            int l2 = (int)(BossStatus.healthScale * (float)(j2 + 1));
            int i1 = 12;
            this.drawTexturedModalRect(k2, i1, 0, 74, j2, 5);
            this.drawTexturedModalRect(k2, i1, 0, 74, j2, 5);
            if (l2 > 0) {
                this.drawTexturedModalRect(k2, i1, 0, 79, l2, 5);
            }
            String s2 = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(s2, i2 / 2 - this.getFontRenderer().getStringWidth(s2) / 2, i1 - 10, 0xFFFFFF);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }

    private void renderPumpkinOverlay(ScaledResolution scaledRes) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
        worldrenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
        worldrenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
        worldrenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderVignette(float lightLevel, ScaledResolution scaledRes) {
        if (!Config.isVignetteEnabled()) {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        } else {
            lightLevel = 1.0f - lightLevel;
            lightLevel = MathHelper.clamp_float(lightLevel, 0.0f, 1.0f);
            WorldBorder worldborder = this.mc.theWorld.getWorldBorder();
            float f2 = (float)worldborder.getClosestDistance(this.mc.thePlayer);
            double d0 = Math.min(worldborder.getResizeSpeed() * (double)worldborder.getWarningTime() * 1000.0, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
            double d1 = Math.max((double)worldborder.getWarningDistance(), d0);
            f2 = (double)f2 < d1 ? 1.0f - (float)((double)f2 / d1) : 0.0f;
            this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(lightLevel - this.prevVignetteBrightness) * 0.01);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
            if (f2 > 0.0f) {
                GlStateManager.color(0.0f, f2, f2, 1.0f);
            } else {
                GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0f);
            }
            this.mc.getTextureManager().bindTexture(vignetteTexPath);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
            worldrenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
            worldrenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
            worldrenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
            tessellator.draw();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
    }

    private void renderPortal(float timeInPortal, ScaledResolution scaledRes) {
        if (timeInPortal < 1.0f) {
            timeInPortal *= timeInPortal;
            timeInPortal *= timeInPortal;
            timeInPortal = timeInPortal * 0.8f + 0.2f;
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, timeInPortal);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
        float f2 = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMinV();
        float f22 = textureatlassprite.getMaxU();
        float f3 = textureatlassprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(f2, f3).endVertex();
        worldrenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(f22, f3).endVertex();
        worldrenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(f22, f1).endVertex();
        worldrenderer.pos(0.0, 0.0, -90.0).tex(f2, f1).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
        ItemStack itemstack = player.inventory.mainInventory[index];
        if (itemstack != null) {
            float f2 = (float)itemstack.animationsToGo - partialTicks;
            if (f2 > 0.0f) {
                GlStateManager.pushMatrix();
                float f1 = 1.0f + f2 / 5.0f;
                GlStateManager.translate(xPos + 8, yPos + 12, 0.0f);
                GlStateManager.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, 1.0f);
                GlStateManager.translate(-(xPos + 8), -(yPos + 12), 0.0f);
            }
            this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);
            if (f2 > 0.0f) {
                GlStateManager.popMatrix();
            }
            this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, itemstack, xPos, yPos);
        }
    }

    public void updateTick() {
        if (this.recordPlayingUpFor > 0) {
            --this.recordPlayingUpFor;
        }
        if (this.titlesTimer > 0) {
            --this.titlesTimer;
            if (this.titlesTimer <= 0) {
                this.displayedTitle = "";
                this.displayedSubTitle = "";
            }
        }
        ++this.updateCounter;
        this.streamIndicator.updateStreamAlpha();
        if (this.mc.thePlayer != null) {
            ItemStack itemstack = this.mc.thePlayer.inventory.getCurrentItem();
            if (itemstack == null) {
                this.remainingHighlightTicks = 0;
            } else if (this.highlightingItemStack != null && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
                if (this.remainingHighlightTicks > 0) {
                    --this.remainingHighlightTicks;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }
            this.highlightingItemStack = itemstack;
        }
    }

    public void setRecordPlayingMessage(String recordName) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", recordName), true);
    }

    public void setRecordPlaying(String message, boolean isPlaying) {
        this.recordPlaying = message;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = isPlaying;
    }

    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut) {
        if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        } else if (title != null) {
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        } else if (subTitle != null) {
            this.displayedSubTitle = subTitle;
        } else {
            if (timeFadeIn >= 0) {
                this.titleFadeIn = timeFadeIn;
            }
            if (displayTime >= 0) {
                this.titleDisplayTime = displayTime;
            }
            if (timeFadeOut >= 0) {
                this.titleFadeOut = timeFadeOut;
            }
            if (this.titlesTimer > 0) {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }

    public void setRecordPlaying(IChatComponent component, boolean isPlaying) {
        this.setRecordPlaying(component.getUnformattedText(), isPlaying);
    }

    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public FontRenderer getFontRenderer() {
        return this.mc.fontRendererObj;
    }

    public GuiSpectator getSpectatorGui() {
        return this.spectatorGui;
    }

    public GuiPlayerTabOverlay getTabList() {
        return this.overlayPlayerList;
    }

    public void resetPlayersOverlayFooterHeader() {
        this.overlayPlayerList.resetFooterHeader();
    }
}

