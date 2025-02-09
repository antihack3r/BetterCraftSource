/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

public interface MathAdapter {
    public int clamp_int(int var1, int var2, int var3);

    public double clamp_double(double var1, double var3, double var5);

    public int floor_float(float var1);

    public int ceiling_float_int(float var1);

    public int ceiling_double_int(double var1);

    public float sin(float var1);

    public float cos(float var1);

    public float clamp_float(float var1, float var2, float var3);

    public float sqrt_float(float var1);

    public float abs(float var1);

    public int hsvToRGB(float var1, float var2, float var3);

    public float wrapAngleTo180_float(float var1);

    public int floor_double(double var1);
}

