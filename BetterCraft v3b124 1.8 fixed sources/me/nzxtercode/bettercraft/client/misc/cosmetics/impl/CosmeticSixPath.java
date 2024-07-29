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
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticSixPath
extends CosmeticBase {
    private SixPathModel sixPathModel;
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/sixpath.png");

    public CosmeticSixPath(RenderPlayer player) {
        super(player);
        this.sixPathModel = new SixPathModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        this.playerRenderer.bindTexture(TEXTURE);
        this.sixPathModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GL11.glPopMatrix();
    }

    @Override
    public int getId() {
        return 17;
    }

    private class SixPathModel
    extends CosmeticModelBase {
        private final ModelRenderer six_path;

        public SixPathModel(RenderPlayer player) {
            super(player);
            this.textureWidth = 128;
            this.textureHeight = 128;
            this.six_path = new ModelRenderer(this);
            this.six_path.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.six_path.setTextureOffset(120, 0).addBox(-1.0f, -10.0f, 6.0f, 2, 2, 2, 0.0f);
            this.six_path.setTextureOffset(120, 0).addBox(-1.0f, -10.0f, 5.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-1.0f, -10.0f, 6.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-1.0f, -10.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-1.0f, -9.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-0.5f, -10.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-1.5f, -10.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-8.0f, -4.0f, 6.0f, 2, 2, 2, 0.0f);
            this.six_path.setTextureOffset(120, 0).addBox(-8.0f, -4.0f, 5.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-8.0f, -4.0f, 6.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-8.0f, -4.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-8.0f, -3.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-7.5f, -4.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-8.5f, -4.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(6.0f, -4.0f, 6.0f, 2, 2, 2, 0.0f);
            this.six_path.setTextureOffset(120, 0).addBox(6.0f, -4.0f, 5.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(6.0f, -4.0f, 6.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(6.0f, -4.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(6.0f, -3.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(6.5f, -4.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(5.5f, -4.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(8.0f, 5.0f, 6.0f, 2, 2, 2, 0.0f);
            this.six_path.setTextureOffset(120, 0).addBox(8.0f, 5.0f, 5.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(8.0f, 5.0f, 6.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(8.0f, 4.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(8.0f, 5.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(8.5f, 5.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(7.5f, 5.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-10.0f, 5.0f, 6.0f, 2, 2, 2, 0.0f);
            this.six_path.setTextureOffset(120, 0).addBox(-10.0f, 5.0f, 5.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-10.0f, 5.0f, 6.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-10.0f, 4.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-10.0f, 5.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-9.5f, 5.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-10.5f, 5.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-6.0f, 13.0f, 6.0f, 2, 2, 2, 0.0f);
            this.six_path.setTextureOffset(120, 0).addBox(-6.0f, 13.0f, 5.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-6.0f, 13.0f, 6.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-6.0f, 12.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-6.0f, 13.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-5.5f, 13.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(-6.5f, 13.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(4.0f, 13.0f, 6.0f, 2, 2, 2, 0.0f);
            this.six_path.setTextureOffset(120, 0).addBox(4.0f, 13.0f, 5.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(4.0f, 13.0f, 6.5f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(4.0f, 12.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(4.0f, 13.5f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(4.5f, 13.0f, 6.0f, 2, 2, 2, -0.25f);
            this.six_path.setTextureOffset(120, 0).addBox(3.5f, 13.0f, 6.0f, 2, 2, 2, -0.25f);
        }

        @Override
        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            this.six_path.render(scale);
        }
    }
}

