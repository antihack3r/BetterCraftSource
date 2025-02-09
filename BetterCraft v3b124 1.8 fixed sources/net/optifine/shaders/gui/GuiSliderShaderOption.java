/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.gui.GuiButtonShaderOption;
import net.optifine.shaders.gui.GuiShaderOptions;

public class GuiSliderShaderOption
extends GuiButtonShaderOption {
    private float sliderValue = 1.0f;
    public boolean dragging;
    private ShaderOption shaderOption = null;

    public GuiSliderShaderOption(int buttonId, int x2, int y2, int w2, int h2, ShaderOption shaderOption, String text) {
        super(buttonId, x2, y2, w2, h2, shaderOption, text);
        this.shaderOption = shaderOption;
        this.sliderValue = shaderOption.getIndexNormalized();
        this.displayString = GuiShaderOptions.getButtonText(shaderOption, this.width);
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft mc2, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging && !GuiScreen.isShiftKeyDown()) {
                this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0f, 1.0f);
                this.shaderOption.setIndexNormalized(this.sliderValue);
                this.sliderValue = this.shaderOption.getIndexNormalized();
                this.displayString = GuiShaderOptions.getButtonText(this.shaderOption, this.width);
            }
            mc2.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc2, int mouseX, int mouseY) {
        if (super.mousePressed(mc2, mouseX, mouseY)) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0f, 1.0f);
            this.shaderOption.setIndexNormalized(this.sliderValue);
            this.displayString = GuiShaderOptions.getButtonText(this.shaderOption, this.width);
            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    @Override
    public void valueChanged() {
        this.sliderValue = this.shaderOption.getIndexNormalized();
    }

    @Override
    public boolean isSwitchable() {
        return false;
    }
}

