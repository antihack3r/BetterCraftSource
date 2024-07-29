/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.layers;

import java.beans.ConstructorProperties;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class ParticleStar {
    private static final long FADE_TIME = 1500L;
    private double x;
    private double y;
    private long timestampSpawned;

    public void render() {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_CAPE_STAR);
        double starSize = (double)(this.timestampSpawned - System.currentTimeMillis()) / 10000.0;
        double offset = starSize / 2.0;
        GL11.glColor4f(255.0f, 255.0f, 255.0f, (float)(1.0 + offset * 15.0));
        draw.drawTexture(this.x - offset, this.y - offset, 0.0, 0.0, 255.0, 255.0, starSize, starSize, 1.1f);
    }

    public boolean isFadedOut() {
        return System.currentTimeMillis() - this.timestampSpawned > 1500L;
    }

    @ConstructorProperties(value={"x", "y", "timestampSpawned"})
    public ParticleStar(double x2, double y2, long timestampSpawned) {
        this.x = x2;
        this.y = y2;
        this.timestampSpawned = timestampSpawned;
    }
}

