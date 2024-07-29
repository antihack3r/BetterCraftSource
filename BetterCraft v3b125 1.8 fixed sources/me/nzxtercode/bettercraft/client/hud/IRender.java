/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.hud;

import me.nzxtercode.bettercraft.client.hud.IRenderConfig;
import me.nzxtercode.bettercraft.client.hud.ScreenPosition;

public interface IRender
extends IRenderConfig {
    public int getWidth();

    public int getHeight();

    public void render(ScreenPosition var1);

    default public void renderDummy(ScreenPosition pos) {
        this.render(pos);
    }

    default public boolean isEnabled() {
        return true;
    }
}

