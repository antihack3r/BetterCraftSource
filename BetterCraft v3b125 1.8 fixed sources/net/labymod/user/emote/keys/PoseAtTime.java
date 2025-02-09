/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote.keys;

import java.beans.ConstructorProperties;
import net.labymod.user.emote.keys.EmotePose;

public class PoseAtTime {
    private EmotePose pose;
    private long offset;
    private boolean animate;

    public void setPose(EmotePose pose) {
        this.pose = pose;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public EmotePose getPose() {
        return this.pose;
    }

    public long getOffset() {
        return this.offset;
    }

    public boolean isAnimate() {
        return this.animate;
    }

    @ConstructorProperties(value={"pose", "offset", "animate"})
    public PoseAtTime(EmotePose pose, long offset, boolean animate) {
        this.pose = pose;
        this.offset = offset;
        this.animate = animate;
    }
}

