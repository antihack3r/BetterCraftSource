/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote.keys;

import net.labymod.user.emote.keys.EmotePose;

public class EmoteBodyPart {
    private int id;
    private long animationStart = 0L;
    private long animationDuration = 0L;
    private long prevOffset = 0L;
    private float x = 0.0f;
    private float y = 0.0f;
    private float z = 0.0f;
    private float rootX = 0.0f;
    private float rootY = 0.0f;
    private float rootZ = 0.0f;
    private float targetX = 0.0f;
    private float targetY = 0.0f;
    private float targetZ = 0.0f;
    private boolean interpolate;

    public EmoteBodyPart(int id2) {
        this.id = id2;
    }

    public void applyPose(EmotePose emoteKeyFrame, long duration) {
        this.rootX = this.targetX;
        this.rootY = this.targetY;
        this.rootZ = this.targetZ;
        this.targetX = emoteKeyFrame.getX() / 57.295776f;
        this.targetY = emoteKeyFrame.getY() / 57.295776f;
        this.targetZ = emoteKeyFrame.getZ() / 57.295776f;
        this.animationStart = System.currentTimeMillis();
        this.animationDuration = duration - this.prevOffset;
        this.prevOffset = duration;
        this.interpolate = emoteKeyFrame.isInterpolate();
    }

    public void cancel() {
        this.rootX = this.targetX;
        this.rootY = this.targetY;
        this.rootZ = this.targetZ;
        this.targetX = this.x;
        this.targetY = this.y;
        this.targetZ = this.z;
        this.animationStart = System.currentTimeMillis();
        this.animationDuration = 0L;
        this.prevOffset = 0L;
    }

    public void animateOnTime() {
        long progress = System.currentTimeMillis() - this.animationStart;
        this.x = EmoteBodyPart.process(this.rootX, this.targetX, progress, this.animationDuration, this.interpolate);
        this.y = EmoteBodyPart.process(this.rootY, this.targetY, progress, this.animationDuration, this.interpolate);
        this.z = EmoteBodyPart.process(this.rootZ, this.targetZ, progress, this.animationDuration, this.interpolate);
    }

    public static float process(float root, float target, long progress, long animationDuration, boolean interpolate) {
        if (root == target || animationDuration == 0L || progress > animationDuration) {
            return target;
        }
        if (interpolate) {
            return EmoteBodyPart.interpolate(root, target, progress, animationDuration);
        }
        return EmoteBodyPart.linear(root, target, progress, animationDuration);
    }

    public static float interpolate(float startY, float endY, float currentTime, float endTime) {
        if (startY == endY || endTime == 0.0f || currentTime > endTime) {
            return endY;
        }
        return startY + EmoteBodyPart.sigmoid(currentTime / endTime * 4.0f) * (endY - startY);
    }

    public static float linear(float root, float target, long progress, long animationDuration) {
        if (root == target || animationDuration == 0L || progress > animationDuration) {
            return target;
        }
        float difference = root - target;
        return root - difference / (float)animationDuration * (float)progress;
    }

    private static float sigmoid(float input) {
        return (float)(1.0 / (1.0 + Math.exp(-input * 2.0f + 4.0f)));
    }

    public int getId() {
        return this.id;
    }

    public long getAnimationStart() {
        return this.animationStart;
    }

    public long getAnimationDuration() {
        return this.animationDuration;
    }

    public long getPrevOffset() {
        return this.prevOffset;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float getRootX() {
        return this.rootX;
    }

    public float getRootY() {
        return this.rootY;
    }

    public float getRootZ() {
        return this.rootZ;
    }

    public float getTargetX() {
        return this.targetX;
    }

    public float getTargetY() {
        return this.targetY;
    }

    public float getTargetZ() {
        return this.targetZ;
    }

    public boolean isInterpolate() {
        return this.interpolate;
    }
}

