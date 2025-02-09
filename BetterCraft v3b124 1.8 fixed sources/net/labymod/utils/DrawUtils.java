/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils;

import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.labymod.core.LabyModCore;
import net.labymod.core.WorldRendererAdapter;
import net.labymod.main.LabyMod;
import net.labymod.utils.texture.PlayerSkinTextureCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class DrawUtils
extends Gui {
    private Minecraft mc;
    public FontRenderer fontRenderer;
    private ScaledResolution scaledResolution;
    private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
    private PlayerSkinTextureCache playerSkinTextureCache = new PlayerSkinTextureCache(Minecraft.getMinecraft().getSkinManager(), Minecraft.getMinecraft().getSessionService());

    public DrawUtils() {
        this.mc = Minecraft.getMinecraft();
        this.scaledResolution = new ScaledResolution(this.mc);
        this.fontRenderer = LabyModCore.getCoreAdapter() == null ? null : LabyModCore.getMinecraft().getFontRenderer();
    }

    public void drawBox(int left, int top, int right, int bottom) {
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.9f);
        DrawUtils.drawRect(left, top, right, bottom, Color.WHITE.getRGB());
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.9f);
        DrawUtils.drawRect(left, top, right, top + 1, Color.WHITE.getRGB());
        DrawUtils.drawRect(left, top, left + 1, bottom, Color.WHITE.getRGB());
        DrawUtils.drawRect(right - 1, top, right, bottom, Color.WHITE.getRGB());
        DrawUtils.drawRect(left, bottom - 1, right, bottom, Color.WHITE.getRGB());
    }

    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    public void setFontRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    public void bindTexture(ResourceLocation resourceLocation) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
    }

    public PlayerSkinTextureCache getPlayerSkinTextureCache() {
        return this.playerSkinTextureCache;
    }

    public void bindTexture(String resourceLocation) {
        this.bindTexture(new ResourceLocation(resourceLocation));
    }

    public double getCustomScaling() {
        double factor = 1.0 + LabyMod.getSettings().moduleEditorZoom * 0.03;
        while ((double)Minecraft.getMinecraft().displayWidth / factor < 320.0) {
            factor -= 0.1;
        }
        while ((double)Minecraft.getMinecraft().displayHeight / factor < 240.0) {
            factor -= 0.1;
        }
        return factor;
    }

    public int getWidth() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public int getHeight() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }

    public void setScaledResolution(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public void drawString(String text, double x2, double y2) {
        this.fontRenderer.drawString(text, (float)x2, (float)y2, 0xFFFFFF, true);
    }

    public void drawStringWithShadow(String text, double x2, double y2, int color) {
        LabyModCore.getMinecraft().getFontRenderer().drawStringWithShadow(text, (float)x2, (float)y2, color);
    }

    public void drawRightString(String text, double x2, double y2) {
        this.drawString(text, x2 - (double)this.getStringWidth(text), y2);
    }

    public void drawRightStringWithShadow(String text, int x2, int y2, int color) {
        LabyModCore.getMinecraft().getFontRenderer().drawStringWithShadow(text, x2 - this.getStringWidth(text), y2, color);
    }

    public void drawCenteredString(String text, double x2, double y2) {
        this.drawString(text, x2 - (double)(this.getStringWidth(text) / 2), y2);
    }

    public void drawString(String text, double x2, double y2, double size) {
        GL11.glPushMatrix();
        GL11.glScaled(size, size, size);
        this.drawString(text, x2 / size, y2 / size);
        GL11.glPopMatrix();
    }

    public void drawCenteredString(String text, double x2, double y2, double size) {
        GL11.glPushMatrix();
        GL11.glScaled(size, size, size);
        this.drawCenteredString(text, x2 / size, y2 / size);
        GL11.glPopMatrix();
    }

    public void drawRightString(String text, double x2, double y2, double size) {
        GL11.glPushMatrix();
        GL11.glScaled(size, size, size);
        this.drawString(text, x2 / size - (double)this.getStringWidth(text), y2 / size);
        GL11.glPopMatrix();
    }

    public void drawItem(ItemStack item, double xPosition, double yPosition, String value) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableCull();
        if (item.hasEffect()) {
            GlStateManager.enableDepth();
            this.renderItemIntoGUI(item, xPosition, yPosition);
            GlStateManager.disableDepth();
        } else {
            this.renderItemIntoGUI(item, xPosition, yPosition);
        }
        this.renderItemOverlayIntoGUI(item, xPosition, yPosition, value);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
    }

    public void renderItemIntoGUI(ItemStack stack, double x2, double y2) {
        LabyModCore.getRenderImplementation().renderItemIntoGUI(stack, x2, y2);
    }

    private void renderItemOverlayIntoGUI(ItemStack stack, double xPosition, double yPosition, String text) {
        LabyModCore.getRenderImplementation().renderItemOverlayIntoGUI(stack, xPosition, yPosition, text);
    }

    public int getStringWidth(String text) {
        return this.fontRenderer.getStringWidth(text);
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i2 = left;
            left = right;
            right = i2;
        }
        if (top < bottom) {
            double j2 = top;
            top = bottom;
            bottom = j2;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f22 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f2, f1, f22, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0).endVertex();
        worldrenderer.pos(right, bottom, 0.0).endVertex();
        worldrenderer.pos(right, top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public boolean drawRect(int mouseX, int mouseY, double left, double top, double right, double bottom, int color, int hoverColor) {
        boolean hover = (double)mouseX > left && (double)mouseX < right && (double)mouseY > top && (double)mouseY < bottom;
        DrawUtils.drawRect(left, top, right, bottom, hover ? hoverColor : color);
        return hover;
    }

    public boolean drawRect(int mouseX, int mouseY, String displayString, double left, double top, double right, double bottom, int color, int hoverColor) {
        boolean hover = (double)mouseX > left && (double)mouseX < right && (double)mouseY > top && (double)mouseY < bottom;
        DrawUtils.drawRect(left, top, right, bottom, hover ? hoverColor : color);
        this.drawCenteredString(displayString, left + (right - left) / 2.0, top + (bottom - top) / 2.0 - 4.0);
        return hover;
    }

    public void drawRectangle(int left, int top, int right, int bottom, int color) {
        DrawUtils.drawRect(left, top, right, bottom, color);
    }

    public static void startScissor(float startX, float startY, float endX, float endY) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        float width = endX - startX;
        float height = endY - startY;
        assert (Minecraft.getMinecraft().currentScreen != null);
        GuiScreen cfr_ignored_0 = Minecraft.getMinecraft().currentScreen;
        float bottomY = (float)GuiScreen.height - endY;
        float factor = scaledResolution.getScaleFactor();
        float scissorX = startX * factor;
        float scissorY = bottomY * factor;
        float scissorWidth = width * factor;
        float scissorHeight = height * factor;
        GL11.glScissor((int)scissorX, (int)scissorY, (int)scissorWidth, (int)scissorHeight);
        GL11.glEnable(3089);
    }

    public static void stopScissor() {
        GL11.glDisable(3089);
    }

    public void drawGradientShadowTop(double y2, double left, double right) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        GlStateManager.disableDepth();
        boolean i1 = true;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture2D();
        this.mc.getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getOptionsBackground());
        int rainbow = ColorUtils.rainbowEffect();
        float r2 = (float)(rainbow >> 16 & 0xFF) / 255.0f;
        float g2 = (float)(rainbow >> 8 & 0xFF) / 255.0f;
        float b2 = (float)(rainbow & 0xFF) / 255.0f;
        float a2 = (float)(rainbow >> 24 & 0xFF) / 255.0f;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(left, y2 + (double)i1, 0.0).tex(0.0, 1.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(right, y2 + (double)i1, 0.0).tex(1.0, 1.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(right, y2, 0.0).tex(1.0, 0.0).color(r2, g2, b2, a2).endVertex();
        worldrenderer.pos(left, y2, 0.0).tex(0.0, 0.0).color(r2, g2, b2, a2).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    public void drawGradientShadowBottom(double y2, double left, double right) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        GlStateManager.disableDepth();
        boolean i1 = true;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture2D();
        this.mc.getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getOptionsBackground());
        int rainbow = ColorUtils.rainbowEffect();
        float r2 = (float)(rainbow >> 16 & 0xFF) / 255.0f;
        float g2 = (float)(rainbow >> 8 & 0xFF) / 255.0f;
        float b2 = (float)(rainbow & 0xFF) / 255.0f;
        float a2 = (float)(rainbow >> 24 & 0xFF) / 255.0f;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(left, y2, 0.0).tex(0.0, 1.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(right, y2, 0.0).tex(1.0, 1.0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(right, y2 - (double)i1, 0.0).tex(1.0, 0.0).color(r2, g2, b2, a2).endVertex();
        worldrenderer.pos(left, y2 - (double)i1, 0.0).tex(0.0, 0.0).color(r2, g2, b2, a2).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    public void drawGradientShadowLeft(double x2, double top, double bottom) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        int i1 = 4;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.enableTexture2D();
        this.mc.getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getOptionsBackground());
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x2 + (double)i1, bottom, 0.0).tex(1.0, 0.0).color(0, 0, 0, 0).endVertex();
        worldrenderer.pos(x2 + (double)i1, top, 0.0).tex(1.0, 1.0).color(0, 0, 0, 0).endVertex();
        worldrenderer.pos(x2, top, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(x2, bottom, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    public void drawGradientShadowRight(double x2, double top, double bottom) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        int i1 = 4;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.enableTexture2D();
        this.mc.getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getOptionsBackground());
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x2, bottom, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(x2, top, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(x2 - (double)i1, top, 0.0).tex(0.0, 1.0).color(0, 0, 0, 0).endVertex();
        worldrenderer.pos(x2 - (double)i1, bottom, 0.0).tex(0.0, 0.0).color(0, 0, 0, 0).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    public void drawIngameBackground() {
        this.drawGradientRect(0, 0, this.getWidth(), this.getHeight(), -1072689136, -804253680);
    }

    public void drawAutoDimmedBackground(double d2) {
        if (LabyMod.getInstance().isInGame()) {
            this.drawIngameBackground();
        } else {
            this.drawDimmedBackground((int)d2);
        }
    }

    public void drawAutoDimmedBackground(int left, int top, int right, int bottom) {
        if (LabyMod.getInstance().isInGame()) {
            this.drawGradientRect(left, top, right, bottom, -1072689136, -804253680);
        } else {
            this.drawDimmedOverlayBackground(left, top, right, bottom);
        }
    }

    public void drawBackground(int tint) {
        this.drawBackground(tint, 0.0, 64);
    }

    public void drawDimmedBackground(int scroll) {
        this.drawBackground(0, -scroll, 32);
    }

    public void drawBackground(int tint, double scrolling, int brightness) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getOptionsBackground());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, this.getHeight(), 0.0).tex(0.0, ((double)this.getHeight() + scrolling) / 32.0 + (double)tint).color(brightness, brightness, brightness, 255).endVertex();
        worldrenderer.pos(this.getWidth(), this.getHeight(), 0.0).tex((float)this.getWidth() / 32.0f, ((double)this.getHeight() + scrolling) / 32.0 + (double)tint).color(brightness, brightness, brightness, 255).endVertex();
        worldrenderer.pos(this.getWidth(), 0.0, 0.0).tex((float)this.getWidth() / 32.0f, (double)tint + scrolling / 32.0).color(brightness, brightness, brightness, 255).endVertex();
        worldrenderer.pos(0.0, 0.0, 0.0).tex(0.0, (double)tint + scrolling / 32.0).color(brightness, brightness, brightness, 255).endVertex();
        tessellator.draw();
    }

    public void drawOverlayBackground(int startY, int endY) {
        int endAlpha = 255;
        int startAlpha = 255;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getOptionsBackground());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, endY, 0.0).tex(0.0, (float)endY / 32.0f).color(64, 64, 64, endAlpha).endVertex();
        worldrenderer.pos(0 + this.getWidth(), endY, 0.0).tex((float)this.getWidth() / 32.0f, (float)endY / 32.0f).color(64, 64, 64, endAlpha).endVertex();
        worldrenderer.pos(0 + this.getWidth(), startY, 0.0).tex((float)this.getWidth() / 32.0f, (float)startY / 32.0f).color(64, 64, 64, startAlpha).endVertex();
        worldrenderer.pos(0.0, startY, 0.0).tex(0.0, (float)startY / 32.0f).color(64, 64, 64, startAlpha).endVertex();
        tessellator.draw();
    }

    public void drawDimmedOverlayBackground(int left, int top, int right, int bottom) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getOptionsBackground());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f2 = 32.0f;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(left, bottom, 0.0).tex((float)left / f2, (float)bottom / f2).color(32, 32, 32, 255).endVertex();
        worldrenderer.pos(right, bottom, 0.0).tex((float)right / f2, (float)bottom / f2).color(32, 32, 32, 255).endVertex();
        worldrenderer.pos(right, top, 0.0).tex((float)right / f2, (float)top / f2).color(32, 32, 32, 255).endVertex();
        worldrenderer.pos(left, top, 0.0).tex((float)left / f2, (float)top / f2).color(32, 32, 32, 255).endVertex();
        tessellator.draw();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableDepth();
    }

    public void drawOverlayBackground(int startX, int startY, int width, int endY) {
        this.drawOverlayBackground(startX, startY, width, endY, 64);
    }

    public void drawOverlayBackground(int startX, int startY, int width, int endY, int brightness) {
        int endAlpha = 255;
        int startAlpha = 255;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getOptionsBackground());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(startX, endY, 0.0).tex(0.0, (float)endY / 32.0f).color(brightness, brightness, brightness, endAlpha).endVertex();
        worldrenderer.pos(startX + width, endY, 0.0).tex((float)width / 32.0f, (float)endY / 32.0f).color(brightness, brightness, brightness, endAlpha).endVertex();
        worldrenderer.pos(startX + width, startY, 0.0).tex((float)width / 32.0f, (float)startY / 32.0f).color(brightness, brightness, brightness, startAlpha).endVertex();
        worldrenderer.pos(startX, startY, 0.0).tex(0.0, (float)startY / 32.0f).color(brightness, brightness, brightness, startAlpha).endVertex();
        tessellator.draw();
    }

    public void drawTexturedModalRect(double x2, double y2, double textureX, double textureY, double width, double height) {
        this.drawTexturedModalRect((int)x2, (int)y2, (int)textureX, (int)textureY, (int)width, (int)height);
    }

    public void drawTexturedModalRect(double left, double top, double right, double bottom) {
        double textureX = 0.0;
        double textureY = 0.0;
        double x2 = left;
        double y2 = top;
        double width = right - left;
        double height = bottom - top;
        float f2 = 0.00390625f;
        float f1 = 0.00390625f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x2 + 0.0, y2 + height, this.zLevel).tex((float)(textureX + 0.0) * f2, (float)(textureY + height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + height, this.zLevel).tex((float)(textureX + width) * f2, (float)(textureY + height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + 0.0, this.zLevel).tex((float)(textureX + width) * f2, (float)(textureY + 0.0) * f1).endVertex();
        worldrenderer.pos(x2 + 0.0, y2 + 0.0, this.zLevel).tex((float)(textureX + 0.0) * f2, (float)(textureY + 0.0) * f1).endVertex();
        tessellator.draw();
    }

    public void drawTexture(double x2, double y2, double imageWidth, double imageHeight, double maxWidth, double maxHeight, float alpha) {
        GL11.glPushMatrix();
        double sizeWidth = maxWidth / imageWidth;
        double sizeHeight = maxHeight / imageHeight;
        GL11.glScaled(sizeWidth, sizeHeight, 0.0);
        if (alpha <= 1.0f) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
        }
        this.drawTexturedModalRect(x2 / sizeWidth, y2 / sizeHeight, x2 / sizeWidth + imageWidth, y2 / sizeHeight + imageHeight);
        if (alpha <= 1.0f) {
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
        }
        GL11.glPopMatrix();
    }

    public void drawRawTexture(double x2, double y2, double imageWidth, double imageHeight, double maxWidth, double maxHeight) {
        GL11.glPushMatrix();
        double sizeWidth = maxWidth / imageWidth;
        double sizeHeight = maxHeight / imageHeight;
        GL11.glScaled(sizeWidth, sizeHeight, 0.0);
        this.drawTexturedModalRect(x2 / sizeWidth, y2 / sizeHeight, x2 / sizeWidth + imageWidth, y2 / sizeHeight + imageHeight);
        GL11.glPopMatrix();
    }

    public void drawTexture(double x2, double y2, double imageWidth, double imageHeight, double maxWidth, double maxHeight) {
        this.drawTexture(x2, y2, imageWidth, imageHeight, maxWidth, maxHeight, 1.0f);
    }

    public void drawTexture(double x2, double y2, double texturePosX, double texturePosY, double imageWidth, double imageHeight, double maxWidth, double maxHeight, float alpha) {
        GL11.glPushMatrix();
        double sizeWidth = maxWidth / imageWidth;
        double sizeHeight = maxHeight / imageHeight;
        GL11.glScaled(sizeWidth, sizeHeight, 0.0);
        if (alpha <= 1.0f) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
        }
        this.drawUVTexture(x2 / sizeWidth, y2 / sizeHeight, texturePosX, texturePosY, x2 / sizeWidth + imageWidth - x2 / sizeWidth, y2 / sizeHeight + imageHeight - y2 / sizeHeight);
        if (alpha <= 1.0f) {
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
        }
        GL11.glPopMatrix();
    }

    public void drawTexture(double x2, double y2, double texturePosX, double texturePosY, double imageWidth, double imageHeight, double maxWidth, double maxHeight) {
        this.drawTexture(x2, y2, texturePosX, texturePosY, imageWidth, imageHeight, maxWidth, maxHeight, 1.0f);
    }

    private void drawUVTexture(double x2, double y2, double textureX, double textureY, double width, double height) {
        float f2 = 0.00390625f;
        float f1 = 0.00390625f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x2 + 0.0, y2 + height, this.zLevel).tex((float)(textureX + 0.0) * f2, (float)(textureY + height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + height, this.zLevel).tex((float)(textureX + width) * f2, (float)(textureY + height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + 0.0, this.zLevel).tex((float)(textureX + width) * f2, (float)(textureY + 0.0) * f1).endVertex();
        worldrenderer.pos(x2 + 0.0, y2 + 0.0, this.zLevel).tex((float)(textureX + 0.0) * f2, (float)(textureY + 0.0) * f1).endVertex();
        tessellator.draw();
    }

    public static void drawEntityOnScreen(int x2, int y2, int size, float mouseX, float mouseY, int rotationX, int rotationY, int rotationZ, EntityLivingBase entity) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x2, y2, 100.0f);
        GlStateManager.scale((float)(-size) - 30.0f, (float)size + 30.0f, size);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float var5 = entity.prevRenderYawOffset;
        float var6 = entity.renderYawOffset;
        float var7 = entity.rotationYaw;
        float var8 = entity.rotationPitch;
        float var9 = entity.prevRotationYawHead;
        float var10 = entity.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f + (float)rotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(rotationY, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(rotationZ, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        entity.renderYawOffset = (float)Math.atan(mouseX / 40.0f) * 20.0f;
        entity.rotationYaw = (float)Math.atan(mouseX / 40.0f) * 40.0f;
        entity.rotationPitch = -((float)Math.atan(mouseY / 40.0f)) * 20.0f;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        entity.prevRenderYawOffset = entity.renderYawOffset;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager var11 = Minecraft.getMinecraft().getRenderManager();
        var11.setPlayerViewY(180.0f);
        var11.setRenderShadow(false);
        LabyModCore.getRenderImplementation().renderEntity(var11, entity, 0.0, rotationY == 0 ? 0.0 : -1.0, 0.0, 0.0f, 1.0f, false);
        var11.setRenderShadow(true);
        entity.renderYawOffset = var6;
        entity.rotationYaw = var7;
        entity.rotationPitch = var8;
        entity.prevRenderYawOffset = var5;
        entity.prevRotationYawHead = var9;
        entity.rotationYawHead = var10;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public String trimStringToWidth(String text, int width) {
        if (text == null) {
            return text;
        }
        return this.fontRenderer.trimStringToWidth(text, width, false);
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        if (wrapWidth < 10) {
            wrapWidth = 10;
        }
        return this.fontRenderer.listFormattedStringToWidth(str, wrapWidth);
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth, int maxLines) {
        List<String> list = this.listFormattedStringToWidth(str, wrapWidth);
        if (list.size() < maxLines) {
            return list;
        }
        ArrayList<String> output = new ArrayList<String>();
        int count = 0;
        for (String line : list) {
            output.add(line);
            if (++count >= maxLines) break;
        }
        return output;
    }

    public void drawHoveringText(int x2, int y2, String ... textLines) {
        if (textLines.length != 0) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i2 = 0;
            String[] stringArray = textLines;
            int n2 = textLines.length;
            int n3 = 0;
            while (n3 < n2) {
                String s2 = stringArray[n3];
                int j2 = this.fontRenderer.getStringWidth(s2);
                if (j2 > i2) {
                    i2 = j2;
                }
                ++n3;
            }
            int l1 = x2 + 7;
            int i22 = y2 - 12;
            int k2 = 8;
            if (textLines.length > 1) {
                k2 += 2 + (textLines.length - 1) * 10;
            }
            if (i22 < 5) {
                i22 = 5;
            }
            if (l1 + i2 > this.getWidth()) {
                l1 -= 12 + i2;
            }
            if (i22 + k2 + 6 > this.getHeight()) {
                i22 = this.getHeight() - k2 - 6;
            }
            this.zLevel = 300.0f;
            int l2 = -267386864;
            this.drawGradientRect(l1 - 3, i22 - 4, l1 + i2 + 3, i22 - 3, l2, l2);
            this.drawGradientRect(l1 - 3, i22 + k2 + 3, l1 + i2 + 3, i22 + k2 + 4, l2, l2);
            this.drawGradientRect(l1 - 3, i22 - 3, l1 + i2 + 3, i22 + k2 + 3, l2, l2);
            this.drawGradientRect(l1 - 4, i22 - 3, l1 - 3, i22 + k2 + 3, l2, l2);
            this.drawGradientRect(l1 + i2 + 3, i22 - 3, l1 + i2 + 4, i22 + k2 + 3, l2, l2);
            int i1 = 0x505000FF;
            int j1 = (i1 & 0xFEFEFE) >> 1 | i1 & 0xFF000000;
            this.drawGradientRect(l1 - 3, i22 - 3 + 1, l1 - 3 + 1, i22 + k2 + 3 - 1, i1, j1);
            this.drawGradientRect(l1 + i2 + 2, i22 - 3 + 1, l1 + i2 + 3, i22 + k2 + 3 - 1, i1, j1);
            this.drawGradientRect(l1 - 3, i22 - 3, l1 + i2 + 3, i22 - 3 + 1, i1, i1);
            this.drawGradientRect(l1 - 3, i22 + k2 + 2, l1 + i2 + 3, i22 + k2 + 3, j1, j1);
            int k1 = 0;
            while (k1 < textLines.length) {
                String s1 = textLines[k1];
                this.fontRenderer.drawStringWithShadow(s1, l1, i22, -1);
                if (k1 == 0) {
                    i22 += 2;
                }
                i22 += 10;
                ++k1;
            }
            this.zLevel = 0.0f;
        }
    }

    public void drawHoveringTextBoxField(int x2, int y2, int width, int height) {
        this.zLevel = 300.0f;
        int color1 = -267386864;
        this.drawGradientRect(x2 - 3, y2 - 4, x2 + width + 3, y2 - 3, color1, color1);
        this.drawGradientRect(x2 - 3, y2 + height + 3, x2 + width + 3, y2 + height + 4, color1, color1);
        this.drawGradientRect(x2 - 3, y2 - 3, x2 + width + 3, y2 + height + 3, color1, color1);
        this.drawGradientRect(x2 - 4, y2 - 3, x2 - 3, y2 + height + 3, color1, color1);
        this.drawGradientRect(x2 + width + 3, y2 - 3, x2 + width + 4, y2 + height + 3, color1, color1);
        int color2 = 0x505000FF;
        int color3 = (color2 & 0xFEFEFE) >> 1 | color2 & 0xFF000000;
        this.drawGradientRect(x2 - 3, y2 - 3 + 1, x2 - 3 + 1, y2 + height + 3 - 1, color2, color3);
        this.drawGradientRect(x2 + width + 2, y2 - 3 + 1, x2 + width + 3, y2 + height + 3 - 1, color2, color3);
        this.drawGradientRect(x2 - 3, y2 - 3, x2 + width + 3, y2 - 3 + 1, color2, color2);
        this.drawGradientRect(x2 - 3, y2 + height + 2, x2 + width + 3, y2 + height + 3, color3, color3);
        this.zLevel = 0.0f;
        GlStateManager.disableDepth();
    }

    public void drawPlayerHead(ResourceLocation resourceLocation, int x2, int y2, int size) {
        if (resourceLocation == null) {
            resourceLocation = DefaultPlayerSkin.getDefaultSkin(UUID.randomUUID());
        }
        GlStateManager.enableAlpha();
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        DrawUtils.drawScaledCustomSizeModalRect(x2, y2, 8.0f, 8.0f, 8, 8, size, size, 64.0f, 64.0f);
        DrawUtils.drawScaledCustomSizeModalRect(x2, y2, 40.0f, 8.0f, 8, 8, size, size, 64.0f, 64.0f);
    }

    public void drawPlayerHead(GameProfile gameProfile, int x2, int y2, int size) {
        ResourceLocation resourceLocation = this.playerSkinTextureCache.getSkinTexture(gameProfile);
        this.drawPlayerHead(resourceLocation, x2, y2, size);
    }

    public void drawPlayerHead(String username, int x2, int y2, int size) {
        ResourceLocation resourceLocation = this.playerSkinTextureCache.getSkinTexture(username);
        this.drawPlayerHead(resourceLocation, x2, y2, size);
    }

    public void drawPlayerHead(UUID uuid, int x2, int y2, int size) {
        ResourceLocation resourceLocation = this.playerSkinTextureCache.getSkinTexture(uuid);
        this.drawPlayerHead(resourceLocation, x2, y2, size);
    }

    @Deprecated
    public void drawMinotarHead(GameProfile gameProfile, int x2, int y2, int size) {
        this.drawPlayerHead(gameProfile, x2, y2, size);
    }

    public void drawRectBorder(double left, double top, double right, double bottom, int color, double thickness) {
        DrawUtils.drawRect(left + thickness, top, right - thickness, top + thickness, color);
        DrawUtils.drawRect(right - thickness, top, right, bottom, color);
        DrawUtils.drawRect(left + thickness, bottom - thickness, right - thickness, bottom, color);
        DrawUtils.drawRect(left, top, left + thickness, bottom, color);
    }

    public void drawImageUrl(String url, double x2, double y2, double imageWidth, double imageHeight, double maxWidth, double maxHeight) {
        this.drawDynamicImageUrl(url, url, x2, y2, imageWidth, imageHeight, maxWidth, maxHeight);
    }

    public void drawDynamicImageUrl(String identifier, String url, double x2, double y2, double imageWidth, double imageHeight, double maxWidth, double maxHeight) {
        ResourceLocation resourceLocation = LabyMod.getInstance().getDynamicTextureManager().getTexture(identifier, url);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        LabyMod.getInstance().getDrawUtils().drawTexture(x2, y2, imageWidth, imageHeight, maxWidth, maxHeight);
    }

    public void renderSkull(GameProfile gameProfile) {
        ModelSkeletonHead modelbase = this.humanoidHead;
        ResourceLocation resourceSkin = this.playerSkinTextureCache.getSkinTexture(gameProfile);
        if (resourceSkin != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourceSkin);
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlpha();
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            GlStateManager.translate(0.0f, 0.2f, 0.0f);
            modelbase.render(null, 0.0f, 0.0f, 0.0f, 180.0f, 0.0f, 0.0625f);
            GlStateManager.popMatrix();
        }
    }
}

