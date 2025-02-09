// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderWitherSkeleton extends RenderSkeleton
{
    private static final ResourceLocation WITHER_SKELETON_TEXTURES;
    
    static {
        WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    }
    
    public RenderWitherSkeleton(final RenderManager p_i47188_1_) {
        super(p_i47188_1_);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final AbstractSkeleton entity) {
        return RenderWitherSkeleton.WITHER_SKELETON_TEXTURES;
    }
    
    @Override
    protected void preRenderCallback(final AbstractSkeleton entitylivingbaseIn, final float partialTickTime) {
        GlStateManager.scale(1.2f, 1.2f, 1.2f);
    }
}
