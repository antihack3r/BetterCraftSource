/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.hud;

import me.nzxtercode.bettercraft.client.hud.ScreenPosition;

public interface IRenderConfig {
    public void save(ScreenPosition var1);

    public ScreenPosition load();
}

