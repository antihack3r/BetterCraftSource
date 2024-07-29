/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18;

import net.labymod.core.MathAdapter;
import net.minecraft.util.MathHelper;

public class MathImplementation
implements MathAdapter {
    @Override
    public int clamp_int(int num, int min, int max) {
        return MathHelper.clamp_int(num, min, max);
    }

    @Override
    public int floor_float(float value) {
        return MathHelper.floor_float(value);
    }

    @Override
    public int ceiling_float_int(float value) {
        return MathHelper.ceiling_float_int(value);
    }

    @Override
    public int ceiling_double_int(double value) {
        return MathHelper.ceiling_double_int(value);
    }

    @Override
    public float sin(float value) {
        return MathHelper.sin(value);
    }

    @Override
    public float cos(float value) {
        return MathHelper.cos(value);
    }

    @Override
    public float clamp_float(float num, float min, float max) {
        return MathHelper.clamp_float(num, min, max);
    }

    @Override
    public float sqrt_float(float value) {
        return MathHelper.sqrt_float(value);
    }

    @Override
    public float abs(float value) {
        return MathHelper.abs(value);
    }

    @Override
    public int hsvToRGB(float r2, float g2, float b2) {
        return MathHelper.hsvToRGB(r2, g2, b2);
    }

    @Override
    public float wrapAngleTo180_float(float value) {
        return MathHelper.wrapAngleTo180_float(value);
    }

    @Override
    public int floor_double(double value) {
        return MathHelper.floor_double(value);
    }

    @Override
    public double clamp_double(double num, double min, double max) {
        return MathHelper.clamp_double(num, min, max);
    }
}

