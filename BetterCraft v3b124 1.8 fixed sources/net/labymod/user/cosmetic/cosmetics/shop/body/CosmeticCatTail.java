/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.body;

import java.awt.Color;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.labymod.user.emote.EmoteRenderer;
import net.labymod.user.emote.keys.PoseAtTime;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class CosmeticCatTail
extends CosmeticRenderer<CosmeticCatTailData> {
    public static final int ID = 3;
    private ModelRenderer tail;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        this.tail = new ModelRenderer(modelCosmetics).setTextureSize(4, 3).setTextureOffset(0, 0);
        this.tail.addBox(-0.5f, 0.0f, 0.0f, 1, 2, 1, modelSize);
        this.tail.setRotationPoint(0.0f, 11.0f, 2.0f);
        this.tail.rotateAngleX = 0.9424778f;
        ModelRenderer last = this.tail;
        int i2 = 0;
        while (i2 <= 7) {
            ModelRenderer part = new ModelRenderer(modelCosmetics).setTextureSize(4, 3);
            part.addBox(-0.5f, 0.0f, 0.0f, 1, 2, 1, modelSize - 0.01f * (float)i2);
            part.setRotationPoint(0.0f, 1.6f, 0.0f);
            last.addChild(part);
            last = part;
            ++i2;
        }
        this.tail.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.tail.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticCatTailData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        ModelRenderer tail = this.bindTextureAndColor(cosmeticData.getColor(), ModTextures.COSMETIC_CAT_TAIL, this.tail);
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0f, 0.1f, -0.15f);
            GlStateManager.rotate(45.0f, 1.0f, 0.0f, 0.0f);
        }
        float partialTicks = LabyMod.getInstance().getPartialTicks();
        AbstractClientPlayer entitylivingbaseIn = (AbstractClientPlayer)entityIn;
        double motionX = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
        double motionY = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
        double motionZ = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
        float motionYaw = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
        double yawSin = LabyModCore.getMath().sin(motionYaw * (float)Math.PI / 180.0f);
        double yawCos = -LabyModCore.getMath().cos(motionYaw * (float)Math.PI / 180.0f);
        float rotation = (float)motionY * 10.0f;
        rotation = LabyModCore.getMath().clamp_float(rotation, -6.0f, 32.0f);
        float motionAdd = (float)(motionX * yawSin + motionZ * yawCos) * 100.0f;
        float motionSub = (float)(motionX * yawCos - motionZ * yawSin) * 100.0f;
        if (motionAdd < 0.0f) {
            motionAdd = 0.0f;
        }
        if (motionAdd >= 130.0f) {
            motionAdd = 130.0f + (motionAdd - 180.0f) * 0.2f;
        }
        try {
            float roll = 0.0f;
            EmoteRenderer emoteRenderer = LabyMod.getInstance().getEmoteRegistry().getPlayingEmotes().get(entitylivingbaseIn.getUniqueID());
            if (emoteRenderer != null && !emoteRenderer.isAborted() && emoteRenderer.isVisible() && !emoteRenderer.isStream()) {
                long duration = System.currentTimeMillis() - emoteRenderer.getStartTime();
                float percent = 0.001f * (float)Math.min(duration, 1000L);
                float fadeOut = emoteRenderer.getTimeout() == 0L ? 0.0f : (float)(duration > emoteRenderer.getResetKeyframeEnd() ? emoteRenderer.getTimeout() : emoteRenderer.getResetKeyframeEnd() - duration);
                float timeout = emoteRenderer.getTimeout() == 0L ? 1.0f : 1.0f / (float)emoteRenderer.getTimeout() * fadeOut;
                roll = 0.3f * (percent *= timeout);
                boolean isBlockMovement = false;
                PoseAtTime[] poseAtTimeArray = emoteRenderer.getEmotePosesAtTime();
                int n2 = poseAtTimeArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    PoseAtTime poseAtTime = poseAtTimeArray[n3];
                    if (poseAtTime != null && poseAtTime.getPose().isBlockMovement()) {
                        isBlockMovement = true;
                        break;
                    }
                    ++n3;
                }
                if (!isBlockMovement) {
                    roll = 0.0f;
                }
            }
            int index = 0;
            ModelRenderer next = this.tail;
            while (next != null && next.childModels != null && next.childModels.size() != 0) {
                float walk = LabyModCore.getMath().cos(movementFactor + (float)index * 0.5f) * 0.03f * (float)index * (float)(index % 4 != 0 ? -1 : 1);
                next.rotateAngleX = index >= 0 && index < 2 ? -0.4f + walkingSpeed / (float)(index + 1) + walk + roll / 2.0f : (index >= 5 ? -walkingSpeed / 2.0f + 0.4f + walk + roll / 2.0f : roll);
                if (index == 0) {
                    ModelRenderer modelRenderer = next;
                    modelRenderer.rotateAngleX += (float)(0.85 + (double)(roll * 3.0f));
                }
                next.rotateAngleZ = motionSub / 400.0f;
                next = next.childModels.get(0);
                ++index;
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            GlStateManager.popMatrix();
        }
        tail.isHidden = false;
        tail.render(scale);
        tail.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 3;
    }

    @Override
    public String getCosmeticName() {
        return "Cat Tail";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticCatTailData
    extends CosmeticData {
        private Color color = Color.WHITE;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) {
            this.color = Color.decode("#" + data[0]);
        }

        public Color getColor() {
            return this.color;
        }
    }
}

