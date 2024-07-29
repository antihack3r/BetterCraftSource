/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.layers;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.cosmetic.layers.CapeParticleRenderer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LayerCustomCape
implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;
    private CapeParticleRenderer capeParticleRenderer = new CapeParticleRenderer();

    public LayerCustomCape(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float var1, float var2, float partialTicks, float var3, float var4, float var5, float scale) {
        if (entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE)) {
            int itemId;
            User user;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            User user2 = user = entitylivingbaseIn == null ? null : LabyMod.getInstance().getUserManager().getUser(entitylivingbaseIn.getUniqueID());
            if (user != null && !user.canRenderMojangCape(entitylivingbaseIn) || entitylivingbaseIn.getLocationCape() == null || LabyModCore.getMinecraft().isWearingElytra(entitylivingbaseIn)) {
                return;
            }
            this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0f, 0.0f, 0.125f);
            double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
            double d2 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
            double d3 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
            float f2 = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
            double d4 = LabyModCore.getMath().sin(f2 * (float)Math.PI / 180.0f);
            double d5 = -LabyModCore.getMath().cos(f2 * (float)Math.PI / 180.0f);
            float f22 = (float)d2 * 10.0f;
            f22 = LabyModCore.getMath().clamp_float(f22, -6.0f, 32.0f);
            float f3 = (float)(d0 * d4 + d3 * d5) * 100.0f;
            float f4 = (float)(d0 * d5 - d3 * d4) * 100.0f;
            if (f3 < 0.0f) {
                f3 = 0.0f;
            }
            if (f3 >= 180.0f) {
                f3 = 180.0f + (f3 - 180.0f) * 0.2f;
            }
            float f5 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
            f22 += LabyModCore.getMath().sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0f) * 32.0f * f5;
            if (entitylivingbaseIn.isSneaking()) {
                f3 += 50.0f;
            }
            boolean swap = LabyMod.getSettings().leftHand;
            ItemStack itemStack = LabyModCore.getMinecraft().getMainHandItem();
            int n2 = itemId = itemStack != null && itemStack.getItem() != null ? Item.getIdFromItem(itemStack.getItem()) : 0;
            if (LabyMod.getSettings().swapBow && itemId == 261) {
                boolean bl2 = swap = !swap;
            }
            if (swap && LabyModCore.getMinecraft().getItemInUseMaxCount() != 0 && itemId == 261 || swap && LabyMod.getInstance().isHasLeftHand()) {
                swap = false;
            }
            if (swap) {
                GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            }
            GlStateManager.rotate(6.0f + f3 / 2.0f + f22, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(f4 / 2.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotate(-f4 / 2.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translate(0.0f, 0.113f, 0.085f);
            }
            this.playerRenderer.getMainModel().renderCape(0.0625f);
            if (swap) {
                GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            }
            this.capeParticleRenderer.render(user, entitylivingbaseIn, partialTicks);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

