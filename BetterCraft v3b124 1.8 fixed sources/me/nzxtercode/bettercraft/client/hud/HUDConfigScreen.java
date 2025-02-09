/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.hud;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Predicate;
import me.nzxtercode.bettercraft.client.hud.HUDManager;
import me.nzxtercode.bettercraft.client.hud.IRender;
import me.nzxtercode.bettercraft.client.hud.ScreenPosition;
import me.nzxtercode.bettercraft.client.hud.ScreenResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;

public class HUDConfigScreen
extends GuiScreen {
    private HashMap<IRender, ScreenPosition> renderers = new HashMap();
    private Optional<IRender> selectedRenderer = Optional.empty();
    private int prevX;
    private int prevY;
    private boolean clear;

    public HUDConfigScreen(HUDManager hm2, boolean clear) {
        this.clear = clear;
        for (IRender irender : hm2.getRegisteredRenderers()) {
            if (!irender.isEnabled()) continue;
            ScreenPosition screenposition = irender.load();
            if (screenposition == null) {
                screenposition = ScreenPosition.fromAbsolute(0, 0);
            }
            this.adjustBounds(irender, screenposition);
            this.renderers.put(irender, screenposition);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Color color = this.clear ? new Color(33, 41, 48, 100) : new Color(33, 41, 48, 255);
        HUDConfigScreen.drawRect(0, 0, width, height, color.getRGB());
        float f2 = this.zLevel;
        this.zLevel = 200.0f;
        this.drawOutline(0, 0, width - 1, height - 1, Color.white.getRGB());
        for (IRender irender : this.renderers.keySet()) {
            ScreenPosition screenposition = this.renderers.get(irender);
            irender.renderDummy(screenposition);
            if (!this.selectedRenderer.isPresent() || irender != this.selectedRenderer.get()) continue;
            this.drawOutline(screenposition.getAbsoluteX(), screenposition.getAbsoluteY(), irender.getWidth(), irender.getHeight(), new Color(255, 255, 255).getRGB());
        }
        if (this.selectedRenderer.isPresent() && Mouse.isButtonDown(0)) {
            ScreenResolution screenresolution = new ScreenResolution(this.mc);
            if (this.renderers.get(this.selectedRenderer.get()).getAbsoluteX() + this.selectedRenderer.get().getWidth() / 2 <= screenresolution.getCenterX() + 3 && this.renderers.get(this.selectedRenderer.get()).getAbsoluteX() + this.selectedRenderer.get().getWidth() / 2 >= screenresolution.getCenterX() - 3) {
                HUDConfigScreen.drawRectEkn(screenresolution.getCenterX(), 0.0, (double)screenresolution.getCenterX() + 0.7, Minecraft.getMinecraft().displayHeight, Color.MAGENTA.getRGB());
            }
            if (this.renderers.get(this.selectedRenderer.get()).getAbsoluteY() + this.selectedRenderer.get().getHeight() / 2 <= screenresolution.getCenterY() + 3 && this.renderers.get(this.selectedRenderer.get()).getAbsoluteY() + this.selectedRenderer.get().getHeight() / 2 >= screenresolution.getCenterY() - 3) {
                HUDConfigScreen.drawRectEkn(0.0, screenresolution.getCenterY(), Minecraft.getMinecraft().displayWidth, (double)screenresolution.getCenterY() + 0.5, Color.MAGENTA.getRGB());
            }
        }
        this.zLevel = f2;
    }

    private void drawOutline(int x2, int y2, int w2, int h2, int color) {
        this.drawHorizontalLineEkn(x2, x2 + w2, y2, color);
        this.drawHorizontalLineEkn(x2, x2 + w2, y2 + h2, color);
        this.drawVerticalLineEkn(x2, y2 + h2, y2, color);
        this.drawVerticalLineEkn(x2 + w2, y2 + h2, y2, color);
    }

    public void drawHorizontalLineEkn(double startX, double endX, double y2, int color) {
        if (endX < startX) {
            double d0 = startX;
            startX = endX;
            endX = d0;
        }
        HUDConfigScreen.drawRectEkn(startX, y2, endX, y2 + 0.5, color);
    }

    public void drawVerticalLineEkn(double x2, double startY, double endY, int color) {
        if (endY < startY) {
            double d0 = startY;
            startY = endY;
            endY = d0;
        }
        HUDConfigScreen.drawRectEkn(x2, startY, x2 + 0.5, endY, color);
    }

    public static void drawRectEkn(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double d0 = left;
            left = right;
            right = d0;
        }
        if (top < bottom) {
            double d1 = top;
            top = bottom;
            bottom = d1;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f22 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
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

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.renderers.entrySet().forEach(entry -> ((IRender)entry.getKey()).save((ScreenPosition)entry.getValue()));
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void mouseClickMove(int x2, int y2, int button, long time) {
        if (this.selectedRenderer.isPresent()) {
            this.moveSelectedRendererby(x2 - this.prevX, y2 - this.prevY);
        }
        this.prevX = x2;
        this.prevY = y2;
    }

    private void moveSelectedRendererby(int offsetX, int offsetY) {
        IRender irender = this.selectedRenderer.get();
        ScreenPosition screenposition = this.renderers.get(irender);
        screenposition.setAbsolute(screenposition.getAbsoluteX() + offsetX, screenposition.getAbsoluteY() + offsetY);
        this.adjustBounds(irender, screenposition);
    }

    @Override
    public void onGuiClosed() {
        HUDManager.setPaused(false);
        for (IRender irender : this.renderers.keySet()) {
            irender.save(this.renderers.get(irender));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    private void adjustBounds(IRender renderer, ScreenPosition screenpos) {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = scaledresolution.getScaledWidth();
        int screenHeight = scaledresolution.getScaledHeight();
        int absoluteX = Math.max(0, Math.min(screenpos.getAbsoluteX(), Math.max(screenWidth - renderer.getWidth(), 0)));
        int absoluteY = Math.max(0, Math.min(screenpos.getAbsoluteY(), Math.max(screenHeight - renderer.getHeight(), 0)));
        screenpos.setAbsolute(absoluteX, absoluteY);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) throws IOException {
        this.prevX = x2;
        this.prevY = y2;
        this.loadMouseOver(x2, y2);
    }

    private void loadMouseOver(int x2, int y2) {
        this.selectedRenderer = this.renderers.keySet().stream().filter(new MouseOverFinder(x2, y2)).findFirst();
    }

    private class MouseOverFinder
    implements Predicate<IRender> {
        private int mouseX;
        private int mouseY;

        public MouseOverFinder(int x2, int y2) {
            this.mouseX = x2;
            this.mouseY = y2;
        }

        @Override
        public boolean test(IRender r2) {
            ScreenPosition screenposition = (ScreenPosition)HUDConfigScreen.this.renderers.get(r2);
            int i2 = screenposition.getAbsoluteX();
            int j2 = screenposition.getAbsoluteY();
            return this.mouseX >= i2 && this.mouseX <= i2 + r2.getWidth() && this.mouseY >= j2 && this.mouseY <= j2 + r2.getHeight();
        }
    }
}

