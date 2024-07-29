/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.core_implementation.mc18.gui.GuiChatAdapter;
import net.labymod.core_implementation.mc18.gui.ModPlayerTabOverlay;
import net.labymod.main.LabyMod;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiSpectator;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;

public class GuiIngameCustom
extends GuiIngame {
    private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    private final Random rand = new Random();
    private final Minecraft mc;
    private final RenderItem itemRenderer;
    private final GuiNewChat persistantChatGUI;
    private final GuiOverlayDebug overlayDebug;
    private final GuiSpectator spectatorGui;
    private final GuiPlayerTabOverlay overlayPlayerList;
    public float prevVignetteBrightness = 1.0f;
    private int updateCounter;
    private String recordPlaying = "";
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;
    private int remainingHighlightTicks;
    private ItemStack highlightingItemStack;
    private int field_175195_w;
    private String field_175201_x = "";
    private String field_175200_y = "";
    private int field_175199_z;
    private int field_175192_A;
    private int field_175193_B;
    private int playerHealth = 0;
    private int lastPlayerHealth = 0;
    private long lastSystemTime = 0L;
    private long healthUpdateCounter = 0L;

    public GuiIngameCustom(Minecraft mcIn) {
        super(mcIn);
        this.mc = mcIn;
        this.itemRenderer = mcIn.getRenderItem();
        this.overlayDebug = new GuiOverlayDebug(mcIn);
        this.spectatorGui = new GuiSpectator(mcIn);
        this.func_175177_a();
        this.persistantChatGUI = new GuiChatAdapter(mcIn);
        this.overlayPlayerList = new ModPlayerTabOverlay(mcIn, this);
    }

    @Override
    public FontRenderer getFontRenderer() {
        return LabyModCore.getMinecraft().getFontRenderer();
    }

    public void func_175177_a() {
        this.field_175199_z = 10;
        this.field_175192_A = 70;
        this.field_175193_B = 20;
    }

    @Override
    public void renderGameOverlay(float partialTicks) {
        int i3;
        float f2;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i2 = scaledresolution.getScaledWidth();
        int j2 = scaledresolution.getScaledHeight();
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();
        if (Minecraft.isFancyGraphicsEnabled()) {
            this.renderVignetteNew(LabyModCore.getMinecraft().getPlayer().getBrightness(partialTicks), scaledresolution);
        } else {
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
        ItemStack itemstack = LabyModCore.getMinecraft().getPlayer().inventory.armorItemInSlot(3);
        if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            this.renderPumpkinOverlayNew(scaledresolution);
        }
        if (!LabyModCore.getMinecraft().getPlayer().isPotionActive(Potion.confusion) && (f2 = LabyModCore.getMinecraft().getPlayer().prevTimeInPortal + (LabyModCore.getMinecraft().getPlayer().timeInPortal - LabyModCore.getMinecraft().getPlayer().prevTimeInPortal) * partialTicks) > 0.0f) {
            this.renderPortalNew(f2, scaledresolution);
        }
        if (this.mc.playerController.isSpectator()) {
            this.spectatorGui.renderTooltip(scaledresolution, partialTicks);
        } else {
            this.renderTooltip(scaledresolution, partialTicks);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(icons);
        GlStateManager.enableBlend();
        if (this.showCrosshair() && !LabyMod.getInstance().getLabyModAPI().isCrosshairHidden()) {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlpha();
            this.drawTexturedModalRect(i2 / 2 - 7, j2 / 2 - 7, 0, 0, 16, 16);
        }
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        this.mc.mcProfiler.startSection("bossHealth");
        this.renderBossHealthNew();
        this.mc.mcProfiler.endSection();
        if (this.mc.playerController.shouldDrawHUD()) {
            this.renderPlayerStatsNew(scaledresolution);
        }
        GlStateManager.disableBlend();
        if (LabyModCore.getMinecraft().getPlayer().getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int j22 = LabyModCore.getMinecraft().getPlayer().getSleepTimer();
            float f22 = (float)j22 / 100.0f;
            if (f22 > 1.0f) {
                f22 = 1.0f - (float)(j22 - 100) / 10.0f;
            }
            int k2 = (int)(220.0f * f22) << 24 | 0x101020;
            GuiIngameCustom.drawRect(0, 0, i2, j2, k2);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int k2 = i2 / 2 - 91;
        if (LabyModCore.getMinecraft().getPlayer().isRidingHorse()) {
            this.renderHorseJumpBar(scaledresolution, k2);
        } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
            this.renderExpBar(scaledresolution, k2);
        }
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.renderSelectedItem(scaledresolution);
        } else if (LabyModCore.getMinecraft().getPlayer().isSpectator()) {
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
            float f3 = (float)this.recordPlayingUpFor - partialTicks;
            int l1 = (int)(f3 * 255.0f / 20.0f);
            if (l1 > 255) {
                l1 = 255;
            }
            if (l1 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(i2 / 2, j2 - 68, 0.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int m2 = 0xFFFFFF;
                if (this.recordIsPlaying) {
                    m2 = LabyModCore.getMath().hsvToRGB(f3 / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                }
                this.getFontRenderer().drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, m2 + (l1 << 24 & 0xFF000000));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }
        if (this.field_175195_w > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            float f4 = (float)this.field_175195_w - partialTicks;
            int i22 = 255;
            if (this.field_175195_w > this.field_175193_B + this.field_175192_A) {
                float f5 = (float)(this.field_175199_z + this.field_175192_A + this.field_175193_B) - f4;
                i22 = (int)(f5 * 255.0f / (float)this.field_175199_z);
            }
            if (this.field_175195_w <= this.field_175193_B) {
                i22 = (int)(f4 * 255.0f / (float)this.field_175193_B);
            }
            if ((i22 = LabyModCore.getMath().clamp_int(i22, 0, 255)) > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(i2 / 2, j2 / 2, 0.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0f, 4.0f, 4.0f);
                int j3 = i22 << 24 & 0xFF000000;
                this.getFontRenderer().drawString(this.field_175201_x, -this.getFontRenderer().getStringWidth(this.field_175201_x) / 2, -10.0f, 0xFFFFFF | j3, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                this.getFontRenderer().drawString(this.field_175200_y, -this.getFontRenderer().getStringWidth(this.field_175200_y) / 2, 5.0f, 0xFFFFFF | j3, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }
        Scoreboard scoreboard = LabyModCore.getMinecraft().getWorld().getScoreboard();
        ScoreObjective scoreobjective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(LabyModCore.getMinecraft().getPlayer().getName());
        if (scoreplayerteam != null && (i3 = scoreplayerteam.getChatFormat().getColorIndex()) >= 0) {
            scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i3);
        }
        ScoreObjective scoreobjective2 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, j2 - 48, 0.0f);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        scoreobjective2 = scoreboard.getObjectiveInDisplaySlot(0);
        if (!this.mc.gameSettings.keyBindPlayerList.isKeyDown() || this.mc.isIntegratedServerRunning() && LabyModCore.getMinecraft().getPlayer().sendQueue.getPlayerInfoMap().size() <= 1 && scoreobjective2 == null) {
            this.overlayPlayerList.updatePlayerList(false);
        } else {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(i2, scoreboard, scoreobjective2);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    @Override
    protected void renderTooltip(ScaledResolution sr2, float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int i2 = sr2.getScaledWidth() / 2;
            float f2 = this.zLevel;
            this.zLevel = -90.0f;
            this.drawTexturedModalRect(i2 - 91, sr2.getScaledHeight() - 22, 0, 0, 182, 22);
            this.drawTexturedModalRect(i2 - 91 - 1 + entityplayer.inventory.currentItem * 20, sr2.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            this.zLevel = f2;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();
            int j2 = 0;
            while (j2 < 9) {
                int k2 = sr2.getScaledWidth() / 2 - 90 + j2 * 20 + 2;
                int l2 = sr2.getScaledHeight() - 16 - 3;
                this.renderHotbarItemNew(j2, k2, l2, partialTicks, entityplayer);
                ++j2;
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    @Override
    public void renderHorseJumpBar(ScaledResolution p_175186_1_, int p_175186_2_) {
        this.mc.mcProfiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        float f2 = LabyModCore.getMinecraft().getPlayer().getHorseJumpPower();
        int i2 = 182;
        int j2 = (int)(f2 * 183.0f);
        int k2 = p_175186_1_.getScaledHeight() - 32 + 3;
        this.drawTexturedModalRect(p_175186_2_, k2, 0, 84, 182, 5);
        if (j2 > 0) {
            this.drawTexturedModalRect(p_175186_2_, k2, 0, 89, j2, 5);
        }
        this.mc.mcProfiler.endSection();
    }

    @Override
    public void renderExpBar(ScaledResolution p_175176_1_, int p_175176_2_) {
        this.mc.mcProfiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        int i2 = LabyModCore.getMinecraft().getPlayer().xpBarCap();
        if (i2 > 0) {
            int j2 = 182;
            int k2 = (int)(LabyModCore.getMinecraft().getPlayer().experience * 183.0f);
            int l2 = p_175176_1_.getScaledHeight() - 32 + 3;
            this.drawTexturedModalRect(p_175176_2_, l2, 0, 64, 182, 5);
            if (k2 > 0) {
                this.drawTexturedModalRect(p_175176_2_, l2, 0, 69, k2, 5);
            }
        }
        this.mc.mcProfiler.endSection();
        if (LabyModCore.getMinecraft().getPlayer().experienceLevel > 0) {
            this.mc.mcProfiler.startSection("expLevel");
            int k2 = 8453920;
            String s2 = "" + LabyModCore.getMinecraft().getPlayer().experienceLevel;
            int l2 = (p_175176_1_.getScaledWidth() - this.getFontRenderer().getStringWidth(s2)) / 2;
            int i22 = p_175176_1_.getScaledHeight() - 31 - 4;
            this.getFontRenderer().drawString(s2, l2 + 1, i22, 0);
            this.getFontRenderer().drawString(s2, l2 - 1, i22, 0);
            this.getFontRenderer().drawString(s2, l2, i22 + 1, 0);
            this.getFontRenderer().drawString(s2, l2, i22 - 1, 0);
            this.getFontRenderer().drawString(s2, l2, i22, 8453920);
            this.mc.mcProfiler.endSection();
        }
    }

    @Override
    public void renderSelectedItem(ScaledResolution p_181551_1_) {
        this.mc.mcProfiler.startSection("selectedItemName");
        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
            int k2;
            String s2 = this.highlightingItemStack.getDisplayName();
            if (this.highlightingItemStack.hasDisplayName()) {
                s2 = (Object)((Object)EnumChatFormatting.ITALIC) + s2;
            }
            int i2 = (p_181551_1_.getScaledWidth() - this.getFontRenderer().getStringWidth(s2)) / 2;
            int j2 = p_181551_1_.getScaledHeight() - 59;
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

    @Override
    public void renderDemo(ScaledResolution p_175185_1_) {
        this.mc.mcProfiler.startSection("demo");
        String s2 = "";
        s2 = LabyModCore.getMinecraft().getWorld().getTotalWorldTime() >= 120500L ? I18n.format("demo.demoExpired", new Object[0]) : I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - LabyModCore.getMinecraft().getWorld().getTotalWorldTime())));
        int i2 = this.getFontRenderer().getStringWidth(s2);
        this.getFontRenderer().drawStringWithShadow(s2, p_175185_1_.getScaledWidth() - i2 - 10, 5.0f, 0xFFFFFF);
        this.mc.mcProfiler.endSection();
    }

    @Override
    protected boolean showCrosshair() {
        if (this.mc.gameSettings.showDebugInfo && !LabyModCore.getMinecraft().getPlayer().hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
            return false;
        }
        if (!this.mc.playerController.isSpectator()) {
            return true;
        }
        if (this.mc.pointedEntity != null) {
            return true;
        }
        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
            return LabyModCore.getMinecraft().getWorld().getTileEntity(blockpos) instanceof IInventory;
        }
        return false;
    }

    @Override
    public void renderStreamIndicator(ScaledResolution p_180478_1_) {
    }

    private void renderScoreboardNew(ScoreObjective p_180475_1_, ScaledResolution p_180475_2_) {
        Scoreboard scoreboard = p_180475_1_.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(p_180475_1_);
        ArrayList<Score> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>(){

            @Override
            public boolean apply(Score p_apply_1_) {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
        }));
        collection = list.size() > 15 ? Lists.newArrayList(Iterables.skip(list, collection.size() - 15)) : list;
        int i2 = this.getFontRenderer().getStringWidth(p_180475_1_.getDisplayName());
        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s2 = String.valueOf(ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName())) + ": " + (Object)((Object)EnumChatFormatting.RED) + score.getScorePoints();
            i2 = Math.max(i2, this.getFontRenderer().getStringWidth(s2));
        }
        int i22 = collection.size() * this.getFontRenderer().FONT_HEIGHT;
        int j1 = p_180475_2_.getScaledHeight() / 2 + i22 / 3;
        int k1 = 3;
        int l1 = p_180475_2_.getScaledWidth() - i2 - 3;
        int m2 = 0;
        for (Score score2 : collection) {
            ScorePlayerTeam scoreplayerteam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
            String s2 = ScorePlayerTeam.formatPlayerName(scoreplayerteam2, score2.getPlayerName());
            String s3 = "" + (Object)((Object)EnumChatFormatting.RED) + score2.getScorePoints();
            int k2 = j1 - ++m2 * this.getFontRenderer().FONT_HEIGHT;
            int l2 = p_180475_2_.getScaledWidth() - 3 + 2;
            GuiIngameCustom.drawRect(l1 - 2, k2, l2, k2 + this.getFontRenderer().FONT_HEIGHT, 0x50000000);
            this.getFontRenderer().drawString(s2, l1, k2, 0x20FFFFFF);
            this.getFontRenderer().drawString(s3, l2 - this.getFontRenderer().getStringWidth(s3), k2, 0x20FFFFFF);
            if (m2 != collection.size()) continue;
            String s4 = p_180475_1_.getDisplayName();
            GuiIngameCustom.drawRect(l1 - 2, k2 - this.getFontRenderer().FONT_HEIGHT - 1, l2, k2 - 1, 0x60000000);
            GuiIngameCustom.drawRect(l1 - 2, k2 - 1, l2, k2, 0x50000000);
            this.getFontRenderer().drawString(s4, l1 + i2 / 2 - this.getFontRenderer().getStringWidth(s4) / 2, k2 - this.getFontRenderer().FONT_HEIGHT, 0x20FFFFFF);
        }
    }

    private void renderPlayerStatsNew(ScaledResolution p_180477_1_) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            boolean flag;
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int i2 = LabyModCore.getMath().ceiling_float_int(entityplayer.getHealth());
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
            boolean flag2 = false;
            FoodStats foodstats = entityplayer.getFoodStats();
            int k2 = foodstats.getFoodLevel();
            int l2 = foodstats.getPrevFoodLevel();
            float saturation = foodstats.getSaturationLevel();
            boolean hasSaturation = false;
            IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int i22 = p_180477_1_.getScaledWidth() / 2 - 91;
            int j22 = p_180477_1_.getScaledWidth() / 2 + 91;
            int k22 = p_180477_1_.getScaledHeight() - 39;
            float f2 = (float)iattributeinstance.getAttributeValue();
            float f22 = entityplayer.getAbsorptionAmount();
            int l22 = LabyModCore.getMath().ceiling_float_int((f2 + f22) / 2.0f / 10.0f);
            int i3 = Math.max(10 - (l22 - 2), 3);
            int j3 = k22 - (l22 - 1) * i3 - 10;
            float f3 = f22;
            int k3 = entityplayer.getTotalArmorValue();
            int l3 = -1;
            if (entityplayer.isPotionActive(Potion.regeneration)) {
                l3 = this.updateCounter % LabyModCore.getMath().ceiling_float_int(f2 + 5.0f);
            }
            this.mc.mcProfiler.startSection("armor");
            this.mc.getTextureManager().bindTexture(icons);
            int i4 = 0;
            while (i4 < 10) {
                if (k3 > 0) {
                    int j4 = i22 + i4 * 8;
                    if (i4 * 2 + 1 < k3) {
                        this.drawTexturedModalRect(j4, j3, 34, 9, 9, 9);
                    }
                    if (i4 * 2 + 1 == k3) {
                        this.drawTexturedModalRect(j4, j3, 25, 9, 9, 9);
                    }
                    if (i4 * 2 + 1 > k3) {
                        this.drawTexturedModalRect(j4, j3, 16, 9, 9, 9);
                    }
                }
                ++i4;
            }
            this.mc.mcProfiler.endStartSection("health");
            int i5 = LabyModCore.getMath().ceiling_float_int((f2 + f22) / 2.0f) - 1;
            while (i5 >= 0) {
                int j5 = 16;
                if (entityplayer.isPotionActive(Potion.poison)) {
                    j5 += 36;
                } else if (entityplayer.isPotionActive(Potion.wither)) {
                    j5 += 72;
                }
                int k4 = 0;
                if (flag) {
                    k4 = 1;
                }
                int l4 = LabyModCore.getMath().ceiling_float_int((float)(i5 + 1) / 10.0f) - 1;
                int i6 = i22 + i5 % 10 * 8;
                int j6 = k22 - l4 * i3;
                if (i2 <= 4) {
                    j6 += this.rand.nextInt(2);
                }
                if (i5 == l3) {
                    j6 -= 2;
                }
                int k5 = 0;
                if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
                    k5 = 5;
                }
                this.drawTexturedModalRect(i6, j6, 16 + k4 * 9, 9 * k5, 9, 9);
                if (!(LabyMod.getSettings().oldHearts && Permissions.isAllowed(Permissions.Permission.ANIMATIONS) || !flag)) {
                    if (i5 * 2 + 1 < j2) {
                        this.drawTexturedModalRect(i6, j6, j5 + 54, 9 * k5, 9, 9);
                    }
                    if (i5 * 2 + 1 == j2) {
                        this.drawTexturedModalRect(i6, j6, j5 + 63, 9 * k5, 9, 9);
                    }
                }
                if (f3 > 0.0f) {
                    if (f3 == f22 && f22 % 2.0f == 1.0f) {
                        this.drawTexturedModalRect(i6, j6, j5 + 153, 9 * k5, 9, 9);
                    } else {
                        this.drawTexturedModalRect(i6, j6, j5 + 144, 9 * k5, 9, 9);
                    }
                    f3 -= 2.0f;
                } else {
                    if (i5 * 2 + 1 < i2) {
                        this.drawTexturedModalRect(i6, j6, j5 + 36, 9 * k5, 9, 9);
                    }
                    if (i5 * 2 + 1 == i2) {
                        this.drawTexturedModalRect(i6, j6, j5 + 45, 9 * k5, 9, 9);
                    }
                }
                --i5;
            }
            Entity entity = entityplayer.ridingEntity;
            this.mc.getTextureManager().bindTexture(icons);
            if (entity == null) {
                int i8;
                int j7;
                int l5;
                int i7;
                this.mc.mcProfiler.endStartSection("food");
                int k6 = 0;
                while (k6 < 10) {
                    i7 = k22;
                    l5 = 16;
                    j7 = 0;
                    if (entityplayer.isPotionActive(Potion.hunger)) {
                        l5 += 36;
                        j7 = 13;
                    }
                    if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0f && this.updateCounter % (k2 * 3 + 1) == 0) {
                        i7 = k22 + (this.rand.nextInt(3) - 1);
                    }
                    i8 = j22 - k6 * 8 - 9;
                    this.drawTexturedModalRect(i8, i7, 16 + j7 * 9, 27, 9, 9);
                    if (k6 * 2 + 1 < k2) {
                        this.drawTexturedModalRect(i8, i7, l5 + 36, 27, 9, 9);
                    }
                    if (k6 * 2 + 1 == k2) {
                        this.drawTexturedModalRect(i8, i7, l5 + 45, 27, 9, 9);
                    }
                    ++k6;
                }
                if (LabyMod.getSettings().showSaturation && Permissions.isAllowed(Permissions.Permission.SATURATION_BAR)) {
                    k6 = 0;
                    while ((float)k6 < saturation / 2.0f) {
                        i7 = k22 - 10;
                        l5 = 16;
                        j7 = 0;
                        if (entityplayer.isPotionActive(Potion.hunger)) {
                            l5 += 36;
                            j7 = 13;
                        }
                        if (saturation <= 0.0f && (float)this.updateCounter % (saturation * 3.0f + 1.0f) == 0.0f) {
                            i7 = k22 + (this.rand.nextInt(3) - 1);
                        }
                        hasSaturation = true;
                        i8 = j22 - k6 * 8 - 9;
                        this.drawTexturedModalRect(i8, i7, 16 + j7 * 9, 27, 9, 9);
                        if ((float)(k6 * 2 + 1) < saturation) {
                            this.drawTexturedModalRect(i8, i7, l5 + 36, 27, 9, 9);
                        }
                        if ((float)(k6 * 2 + 1) == saturation) {
                            this.drawTexturedModalRect(i8, i7, l5 + 45, 27, 9, 9);
                        }
                        ++k6;
                    }
                }
            } else if (entity instanceof EntityLivingBase) {
                this.mc.mcProfiler.endStartSection("mountHealth");
                EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
                int j8 = (int)Math.ceil(entitylivingbase.getHealth());
                float f4 = entitylivingbase.getMaxHealth();
                int k7 = (int)(f4 + 0.5f) / 2;
                if (k7 > 30) {
                    k7 = 30;
                }
                int j9 = k22;
                int k8 = 0;
                while (k7 > 0) {
                    int l6 = Math.min(k7, 10);
                    k7 -= l6;
                    int i9 = 0;
                    while (i9 < l6) {
                        int j10 = 52;
                        int k9 = 0;
                        int l7 = j22 - i9 * 8 - 9;
                        this.drawTexturedModalRect(l7, j9, 52 + k9 * 9, 9, 9, 9);
                        if (i9 * 2 + 1 + k8 < j8) {
                            this.drawTexturedModalRect(l7, j9, 88, 9, 9, 9);
                        }
                        if (i9 * 2 + 1 + k8 == j8) {
                            this.drawTexturedModalRect(l7, j9, 97, 9, 9, 9);
                        }
                        ++i9;
                    }
                    j9 -= 10;
                    k8 += 20;
                }
            }
            this.mc.mcProfiler.endStartSection("air");
            if (entityplayer.isInsideOfMaterial(Material.water)) {
                int l8 = LabyModCore.getMinecraft().getPlayer().getAir();
                int k10 = LabyModCore.getMath().ceiling_double_int((double)(l8 - 2) * 10.0 / 300.0);
                int i10 = LabyModCore.getMath().ceiling_double_int((double)l8 * 10.0 / 300.0) - k10;
                k22 -= 10;
                if (hasSaturation) {
                    k22 -= 10;
                }
                int l9 = 0;
                while (l9 < k10 + i10) {
                    if (l9 < k10) {
                        this.drawTexturedModalRect(j22 - l9 * 8 - 9, k22, 16, 18, 9, 9);
                    } else {
                        this.drawTexturedModalRect(j22 - l9 * 8 - 9, k22, 25, 18, 9, 9);
                    }
                    ++l9;
                }
            }
            this.mc.mcProfiler.endSection();
        }
    }

    private void renderBossHealthNew() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime;
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i2 = scaledresolution.getScaledWidth();
            int j2 = 182;
            int k2 = i2 / 2 - 91;
            int l2 = (int)(BossStatus.healthScale * 183.0f);
            int i22 = 12;
            if (LabyMod.getSettings().showBossBar) {
                this.drawTexturedModalRect(k2, 12, 0, 74, 182, 5);
                this.drawTexturedModalRect(k2, 12, 0, 74, 182, 5);
                this.drawTexturedModalRect(k2, 12, 0, 74, 182, 5);
                this.drawTexturedModalRect(k2, 12, 0, 74, 182, 5);
                if (l2 > 0) {
                    this.drawTexturedModalRect(k2, 12, 0, 79, l2, 5);
                }
            }
            String s2 = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(s2, i2 / 2 - this.getFontRenderer().getStringWidth(s2) / 2, 2.0f, 0xFFFFFF);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }

    private void renderPumpkinOverlayNew(ScaledResolution p_180476_1_) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0, p_180476_1_.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
        worldrenderer.pos(p_180476_1_.getScaledWidth(), p_180476_1_.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
        worldrenderer.pos(p_180476_1_.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
        worldrenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderVignetteNew(float p_180480_1_, ScaledResolution p_180480_2_) {
        p_180480_1_ = 1.0f - p_180480_1_;
        p_180480_1_ = LabyModCore.getMath().clamp_float(p_180480_1_, 0.0f, 1.0f);
        WorldBorder worldborder = LabyModCore.getMinecraft().getWorld().getWorldBorder();
        float f2 = (float)worldborder.getClosestDistance(LabyModCore.getMinecraft().getPlayer());
        double d0 = Math.min(worldborder.getResizeSpeed() * (double)worldborder.getWarningTime() * 1000.0, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
        double d2 = Math.max((double)worldborder.getWarningDistance(), d0);
        f2 = (double)f2 < d2 ? 1.0f - (float)((double)f2 / d2) : 0.0f;
        this.prevVignetteBrightness += (float)((double)(p_180480_1_ - this.prevVignetteBrightness) * 0.01);
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
        worldrenderer.pos(0.0, p_180480_2_.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
        worldrenderer.pos(p_180480_2_.getScaledWidth(), p_180480_2_.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
        worldrenderer.pos(p_180480_2_.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
        worldrenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    private void renderPortalNew(float p_180474_1_, ScaledResolution p_180474_2_) {
        if (p_180474_1_ < 1.0f) {
            p_180474_1_ *= p_180474_1_;
            p_180474_1_ *= p_180474_1_;
            p_180474_1_ = p_180474_1_ * 0.8f + 0.2f;
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, p_180474_1_);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
        float f2 = textureatlassprite.getMinU();
        float f22 = textureatlassprite.getMinV();
        float f3 = textureatlassprite.getMaxU();
        float f4 = textureatlassprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0, p_180474_2_.getScaledHeight(), -90.0).tex(f2, f4).endVertex();
        worldrenderer.pos(p_180474_2_.getScaledWidth(), p_180474_2_.getScaledHeight(), -90.0).tex(f3, f4).endVertex();
        worldrenderer.pos(p_180474_2_.getScaledWidth(), 0.0, -90.0).tex(f3, f22).endVertex();
        worldrenderer.pos(0.0, 0.0, -90.0).tex(f2, f22).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderHotbarItemNew(int index, int xPos, int yPos, float partialTicks, EntityPlayer p_175184_5_) {
        ItemStack itemstack = p_175184_5_.inventory.mainInventory[index];
        if (itemstack != null) {
            float f2 = (float)itemstack.animationsToGo - partialTicks;
            if (f2 > 0.0f) {
                GlStateManager.pushMatrix();
                float f22 = 1.0f + f2 / 5.0f;
                GlStateManager.translate(xPos + 8, yPos + 12, 0.0f);
                GlStateManager.scale(1.0f / f22, (f22 + 1.0f) / 2.0f, 1.0f);
                GlStateManager.translate(-(xPos + 8), -(yPos + 12), 0.0f);
            }
            this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);
            if (f2 > 0.0f) {
                GlStateManager.popMatrix();
            }
            this.itemRenderer.renderItemOverlays(LabyModCore.getMinecraft().getFontRenderer(), itemstack, xPos, yPos);
        }
    }

    @Override
    public void updateTick() {
        if (this.recordPlayingUpFor > 0) {
            --this.recordPlayingUpFor;
        }
        if (this.field_175195_w > 0) {
            --this.field_175195_w;
            if (this.field_175195_w <= 0) {
                this.field_175201_x = "";
                this.field_175200_y = "";
            }
        }
        ++this.updateCounter;
        if (LabyModCore.getMinecraft().getPlayer() != null) {
            ItemStack itemstack = LabyModCore.getMinecraft().getPlayer().inventory.getCurrentItem();
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

    @Override
    public void setRecordPlayingMessage(String p_73833_1_) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", p_73833_1_), true);
    }

    @Override
    public void setRecordPlaying(String p_110326_1_, boolean p_110326_2_) {
        this.recordPlaying = p_110326_1_;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = p_110326_2_;
    }

    @Override
    public void displayTitle(String p_175178_1_, String p_175178_2_, int p_175178_3_, int p_175178_4_, int p_175178_5_) {
        if (p_175178_1_ == null && p_175178_2_ == null && p_175178_3_ < 0 && p_175178_4_ < 0 && p_175178_5_ < 0) {
            this.field_175201_x = "";
            this.field_175200_y = "";
            this.field_175195_w = 0;
        } else if (p_175178_1_ != null) {
            this.field_175201_x = p_175178_1_;
            this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
        } else if (p_175178_2_ != null) {
            this.field_175200_y = p_175178_2_;
        } else {
            if (p_175178_3_ >= 0) {
                this.field_175199_z = p_175178_3_;
            }
            if (p_175178_4_ >= 0) {
                this.field_175192_A = p_175178_4_;
            }
            if (p_175178_5_ >= 0) {
                this.field_175193_B = p_175178_5_;
            }
            if (this.field_175195_w > 0) {
                this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
            }
        }
    }

    @Override
    public void setRecordPlaying(IChatComponent p_175188_1_, boolean p_175188_2_) {
        this.setRecordPlaying(p_175188_1_.getUnformattedText(), p_175188_2_);
    }

    @Override
    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    @Override
    public int getUpdateCounter() {
        return this.updateCounter;
    }

    @Override
    public GuiSpectator getSpectatorGui() {
        return this.spectatorGui;
    }

    @Override
    public GuiPlayerTabOverlay getTabList() {
        return this.overlayPlayerList;
    }

    public void func_181029_i() {
    }
}

