/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.elements;

import net.labymod.core.LabyModCore;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class CustomGuiButton
extends GuiButton {
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");

    public CustomGuiButton(int buttonId, int x2, int y2, String buttonText) {
        super(buttonId, x2, y2, buttonText);
    }

    public CustomGuiButton(int buttonId, int x2, int y2, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x2, y2, widthIn, heightIn, buttonText);
    }

    public void setPosition(int minX, int minY, int maxX, int maxY) {
        LabyModCore.getMinecraft().setButtonXPosition(this, minX);
        LabyModCore.getMinecraft().setButtonYPosition(this, minY);
        this.width = maxX - minX;
        this.height = maxY - minY;
    }

    public void setXPosition(int x2) {
        LabyModCore.getMinecraft().setButtonXPosition(this, x2);
    }

    public void setYPosition(int y2) {
        LabyModCore.getMinecraft().setButtonYPosition(this, y2);
    }

    public int getXPosition() {
        return LabyModCore.getMinecraft().getXPosition(this);
    }

    public int getYPosition() {
        return LabyModCore.getMinecraft().getYPosition(this);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getText() {
        return this.displayString;
    }
}

