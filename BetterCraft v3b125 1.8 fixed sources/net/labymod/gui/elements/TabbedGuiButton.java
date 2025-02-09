/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.elements;

import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.CustomGuiButton;
import net.labymod.main.LabyMod;
import net.labymod.settings.LabyModModuleEditorGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TabbedGuiButton
extends CustomGuiButton {
    private ResourceLocation icon;
    private IconRenderCallback iconRenderCallback;
    private boolean rightBound = false;

    public TabbedGuiButton(ResourceLocation icon, int buttonId, int x2, int y2, String buttonText) {
        super(buttonId, x2, y2, buttonText);
        this.icon = icon;
    }

    public TabbedGuiButton(ResourceLocation icon, int buttonId, int x2, int y2, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x2, y2, widthIn, heightIn, buttonText);
        this.icon = icon;
    }

    public TabbedGuiButton(int buttonId, int x2, int y2, String buttonText) {
        this(null, buttonId, x2, y2, buttonText);
    }

    public TabbedGuiButton(int buttonId, int x2, int y2, int widthIn, int heightIn, String buttonText) {
        this(null, buttonId, x2, y2, widthIn, heightIn, buttonText);
    }

    private boolean isHovered(int mouseX, int mouseY) {
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof LabyModModuleEditorGui) {
            ScaledResolution scaled = LabyMod.getInstance().getDrawUtils().getScaledResolution();
            double rescale = (double)scaled.getScaleFactor() / LabyMod.getInstance().getDrawUtils().getCustomScaling();
            mouseX = (int)((double)mouseX / rescale);
            mouseY = (int)((double)mouseY / rescale);
        }
        int xPosition = LabyModCore.getMinecraft().getXPosition(this);
        int yPosition = LabyModCore.getMinecraft().getYPosition(this);
        return mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + this.width && mouseY < yPosition + this.height;
    }

    public void drawButton(Minecraft mc2, int mouseX, int mouseY, float partialTicks) {
        this.drawButton(mc2, mouseX, mouseY);
    }

    @Override
    public void drawButton(Minecraft mc2, int mouseX, int mouseY) {
        if (this.visible) {
            int xPosition = LabyModCore.getMinecraft().getXPosition(this);
            int yPosition = LabyModCore.getMinecraft().getYPosition(this);
            FontRenderer fontrenderer = LabyModCore.getMinecraft().getFontRenderer();
            mc2.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = this.isHovered(mouseX, mouseY);
            int i2 = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(xPosition, yPosition, 0, 46 + i2 * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(xPosition + this.width / 2, yPosition, 200 - this.width / 2, 46 + i2 * 20, this.width / 2, this.height);
            this.mouseDragged(mc2, mouseX, mouseY);
            int j2 = 0xE0E0E0;
            if (!this.enabled) {
                j2 = 0xA0A0A0;
            } else if (this.hovered) {
                j2 = 0xFFFFA0;
            }
            if (this.icon == null) {
                TabbedGuiButton.drawCenteredString(fontrenderer, this.displayString, xPosition + this.width / 2, yPosition + (this.height - 8) / 2, j2);
            } else {
                int padding = 3;
                int iconSize = this.height - padding * 2;
                int stringWidth = LabyMod.getInstance().getDrawUtils().getStringWidth(this.displayString) + iconSize + padding;
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.icon);
                if (this.iconRenderCallback == null) {
                    LabyMod.getInstance().getDrawUtils().drawTexture(xPosition + this.width / 2 - stringWidth / 2, yPosition + padding, 256.0, 256.0, iconSize, iconSize);
                } else {
                    this.iconRenderCallback.render(xPosition + this.width / 2 - stringWidth / 2, yPosition + padding, iconSize);
                }
                this.drawString(fontrenderer, this.displayString, xPosition - 1 + this.width / 2 - stringWidth / 2 + iconSize + padding, yPosition + (this.height - 8) / 2, j2);
            }
        }
    }

    public void setIconRenderCallback(IconRenderCallback iconRenderCallback) {
        this.iconRenderCallback = iconRenderCallback;
    }

    public void setRightBound(boolean rightBound) {
        this.rightBound = rightBound;
    }

    public boolean isRightBound() {
        return this.rightBound;
    }

    public static interface IconRenderCallback {
        public void render(int var1, int var2, int var3);
    }
}

