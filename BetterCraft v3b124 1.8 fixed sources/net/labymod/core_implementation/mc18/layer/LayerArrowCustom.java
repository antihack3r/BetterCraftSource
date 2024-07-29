/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.layer;

import java.util.Random;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;

public class LayerArrowCustom
implements LayerRenderer<EntityLivingBase> {
    private final RendererLivingEntity field_177168_a;

    public LayerArrowCustom(RendererLivingEntity p_i46124_1_) {
        this.field_177168_a = p_i46124_1_;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        int i2 = entitylivingbaseIn.getArrowCountInEntity();
        if (i2 > 0) {
            EntityArrow entity = new EntityArrow(entitylivingbaseIn.worldObj, entitylivingbaseIn.posX, entitylivingbaseIn.posY, entitylivingbaseIn.posZ);
            Random random = new Random(entitylivingbaseIn.getEntityId());
            RenderHelper.disableStandardItemLighting();
            int j2 = 0;
            while (j2 < i2) {
                ModelRenderer modelrenderer = this.field_177168_a.getMainModel().getRandomModelBox(random);
                if (!modelrenderer.isHidden && modelrenderer.cubeList.size() != 0) {
                    float n2;
                    float n3;
                    GlStateManager.pushMatrix();
                    ModelBox modelbox = modelrenderer.cubeList.get(random.nextInt(modelrenderer.cubeList.size()));
                    modelrenderer.postRender(0.0625f);
                    float f2 = random.nextFloat();
                    float f22 = random.nextFloat();
                    float f3 = random.nextFloat();
                    float f4 = (modelbox.posX1 + (modelbox.posX2 - modelbox.posX1) * f2) / 16.0f;
                    float f5 = (modelbox.posY1 + (modelbox.posY2 - modelbox.posY1) * f22) / 16.0f;
                    float f6 = (modelbox.posZ1 + (modelbox.posZ2 - modelbox.posZ1) * f3) / 16.0f;
                    GlStateManager.translate(f4, f5, f6);
                    f2 = f2 * 2.0f - 1.0f;
                    f22 = f22 * 2.0f - 1.0f;
                    f3 = f3 * 2.0f - 1.0f;
                    f22 *= -1.0f;
                    float f7 = MathHelper.sqrt_float((f2 *= -1.0f) * f2 + (f3 *= -1.0f) * f3);
                    EntityArrow entity2 = entity;
                    EntityArrow entity3 = entity;
                    entity3.rotationYaw = n3 = (float)(Math.atan2(f2, f3) * 180.0 / Math.PI);
                    entity2.prevRotationYaw = n3;
                    EntityArrow entity4 = entity;
                    EntityArrow entity5 = entity;
                    entity5.rotationPitch = n2 = (float)(Math.atan2(f22, f7) * 180.0 / Math.PI);
                    entity4.prevRotationPitch = n2;
                    double d0 = 0.0;
                    double d2 = 0.0;
                    double d3 = 0.0;
                    this.field_177168_a.getRenderManager().renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0f, partialTicks);
                    GlStateManager.popMatrix();
                }
                ++j2;
            }
            RenderHelper.enableStandardItemLighting();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

