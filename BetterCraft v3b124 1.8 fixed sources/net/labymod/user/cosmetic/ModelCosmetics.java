/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.nzxtercode.bettercraft.client.events.PlayerTickEvent;
import me.nzxtercode.bettercraft.client.misc.irc.IRC;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.gui.skin.GuiSkinCustomization;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.user.User;
import net.labymod.user.UserManager;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.sticker.StickerRenderer;
import net.labymod.user.cosmetic.util.CosmeticClassLoader;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.labymod.user.emote.EmoteRenderer;
import net.lenni0451.eventapi.events.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModelCosmetics
extends ModelPlayer {
    private static final Class<?>[] partialTicksBlackList = new Class[]{GuiInventory.class, GuiContainerCreative.class, GuiSkinCustomization.class};
    private static final CosmeticClassLoader cosmeticClassLoader = new CosmeticClassLoader();
    private final StickerRenderer stickerRenderer;
    private final boolean mc18;
    protected Map<Integer, CosmeticRenderer<CosmeticData>> cosmeticRenderers = new HashMap<Integer, CosmeticRenderer<CosmeticData>>();

    public ModelCosmetics(float modelSize, boolean value) {
        super(modelSize, value);
        this.mc18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
        this.stickerRenderer = new StickerRenderer(this, modelSize);
        try {
            for (Class<?> loadedClassInfo : cosmeticClassLoader.getCosmeticClasses()) {
                CosmeticRenderer cosmeticRenderer = (CosmeticRenderer)loadedClassInfo.newInstance();
                this.cosmeticRenderers.put(cosmeticRenderer.getCosmeticId(), cosmeticRenderer);
                cosmeticRenderer.addModels(this, modelSize);
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    public ModelCosmetics(float p_i46304_1_, float f2, int i2, int j2) {
        this(p_i46304_1_, false);
    }

    @EventTarget
    public void handleEvent(PlayerTickEvent event) {
        for (CosmeticRenderer<CosmeticData> cosmeticRenderer : this.cosmeticRenderers.values()) {
            cosmeticRenderer.onTick();
        }
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch, float scale) {
        AbstractClientPlayer abstractClientPlayer;
        int itemId;
        boolean swap = LabyMod.getSettings().leftHand;
        ItemStack itemStack = LabyModCore.getMinecraft().getMainHandItem();
        int n2 = itemId = itemStack != null && itemStack.getItem() != null ? Item.getIdFromItem(itemStack.getItem()) : 0;
        if (LabyMod.getSettings().swapBow && itemId == 261) {
            boolean bl2 = swap = !swap;
        }
        if (swap && LabyModCore.getMinecraft().getItemInUseMaxCount() != 0 && itemId == 261 || swap && LabyMod.getInstance().isHasLeftHand()) {
            swap = false;
        }
        if (entityIn instanceof AbstractClientPlayer && LabyMod.getSettings().emotes) {
            abstractClientPlayer = (AbstractClientPlayer)entityIn;
            EmoteRenderer emoteRenderer = LabyMod.getInstance().getEmoteRegistry().getEmoteRendererFor(abstractClientPlayer);
            if (emoteRenderer != null && emoteRenderer.isVisible()) {
                emoteRenderer.checkForNextFrame();
                emoteRenderer.animate();
                emoteRenderer.transformEntity(entityIn, false, yaw, pitch);
                yaw = emoteRenderer.getFadedYaw();
                pitch = emoteRenderer.getFadedPitch();
            }
            this.bipedHead.rotateAngleY = yaw / 57.295776f;
            this.bipedHead.rotateAngleX = pitch / 57.295776f;
        }
        if (swap) {
            this.transformForLeftHand(entityIn, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch, scale);
        } else {
            super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch, scale);
        }
        if (LabyModCore.getMinecraft().isElytraFlying(entityIn)) {
            pitch = -45.0f;
        }
        if (entityIn instanceof AbstractClientPlayer && (LabyMod.getSettings().cosmetics || LabyMod.getSettings().stickers)) {
            UUID uuid;
            abstractClientPlayer = (AbstractClientPlayer)entityIn;
            UserManager userManager = LabyMod.getInstance().getUserManager();
            if (userManager.isWhitelisted(uuid = abstractClientPlayer.getUniqueID()) && !entityIn.isInvisible()) {
                User user = userManager.getUser(uuid);
                if (LabyMod.getSettings().cosmetics) {
                    boolean canAnimate = true;
                    if (Minecraft.getMinecraft().currentScreen != null) {
                        Class<?>[] classArray = partialTicksBlackList;
                        int n3 = partialTicksBlackList.length;
                        int n4 = 0;
                        while (n4 < n3) {
                            Class<?> clazz = classArray[n4];
                            if (clazz.isAssignableFrom(Minecraft.getMinecraft().currentScreen.getClass())) {
                                canAnimate = false;
                            }
                            ++n4;
                        }
                    }
                    ResourceLocation tex = abstractClientPlayer.getLocationSkin();
                    user.resetMaxNameTagHeight();
                    GlStateManager.pushMatrix();
                    GlStateManager.enableAlpha();
                    if (entityIn.isSneaking()) {
                        GlStateManager.translate(0.0f, 0.2f, 0.0f);
                    }
                    for (Map.Entry<Integer, CosmeticData> entry : user.getCosmetics().entrySet()) {
                        CosmeticData data = entry.getValue();
                        if (!data.isEnabled() || IRC.getInstance().isUserConnected(abstractClientPlayer.getGameProfile().getName())) continue;
                        GlStateManager.pushMatrix();
                        GlStateManager.disableBlend();
                        GlStateManager.enableLighting();
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        GlStateManager.enableLighting();
                        GlStateManager.enableLight(0);
                        GlStateManager.enableLight(1);
                        GlStateManager.enableColorMaterial();
                        GlStateManager.colorMaterial(1032, 5634);
                        CosmeticRenderer<CosmeticData> cosmeticRenderer = this.cosmeticRenderers.get(entry.getKey());
                        cosmeticRenderer.render(this, entityIn, data, scale, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch, canAnimate);
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        GlStateManager.enableTexture2D();
                        GlStateManager.shadeModel(7424);
                        GlStateManager.enableAlpha();
                        GlStateManager.disableBlend();
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        GlStateManager.disableBlend();
                        GlStateManager.resetColor();
                        user.applyNameTagHeight(cosmeticRenderer.getNameTagHeight());
                        Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
                        GlStateManager.popMatrix();
                    }
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GlStateManager.enableAlpha();
                    GlStateManager.popMatrix();
                }
                if (LabyMod.getSettings().stickers && user.isStickerVisible()) {
                    long timePassed = System.currentTimeMillis() - user.getStickerStartedPlaying();
                    this.stickerRenderer.render(entityIn, user, timePassed, scale, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch);
                }
            }
        }
        if (swap) {
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            GlStateManager.disableCull();
        }
    }

    @Override
    public void setRotationAngles(float movementFactor, float walkingSpeed, float tickValue, float var4, float var5, float var6, Entity entityIn) {
        super.setRotationAngles(movementFactor, walkingSpeed, tickValue, var4, var5, var6, entityIn);
        if (LabyModCore.getMinecraft().isRightArmPoseBow(this) && this.mc18 && LabyMod.getSettings().oldSword && Permissions.isAllowed(Permissions.Permission.ANIMATIONS)) {
            this.bipedRightArm.rotateAngleY = 0.0f;
            if (this.swingProgress > -9990.0f) {
                ModelRenderer bipedRightArm = this.bipedRightArm;
                bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
                ModelRenderer bipedRightArm2 = this.bipedRightArm;
                bipedRightArm2.rotateAngleY += this.bipedBody.rotateAngleY * 2.0f;
            }
            if (LabyModCore.getMinecraft().isAimedBow(this)) {
                this.bipedRightArm.rotateAngleY = -0.1f + this.bipedHead.rotateAngleY;
            }
            this.bipedRightArmwear.rotateAngleY = this.bipedRightArm.rotateAngleY;
        }
        for (CosmeticRenderer<CosmeticData> cosmeticRenderer : this.cosmeticRenderers.values()) {
            cosmeticRenderer.setRotationAngles(this, movementFactor, walkingSpeed, tickValue, var4, var5, var6, entityIn);
        }
    }

    @Override
    public void setInvisible(boolean invisible) {
        for (CosmeticRenderer<CosmeticData> cosmeticRenderer : this.cosmeticRenderers.values()) {
            cosmeticRenderer.setInvisible(invisible);
        }
    }

    public void setVisible(boolean visible) {
        for (CosmeticRenderer<CosmeticData> cosmeticRenderer : this.cosmeticRenderers.values()) {
            cosmeticRenderer.setInvisible(visible);
        }
    }

    @Override
    public void setTextureOffset(String partName, int x2, int y2) {
        super.setTextureOffset(partName, x2, y2);
    }

    private void transformForLeftHand(Entity entityIn, float var1, float var2, float var3, float var4, float var5, float scale) {
        this.bipedRightArm.isHidden = true;
        this.bipedRightArmwear.isHidden = true;
        this.bipedLeftArm.isHidden = true;
        this.bipedLeftArmwear.isHidden = true;
        super.render(entityIn, var1, var2, var3, var4, var5, scale);
        GlStateManager.pushMatrix();
        GlStateManager.scale(-1.0f, 1.0f, 1.0f);
        GlStateManager.disableCull();
        this.bipedRightArm.isHidden = false;
        this.bipedRightArmwear.isHidden = false;
        this.bipedLeftArm.isHidden = false;
        this.bipedLeftArmwear.isHidden = false;
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0f, 0.2f, 0.0f);
        }
        this.bipedRightArm.render(scale);
        this.bipedRightArmwear.render(scale);
        this.bipedLeftArm.render(scale);
        this.bipedLeftArmwear.render(scale);
        GlStateManager.scale(-1.0f, 1.0f, 1.0f);
        GlStateManager.disableCull();
        GlStateManager.popMatrix();
    }

    public Map<Integer, CosmeticRenderer<CosmeticData>> getCosmeticRenderers() {
        return this.cosmeticRenderers;
    }
}

