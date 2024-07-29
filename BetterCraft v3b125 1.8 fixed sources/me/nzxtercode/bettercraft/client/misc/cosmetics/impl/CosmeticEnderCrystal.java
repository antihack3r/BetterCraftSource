/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import java.util.Random;
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

public class CosmeticEnderCrystal
extends CosmeticBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/endercrystal.png");
    public int innerRotation;
    private EnderCrystalModel enderCrystalModel;

    public CosmeticEnderCrystal(RenderPlayer player) {
        super(player);
        this.enderCrystalModel = new EnderCrystalModel(player);
        this.innerRotation = new Random().nextInt(100000);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.enderCrystalModel = new EnderCrystalModel(this.playerRenderer);
        ++this.innerRotation;
        GlStateManager.pushMatrix();
        this.playerRenderer.bindTexture(TEXTURE);
        if (player.isSneaking()) {
            GL11.glTranslated(0.0, 0.3, 0.0);
        }
        GL11.glTranslated(0.0, -0.9, 0.0);
        float f2 = this.innerRotation;
        float f1 = MathHelper.sin(f2 * 0.2f) / 2.0f + 0.5f;
        f1 = f1 * f1 + f1;
        this.enderCrystalModel.render(player, 0.0f, f2 * 0.05f, f1 * 0.002f, 0.0f, 0.0f, 0.1f);
        GL11.glPopMatrix();
    }

    @Override
    public int getId() {
        return 11;
    }

    public static class EnderCrystalModel
    extends CosmeticModelBase {
        ModelRenderer glass = new ModelRenderer(this, "glass");

        public EnderCrystalModel(RenderPlayer player) {
            super(player);
            this.glass.setTextureOffset(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(p_78088_3_, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, 0.8f + p_78088_4_, 0.0f);
            GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
            this.glass.render(scale);
            float f2 = 0.875f;
            GlStateManager.scale(f2, f2, f2);
            GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
            GlStateManager.rotate(p_78088_3_, 0.0f, 1.0f, 0.0f);
            this.glass.render(scale);
            GlStateManager.popMatrix();
        }
    }
}

