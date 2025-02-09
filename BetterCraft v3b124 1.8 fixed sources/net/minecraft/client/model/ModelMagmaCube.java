/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

public class ModelMagmaCube
extends ModelBase {
    ModelRenderer[] segments = new ModelRenderer[8];
    ModelRenderer core;

    public ModelMagmaCube() {
        int i2 = 0;
        while (i2 < this.segments.length) {
            int j2 = 0;
            int k2 = i2;
            if (i2 == 2) {
                j2 = 24;
                k2 = 10;
            } else if (i2 == 3) {
                j2 = 24;
                k2 = 19;
            }
            this.segments[i2] = new ModelRenderer(this, j2, k2);
            this.segments[i2].addBox(-4.0f, 16 + i2, -4.0f, 8, 1, 8);
            ++i2;
        }
        this.core = new ModelRenderer(this, 0, 16);
        this.core.addBox(-2.0f, 18.0f, -2.0f, 4, 4, 4);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime) {
        EntityMagmaCube entitymagmacube = (EntityMagmaCube)entitylivingbaseIn;
        float f2 = entitymagmacube.prevSquishFactor + (entitymagmacube.squishFactor - entitymagmacube.prevSquishFactor) * partialTickTime;
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        int i2 = 0;
        while (i2 < this.segments.length) {
            this.segments[i2].rotationPointY = (float)(-(4 - i2)) * f2 * 1.7f;
            ++i2;
        }
    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.core.render(scale);
        int i2 = 0;
        while (i2 < this.segments.length) {
            this.segments[i2].render(scale);
            ++i2;
        }
    }
}

