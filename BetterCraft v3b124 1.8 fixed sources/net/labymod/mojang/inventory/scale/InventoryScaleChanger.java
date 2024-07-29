/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.mojang.inventory.scale;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.mojang.inventory.scale.EnumGuiScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class InventoryScaleChanger {
    private boolean initialized = false;
    private double scaledWidthD;
    private double scaledHeightD;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;
    private int mouseX;
    private int mouseY;

    public boolean initGui() {
        EnumGuiScale enumScale = LabyMod.getSettings().customInventoryScale;
        if (enumScale != EnumGuiScale.DEFAULT) {
            Minecraft minecraft = Minecraft.getMinecraft();
            this.scaledWidth = minecraft.displayWidth;
            this.scaledHeight = minecraft.displayHeight;
            this.scaleFactor = 1;
            boolean flag = minecraft.isUnicode();
            int i2 = enumScale.ordinal();
            if (i2 == 0) {
                i2 = 1000;
            }
            while (this.scaleFactor < i2 && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
                ++this.scaleFactor;
            }
            if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
                --this.scaleFactor;
            }
            this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
            this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
            this.scaledWidth = LabyModCore.getMath().ceiling_double_int(this.scaledWidthD);
            this.scaledHeight = LabyModCore.getMath().ceiling_double_int(this.scaledHeightD);
            this.initialized = true;
            return true;
        }
        return false;
    }

    public boolean drawScreen(int mouseX, int mouseY) {
        if (this.initialized) {
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, this.scaledWidthD, this.scaledHeightD, 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0f, 0.0f, -2000.0f);
            this.mouseX = Mouse.getX() * this.scaledWidth / Minecraft.getMinecraft().displayWidth;
            this.mouseY = this.scaledHeight - Mouse.getY() * this.scaledHeight / Minecraft.getMinecraft().displayHeight - 1;
            return true;
        }
        return false;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public double getScaledWidthD() {
        return this.scaledWidthD;
    }

    public double getScaledHeightD() {
        return this.scaledHeightD;
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }

    public int getScaleFactor() {
        return this.scaleFactor;
    }

    public int getMouseX() {
        return this.mouseX;
    }

    public int getMouseY() {
        return this.mouseY;
    }
}

