/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.body;

import java.awt.Color;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticWatch
extends CosmeticRenderer<CosmeticWatchData> {
    public static final int ID = 33;
    private ModelRenderer watchCase;
    private ModelRenderer numberField;
    private ModelRenderer band;
    private ModelRenderer pointerHourMinute;
    private ModelRenderer pointerSeconds;
    private Calendar calendar = Calendar.getInstance();

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int widthCase = 36;
        int heightCase = 19;
        int widthDesign = 20;
        int heightDesign = 21;
        boolean isSlim = modelCosmetics.bipedLeftArm.cubeList.get((int)0).posX2 == 2.0f;
        float caseDepth = isSlim ? 0.0f : 4.6f;
        this.band = new ModelRenderer(modelCosmetics).setTextureSize(20, 21).setTextureOffset(0, isSlim ? 6 : 0);
        this.band.addBox(isSlim ? -1.5f : -1.4f, 8.7f, -2.5f, isSlim ? 4 : 5, 1, 5, modelSize);
        this.band.isHidden = true;
        this.watchCase = new ModelRenderer(modelCosmetics).setTextureSize(36, 19).setTextureOffset(0, 0);
        this.watchCase.addBox(-3.0f, -4.0f, caseDepth, 6, 8, 1, modelSize);
        this.watchCase.rotateAngleY = (float)Math.toRadians(90.0);
        this.watchCase.isHidden = true;
        ModelRenderer model = new ModelRenderer(modelCosmetics).setTextureSize(36, 19).setTextureOffset(16, 0);
        model.addBox(-4.0f, -3.0f, caseDepth, 1, 6, 1, modelSize);
        model.addBox(3.0f, -3.0f, caseDepth, 1, 6, 1, modelSize);
        this.watchCase.addChild(model);
        this.numberField = new ModelRenderer(modelCosmetics).setTextureSize(20, 21).setTextureOffset(0, 12);
        this.numberField.addBox(-3.0f, -4.0f, caseDepth - 0.1f, 6, 8, 1, modelSize);
        this.numberField.rotateAngleY = (float)Math.toRadians(90.0);
        this.numberField.isHidden = true;
        float depht = caseDepth + 0.3f;
        float addedScale = 0.01f;
        model = new ModelRenderer(modelCosmetics).setTextureSize(36, 19).setTextureOffset(0, 9);
        model.addBox(-2.0f, -4.0f, depht, 4, 1, 1, modelSize + 0.01f);
        model.addBox(-2.0f, 3.0f, depht, 4, 1, 1, modelSize + 0.01f);
        this.watchCase.addChild(model);
        model = new ModelRenderer(modelCosmetics).setTextureSize(36, 19).setTextureOffset(0, 9);
        model.addBox(-2.0f, -4.0f, depht, 4, 1, 1, modelSize + 0.01f);
        model.addBox(-2.0f, 3.0f, depht, 4, 1, 1, modelSize + 0.01f);
        model.rotateAngleZ = (float)Math.toRadians(90.0);
        this.watchCase.addChild(model);
        model = new ModelRenderer(modelCosmetics).setTextureSize(36, 19).setTextureOffset(14, 9);
        model.addBox(2.0f, -3.0f, depht, 1, 1, 1, modelSize + 0.01f);
        model.addBox(-3.0f, -3.0f, depht, 1, 1, 1, modelSize + 0.01f);
        model.addBox(2.0f, 2.0f, depht, 1, 1, 1, modelSize + 0.01f);
        model.addBox(-3.0f, 2.0f, depht, 1, 1, 1, modelSize + 0.01f);
        model.addBox(-0.5f, -0.5f, caseDepth + 0.6f, 1, 1, 1, -0.2f);
        this.watchCase.addChild(model);
        this.pointerHourMinute = new ModelRenderer(modelCosmetics);
        this.pointerHourMinute.isHidden = true;
        ModelRenderer pointerHour = new ModelRenderer(modelCosmetics).setTextureSize(36, 19).setTextureOffset(24, 0);
        pointerHour.addBox(6.0f * caseDepth, 0.0f, -1.0f, 1, 12, 2, modelSize);
        this.pointerHourMinute.addChild(pointerHour);
        ModelRenderer pointerMinute = new ModelRenderer(modelCosmetics).setTextureSize(36, 19).setTextureOffset(30, 0);
        pointerMinute.addBox(6.0f * caseDepth + 0.2f, 0.0f, -1.0f, 1, 17, 2, modelSize);
        this.pointerHourMinute.addChild(pointerMinute);
        this.pointerSeconds = new ModelRenderer(modelCosmetics).setTextureSize(36, 19).setTextureOffset(20, 0);
        this.pointerSeconds.addBox(6.0f * caseDepth + 0.3f, 0.0f, -0.5f, 1, 16, 1, modelSize);
        this.pointerSeconds.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.watchCase.showModel = invisible;
        this.band.showModel = invisible;
        this.pointerHourMinute.showModel = invisible;
        this.pointerSeconds.showModel = invisible;
        this.numberField.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticWatchData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        ResourceLocation textureDesign = LabyMod.getInstance().getUserManager().getCosmeticImageManager().getWatchImageHandler().getResourceLocation((AbstractClientPlayer)entityIn);
        if (textureDesign == null) {
            return;
        }
        Color caseColor = cosmeticData.getColor();
        this.watchCase.isHidden = false;
        this.band.isHidden = false;
        this.numberField.isHidden = false;
        this.pointerHourMinute.isHidden = false;
        this.pointerSeconds.isHidden = false;
        ModelRenderer model = cosmeticData.isUseRightHand() ? modelCosmetics.bipedRightArm : modelCosmetics.bipedLeftArm;
        GlStateManager.pushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureDesign);
        GlStateManager.translate(model.rotationPointX * scale, model.rotationPointY * scale, model.rotationPointZ * scale);
        GlStateManager.rotate(model.rotateAngleZ * 57.295776f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(model.rotateAngleY * 57.295776f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(model.rotateAngleX * 57.295776f, 1.0f, 0.0f, 0.0f);
        if (cosmeticData.isUseRightHand()) {
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        }
        GlStateManager.translate(scale * -1.0f, scale * -2.0f, scale * -2.0f);
        GlStateManager.translate(0.0f, scale * (float)(-cosmeticData.getPositionY()) * 0.5f, 0.0f);
        double bandScale = 0.825f;
        double watchScale = 0.2f;
        double pointerScale = 0.032f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(scale * 1.0f, scale * 2.0f, scale * 2.0f);
        GlStateManager.pushMatrix();
        GlStateManager.scale((double)0.825f, (double)0.825f, (double)0.825f);
        GlStateManager.translate(scale * 0.1f, scale * 1.7f, 0.0f);
        this.band.render(scale);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(scale * 2.1f, scale * 9.0f, 0.0f);
        GlStateManager.scale((double)0.2f, (double)0.2f, (double)0.2f);
        GlStateManager.rotate(cosmeticData.isUseRightHand() ? -90.0f : 90.0f, -1.0f, 0.0f, 0.0f);
        this.bindTextureAndColor(caseColor, ModTextures.COSMETIC_WATCH_CASE, this.watchCase).render(scale);
        this.bindTextureAndColor(Color.white, textureDesign, this.numberField).render(scale);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.translate(scale * 3.0f + 0.02f, scale * 11.0f, scale * 2.0f);
        GlStateManager.scale((double)0.032f, (double)0.032f, (double)0.032f);
        this.calendar.setTimeInMillis(System.currentTimeMillis());
        int minute = this.calendar.get(12);
        int hour = this.calendar.get(10);
        int seconds = this.calendar.get(13);
        float hourInterpolation = 0.016666668f * (float)minute;
        float mirror = cosmeticData.isUseRightHand() ? 0.0f : 180.0f;
        List<ModelRenderer> cm2 = this.pointerHourMinute.childModels;
        cm2.get((int)0).rotateAngleX = (float)Math.toRadians(-30.0f * ((float)hour + hourInterpolation) - mirror);
        cm2.get((int)1).rotateAngleX = (float)Math.toRadians(-6.0f * (float)minute - mirror);
        this.pointerSeconds.rotateAngleX = (float)Math.toRadians(-6.0f * (float)seconds - mirror);
        GlStateManager.rotate(90.0f, -1.0f, 0.0f, 0.0f);
        if (cosmeticData.isDisplaySecondPointer()) {
            this.bindTextureAndColor(Color.WHITE, ModTextures.COSMETIC_WATCH_CASE, this.pointerSeconds).render(scale);
        }
        this.bindTextureAndColor(caseColor, ModTextures.COSMETIC_WATCH_CASE, this.pointerHourMinute).render(scale);
        GlStateManager.popMatrix();
        this.band.isHidden = true;
        this.watchCase.isHidden = true;
        this.numberField.isHidden = true;
        this.pointerHourMinute.isHidden = true;
        this.pointerSeconds.isHidden = true;
    }

    @Override
    public int getCosmeticId() {
        return 33;
    }

    @Override
    public String getCosmeticName() {
        return "Watch";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticWatchData
    extends CosmeticData {
        private Color color = Color.DARK_GRAY;
        private boolean useRightHand;
        private int positionY;
        private boolean displaySecondPointer;
        private UserTextureContainer userTextureContainer;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void init(User user) {
            this.userTextureContainer = user.getWatchContainer();
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.color = Color.decode("#" + data[0]);
            this.useRightHand = Integer.parseInt(data[1]) == 1;
            this.positionY = Integer.parseInt(data[2]);
            this.displaySecondPointer = Integer.parseInt(data[3]) == 1;
            this.userTextureContainer.setFileName(UUID.fromString(data[4]));
            this.positionY = Math.max(0, this.positionY);
            this.positionY = Math.min(10, this.positionY);
        }

        public Color getColor() {
            return this.color;
        }

        public boolean isUseRightHand() {
            return this.useRightHand;
        }

        public int getPositionY() {
            return this.positionY;
        }

        public boolean isDisplaySecondPointer() {
            return this.displaySecondPointer;
        }
    }
}

