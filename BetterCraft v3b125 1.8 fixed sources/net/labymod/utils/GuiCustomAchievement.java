/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

public class GuiCustomAchievement
extends Gui {
    private Minecraft mc;
    private int width;
    private int height;
    private String achievementTitle;
    private String achievementDescription;
    private long notificationTime;
    private GameProfile gameProfile;
    private String imageURL;

    public GuiCustomAchievement(Minecraft mc2) {
        this.mc = mc2;
    }

    public void displayAchievement(String title, String description) {
        this.achievementTitle = title;
        this.achievementDescription = description;
        this.notificationTime = Minecraft.getSystemTime();
        this.gameProfile = null;
        this.imageURL = null;
    }

    public void displayAchievement(GameProfile gameProfile, String title, String description) {
        this.achievementTitle = title;
        this.achievementDescription = description;
        this.notificationTime = Minecraft.getSystemTime();
        this.gameProfile = gameProfile;
        this.imageURL = null;
    }

    public void displayAchievement(String imageURL, String title, String description) {
        this.achievementTitle = title;
        this.achievementDescription = description;
        this.notificationTime = Minecraft.getSystemTime();
        this.imageURL = imageURL;
        this.gameProfile = null;
    }

    private void updateAchievementWindowScale() {
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, this.width, this.height, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
    }

    public void updateAchievementWindow() {
        if (this.notificationTime != 0L) {
            double d0 = (double)(Minecraft.getSystemTime() - this.notificationTime) / 3000.0;
            if (d0 < 0.0 || d0 > 1.0) {
                this.notificationTime = 0L;
                return;
            }
            this.updateAchievementWindowScale();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            double d2 = d0 * 2.0;
            if (d2 > 1.0) {
                d2 = 2.0 - d2;
            }
            d2 *= 4.0;
            if ((d2 = 1.0 - d2) < 0.0) {
                d2 = 0.0;
            }
            d2 *= d2;
            d2 *= d2;
            int i2 = this.width - 160;
            int j2 = 0 - (int)(d2 * 36.0);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture2D();
            this.mc.getTextureManager().bindTexture(ModTextures.ACHIEVEMENT);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(i2, j2, 96, 202, 160, 32);
            DrawUtils draw = LabyMod.getInstance().getDrawUtils();
            int startX = i2 + (this.gameProfile == null && this.imageURL == null ? 8 : 30);
            String title = draw.trimStringToWidth(this.achievementTitle, this.width - startX - 10);
            List<String> descriptions = draw.listFormattedStringToWidth(this.achievementDescription, (int)((double)(this.width - startX - 10) / 0.7), 2);
            if (descriptions.size() == 1) {
                draw.drawString(draw.fontRenderer, title, startX, j2 + 7, -256);
                draw.drawString(this.achievementDescription, startX, j2 + 5 + 13, 0.7);
            } else {
                draw.drawString(draw.fontRenderer, title, startX, j2 + 5, -256);
                int y2 = j2 + 14 - 7;
                for (String line : descriptions) {
                    DrawUtils drawUtils = draw;
                    String text = line;
                    double x2 = startX;
                    drawUtils.drawString(text, x2, y2 += 7, 0.75);
                }
            }
            if (this.gameProfile != null) {
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.disableLighting();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableColorMaterial();
                GlStateManager.disableLighting();
                GlStateManager.color(1.0f, 1.0f, 1.0f);
                draw.drawPlayerHead(this.gameProfile, i2 + 8, j2 + 8, 16);
            }
            if (this.imageURL != null) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                LabyMod.getInstance().getDrawUtils().drawImageUrl(this.imageURL, i2 + 8, j2 + 8, 255.0, 255.0, 16.0, 16.0);
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
    }

    public void clearAchievements() {
        this.notificationTime = 0L;
        this.gameProfile = null;
        this.imageURL = null;
    }
}

