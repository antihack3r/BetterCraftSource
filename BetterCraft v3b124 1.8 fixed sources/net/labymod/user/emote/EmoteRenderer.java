/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote;

import java.util.UUID;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.user.emote.keys.EmoteBodyPart;
import net.labymod.user.emote.keys.EmotePose;
import net.labymod.user.emote.keys.PoseAtTime;
import net.labymod.user.emote.keys.provider.EmoteProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class EmoteRenderer {
    private UUID uuid;
    private short emoteId;
    private long timeout;
    private boolean stream;
    private EmoteProvider emoteProvider;
    private long startTime = System.currentTimeMillis();
    private PoseAtTime[] emotePosesAtTime = new PoseAtTime[7];
    private boolean aborted = false;
    private long resetKeyframeEnd = -1L;
    private EmoteBodyPart[] bodyParts;
    private float fadedYaw;
    private float fadedPitch;
    private boolean visible = true;
    private boolean mc18 = Source.ABOUT_MC_VERSION.startsWith("1.8");

    public EmoteRenderer(UUID uuid, short emoteId, long timeout, boolean stream, EmoteProvider emoteProvider, EmoteRenderer prevRenderer) {
        this.uuid = uuid;
        this.emoteId = emoteId;
        this.timeout = timeout;
        this.stream = stream;
        this.emoteProvider = emoteProvider;
        this.bodyParts = new EmoteBodyPart[7];
        int i2 = 0;
        while (i2 < this.bodyParts.length) {
            this.bodyParts[i2] = new EmoteBodyPart(i2);
            ++i2;
        }
        if (prevRenderer != null) {
            EmoteBodyPart[] emoteBodyPartArray = this.bodyParts = prevRenderer.getBodyParts();
            int n2 = this.bodyParts.length;
            int n3 = 0;
            while (n3 < n2) {
                EmoteBodyPart bodyPart = emoteBodyPartArray[n3];
                bodyPart.cancel();
                ++n3;
            }
        }
    }

    public void checkForNextFrame() {
        int bodyPartId;
        long currentTick = System.currentTimeMillis() - this.startTime;
        if (this.emoteProvider.isWaiting()) {
            return;
        }
        boolean shouldReset = true;
        if (this.resetKeyframeEnd == -1L) {
            bodyPartId = 0;
            while (bodyPartId < this.emotePosesAtTime.length) {
                PoseAtTime emotePose = this.emotePosesAtTime[bodyPartId];
                if (emotePose != null && emotePose.getOffset() >= currentTick || this.emoteProvider.hasNext(bodyPartId)) {
                    shouldReset = false;
                }
                if ((emotePose == null || emotePose.getOffset() < currentTick) && this.emoteProvider.hasNext(bodyPartId)) {
                    PoseAtTime nextPose;
                    this.emotePosesAtTime[bodyPartId] = nextPose = this.emoteProvider.next(bodyPartId);
                    this.bindNextPose(this.emotePosesAtTime[bodyPartId]);
                }
                ++bodyPartId;
            }
        }
        if (shouldReset) {
            if (this.resetKeyframeEnd == -1L) {
                this.resetKeyframeEnd = currentTick + this.timeout;
                bodyPartId = 0;
                while (bodyPartId < this.emotePosesAtTime.length) {
                    EmotePose pose = new EmotePose(bodyPartId, 0.0f, 0.0f, 0.0f, false);
                    EmoteBodyPart[] emoteBodyPartArray = this.bodyParts;
                    int n2 = this.bodyParts.length;
                    int n3 = 0;
                    while (n3 < n2) {
                        EmoteBodyPart bodyPart = emoteBodyPartArray[n3];
                        if (bodyPart.getId() == pose.getBodyPart()) {
                            bodyPart.cancel();
                            bodyPart.applyPose(pose, this.timeout);
                        }
                        ++n3;
                    }
                    ++bodyPartId;
                }
            } else if (this.resetKeyframeEnd < currentTick && !this.aborted) {
                this.aborted = true;
                if (this.aborted) {
                    LabyMod.getInstance().getEmoteRegistry().setCleanPlayingMap(true);
                }
            }
        }
    }

    public void bindNextPose(PoseAtTime poseAtTime) {
        EmoteBodyPart[] emoteBodyPartArray = this.bodyParts;
        int n2 = this.bodyParts.length;
        int n3 = 0;
        while (n3 < n2) {
            EmoteBodyPart bodyPart = emoteBodyPartArray[n3];
            if (bodyPart.getId() == poseAtTime.getPose().getBodyPart()) {
                bodyPart.applyPose(poseAtTime.getPose(), poseAtTime.isAnimate() ? poseAtTime.getOffset() : 0L);
            }
            ++n3;
        }
    }

    public void animate() {
        EmoteBodyPart[] emoteBodyPartArray = this.bodyParts;
        int n2 = this.bodyParts.length;
        int n3 = 0;
        while (n3 < n2) {
            EmoteBodyPart bodyPart = emoteBodyPartArray[n3];
            bodyPart.animateOnTime();
            ++n3;
        }
    }

    public void transformEntity(Entity entity, boolean firstPerson, float yaw, float pitch) {
        if (this.aborted || !this.visible) {
            return;
        }
        if (!this.mc18 && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            this.checkForNextFrame();
            this.animate();
        }
        this.fadedYaw = yaw;
        this.fadedPitch = pitch;
        Object[] objectArray = this.bodyParts;
        int n2 = this.bodyParts.length;
        int n3 = 0;
        while (n3 < n2) {
            EmoteBodyPart bodyPart = objectArray[n3];
            PoseAtTime[] poseAtTimeArray = this.emotePosesAtTime;
            int n4 = this.emotePosesAtTime.length;
            int n5 = 0;
            while (n5 < n4) {
                PoseAtTime pose = poseAtTimeArray[n5];
                if (pose != null && pose.getPose().getBodyPart() == bodyPart.getId()) {
                    switch (bodyPart.getId()) {
                        case 0: {
                            float diff;
                            float maxFade;
                            float emoteYaw = bodyPart.getX() * 57.295776f / 2.0f;
                            float emotePitch = bodyPart.getY() * 57.295776f / 2.0f;
                            if (this.stream || this.timeout == 0L) {
                                this.fadedYaw = emoteYaw;
                                this.fadedPitch = emotePitch;
                                break;
                            }
                            long currentTick = System.currentTimeMillis() - this.startTime;
                            long keyframeEnd = this.resetKeyframeEnd;
                            float fadeIn = maxFade - ((float)currentTick > (maxFade = (float)this.timeout) ? maxFade : (float)currentTick);
                            float fadeOut = this.timeout - (currentTick > keyframeEnd ? 0L : keyframeEnd - currentTick);
                            float fade = fadeIn;
                            if (keyframeEnd != -1L) {
                                fade = fadeOut;
                            }
                            float rangeYaw = (diff = (yaw - emoteYaw + 180.0f) % 360.0f - 180.0f) < -180.0f ? diff + 360.0f : diff;
                            this.fadedYaw = emoteYaw + rangeYaw / maxFade * fade;
                            float rangePitch = pitch - emotePitch;
                            this.fadedPitch = emotePitch + rangePitch / maxFade * fade;
                            break;
                        }
                        case 5: {
                            GlStateManager.translate(0.0f, firstPerson ? 1.0f : 0.4f, 0.0f);
                            int modifier = firstPerson ? -1 : 1;
                            GlStateManager.rotate(bodyPart.getX() * 57.295776f * (float)modifier, 1.0f, 0.0f, 0.0f);
                            GlStateManager.rotate(bodyPart.getY() * 57.295776f, 0.0f, 1.0f, 0.0f);
                            GlStateManager.rotate(bodyPart.getZ() * 57.295776f, 0.0f, 0.0f, 1.0f);
                            GlStateManager.translate(0.0f, firstPerson ? -1.0f : -0.4f, 0.0f);
                            break;
                        }
                        case 6: {
                            GlStateManager.translate((double)bodyPart.getX() / 10.0, (double)bodyPart.getY() / 10.0, (double)bodyPart.getZ() / 10.0);
                        }
                    }
                }
                ++n5;
            }
            ++n3;
        }
        if (Math.abs(entity.prevPosX - entity.posX) > 0.04 || Math.abs(entity.prevPosY - entity.posY) > 0.04 || Math.abs(entity.prevPosZ - entity.posZ) > 0.04) {
            objectArray = this.emotePosesAtTime;
            n2 = this.emotePosesAtTime.length;
            n3 = 0;
            while (n3 < n2) {
                Object poseAtTime = objectArray[n3];
                if (poseAtTime != null && ((PoseAtTime)poseAtTime).getPose().isBlockMovement()) {
                    this.abort();
                    break;
                }
                ++n3;
            }
        }
        if (entity instanceof EntityPlayer && ((EntityPlayer)entity).hurtTime != 0) {
            this.abort();
        }
        if (LabyModCore.getMinecraft().isElytraFlying(entity)) {
            this.abort();
        }
    }

    public void transformModel(ModelBiped model) {
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            this.checkForNextFrame();
            this.animate();
            Minecraft.getMinecraft().renderGlobal.setDisplayListEntitiesDirty();
        }
        if (model == null || this.aborted || !this.visible) {
            return;
        }
        EmoteBodyPart[] emoteBodyPartArray = this.bodyParts;
        int n2 = this.bodyParts.length;
        int n3 = 0;
        while (n3 < n2) {
            EmoteBodyPart bodyPart = emoteBodyPartArray[n3];
            PoseAtTime[] poseAtTimeArray = this.emotePosesAtTime;
            int n4 = this.emotePosesAtTime.length;
            int n5 = 0;
            while (n5 < n4) {
                PoseAtTime pose = poseAtTimeArray[n5];
                if (pose != null && pose.getPose().getBodyPart() == bodyPart.getId()) {
                    switch (bodyPart.getId()) {
                        case 0: {
                            model.bipedHead.rotateAngleY = this.getFadedYaw() / 57.295776f;
                            model.bipedHead.rotateAngleX = this.getFadedPitch() / 57.295776f;
                            break;
                        }
                        case 1: {
                            model.bipedRightArm.rotateAngleX = bodyPart.getX();
                            model.bipedRightArm.rotateAngleY = bodyPart.getY();
                            model.bipedRightArm.rotateAngleZ = bodyPart.getZ();
                            break;
                        }
                        case 2: {
                            model.bipedLeftArm.rotateAngleX = bodyPart.getX();
                            model.bipedLeftArm.rotateAngleY = bodyPart.getY();
                            model.bipedLeftArm.rotateAngleZ = bodyPart.getZ();
                            break;
                        }
                        case 3: {
                            model.bipedLeftLeg.rotateAngleX = bodyPart.getX();
                            model.bipedLeftLeg.rotateAngleY = bodyPart.getY();
                            model.bipedLeftLeg.rotateAngleZ = bodyPart.getZ();
                            break;
                        }
                        case 4: {
                            model.bipedRightLeg.rotateAngleX = bodyPart.getX();
                            model.bipedRightLeg.rotateAngleY = bodyPart.getY();
                            model.bipedRightLeg.rotateAngleZ = bodyPart.getZ();
                        }
                    }
                }
                if (model instanceof ModelPlayer) {
                    EmoteRenderer.copyToSecondLayer((ModelPlayer)model);
                }
                ++n5;
            }
            ++n3;
        }
    }

    public static void resetModel(ModelBiped model) {
        EmoteRenderer.resetModelRenderer(model.bipedRightArm);
        EmoteRenderer.resetModelRenderer(model.bipedLeftArm);
        EmoteRenderer.resetModelRenderer(model.bipedLeftLeg);
        EmoteRenderer.resetModelRenderer(model.bipedRightLeg);
        EmoteRenderer.resetModelRenderer(model.bipedBody);
        if (model instanceof ModelPlayer) {
            EmoteRenderer.copyToSecondLayer((ModelPlayer)model);
        }
    }

    private static void resetModelRenderer(ModelRenderer modelRenderer) {
        modelRenderer.rotateAngleX = 0.0f;
        modelRenderer.rotateAngleY = 0.0f;
        modelRenderer.rotateAngleZ = 0.0f;
    }

    private static void copyToSecondLayer(ModelPlayer model) {
        ModelBiped.copyModelAngles(model.bipedHead, model.bipedHeadwear);
        ModelBiped.copyModelAngles(model.bipedBody, model.bipedBodyWear);
        ModelBiped.copyModelAngles(model.bipedRightArm, model.bipedRightArmwear);
        ModelBiped.copyModelAngles(model.bipedLeftArm, model.bipedLeftArmwear);
        ModelBiped.copyModelAngles(model.bipedLeftLeg, model.bipedLeftLegwear);
        ModelBiped.copyModelAngles(model.bipedRightLeg, model.bipedRightLegwear);
    }

    public void abort() {
        EmoteBodyPart[] emoteBodyPartArray = this.bodyParts;
        int n2 = this.bodyParts.length;
        int n3 = 0;
        while (n3 < n2) {
            EmoteBodyPart bodyPart = emoteBodyPartArray[n3];
            bodyPart.cancel();
            ++n3;
        }
        this.emoteProvider.clear();
        this.resetKeyframeEnd = 0L;
        this.checkForNextFrame();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public short getEmoteId() {
        return this.emoteId;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public boolean isStream() {
        return this.stream;
    }

    public EmoteProvider getEmoteProvider() {
        return this.emoteProvider;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public PoseAtTime[] getEmotePosesAtTime() {
        return this.emotePosesAtTime;
    }

    public boolean isAborted() {
        return this.aborted;
    }

    public long getResetKeyframeEnd() {
        return this.resetKeyframeEnd;
    }

    public EmoteBodyPart[] getBodyParts() {
        return this.bodyParts;
    }

    public float getFadedYaw() {
        return this.fadedYaw;
    }

    public float getFadedPitch() {
        return this.fadedPitch;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean isMc18() {
        return this.mc18;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

