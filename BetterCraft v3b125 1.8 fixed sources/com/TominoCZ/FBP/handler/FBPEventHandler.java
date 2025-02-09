/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.handler;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.particle.FBPParticleDigging;
import com.TominoCZ.FBP.particle.FBPParticleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class FBPEventHandler {
    Minecraft mc = Minecraft.getMinecraft();

    public void onWorldLoadEvent() {
    }

    public void onEntityJoinWorldEvent(Entity entity, World world) {
        if (entity == this.mc.thePlayer) {
            FBP.fancyEffectRenderer = new FBPParticleManager(world, this.mc.getRenderManager().renderEngine, new FBPParticleDigging.Factory());
            if (FBP.originalEffectRenderer == null || FBP.originalEffectRenderer != this.mc.effectRenderer && FBP.originalEffectRenderer != FBP.fancyEffectRenderer) {
                FBP.originalEffectRenderer = this.mc.effectRenderer;
            }
            this.mc.effectRenderer = FBP.fancyEffectRenderer;
        }
    }
}

