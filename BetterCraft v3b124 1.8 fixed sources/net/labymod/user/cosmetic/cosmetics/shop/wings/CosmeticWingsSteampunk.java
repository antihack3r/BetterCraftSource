/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.wings;

import java.awt.Color;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.labymod.user.cosmetic.util.ModelRendererHook;
import net.labymod.utils.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CosmeticWingsSteampunk
extends CosmeticRenderer<CosmeticWingsSteamPunkData> {
    public static final int ID = 14;
    private ModelRenderer model;
    private float lastAnimationTick;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 26;
        int height = 22;
        float layerOffset = 0.5f;
        ModelRenderer bone3 = new ModelRenderer(modelCosmetics).setTextureSize(26, 22).setTextureOffset(0, 0);
        bone3.addBox(-0.5f, -0.5f, 1.0f, 1, 10, 1);
        bone3.setRotationPoint(1.0f, 2.0f, 0.0f);
        ModelRenderer bone3Sub = new ModelRenderer(modelCosmetics).setTextureSize(26, 22).setTextureOffset(8, 0);
        bone3Sub.addBox(-0.5f, -1.5f, 1.0f, 1, 5, 1);
        bone3Sub.setRotationPoint(0.0f, 9.0f, 0.0f);
        bone3Sub.addChild(bone3);
        final ModelRendererHook bone3Gear = (ModelRendererHook)new ModelRendererHook(modelCosmetics).setTextureSize(26, 22).setTextureOffset(16, 0);
        bone3Gear.addBox(-2.0f, -2.0f, 1.1f, 4, 4, 1);
        bone3Gear.setRotationPoint(0.0f, 0.0f, 1.0f);
        bone3Gear.setHook(new Consumer<ModelRendererHook>(){

            @Override
            public void accept(ModelRendererHook model) {
                GlStateManager.enableBlend();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                bone3Gear.renderSuper();
                GlStateManager.disableBlend();
            }
        });
        bone3Sub.addChild(bone3Gear);
        ModelRenderer bone4 = new ModelRenderer(modelCosmetics).setTextureSize(26, 22).setTextureOffset(22, 11);
        bone4.addBox(-0.5f, -0.5f, 0.4f, 1, 10, 1);
        bone4.setRotationPoint(0.0f, 3.0f, 0.0f);
        bone4.addChild(bone3Sub);
        ModelRenderer bone5 = new ModelRenderer(modelCosmetics).setTextureSize(26, 22).setTextureOffset(12, 0);
        bone5.addBox(-0.5f, -0.5f, 0.5f, 1, 4, 1);
        bone5.setRotationPoint(0.0f, 0.0f, 0.0f);
        bone5.addChild(bone4);
        final ModelRendererHook bone1Gear = (ModelRendererHook)new ModelRendererHook(modelCosmetics).setTextureSize(26, 22).setTextureOffset(8, 6);
        bone1Gear.addBox(-1.0f, -1.0f, 2.0f, 2, 2, 1);
        bone1Gear.setRotationPoint(0.0f, 3.0f, 0.0f);
        bone1Gear.setHook(new Consumer<ModelRendererHook>(){

            @Override
            public void accept(ModelRendererHook model) {
                GlStateManager.enableBlend();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                bone1Gear.renderSuper();
                GlStateManager.disableBlend();
            }
        });
        bone5.addChild(bone1Gear);
        ModelRenderer stick1 = new ModelRenderer(modelCosmetics).setTextureSize(26, 22).setTextureOffset(0, 11);
        stick1.addBox(-0.5f, -0.5f, 0.5f, 1, 10, 1);
        stick1.setRotationPoint(0.0f, 9.0f, 0.0f);
        bone4.addChild(stick1);
        ModelRenderer stick2 = new ModelRenderer(modelCosmetics).setTextureSize(26, 22).setTextureOffset(4, 11);
        stick2.addBox(-0.5f, -0.5f, 0.6f, 1, 9, 1);
        stick2.setRotationPoint(0.0f, 9.0f, 0.0f);
        bone4.addChild(stick2);
        ModelRenderer stick3 = new ModelRenderer(modelCosmetics).setTextureSize(26, 22).setTextureOffset(8, 11);
        stick3.addBox(-0.5f, -0.5f, 0.7f, 1, 8, 1);
        stick3.setRotationPoint(0.0f, 9.0f, 0.0f);
        bone4.addChild(stick3);
        ModelRendererHook textile1 = (ModelRendererHook)new ModelRendererHook(modelCosmetics).setTextureSize(26, 22).setTextureOffset(16, 5);
        textile1.addBox(-0.5f, -0.5f, 0.4f, 4, 8, 1);
        textile1.setRotationPoint(-3.8f, 2.0f, 0.0f);
        textile1.setHook(new Consumer<ModelRendererHook>(){

            @Override
            public void accept(ModelRendererHook model) {
                CosmeticWingsSteampunk.this.renderTextile(model, 1);
            }
        });
        bone3.addChild(textile1);
        ModelRendererHook textile2 = (ModelRendererHook)new ModelRendererHook(modelCosmetics).setTextureSize(26, 22).setTextureOffset(16, 5);
        textile2.addBox(-0.5f, -0.5f, 0.4f, 4, 8, 1);
        textile2.setRotationPoint(-3.8f, 2.0f, 0.0f);
        textile2.setHook(new Consumer<ModelRendererHook>(){

            @Override
            public void accept(ModelRendererHook model) {
                CosmeticWingsSteampunk.this.renderTextile(model, 2);
            }
        });
        stick1.addChild(textile2);
        ModelRendererHook textile3 = (ModelRendererHook)new ModelRendererHook(modelCosmetics).setTextureSize(26, 22).setTextureOffset(16, 5);
        textile3.addBox(-0.5f, -0.5f, 0.4f, 4, 8, 1);
        textile3.setRotationPoint(-3.8f, 2.0f, 0.0f);
        textile3.setHook(new Consumer<ModelRendererHook>(){

            @Override
            public void accept(ModelRendererHook model) {
                CosmeticWingsSteampunk.this.renderTextile(model, 3);
            }
        });
        stick2.addChild(textile3);
        ModelRendererHook textile4 = (ModelRendererHook)new ModelRendererHook(modelCosmetics).setTextureSize(26, 22).setTextureOffset(16, 5);
        textile4.addBox(-0.5f, -0.5f, 0.4f, 4, 8, 1);
        textile4.setRotationPoint(-3.8f, 2.0f, 0.0f);
        textile4.setHook(new Consumer<ModelRendererHook>(){

            @Override
            public void accept(ModelRendererHook model) {
                CosmeticWingsSteampunk.this.renderTextile(model, 4);
            }
        });
        stick3.addChild(textile4);
        ModelRenderer connection = new ModelRenderer(modelCosmetics).setTextureSize(26, 22).setTextureOffset(12, 11);
        connection.addBox(0.5f, 0.0f, -1.0f, 1, 10, 1);
        connection.setRotationPoint(0.0f, 1.5f, 0.0f);
        bone5.addChild(connection);
        this.model = bone5;
        this.model.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.model.showModel = invisible;
    }

    private void renderTextile(ModelRendererHook model, int index) {
        boolean fogEnabled = GL11.glGetBoolean(2912);
        GlStateManager.pushMatrix();
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.depthMask(false);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.VOID);
        double heightMain = 0.0;
        double heightOff = 0.0;
        double distance = 0.0;
        float zMain = 0.07f;
        float zOff = 0.07f;
        float transparency = (1.0f - this.lastAnimationTick) / 2.0f;
        switch (index) {
            case 1: {
                heightMain = 0.6f;
                heightOff = 0.4f;
                distance = (double)this.lastAnimationTick / 3.0 - 0.32;
                zMain = 0.09f;
                break;
            }
            case 2: {
                heightMain = 0.6f;
                heightOff = 0.43f;
                distance = (double)this.lastAnimationTick / 3.0 - 0.32;
                break;
            }
            case 3: {
                heightMain = 0.53f;
                heightOff = 0.41f;
                distance = (double)this.lastAnimationTick / 4.0 - 0.26;
                break;
            }
            case 4: {
                heightMain = 0.47f;
                heightOff = 0.46f;
                distance = (double)this.lastAnimationTick / 2.6 - 0.42;
            }
        }
        GL11.glBegin(4);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, transparency);
        if (index == 1) {
            GL11.glVertex3f(-0.06f, -0.0f, zMain);
        } else {
            GL11.glVertex3f(0.0f, 0.0f, zMain);
        }
        GL11.glVertex3d(distance, heightOff, index == 4 ? (double)0.05f : (double)0.07f);
        GL11.glVertex3d(0.0, heightMain, zMain);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnd();
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_WINGS_STEAMPUNK);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlpha();
        if (fogEnabled) {
            GlStateManager.enableFog();
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticWingsSteamPunkData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        float animation = 1.0f - cosmeticData.getAnimationTick(walkingSpeed / 1.3f) + (float)Math.cos(movementFactor / 2.0f) / 15.0f * walkingSpeed - 0.1f;
        if (entityIn.isSneaking()) {
            animation -= 0.3f;
        }
        this.lastAnimationTick = animation;
        this.model.rotateAngleX = 0.4f;
        this.model.rotateAngleY = 0.0f;
        this.model.rotateAngleZ = animation - 1.0f;
        ModelRenderer connection = this.model.childModels.get(2);
        connection.rotateAngleX = 0.6f;
        connection.rotateAngleY = -0.4f;
        connection.rotateAngleZ = -animation * 1.5f - 1.2f;
        ModelRenderer bone2 = this.model.childModels.get(0);
        bone2.rotateAngleX = 0.5f;
        bone2.rotateAngleY = -0.4f;
        bone2.rotateAngleZ = -animation * 1.4f - 1.5f;
        ModelRenderer gear1 = this.model.childModels.get(1);
        gear1.rotateAngleZ = animation;
        ModelRenderer bone3 = bone2.childModels.get(0);
        bone3.rotateAngleZ = animation * 1.8f * 1.2f + 1.8f - 1.0f;
        ModelRenderer gear2 = bone3.childModels.get(1);
        gear2.rotateAngleZ = animation;
        ModelRenderer stick1 = bone2.childModels.get(1);
        stick1.rotateAngleZ = (animation * 0.9f + 2.1f) * 2.0f - 3.0f;
        ModelRenderer stick2 = bone2.childModels.get(2);
        stick2.rotateAngleZ = (animation * 0.6f + 2.4f) * 2.0f - 3.0f;
        ModelRenderer stick3 = bone2.childModels.get(3);
        stick3.rotateAngleZ = (animation * 0.3f + 2.7f) * 2.0f - 3.0f;
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, (double)-0.01f, -0.0);
            GlStateManager.rotate(20.0f, 1.0f, 0.0f, 0.0f);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_WINGS_STEAMPUNK);
        this.model.isHidden = false;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.4f);
        GlStateManager.translate(0.0f, 0.1f, 0.1f);
        GlStateManager.scale(0.8f, 0.8f, 0.8f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlpha();
        Color color = cosmeticData.getColor();
        int i2 = -1;
        while (i2 <= 1) {
            GlStateManager.pushMatrix();
            if (i2 == 1) {
                GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            }
            GlStateManager.translate(0.1, 0.0, 0.0);
            GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 0.5f);
            if (i2 == 1 && cosmeticData.getSecondColor() != null) {
                Color secondColor = cosmeticData.getSecondColor();
                GL11.glColor4f((float)secondColor.getRed() / 255.0f, (float)secondColor.getGreen() / 255.0f, (float)secondColor.getBlue() / 255.0f, 0.5f);
            }
            this.model.render(scale);
            GlStateManager.popMatrix();
            i2 += 2;
        }
        this.model.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 14;
    }

    @Override
    public String getCosmeticName() {
        return "SteampunkWings";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticWingsSteamPunkData
    extends CosmeticData {
        public long lastWalking;
        private Color color = new Color(60, 60, 60);
        private Color secondColor = null;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.color = Color.decode("#" + data[0]);
            if (data.length > 1) {
                this.secondColor = Color.decode("#" + data[1]);
            }
        }

        public float getAnimationTick(float walkingSpeed) {
            long fadeOutAnimationTick = this.lastWalking - System.currentTimeMillis();
            if ((double)walkingSpeed > 0.7) {
                this.lastWalking = System.currentTimeMillis() + 2500L;
            }
            float fadeOut = 0.00136f * (float)(this.lastWalking - System.currentTimeMillis());
            if (fadeOutAnimationTick < 500L && fadeOutAnimationTick >= 0L) {
                return Math.max(fadeOut, walkingSpeed);
            }
            if (fadeOutAnimationTick <= 0L || (double)walkingSpeed >= 0.7) {
                return walkingSpeed;
            }
            if (fadeOutAnimationTick < 500L) {
                return fadeOut;
            }
            return 0.7f + (float)Math.cos((float)(fadeOutAnimationTick + 900L) / 360.0f) / 20.0f;
        }

        public long getLastWalking() {
            return this.lastWalking;
        }

        public void setLastWalking(long lastWalking) {
            this.lastWalking = lastWalking;
        }

        public Color getColor() {
            return this.color;
        }

        public Color getSecondColor() {
            return this.secondColor;
        }
    }
}

