/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.gui;

import net.minecraft.client.gui.GuiButton;
import net.optifine.shaders.config.ShaderOption;

public class GuiButtonShaderOption
extends GuiButton {
    private ShaderOption shaderOption = null;

    public GuiButtonShaderOption(int buttonId, int x2, int y2, int widthIn, int heightIn, ShaderOption shaderOption, String text) {
        super(buttonId, x2, y2, widthIn, heightIn, text);
        this.shaderOption = shaderOption;
    }

    public ShaderOption getShaderOption() {
        return this.shaderOption;
    }

    public void valueChanged() {
    }

    public boolean isSwitchable() {
        return true;
    }
}

