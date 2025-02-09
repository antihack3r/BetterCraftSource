/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;

public class ModelWither
extends ModelBase {
    private ModelRenderer[] field_82905_a;
    private ModelRenderer[] field_82904_b;

    public ModelWither(float p_i46302_1_) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_82905_a = new ModelRenderer[3];
        this.field_82905_a[0] = new ModelRenderer(this, 0, 16);
        this.field_82905_a[0].addBox(-10.0f, 3.9f, -0.5f, 20, 3, 3, p_i46302_1_);
        this.field_82905_a[1] = new ModelRenderer(this).setTextureSize(this.textureWidth, this.textureHeight);
        this.field_82905_a[1].setRotationPoint(-2.0f, 6.9f, -0.5f);
        this.field_82905_a[1].setTextureOffset(0, 22).addBox(0.0f, 0.0f, 0.0f, 3, 10, 3, p_i46302_1_);
        this.field_82905_a[1].setTextureOffset(24, 22).addBox(-4.0f, 1.5f, 0.5f, 11, 2, 2, p_i46302_1_);
        this.field_82905_a[1].setTextureOffset(24, 22).addBox(-4.0f, 4.0f, 0.5f, 11, 2, 2, p_i46302_1_);
        this.field_82905_a[1].setTextureOffset(24, 22).addBox(-4.0f, 6.5f, 0.5f, 11, 2, 2, p_i46302_1_);
        this.field_82905_a[2] = new ModelRenderer(this, 12, 22);
        this.field_82905_a[2].addBox(0.0f, 0.0f, 0.0f, 3, 6, 3, p_i46302_1_);
        this.field_82904_b = new ModelRenderer[3];
        this.field_82904_b[0] = new ModelRenderer(this, 0, 0);
        this.field_82904_b[0].addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8, p_i46302_1_);
        this.field_82904_b[1] = new ModelRenderer(this, 32, 0);
        this.field_82904_b[1].addBox(-4.0f, -4.0f, -4.0f, 6, 6, 6, p_i46302_1_);
        this.field_82904_b[1].rotationPointX = -8.0f;
        this.field_82904_b[1].rotationPointY = 4.0f;
        this.field_82904_b[2] = new ModelRenderer(this, 32, 0);
        this.field_82904_b[2].addBox(-4.0f, -4.0f, -4.0f, 6, 6, 6, p_i46302_1_);
        this.field_82904_b[2].rotationPointX = 10.0f;
        this.field_82904_b[2].rotationPointY = 4.0f;
    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        ModelRenderer[] modelRendererArray = this.field_82904_b;
        int n2 = this.field_82904_b.length;
        int n3 = 0;
        while (n3 < n2) {
            ModelRenderer modelrenderer = modelRendererArray[n3];
            modelrenderer.render(scale);
            ++n3;
        }
        modelRendererArray = this.field_82905_a;
        n2 = this.field_82905_a.length;
        n3 = 0;
        while (n3 < n2) {
            ModelRenderer modelrenderer1 = modelRendererArray[n3];
            modelrenderer1.render(scale);
            ++n3;
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        float f2 = MathHelper.cos(ageInTicks * 0.1f);
        this.field_82905_a[1].rotateAngleX = (0.065f + 0.05f * f2) * (float)Math.PI;
        this.field_82905_a[2].setRotationPoint(-2.0f, 6.9f + MathHelper.cos(this.field_82905_a[1].rotateAngleX) * 10.0f, -0.5f + MathHelper.sin(this.field_82905_a[1].rotateAngleX) * 10.0f);
        this.field_82905_a[2].rotateAngleX = (0.265f + 0.1f * f2) * (float)Math.PI;
        this.field_82904_b[0].rotateAngleY = netHeadYaw / 57.295776f;
        this.field_82904_b[0].rotateAngleX = headPitch / 57.295776f;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime) {
        EntityWither entitywither = (EntityWither)entitylivingbaseIn;
        int i2 = 1;
        while (i2 < 3) {
            this.field_82904_b[i2].rotateAngleY = (entitywither.func_82207_a(i2 - 1) - entitylivingbaseIn.renderYawOffset) / 57.295776f;
            this.field_82904_b[i2].rotateAngleX = entitywither.func_82210_r(i2 - 1) / 57.295776f;
            ++i2;
        }
    }
}

