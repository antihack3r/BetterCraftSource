/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.layers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.cosmetic.layers.ParticleStar;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class CapeParticleRenderer {
    private Random random = LabyMod.getRandom();
    private List<ParticleStar> starList = new LinkedList<ParticleStar>();
    private long lastStarSpawned = -1L;

    public void render(User user, AbstractClientPlayer entitylivingbaseIn, float partialTicks) {
        if (!LabyMod.getSettings().capeOriginalParticles || user.isMojangCapeModified()) {
            return;
        }
        if (this.lastStarSpawned < System.currentTimeMillis()) {
            this.lastStarSpawned = System.currentTimeMillis() + 200L;
            double x2 = (0.5 - this.random.nextDouble()) * 0.5;
            double y2 = this.random.nextDouble() - 0.025;
            ParticleStar particleStar = new ParticleStar(x2, y2, System.currentTimeMillis());
            this.starList.add(particleStar);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, 0.0, -0.07);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Iterator<ParticleStar> iterator = this.starList.iterator();
        while (iterator.hasNext()) {
            ParticleStar next = iterator.next();
            next.render();
            if (!next.isFadedOut()) continue;
            iterator.remove();
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
}

