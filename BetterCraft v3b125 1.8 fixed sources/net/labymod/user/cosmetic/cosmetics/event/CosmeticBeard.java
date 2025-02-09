/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.event;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class CosmeticBeard
extends CosmeticRenderer<CosmeticBeardData> {
    public static final int ID = 5;
    private ModelRenderer beard;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int beardWidth = 7;
        int partLength = 3;
        this.beard = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        this.beard.setRotationPoint(0.0f, -2.0f, -5.0f);
        this.beard.setTextureOffset(0, 0).addBox(-3.5f, 0.0f, 0.0f, 7, 1, 1);
        ModelRenderer rightBeardCorner = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        rightBeardCorner.setRotationPoint(-1.5f, 1.0f, 0.0f);
        rightBeardCorner.setTextureOffset(0, 0).addBox(0.0f, 0.0f, 0.0f, 1, 3, 1);
        rightBeardCorner.rotateAngleZ = 1.5707964f;
        this.beard.addChild(rightBeardCorner);
        ModelRenderer leftBeardCorner = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        leftBeardCorner.setRotationPoint(1.5f, 1.0f, 0.0f);
        leftBeardCorner.setTextureOffset(0, 0).addBox(0.0f, 0.0f, 0.0f, 3, 1, 1);
        this.beard.addChild(leftBeardCorner);
        ModelRenderer beardFront = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        beardFront.setRotationPoint(0.0f, 1.3f, -0.3f);
        beardFront.setTextureOffset(0, 0).addBox(-3.5f, 0.0f, 0.0f, 7, 2, 2);
        this.beard.addChild(beardFront);
        ModelRenderer firstPart = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        firstPart.setRotationPoint(0.0f, 2.0f, 0.0f);
        firstPart.setTextureOffset(0, 0).addBox(-2.5f, 0.0f, 0.0f, 5, 3, 2);
        beardFront.addChild(firstPart);
        ModelRenderer secondPart = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        secondPart.setRotationPoint(0.0f, 3.0f, 0.0f);
        secondPart.setTextureOffset(0, 0).addBox(-2.0f, 0.0f, 0.0f, 4, 3, 2);
        firstPart.addChild(secondPart);
        ModelRenderer thirdPart = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        thirdPart.setRotationPoint(0.0f, 3.0f, 0.0f);
        thirdPart.setTextureOffset(0, 0).addBox(-1.5f, 0.0f, 0.0f, 3, 3, 2);
        secondPart.addChild(thirdPart);
        this.beard.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.beard.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticBeardData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        float partialTicks = LabyMod.getInstance().getPartialTicks();
        AbstractClientPlayer clientPlayer = (AbstractClientPlayer)entityIn;
        ModelRenderer beard = this.bindTextureAndColor(null, ModTextures.COSMETIC_XMAS, this.beard);
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.scale(0.95f, 0.95f, 0.95f);
        ModelRenderer beardFront = beard.childModels.get(2);
        ModelRenderer firstPart = beardFront.childModels.get(0);
        ModelRenderer secondPart = firstPart.childModels.get(0);
        ModelRenderer thirdPart = secondPart.childModels.get(0);
        if (canAnimate) {
            AbstractClientPlayer entitylivingbaseIn = (AbstractClientPlayer)entityIn;
            double motionX = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
            double motionY = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
            double motionZ = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
            float motionYaw = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
            double motionSin = LabyModCore.getMath().sin(motionYaw * (float)Math.PI / 180.0f);
            double motionCos = -LabyModCore.getMath().cos(motionYaw * (float)Math.PI / 180.0f);
            float rotation = (float)motionY * 10.0f;
            rotation = LabyModCore.getMath().clamp_float(rotation, -6.0f, 32.0f);
            float motionAdd = (float)(motionX * motionSin + motionZ * motionCos) * 100.0f;
            float motionSub = (float)(motionX * motionCos - motionZ * motionSin) * 100.0f;
            if (motionAdd < 0.0f) {
                motionAdd = 0.0f;
            }
            float pitch = clientPlayer.prevRotationPitch + (clientPlayer.rotationPitch - clientPlayer.prevRotationPitch) * partialTicks;
            float bendLimit = 30.0f;
            if (pitch < -30.0f) {
                pitch = -30.0f;
            }
            beardFront.rotateAngleX = pitch / -50.0f;
            float strength = 200.0f;
            float bend = pitch < 0.0f ? pitch / -50.0f : 0.0f;
            firstPart.rotateAngleZ = motionSub / 200.0f;
            secondPart.rotateAngleZ = motionSub / 200.0f;
            thirdPart.rotateAngleZ = motionSub / 200.0f;
            firstPart.rotateAngleX = motionAdd / 200.0f;
            secondPart.rotateAngleX = motionAdd / 200.0f;
            thirdPart.rotateAngleX = motionAdd / 200.0f;
            firstPart.rotationPointZ = pitch / 400.0f - bend;
            secondPart.rotationPointZ = pitch / 400.0f;
            thirdPart.rotationPointZ = pitch / 400.0f + bend;
        }
        switch (cosmeticData.getLength()) {
            case 0: {
                firstPart.isHidden = true;
                break;
            }
            case 1: {
                secondPart.isHidden = true;
                break;
            }
            case 2: {
                thirdPart.isHidden = true;
            }
        }
        beard.isHidden = false;
        beard.render(scale);
        firstPart.isHidden = false;
        secondPart.isHidden = false;
        thirdPart.isHidden = false;
        beard.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public void onTick() {
    }

    @Override
    public int getCosmeticId() {
        return 5;
    }

    @Override
    public String getCosmeticName() {
        return "Beard";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.0f;
    }

    public static class CosmeticBeardData
    extends CosmeticData {
        private int length = 3;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.length = Integer.parseInt(data[0]);
        }

        public int getLength() {
            return this.length;
        }
    }
}

