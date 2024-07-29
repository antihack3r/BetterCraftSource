/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.partner;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.List;
import java.util.Random;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CosmeticReved
extends CosmeticRenderer<CosmeticRevedData> {
    public static final int ID = 30;
    private List<ModelRenderer> framesRight;
    private List<ModelRenderer> framesLeft;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 5;
        int height = 12;
        int totalFrames = 120;
        boolean isSlim = modelCosmetics.bipedLeftArm.cubeList.get((int)0).posX2 == 2.0f;
        this.framesRight = Lists.newArrayList();
        this.framesLeft = Lists.newArrayList();
        int slimSkinOffset = 0;
        int currentFrame = 0;
        while (currentFrame < 120) {
            int sides = 0;
            while (sides < 2) {
                boolean rightSide = sides == 0;
                ModelRenderer modelFire = new ModelRenderer(modelCosmetics).setTextureSize(600, 34);
                modelFire.setTextureOffset(isSlim ? slimSkinOffset : currentFrame * 5, isSlim ? 17 : 0);
                modelFire.setRotationPoint(rightSide ? -5.0f : 5.0f, 12.0f, 0.0f);
                modelFire.addBox(rightSide ? -2.6f - (float)(!isSlim ? 1 : 0) : -1.4f, -0.72f, -2.5f, isSlim ? 4 : 5, 12, 5, modelSize);
                modelFire.isHidden = true;
                if (rightSide) {
                    this.framesRight.add(modelFire);
                } else {
                    this.framesLeft.add(modelFire);
                }
                ++sides;
            }
            slimSkinOffset += 5 - currentFrame % 2;
            ++currentFrame;
        }
    }

    @Override
    public void setInvisible(boolean invisible) {
        for (ModelRenderer frame : this.framesRight) {
            frame.showModel = invisible;
        }
        for (ModelRenderer frame : this.framesLeft) {
            frame.showModel = invisible;
        }
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticRevedData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        boolean fireOnRightHand = cosmeticData.isUseRightSide();
        boolean coloredTexture = cosmeticData.isUseColoredTexture() && !cosmeticData.isRainbow();
        List<ModelRenderer> modelFire = fireOnRightHand ? this.framesRight : this.framesLeft;
        int index = (int)(System.currentTimeMillis() / 70L % (long)(modelFire.size() / 2 - 2)) * 2;
        ModelRenderer modelFireRing = modelFire.get(index);
        int color = cosmeticData.getColor().getRGB();
        if (cosmeticData.isRainbow()) {
            long time = System.currentTimeMillis() % 10000L;
            color = Color.HSBtoRGB((float)time / 10000.0f, 1.0f, 1.0f);
        }
        this.bindTextureAndColor(color, coloredTexture ? ModTextures.COSMETIC_REVED_ARM_COLORED : ModTextures.COSMETIC_REVED_ARM, modelFireRing);
        ModelBiped.copyModelAngles(fireOnRightHand ? modelCosmetics.bipedRightArm : modelCosmetics.bipedLeftArm, modelFireRing);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.alphaFunc(516, 0.003921569f);
        float size = 0.91f;
        GlStateManager.translate(modelFireRing.rotationPointX * scale, modelFireRing.rotationPointY * scale, modelFireRing.rotationPointZ * scale);
        GlStateManager.scale(0.91f, 0.91f, 0.91f);
        GlStateManager.translate(-modelFireRing.rotationPointX * scale, -modelFireRing.rotationPointY * scale, -modelFireRing.rotationPointZ * scale);
        modelFireRing.isHidden = false;
        modelFireRing.render(scale);
        modelFireRing.isHidden = true;
        GlStateManager.pushMatrix();
        GlStateManager.translate(modelFireRing.rotationPointX * scale, modelFireRing.rotationPointY * scale, modelFireRing.rotationPointZ * scale);
        GlStateManager.translate(-0.25, -0.2, -0.2);
        FireParticle[] fireParticleArray = cosmeticData.getParticle();
        int n2 = fireParticleArray.length;
        int n3 = 0;
        while (n3 < n2) {
            FireParticle particle = fireParticleArray[n3];
            particle.render(entityIn, coloredTexture, firstRotationX, color);
            ++n3;
        }
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 30;
    }

    @Override
    public String getCosmeticName() {
        return "Reved";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.0f;
    }

    public static class CosmeticRevedData
    extends CosmeticData {
        private Color color = Color.WHITE;
        private boolean useRightSide = false;
        private boolean useColoredTexture = true;
        private FireParticle[] particle = new FireParticle[0];
        private boolean rainbow = false;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.particle = new FireParticle[20];
            int i2 = 0;
            while (i2 < 20) {
                this.particle[i2] = new FireParticle();
                ++i2;
            }
            this.color = Color.decode("#" + data[0]);
            if (!this.color.equals(Color.WHITE)) {
                this.useColoredTexture = false;
            }
            if (data.length >= 2) {
                boolean bl2 = this.useRightSide = Integer.parseInt(data[1]) == 1;
            }
            if (data.length >= 3 && Integer.parseInt(data[2]) == 1) {
                this.rainbow = true;
                this.useColoredTexture = true;
            }
        }

        public Color getColor() {
            return this.color;
        }

        public boolean isUseRightSide() {
            return this.useRightSide;
        }

        public boolean isUseColoredTexture() {
            return this.useColoredTexture;
        }

        public FireParticle[] getParticle() {
            return this.particle;
        }

        public boolean isRainbow() {
            return this.rainbow;
        }
    }

    public static class FireParticle {
        private double x;
        private double y;
        private double z;
        private float rotZ;
        private long timestamp;
        private long lifetime;

        public void render(Entity entityIn, boolean coloredTexture, float firstRotationX, int color) {
            long alive = System.currentTimeMillis() - this.timestamp;
            double percent = 100.0 / (double)this.lifetime * (double)alive - 50.0;
            double fade = 255.0 - Math.min(255.0, percent * percent);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.translate(this.x, this.y - (double)((float)alive / 6000.0f), this.z);
            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            if (entityIn instanceof AbstractClientPlayer) {
                AbstractClientPlayer entity = (AbstractClientPlayer)entityIn;
                float rotation = renderManager.playerViewY - entity.rotationYawHead;
                if (entity != Minecraft.getMinecraft().getRenderViewEntity()) {
                    GlStateManager.rotate(rotation + 180.0f, 0.0f, 1.0f, 0.0f);
                } else if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 1) {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                }
                GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
            }
            GlStateManager.rotate(this.rotZ + (float)alive / 100.0f, 0.0f, 0.0f, 1.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            if (!coloredTexture) {
                int red = color >> 16 & 0xFF;
                int green = color >> 8 & 0xFF;
                int blue = color >> 0 & 0xFF;
                GL11.glColor4f((float)red / 255.0f, (float)green / 255.0f, (float)blue / 255.0f, (float)fade / 255.0f);
            }
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_REVED_FLAME);
            LabyMod.getInstance().getDrawUtils().drawTexture(0.0, 0.0, coloredTexture ? 0.0 : 127.0, 0.0, 127.0, 255.0, 0.05, 0.05);
            GlStateManager.popMatrix();
            if (alive > this.lifetime) {
                Random random = LabyMod.getRandom();
                this.timestamp = System.currentTimeMillis();
                this.lifetime = (random.nextInt(4) + 1) * 1000;
                this.x = random.nextDouble() / 2.8;
                this.y = random.nextDouble();
                this.z = random.nextDouble() / 2.5;
                this.rotZ = random.nextFloat() * 360.0f;
            }
        }
    }
}

