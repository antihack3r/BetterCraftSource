/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticModelBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CosmeticVillagerNose
extends CosmeticBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/villagernose.png");
    private NoseModel noseModel;

    public CosmeticVillagerNose(RenderPlayer player) {
        super(player);
        this.noseModel = new NoseModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.noseModel = new NoseModel(this.playerRenderer);
        this.playerRenderer.bindTexture(TEXTURE);
        this.noseModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
    }

    @Override
    public int getId() {
        return 24;
    }

    public static class NoseModel
    extends CosmeticModelBase {
        private final ModelRenderer villagerNose = new ModelRenderer(this).setTextureSize(64, 64);

        public NoseModel(RenderPlayer player) {
            super(player);
            this.villagerNose.setRotationPoint(0.0f, -2.0f, 0.0f);
            this.villagerNose.setTextureOffset(24, 0).addBox(-1.0f, -2.0f, -6.0f, 2, 4, 2, 0.0f);
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            this.villagerNose.rotateAngleX = this.playerModel.bipedHead.rotateAngleX;
            this.villagerNose.rotateAngleY = this.playerModel.bipedHead.rotateAngleY;
            this.villagerNose.rotationPointX = 0.0f;
            this.villagerNose.rotationPointY = 0.0f;
            this.villagerNose.render(scale);
        }
    }
}

