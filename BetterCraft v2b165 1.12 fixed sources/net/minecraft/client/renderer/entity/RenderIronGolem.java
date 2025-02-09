// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerIronGolemFlower;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.monster.EntityIronGolem;

public class RenderIronGolem extends RenderLiving<EntityIronGolem>
{
    private static final ResourceLocation IRON_GOLEM_TEXTURES;
    
    static {
        IRON_GOLEM_TEXTURES = new ResourceLocation("textures/entity/iron_golem.png");
    }
    
    public RenderIronGolem(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelIronGolem(), 0.5f);
        ((RenderLivingBase<EntityLivingBase>)this).addLayer(new LayerIronGolemFlower(this));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityIronGolem entity) {
        return RenderIronGolem.IRON_GOLEM_TEXTURES;
    }
    
    @Override
    protected void rotateCorpse(final EntityIronGolem entityLiving, final float p_77043_2_, final float p_77043_3_, final float partialTicks) {
        super.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
        if (entityLiving.limbSwingAmount >= 0.01) {
            final float f = 13.0f;
            final float f2 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0f - partialTicks) + 6.0f;
            final float f3 = (Math.abs(f2 % 13.0f - 6.5f) - 3.25f) / 3.25f;
            GlStateManager.rotate(6.5f * f3, 0.0f, 0.0f, 1.0f);
        }
    }
}
