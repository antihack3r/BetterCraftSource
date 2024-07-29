/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.util.prediction;

public class JumpVec2f {
    public static final JumpVec2f ZERO = new JumpVec2f(0.0f, 0.0f);
    public static final JumpVec2f ONE = new JumpVec2f(1.0f, 1.0f);
    public static final JumpVec2f UNIT_X = new JumpVec2f(1.0f, 0.0f);
    public static final JumpVec2f NEGATIVE_UNIT_X = new JumpVec2f(-1.0f, 0.0f);
    public static final JumpVec2f UNIT_Y = new JumpVec2f(0.0f, 1.0f);
    public static final JumpVec2f NEGATIVE_UNIT_Y = new JumpVec2f(0.0f, -1.0f);
    public static final JumpVec2f MAX = new JumpVec2f(Float.MAX_VALUE, Float.MAX_VALUE);
    public static final JumpVec2f MIN = new JumpVec2f(Float.MIN_VALUE, Float.MIN_VALUE);
    public final float x;
    public final float y;

    public JumpVec2f(float xIn, float yIn) {
        this.x = xIn;
        this.y = yIn;
    }
}

