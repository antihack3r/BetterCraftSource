/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.gui.section.GuiUISettings;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButton
extends Gui {
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    protected int width = 200;
    protected int height = 20;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled = true;
    public boolean visible = true;
    public boolean hovered;
    public int hoverTime;
    public boolean animation;
    public int renderedY;

    public GuiButton(int buttonId, int x2, int y2, String buttonText) {
        this(buttonId, x2, y2, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x2, int y2, int widthIn, int heightIn, String buttonText) {
        this.id = buttonId;
        this.xPosition = x2;
        this.yPosition = y2;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
        this.renderedY = this.yPosition >= GuiScreen.height / 2 ? GuiScreen.height : -heightIn;
    }

    protected int getHoverState(boolean mouseOver) {
        int i2 = 1;
        if (!this.enabled) {
            i2 = 0;
        } else if (mouseOver) {
            i2 = 2;
        }
        return i2;
    }

    public void drawButton(Minecraft mc2, int mouseX, int mouseY) {
        if (GuiUISettings.enabledUI[2]) {
            int r2;
            this.renderedY = this.yPosition >= GuiScreen.height / 2 ? (this.renderedY > this.yPosition ? (this.renderedY -= 7) : this.yPosition) : (this.renderedY < this.yPosition ? (this.renderedY += 7) : this.yPosition);
            if (!GuiUISettings.enabledAnimations[0]) {
                this.renderedY = this.yPosition;
            }
            if (!this.visible) {
                return;
            }
            FontRenderer fontrenderer = mc2.fontRendererObj;
            mc2.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i2 = this.getHoverState(this.hovered);
            int color = ColorUtils.rainbowEffect();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            int n2 = r2 = !this.enabled ? -1867863382 : 0x60AAAAAA;
            if (!this.enabled) {
                r2 -= -1867863382;
            } else if (this.hovered) {
                r2 -= Integer.MIN_VALUE;
            }
            Gui.drawRect(this.xPosition + this.hoverTime, this.renderedY, this.xPosition + this.width - this.hoverTime, this.renderedY + this.height, r2);
            Gui.drawRect(this.xPosition + this.hoverTime, this.renderedY, this.xPosition + this.width - this.hoverTime, this.renderedY + 1, color);
            Gui.drawRect(this.xPosition + this.hoverTime, this.renderedY + this.height, this.xPosition + this.width - this.hoverTime, this.renderedY + this.height - 1, color);
            this.mouseDragged(mc2, mouseX, mouseY);
            if (this.hovered) {
                Gui.drawRect(this.xPosition + this.hoverTime, this.renderedY, this.xPosition + this.hoverTime + 1, this.renderedY + this.height, color);
                Gui.drawRect(this.xPosition + this.width - this.hoverTime, this.renderedY, this.xPosition + this.width - this.hoverTime - 1, this.renderedY + this.height, color);
                ++this.hoverTime;
            }
            if (!this.hovered) {
                --this.hoverTime;
            }
            if (this.hoverTime * 30 > 255) {
                this.hoverTime = 8;
            }
            if (this.hoverTime < 0) {
                this.hoverTime = 0;
            }
            double scale = Math.min(1.0, 1.0 - (double)(this.hoverTime / 3) / 8.0);
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            GuiButton.drawCenteredString(fontrenderer, this.displayString, (int)((double)(this.xPosition + this.width / 2) / scale), (int)((double)((int)((double)(this.renderedY + (this.height - 8) / 2) / scale)) + ((double)mc2.fontRendererObj.FONT_HEIGHT - (double)mc2.fontRendererObj.FONT_HEIGHT * scale)), Config.getInstance().getColor("Buttons").get("string").getAsInt());
            GlStateManager.popMatrix();
        } else if (this.visible) {
            FontRenderer fontrenderer = mc2.fontRendererObj;
            mc2.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i3 = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i3 * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i3 * 20, this.width / 2, this.height);
            this.mouseDragged(mc2, mouseX, mouseY);
            int j2 = 0xE0E0E0;
            if (!this.enabled) {
                j2 = 0xA0A0A0;
            } else if (this.hovered) {
                j2 = 0xFFFFA0;
            }
            GuiButton.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j2);
        }
    }

    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

    protected void mouseDragged(Minecraft mc2, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean mousePressed(Minecraft mc2, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}

