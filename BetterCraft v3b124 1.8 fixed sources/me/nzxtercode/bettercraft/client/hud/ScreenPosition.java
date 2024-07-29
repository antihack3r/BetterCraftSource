/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.hud;

import me.nzxtercode.bettercraft.client.hud.ScreenResolution;
import net.minecraft.client.Minecraft;

public class ScreenPosition {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private int x;
    private int y;

    public ScreenPosition(double x2, double y2) {
        this.setRelativ(x2, y2);
    }

    public ScreenPosition(int x2, int y2) {
        this.setAbsolute(x2, y2);
    }

    public static ScreenPosition fromRelativPosition(double x2, double y2) {
        return new ScreenPosition(x2, y2);
    }

    public static ScreenPosition fromAbsolute(int x2, int y2) {
        return new ScreenPosition(x2, y2);
    }

    public int getAbsoluteX() {
        new ScreenResolution(mc);
        return this.x;
    }

    public int getAbsoluteY() {
        new ScreenResolution(mc);
        return this.y;
    }

    public double getRelativX() {
        ScreenResolution screenresolution = new ScreenResolution(mc);
        return (int)((double)this.x / screenresolution.getScaledWidth_double());
    }

    public double getRelativY() {
        ScreenResolution screenresolution = new ScreenResolution(mc);
        return (int)((double)this.y / screenresolution.getScaledHeight_double());
    }

    public void setAbsolute(int x2, int y2) {
        this.x = x2;
        this.y = y2;
    }

    public void setRelativ(double x2, double y2) {
        ScreenResolution screenresolution = new ScreenResolution(mc);
        this.x = (int)(x2 / (double)screenresolution.getScaledWidth());
        this.y = (int)(y2 / (double)screenresolution.getScaledHeight());
    }
}

