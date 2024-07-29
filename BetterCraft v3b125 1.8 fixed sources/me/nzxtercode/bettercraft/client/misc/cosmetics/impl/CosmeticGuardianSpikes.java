/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticModelBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticGuardianSpikes
extends CosmeticBase {
    private static ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/guardian.png");
    private GuardianSpikesModel guardianSpikesModel;

    public CosmeticGuardianSpikes(RenderPlayer player) {
        super(player);
        this.guardianSpikesModel = new GuardianSpikesModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.guardianSpikesModel = new GuardianSpikesModel(this.playerRenderer);
        GlStateManager.pushMatrix();
        GL11.glTranslated(0.0, -1.2f, 0.0);
        if (player.isSneaking()) {
            GL11.glTranslated(0.0, 0.2f, 0.0);
        }
        float[] color = new float[]{1.0f, 1.0f, 1.0f};
        GL11.glColor3d(color[0], color[1], color[2]);
        this.playerRenderer.bindTexture(TEXTURE);
        this.guardianSpikesModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public int getId() {
        return 13;
    }

    public static class GuardianSpikesModel
    extends CosmeticModelBase {
        private ModelRenderer[] guardianSpines;

        public GuardianSpikesModel(RenderPlayer player) {
            super(player);
            this.textureWidth = 64;
            this.textureHeight = 64;
            this.guardianSpines = new ModelRenderer[12];
            int i2 = 0;
            while (i2 < this.guardianSpines.length) {
                this.guardianSpines[i2] = new ModelRenderer(this, 0, 0);
                this.guardianSpines[i2].addBox(-1.0f, -4.5f, -1.0f, 2, 9, 2);
                ++i2;
            }
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            float[] afloat = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
            float[] afloat1 = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
            float[] afloat2 = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
            float[] afloat3 = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
            float[] afloat4 = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
            float[] afloat5 = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
            float f2 = (1.0f - p_78088_3_) * 0.55f;
            int i2 = 0;
            while (i2 < 12) {
                this.guardianSpines[i2].rotateAngleX = (float)Math.PI * afloat[i2];
                this.guardianSpines[i2].rotateAngleY = (float)Math.PI * afloat1[i2];
                this.guardianSpines[i2].rotateAngleZ = (float)Math.PI * afloat2[i2];
                this.guardianSpines[i2].rotationPointX = afloat3[i2] * (1.0f + MathHelper.cos(p_78088_3_ * 1.5f + (float)i2) * 0.01f - f2);
                this.guardianSpines[i2].rotationPointY = 16.0f + afloat4[i2] * (1.0f + MathHelper.cos(p_78088_3_ * 1.5f + (float)i2) * 0.01f - f2);
                this.guardianSpines[i2].rotationPointZ = afloat5[i2] * (1.0f + MathHelper.cos(p_78088_3_ * 1.5f + (float)i2) * 0.01f - f2);
                this.guardianSpines[i2].render(scale);
                ++i2;
            }
        }
    }
}

