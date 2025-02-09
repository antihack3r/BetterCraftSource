/*
 * Decompiled with CFR 0.152.
 */
package lumien.chunkanimator;

import lumien.chunkanimator.handler.AnimationHandler;

public class ChunkAnimator {
    public int mode = 0;
    public int animationDuration = 2000;
    public int easingFunction = 10;
    public boolean disableAroundPlayer = false;
    private static final ChunkAnimator INSTANCE = new ChunkAnimator();
    public AnimationHandler animationHandler;

    public static ChunkAnimator getInstance() {
        return INSTANCE;
    }

    public void init() {
        this.animationHandler = new AnimationHandler();
    }
}

