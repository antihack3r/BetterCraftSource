/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft;

public final class Quaternion {
    private final float x;
    private final float y;
    private final float z;
    private final float w;

    public Quaternion(float x2, float y2, float z2, float w2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.w = w2;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public float w() {
        return this.w;
    }

    public String toString() {
        return "Quaternion{x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", w=" + this.w + '}';
    }
}

