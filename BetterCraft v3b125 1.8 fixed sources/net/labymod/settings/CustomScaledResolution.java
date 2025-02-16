/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings;

import net.labymod.core.LabyModCore;
import net.minecraft.client.Minecraft;

public class CustomScaledResolution {
    private final double scaledWidthD;
    private final double scaledHeightD;
    private double scaledWidth;
    private double scaledHeight;
    private double scaleFactor;

    public CustomScaledResolution(Minecraft mc2, double scale) {
        this.scaledWidth = mc2.displayWidth;
        this.scaledHeight = mc2.displayHeight;
        this.scaleFactor = 1.0;
        boolean flag = mc2.isUnicode();
        double i2 = scale;
        if (i2 == 0.0) {
            i2 = 1000.0;
        }
        while (this.scaleFactor < i2 && this.scaledWidth / (this.scaleFactor + 1.0) >= 320.0 && this.scaledHeight / (this.scaleFactor + 1.0) >= 240.0) {
            this.scaleFactor += 1.0;
        }
        if (flag && this.scaleFactor % 2.0 != 0.0 && this.scaleFactor != 1.0) {
            this.scaleFactor -= 1.0;
        }
        this.scaledWidthD = this.scaledWidth / this.scaleFactor;
        this.scaledHeightD = this.scaledHeight / this.scaleFactor;
        this.scaledWidth = LabyModCore.getMath().ceiling_double_int(this.scaledWidthD);
        this.scaledHeight = LabyModCore.getMath().ceiling_double_int(this.scaledHeightD);
    }

    public int getScaledWidth() {
        return (int)this.scaledWidth;
    }

    public int getScaledHeight() {
        return (int)this.scaledHeight;
    }

    public double getScaledWidth_double() {
        return this.scaledWidthD;
    }

    public double getScaledHeight_double() {
        return this.scaledHeightD;
    }

    public double getScaleFactor() {
        return this.scaleFactor;
    }
}

