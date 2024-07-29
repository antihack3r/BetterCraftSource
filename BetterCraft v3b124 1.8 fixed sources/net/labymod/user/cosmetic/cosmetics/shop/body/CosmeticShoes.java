/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.body;

import java.util.UUID;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticShoes
extends CosmeticRenderer<CosmeticShoesData> {
    public static final int ID = 27;
    private ModelRenderer shoeRight;
    private ModelRenderer shoeLeft;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 27;
        int height = 18;
        this.shoeRight = new ModelRenderer(modelCosmetics).setTextureSize(27, 18).setTextureOffset(0, 0);
        this.shoeRight.addBox(-2.6f, 9.0f, -2.5f, 5, 4, 5, modelSize);
        this.shoeRight.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.shoeRight.isHidden = true;
        ModelRenderer shoeFront = new ModelRenderer(modelCosmetics).setTextureSize(27, 18).setTextureOffset(15, 0);
        shoeFront.addBox(-2.6f, 9.0f, -2.5f, 5, 3, 1, modelSize);
        shoeFront.setRotationPoint(0.0f, 1.0f, -1.0f);
        this.shoeRight.addChild(shoeFront);
        this.shoeLeft = new ModelRenderer(modelCosmetics).setTextureSize(27, 18).setTextureOffset(0, 9);
        this.shoeLeft.addBox(-2.4f, 9.0f, -2.5f, 5, 4, 5, modelSize);
        this.shoeLeft.setRotationPoint(1.9f, 12.001f, 0.001f);
        this.shoeLeft.isHidden = true;
        shoeFront = new ModelRenderer(modelCosmetics).setTextureSize(27, 18).setTextureOffset(15, 9);
        shoeFront.addBox(-2.4f, 9.0f, -2.5f, 5, 3, 1, modelSize);
        shoeFront.setRotationPoint(0.0f, 1.0f, -1.0f);
        this.shoeLeft.addChild(shoeFront);
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.shoeRight.showModel = invisible;
        this.shoeLeft.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticShoesData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        ResourceLocation location = LabyMod.getInstance().getUserManager().getCosmeticImageManager().getShoesImageHandler().getResourceLocation((AbstractClientPlayer)entityIn);
        if (location == null) {
            return;
        }
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
        this.shoeRight.isHidden = false;
        this.shoeLeft.isHidden = false;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        ModelBiped.copyModelAngles(modelCosmetics.bipedRightLeg, this.shoeRight);
        ModelBiped.copyModelAngles(modelCosmetics.bipedLeftLeg, this.shoeLeft);
        this.shoeRight.render(scale);
        this.shoeLeft.render(scale);
        this.shoeLeft.isHidden = true;
        this.shoeRight.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 27;
    }

    @Override
    public String getCosmeticName() {
        return "Shoes";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticShoesData
    extends CosmeticData {
        private UserTextureContainer userTextureContainer;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void init(User user) {
            this.userTextureContainer = user.getShoesContainer();
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.userTextureContainer.setFileName(UUID.fromString(data[0]));
        }
    }
}

