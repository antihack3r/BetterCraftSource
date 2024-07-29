/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote.keys;

import java.beans.ConstructorProperties;

public class EmotePose {
    public static final int POSE_COUNT = 7;
    public static final int[] BLOCKING_MOVEMENT_IDS = new int[]{3, 4, 5, 6};
    private int bodyPart;
    private float x;
    private float y;
    private float z;
    private boolean interpolate;

    public String getName() {
        switch (this.bodyPart) {
            case 0: {
                return "Head";
            }
            case 1: {
                return "Right arm";
            }
            case 2: {
                return "Left arm";
            }
            case 3: {
                return "Left leg";
            }
            case 4: {
                return "Right leg";
            }
            case 5: {
                return "Chest";
            }
            case 6: {
                return "Position";
            }
        }
        return "Unknown";
    }

    public boolean isBlockMovement() {
        int[] nArray = BLOCKING_MOVEMENT_IDS;
        int n2 = BLOCKING_MOVEMENT_IDS.length;
        int n3 = 0;
        while (n3 < n2) {
            int id2 = nArray[n3];
            if (id2 == this.bodyPart) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public int getBodyPart() {
        return this.bodyPart;
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

    public boolean isInterpolate() {
        return this.interpolate;
    }

    public void setBodyPart(int bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void setX(float x2) {
        this.x = x2;
    }

    public void setY(float y2) {
        this.y = y2;
    }

    public void setZ(float z2) {
        this.z = z2;
    }

    public void setInterpolate(boolean interpolate) {
        this.interpolate = interpolate;
    }

    @ConstructorProperties(value={"bodyPart", "x", "y", "z", "interpolate"})
    public EmotePose(int bodyPart, float x2, float y2, float z2, boolean interpolate) {
        this.bodyPart = bodyPart;
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.interpolate = interpolate;
    }
}

