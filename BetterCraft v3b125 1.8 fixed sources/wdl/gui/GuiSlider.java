/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;

class GuiSlider
extends GuiButton {
    private float sliderValue;
    private boolean dragging;
    private final String text;
    private final int max;

    public GuiSlider(int id2, int x2, int y2, int width, int height, String text, int value, int max) {
        super(id2, x2, y2, width, height, text);
        this.text = text;
        this.max = max;
        this.setValue(value);
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft mc2, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0f, 1.0f);
                this.dragging = true;
                this.displayString = I18n.format(this.text, this.getValue());
            }
            mc2.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (this.enabled) {
                this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
                this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
            } else {
                this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 46, 4, 20);
                this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 46, 4, 20);
            }
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc2, int mouseX, int mouseY) {
        if (super.mousePressed(mc2, mouseX, mouseY)) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0f, 1.0f);
            this.displayString = I18n.format(this.text, this.getValue());
            this.dragging = true;
            return true;
        }
        return false;
    }

    public int getValue() {
        return (int)(this.sliderValue * (float)this.max);
    }

    public void setValue(int value) {
        this.sliderValue = (float)value / (float)this.max;
        this.displayString = I18n.format(this.text, this.getValue());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}

