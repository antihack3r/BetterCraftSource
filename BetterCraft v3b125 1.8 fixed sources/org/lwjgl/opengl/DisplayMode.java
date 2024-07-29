/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.opengl;

public final class DisplayMode {
    private final int width;
    private final int height;
    private final int bpp;
    private final int freq;
    private final boolean fullscreen;

    public DisplayMode(int width, int height) {
        this(width, height, 0, 0, false);
    }

    DisplayMode(int width, int height, int bpp2, int freq) {
        this(width, height, bpp2, freq, true);
    }

    private DisplayMode(int width, int height, int bpp2, int freq, boolean fullscreen) {
        this.width = width;
        this.height = height;
        this.bpp = bpp2;
        this.freq = freq;
        this.fullscreen = fullscreen;
    }

    public boolean isFullscreenCapable() {
        return this.fullscreen;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getBitsPerPixel() {
        return this.bpp;
    }

    public int getFrequency() {
        return this.freq;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DisplayMode)) {
            return false;
        }
        DisplayMode dm2 = (DisplayMode)obj;
        return dm2.width == this.width && dm2.height == this.height && dm2.bpp == this.bpp && dm2.freq == this.freq;
    }

    public int hashCode() {
        return this.width ^ this.height ^ this.freq ^ this.bpp;
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder(32);
        sb2.append(this.width);
        sb2.append(" x ");
        sb2.append(this.height);
        sb2.append(" x ");
        sb2.append(this.bpp);
        sb2.append(" @");
        sb2.append(this.freq);
        sb2.append("Hz");
        return sb2.toString();
    }
}

